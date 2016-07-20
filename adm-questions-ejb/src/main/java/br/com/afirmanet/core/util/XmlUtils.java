package br.com.afirmanet.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import lombok.SneakyThrows;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.afirmanet.core.exception.SystemException;

/**
 * <p>
 * Classe utilitária para manipulação de XML.
 * </p>
 */
public final class XmlUtils {

	private static volatile Map<Class<?>, JAXBContext> jaxbContextMap;

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private XmlUtils() {
		// Classe utilitária não deve ter construtor público ou default
	}

	/**
	 * <p>
	 * Recupera todos os <i>nodes</i> encontrado no arquivo XML informado, de acordo com a expressão XPath informada.
	 * </p>
	 *
	 * @param fileName
	 *        uma <code>String</code> que representa o nome do arquivo XML
	 * @param xPathExpression
	 *        uma <code>String</code> que define a expressão XPath que será considerada na extração dos nodes
	 *
	 * @return todos os <i>nodes</i> encontrados no arquivo XML que correspondam a expressão XPath
	 *
	 * @throws SystemException
	 *         se o <code>DocumentBuilder</code> não puder ser criado, se ocorrer algum erro de I/O, se a
	 *         <code>String</code> que representa a expressão XPath não puder ser avaliada, ou se ocorrer algum erro de
	 *         parse
	 *
	 */
	public static NodeList getNodeList(String fileName, String xPathExpression) {
		try (InputStream inputStream = XmlUtils.class.getResourceAsStream(fileName)) {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();
			NodeList nodeList = (NodeList) xPath.evaluate(xPathExpression, document, XPathConstants.NODESET);

			return nodeList;

		} catch (XPathExpressionException | ParserConfigurationException | IOException | SAXException e) {
			throw new SystemException(e);
		}
	}

	public static String getNodeValue(String fileName, String xPathExpression) {
		try (InputStream inputStream = XmlUtils.class.getResourceAsStream(fileName)) {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();
			String value = xPath.evaluate(xPathExpression, document);

			return value;

		} catch (Throwable e) {
			throw new SystemException(e);
		}
	}

	private static JAXBContext getJAXBContext(final Class<?> rootNode) {
		try {
			if (jaxbContextMap == null) {
				jaxbContextMap = new HashMap<>();
			}

			JAXBContext jaxbContext = jaxbContextMap.get(rootNode);
			if (jaxbContext == null) {
				jaxbContext = JAXBContext.newInstance(rootNode);
				jaxbContextMap.put(rootNode, jaxbContext);
			}

			return jaxbContext;

		} catch (JAXBException e) {
			throw new SystemException(e);
		}
	}

	public static <T> String marshalToString(final JAXBContext jaxbContext, final T rootNodeInstance, final Map<String, Object> marshallerProperties) {
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();

			for (Entry<String, Object> entry : marshallerProperties.entrySet()) {
				marshaller.setProperty(entry.getKey(), entry.getValue());
			}

			StringWriter stringWriter = new StringWriter();
			marshaller.marshal(rootNodeInstance, stringWriter);

			return stringWriter.toString();

		} catch (JAXBException e) {
			throw new SystemException(e);
		}
	}

	public static <T> String marshalToString(final T rootNodeInstance, final Map<String, Object> marshallerProperties) {
		return marshalToString(getJAXBContext(rootNodeInstance.getClass()), rootNodeInstance, marshallerProperties);
	}

	public static <T> String marshalToString(final T rootNodeInstance) {
		Map<String, Object> properties = new HashMap<>();
		properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		properties.put(Marshaller.JAXB_ENCODING, "ISO-8859-1");

		return marshalToString(rootNodeInstance, properties);
	}

	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(final JAXBContext jaxbContext, final String xmlContent, Class<T> rootNode) {
		try {
			if (StringUtils.isBlank(xmlContent) || rootNode == null) {
				return (T) null;
			}

			Source source = new StreamSource(new StringReader(xmlContent));

			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			T instance = (T) unmarshaller.unmarshal(source);

			return instance;
		} catch (JAXBException e) {
			throw new SystemException(e);
		}
	}

	public static <T> T unmarshal(final String xmlContent, Class<T> rootNode) {
		return unmarshal(getJAXBContext(rootNode), xmlContent, rootNode);
	}

	@SneakyThrows(SAXException.class)
	public static Schema newSchema(final URL xsdURL) {
		String schemaLanguage = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		SchemaFactory schemaFactory = SchemaFactory.newInstance(schemaLanguage);

		Schema schema = schemaFactory.newSchema(xsdURL);
		return schema;
	}

	public static void parse(Schema schema, Source source) {
		try {
			Validator validator = schema.newValidator();
			validator.validate(source);

		} catch (IOException | SAXException e) {
			throw new SystemException(e);
		}
	}

	public static void parse(Schema schema, String xmlContent) {
		Source source = new StreamSource(IoUtils.toInputStream(xmlContent));
		parse(schema, source);
	}

	/**
	 * <p>
	 * Decodifica caracteres especiais da <code>String</code> informada, ou seja, converte os caracteres de
	 * <b>escapes</b> para caracteres com representação <b>Unicode</b>.
	 * </p>
	 *
	 * <pre>
	 * XmlUtils.unescape("texto simples")            = texto simples
	 * XmlUtils.unescape("")                         =  // vazio
	 * XmlUtils.unescape(null)                       = null
	 * </pre>
	 *
	 * @param str
	 *        a <code>String</code> para decodificação, pode ser <code>null</code>
	 *
	 * @return uma <code>String</code> que representa os caracteres decodificados
	 *
	 */
	public static String unescape(String str) {
		return StringEscapeUtils.unescapeXml(str);
	}

	public static String getValueOfTag(String xml, String tagName, String attribName) {
		Matcher matcher = Pattern.compile("<\\s*" + tagName + "(?:[^<]*)?\\s" + attribName + "\\s*=\\s*\"([^<\"]*)\"(?:\\s[^<]*)?(?:/?\\s*)>").matcher(xml);
		if (matcher.find()) {
			String attribAspas = matcher.group(1);
			if (attribAspas != null) {
				return unescape(attribAspas.replaceAll("\"", ""));
			}
		}

		return null;
	}

}
