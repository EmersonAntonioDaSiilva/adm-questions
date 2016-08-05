//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.8-b130911.1802 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2016.08.03 às 03:49:06 PM BRT 
//


package br.com.afirmanet.questions.dialog;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de actionOperatorType.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <p>
 * <pre>
 * &lt;simpleType name="actionOperatorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DO_NOTHING_STR"/>
 *     &lt;enumeration value="SET_TO"/>
 *     &lt;enumeration value="SET_TO_USER_INPUT"/>
 *     &lt;enumeration value="SET_TO_USER_INPUT_CORRECTED"/>
 *     &lt;enumeration value="INCREMENT_BY"/>
 *     &lt;enumeration value="DECREMENT_BY"/>
 *     &lt;enumeration value="SET_TO_YES"/>
 *     &lt;enumeration value="SET_TO_NO"/>
 *     &lt;enumeration value="YES"/>
 *     &lt;enumeration value="NO"/>
 *     &lt;enumeration value="SET_AS_USER_INPUT"/>
 *     &lt;enumeration value="SET_TO_BLANK"/>
 *     &lt;enumeration value="APPEND"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "actionOperatorType")
@XmlEnum
public enum ActionOperatorType {

    DO_NOTHING_STR,
    SET_TO,
    SET_TO_USER_INPUT,
    SET_TO_USER_INPUT_CORRECTED,
    INCREMENT_BY,
    DECREMENT_BY,
    SET_TO_YES,
    SET_TO_NO,
    YES,
    NO,
    SET_AS_USER_INPUT,
    SET_TO_BLANK,
    APPEND;

    public String value() {
        return name();
    }

    public static ActionOperatorType fromValue(String v) {
        return valueOf(v);
    }

}
