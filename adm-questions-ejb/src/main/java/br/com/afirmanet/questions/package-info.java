@TypeDefs({ @TypeDef(name = "localDateType", defaultForType = LocalDate.class, typeClass = LocalDateUserType.class),
	@TypeDef(name = "localDateTimeType", defaultForType = LocalDateTime.class, typeClass = LocalDateTimeUserType.class),
	@TypeDef(name = "localTimeType", defaultForType = LocalTime.class, typeClass = LocalTimeUserType.class) })
package br.com.afirmanet.questions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import br.com.afirmanet.core.persistence.type.LocalDateTimeUserType;
import br.com.afirmanet.core.persistence.type.LocalDateUserType;
import br.com.afirmanet.core.persistence.type.LocalTimeUserType;

