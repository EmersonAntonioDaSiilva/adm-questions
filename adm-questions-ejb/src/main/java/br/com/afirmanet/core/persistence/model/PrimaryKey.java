package br.com.afirmanet.core.persistence.model;

import java.io.Serializable;

public interface PrimaryKey<ID extends Serializable> extends Serializable {

	ID getId();

}
