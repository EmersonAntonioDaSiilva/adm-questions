//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementa��o de Refer�ncia (JAXB) de Bind XML, v2.2.8-b130911.1802 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modifica��es neste arquivo ser�o perdidas ap�s a recompila��o do esquema de origem. 
// Gerado em: 2016.08.03 �s 03:49:06 PM BRT 
//


package br.com.afirmanet.questions.dialog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de chatflowNode complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conte�do esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="chatflowNode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="gid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="date" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isOffline" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isShowRelatedNodes" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "chatflowNode")
@XmlSeeAlso({
    OutputNodeType.class,
    DefaultNodeType.class,
    IfNodeType.class,
    InputNodeType.class,
    GotoNodeType.class,
    ConceptNodeType.class,
    GetUserInputNodeType.class,
    SearchNodeType.class,
    FolderNodeType.class,
    FunctionNodeType.class,
    RandomNodeType.class
})
public abstract class ChatflowNode {

    @XmlAttribute(name = "gid")
    protected String gid;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "comment")
    protected String comment;
    @XmlAttribute(name = "date")
    protected String date;
    @XmlAttribute(name = "isOffline")
    protected Boolean isOffline;
    @XmlAttribute(name = "isShowRelatedNodes")
    protected Boolean isShowRelatedNodes;

    /**
     * Obt�m o valor da propriedade gid.
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
     * Obt�m o valor da propriedade id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define o valor da propriedade id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obt�m o valor da propriedade comment.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Define o valor da propriedade comment.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Obt�m o valor da propriedade date.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDate() {
        return date;
    }

    /**
     * Define o valor da propriedade date.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDate(String value) {
        this.date = value;
    }

    /**
     * Obt�m o valor da propriedade isOffline.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsOffline() {
        return isOffline;
    }

    /**
     * Define o valor da propriedade isOffline.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsOffline(Boolean value) {
        this.isOffline = value;
    }

    /**
     * Obt�m o valor da propriedade isShowRelatedNodes.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsShowRelatedNodes() {
        return isShowRelatedNodes;
    }

    /**
     * Define o valor da propriedade isShowRelatedNodes.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsShowRelatedNodes(Boolean value) {
        this.isShowRelatedNodes = value;
    }

}
