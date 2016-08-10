//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.8-b130911.1802 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2016.08.03 às 03:49:06 PM BRT 
//


package br.com.afirmanet.questions.dialog;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de profileValueRuleType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="profileValueRuleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subrules" type="{}profileValueSubruleType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="engaging" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="folderid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="gid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="match" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileValueRuleType", propOrder = {
    "description",
    "subrules"
})
public class ProfileValueRuleType {

    protected String description;
    protected List<ProfileValueSubruleType> subrules;
    @XmlAttribute(name = "engaging", required = true)
    protected int engaging;
    @XmlAttribute(name = "folderid")
    protected String folderid;
    @XmlAttribute(name = "gid")
    protected String gid;
    @XmlAttribute(name = "match")
    protected String match;
    @XmlAttribute(name = "type")
    protected String type;

    /**
     * Obtém o valor da propriedade description.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Define o valor da propriedade description.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the subrules property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subrules property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubrules().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProfileValueSubruleType }
     * 
     * 
     */
    public List<ProfileValueSubruleType> getSubrules() {
        if (subrules == null) {
            subrules = new ArrayList<ProfileValueSubruleType>();
        }
        return this.subrules;
    }

    /**
     * Obtém o valor da propriedade engaging.
     * 
     */
    public int getEngaging() {
        return engaging;
    }

    /**
     * Define o valor da propriedade engaging.
     * 
     */
    public void setEngaging(int value) {
        this.engaging = value;
    }

    /**
     * Obtém o valor da propriedade folderid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderid() {
        return folderid;
    }

    /**
     * Define o valor da propriedade folderid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderid(String value) {
        this.folderid = value;
    }

    /**
     * Obtém o valor da propriedade gid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGid() {
        return gid;
    }

    /**
     * Define o valor da propriedade gid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGid(String value) {
        this.gid = value;
    }

    /**
     * Obtém o valor da propriedade match.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatch() {
        return match;
    }

    /**
     * Define o valor da propriedade match.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatch(String value) {
        this.match = value;
    }

    /**
     * Obtém o valor da propriedade type.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Define o valor da propriedade type.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
