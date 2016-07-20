package br.com.afirmanet.core.io.jatb.adapter;

public abstract class TxtAdapter<ValueType, BoundType> {

	protected TxtAdapter() {
	}

	public abstract BoundType unmarshal(ValueType v);

	public abstract ValueType marshal(BoundType v);

	public abstract ValueType marshalRemove(BoundType v, String caracter);

}
