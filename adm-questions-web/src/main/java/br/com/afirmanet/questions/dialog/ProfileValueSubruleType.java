//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.8-b130911.1802 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2016.08.03 às 03:49:06 PM BRT 
//


package br.com.afirmanet.questions.dialog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de profileValueSubruleType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="profileValueSubruleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="condType" type="{}conditionOperatorType" />
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="varName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileValueSubruleType")
public class ProfileValueSubruleType {

    @XmlAttribute(name = "condType")
    protected ConditionOperatorType condType;
    @XmlAttribute(name = "value")
    protected String value;
    @XmlAttribute(name = "varName")
    protected String varName;

    /**
     * Obtém o valor da propriedade condType.
     * 
     * @return
     *     possible object is
     *     {@link ConditionOperatorType }
     *     
     */
    public ConditionOperatorType getCondType() {
        return condType;
    }

    /**
     * Define o valor da propriedade condType.
     * 
     * @param value
     *     allowed object is
     *     {@link ConditionOperatorType }
     *     
     */
    public void setCondType(ConditionOperatorType value) {
        this.condType = value;
    }

    /**
     * Obtém o valor da propriedade value.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Define o valor da propriedade value.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Obtém o valor da propriedade varName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVarName() {
        return varName;
    }

    /**
     * Define o valor da propriedade varName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVarName(String value) {
        this.varName = value;
    }

}
