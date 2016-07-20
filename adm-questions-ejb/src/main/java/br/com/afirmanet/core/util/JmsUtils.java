package br.com.afirmanet.core.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.SneakyThrows;

import com.google.common.collect.Lists;

public final class JmsUtils {

	public static void sendMessages(String queueName, Collection<? extends Serializable> messages) {
		ConnectionFactory connectionFactory = createConectionFactory();

		try (JMSContext jmsContext = connectionFactory.createContext()) {

			Queue queue = jmsContext.createQueue(queueName);
			JMSProducer producer = jmsContext.createProducer();

			for (Serializable message : messages) {
				producer.send(queue, message);
			}
		}

	}

	public static <T extends Serializable> void sendMessage(String queueName, T message) {
		Collection<T> lista = new ArrayList<>();
		lista.add(message);

		sendMessages(queueName, lista);
	}

	public static <T extends Serializable> void sendMessages(String queueName, List<T> messages, Integer partitionSize) {
		Collection<ArrayList<T>> tempList = new ArrayList<>();

		List<List<T>> listPartitioned = Lists.partition(messages, partitionSize);
		for (List<T> list : listPartitioned) {
			tempList.add(new ArrayList<>(list));
		}

		sendMessages(queueName, tempList);
	}

	@SneakyThrows(JMSException.class)
	public static Boolean hasMoreMessages(String queueName) {
		ConnectionFactory connectionFactory = createConectionFactory();

		try (JMSContext jmsContext = connectionFactory.createContext()) {

			Queue queue = jmsContext.createQueue(queueName);
			try (QueueBrowser queueBrowser = jmsContext.createBrowser(queue)) {

				Boolean hasMoreMessages = queueBrowser.getEnumeration().hasMoreElements();
				return hasMoreMessages;
			}
		}
	}

	@SneakyThrows(NamingException.class)
	private static ConnectionFactory createConectionFactory() {
		javax.naming.Context context = new InitialContext();
		ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
		return connectionFactory;
	}

}