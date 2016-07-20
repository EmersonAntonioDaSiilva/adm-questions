package br.com.afirmanet.core.io.jatb.validator;

import lombok.NoArgsConstructor;
import br.com.afirmanet.core.io.jatb.annotation.TxtField;

@NoArgsConstructor
public abstract class TxtValidator<ValueType, BoundType> {

	public abstract boolean unmarshal(TxtField txtField, ValueType value);

	public abstract boolean marshal(TxtField txtField, BoundType value);

}
