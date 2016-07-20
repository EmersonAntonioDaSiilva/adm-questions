package br.com.afirmanet.core.balancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.exception.SystemException;

/**
 *
 * <h1>Exemplo de Uso</h1>
 *
 * <pre>
 * // Recupera as urls/targets para acesso aos servidores
 * String urls = parametroSistemaDAO.getValue(Parametro.URLS_WS_PROCESSAMENTO_CESTA_CREDITO);
 *
 * Iterable&lt;String&gt; valores = Splitter.on(&quot;;&quot;).omitEmptyStrings().trimResults().split(urls);
 * Iterator&lt;String&gt; iterator = valores.iterator();
 *
 * // Executa a regra de negócio utilizando o balanceamento
 * Balancer balancer = new Balancer(iterator);
 * balancer.init(new Executor&lt;Object&gt;() {
 *
 * 	&#064;Override
 * 	public Object run(String target, Object... parameters) {
 * 		URL url = new URL(target);
 * 		br.com.afirmanet.sda.ws.cestacredito.client.CestaCredito port = new br.com.afirmanet.sda.ws.cestacredito.client.CestaCreditoService(url).getCestaCreditoPort();
 * 		port.processarCesta(arquivo.getName());
 *
 * 		return null;
 * 	}
 *
 * });
 *
 * </pre>
 *
 */
@Slf4j
public class Balancer {

	private List<Server> servers = new ArrayList<>();

	public Balancer(Iterator<String> iterator) {
		if (servers.isEmpty()) {
			initTargets(iterator);
		}
	}

	public <T> T init(Executor<T> executor, Object... parameters) {
		T object = (T) null;

		Server server = balanceServers();

		try {
			object = executor.run(server.getTarget(), parameters);

		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			server.addError();
			log.error("Servidor inacessível: {}", server.getTarget());
			log.error(e.getMessage(), e);
			return init(executor, parameters);
		}

		availableServers();

		return object;
	}

	private Server balanceServers() {
		Collections.shuffle(servers);
		for (Server server : servers) {
			if (server.isAvailable()) {
				return server;
			}
		}

		availableServers();

		throw new SystemException("Os servidores/serviços não estão acessíveis!");
	}

	private void availableServers() {
		for (Server server : servers) {
			server.setAvailable();
		}
	}

	private void initTargets(Iterator<String> iterator) {
		while (iterator.hasNext()) {
			Server servidor = new Server(iterator.next());
			servers.add(servidor);
		}
	}

}
