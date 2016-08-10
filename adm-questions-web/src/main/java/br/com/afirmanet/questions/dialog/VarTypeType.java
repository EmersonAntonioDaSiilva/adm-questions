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
 * <p>Classe Java de varTypeType.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <p>
 * <pre>
 * &lt;simpleType name="varTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TEXT"/>
 *     &lt;enumeration value="NUMBER"/>
 *     &lt;enumeration value="YESNO"/>
 *     &lt;enumeration value="DATETIME"/>
 *     &lt;enumeration value="TAG"/>
 *     &lt;enumeration value="CONST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "varTypeType")
@XmlEnum
public enum VarTypeType {

    TEXT,
    NUMBER,
    YESNO,
    DATETIME,
    TAG,
    CONST;

    public String value() {
        return name();
    }

    public static VarTypeType fromValue(String v) {
        return valueOf(v);
    }

}
