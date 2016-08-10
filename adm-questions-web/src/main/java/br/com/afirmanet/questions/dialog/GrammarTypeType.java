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
 * <p>Classe Java de grammarTypeType.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <p>
 * <pre>
 * &lt;simpleType name="grammarTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VARIATIONS"/>
 *     &lt;enumeration value="JSGF"/>
 *     &lt;enumeration value="GRXML"/>
 *     &lt;enumeration value="AQL"/>
 *     &lt;enumeration value="DICT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "grammarTypeType")
@XmlEnum
public enum GrammarTypeType {

    VARIATIONS,
    JSGF,
    GRXML,
    AQL,
    DICT;

    public String value() {
        return name();
    }

    public static GrammarTypeType fromValue(String v) {
        return valueOf(v);
    }

}
