package br.com.afirmanet.core.balancer;

import org.apache.commons.lang3.StringUtils;

public class Server {

	private String target = StringUtils.EMPTY;

	private Integer errors = 0;

	public Server(String target) {
		this.target = target;
	}

	public Boolean isAvailable() {
		return errors == 0;
	}

	public void setAvailable() {
		errors = 0;
	}

	public String getTarget() {
		return target;
	}

	public void addError() {
		errors++;
	}

}
