package br.com.afirmanet.core.balancer;

public interface Executor<T> {

	T run(String target, Object... parameters);

}