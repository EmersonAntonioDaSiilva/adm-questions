package br.com.afirmanet.core.util;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.com.afirmanet.core.exception.SystemException;

/**
 * <p>
 * Classe utilitária cujos métodos utilizam como base a API de reflexão.
 * </p>
 */
public final class ReflectionUtils {

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private ReflectionUtils() {
		// Classe utilitária não deve ter construtor público ou default
	}

	public static Field findIdColumn(Class<?> clazz) {
		Field field = null;
		Field[] fileds = clazz.getDeclaredFields();

		for (Field f : fileds) {
			for (Annotation annotation : f.getAnnotations()) {
				if (annotation.annotationType().equals(Id.class)) {
					field = f;
					break;
				}
			}
		}

		return field;
	}

	/**
	 * <p>
	 * Obtém o nome do atributo que está anotado com <code>'@Id'</code>.
	 * </p>
	 *
	 * @param clazz
	 *        um <code>Class</code> que representa a classe que se deseja localizar o nome do atributo
	 *
	 * @return nome do atributo
	 */
	public static String findIdColumnName(final Class<?> clazz) {
		Field field = findIdColumn(clazz);
		return field != null ? field.getName() : null;
	}

	/**
	 * <p>
	 * Recupera o tipo do atributo que está anotado com <code>'@Id'</code>.
	 *
	 * @param clazz
	 *        instância da classe que se deseja localizar o nome do atributo
	 * @return tipo do atributo que está anotado com <code>'@Id'</code>
	 */
	public static Class<?> findIdColumnType(Class<?> clazz) {
		Field field = findIdColumn(clazz);
		return field != null ? field.getType() : null;
	}

	public static Boolean isAnnotationPresent(final Class<?> clazz, final PropertyDescriptor property, final Class<? extends Annotation> annotationClass) {
		try {
			Field field = clazz.getDeclaredField(property.getName());
			return field.isAnnotationPresent(annotationClass);

		} catch (Throwable e) {
			return false;
		}
	}

	public static <T> List<Class<T>> findClassesImplementing(final Class<T> interfaceClass, final String packageName) {
		return findClassesImplementing(interfaceClass, packageName, false);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<Class<T>> findClassesImplementing(final Class<T> interfaceClass, final String packageName, final boolean ignoreInterface) {
		if (interfaceClass == null) {
			throw new IllegalArgumentException("A interface a ser pesquisada não pode ser nula.");
		}

		if (packageName == null) {
			throw new IllegalArgumentException("O nome do pacote em que se deseja realizar a pesquisa não pode ser nulo.");
		}

		List<Class<T>> classesImplementing = new ArrayList<>();

		List<Class<?>> classesFromPackage = findClassesFromPackage(packageName);
		for (Class<?> classFromPackage : classesFromPackage) {
			if (classFromPackage == null || classFromPackage.equals(interfaceClass) || !interfaceClass.isAssignableFrom(classFromPackage) || (classFromPackage.isInterface() && ignoreInterface)) {
				continue;
			}

			Class<T> clazz = (Class<T>) classFromPackage;
			classesImplementing.add(clazz);
		}

		return classesImplementing;
	}

	public static List<Class<?>> findClassesFromPackage(final String packageName) {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			assert classLoader != null;
			String path = packageName.replace('.', '/');

			List<File> directories = new ArrayList<>();

			Enumeration<URL> resources = classLoader.getResources(path);
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				directories.add(new File(resource.getFile()));
			}

			List<Class<?>> classes = new ArrayList<>();
			for (File directory : directories) {
				classes.addAll(findClasses(directory, packageName));
			}

			return classes;

		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	private static List<Class<?>> findClasses(final File directory, final String packageName) {
		try {
			List<Class<?>> classes = new ArrayList<>();

			// Caso o diretório não exista, verificamos se o mesmo não está comprimido em .jar
			if (!directory.exists()) {
				String jarPath = directory.getPath().split("!")[0].replace("file:\\", "");
				if (jarPath.endsWith(".jar")) {
					try (JarFile jarFile = new JarFile(jarPath)) {
						Enumeration<JarEntry> jarEntries = jarFile.entries();

						String packageDirectory = packageName.replace('.', '/');

						while (jarEntries.hasMoreElements()) {
							JarEntry jarEntry = jarEntries.nextElement();
							if (jarEntry.getName().startsWith(packageDirectory) && jarEntry.getName().endsWith(".class")) {
								String className = jarEntry.getName().replace('/', '.').substring(0, jarEntry.getName().length() - 6);
								classes.add(Class.forName(className, false, Thread.currentThread().getContextClassLoader()));
							}
						}
					}
				}

				return classes;
			}

			File[] files = directory.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					assert !file.getName().contains(".");
					classes.addAll(findClasses(file, packageName + "." + file.getName()));

				} else if (file.getName().endsWith(".class")) {
					String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
					classes.add(Class.forName(className, false, Thread.currentThread().getContextClassLoader()));
				}
			}

			return classes;

		} catch (IOException | ClassNotFoundException e) {
			throw new SystemException(e);
		}
	}

	public static boolean isEntity(Class<?> clazz) {
		return clazz.getAnnotation(Entity.class) != null;
	}

}
