package br.com.afirmanet.core.persistence;

import javax.persistence.metamodel.SingularAttribute;

import lombok.Getter;
import br.com.afirmanet.core.enumeration.OrderEnum;

@SuppressWarnings("rawtypes")
public class Order {

	@Getter
	private OrderEnum orderEnum;

	@Getter
	private SingularAttribute singularAttribute;

	private Order(OrderEnum orderEnum, SingularAttribute singularAttribute) {
		this.orderEnum = orderEnum;
		this.singularAttribute = singularAttribute;
	}

	public static Order desc(SingularAttribute singularAttribute) {
		return new Order(OrderEnum.DESC, singularAttribute);
	}

	public static Order asc(SingularAttribute singularAttribute) {
		return new Order(OrderEnum.ASC, singularAttribute);
	}

}
