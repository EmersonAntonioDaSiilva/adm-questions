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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de folderNodeType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="folderNodeType">
 *   &lt;complexContent>
 *     &lt;extension base="{}chatflowNode">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;group ref="{}commonChatflowNodePropertiesChilds"/>
 *         &lt;/choice>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;group ref="{}commonChatflowNodeChilds"/>
 *           &lt;element name="concept" type="{}conceptNodeType"/>
 *         &lt;/choice>
 *         &lt;element name="autoLearnVariations" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="item" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="selectionType" type="{}selectionTypeType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "folderNodeType", propOrder = {
    "actionOrScript",
    "inputOrOutputOrDefault",
    "autoLearnVariations"
})
public class FolderNodeType
    extends ChatflowNode
{

    @XmlElements({
        @XmlElement(name = "action", type = ActionType.class),
        @XmlElement(name = "script", type = ScriptType.class)
    })
    protected List<Object> actionOrScript;
    @XmlElements({
        @XmlElement(name = "input", type = InputNodeType.class),
        @XmlElement(name = "output", type = OutputNodeType.class),
        @XmlElement(name = "default", type = DefaultNodeType.class),
        @XmlElement(name = "function", type = FunctionNodeType.class),
        @XmlElement(name = "getUserInput", type = GetUserInputNodeType.class),
        @XmlElement(name = "goto", type = GotoNodeType.class),
        @XmlElement(name = "if", type = IfNodeType.class),
        @XmlElement(name = "random", type = RandomNodeType.class),
        @XmlElement(name = "search", type = SearchNodeType.class),
        @XmlElement(name = "folder", type = FolderNodeType.class),
        @XmlElement(name = "concept", type = ConceptNodeType.class)
    })
    protected List<ChatflowNode> inputOrOutputOrDefault;
    protected FolderNodeType.AutoLearnVariations autoLearnVariations;
    @XmlAttribute(name = "label")
    protected String label;
    @XmlAttribute(name = "selectionType")
    protected SelectionTypeType selectionType;

    /**
     * Gets the value of the actionOrScript property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actionOrScript property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActionOrScript().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ActionType }
     * {@link ScriptType }
     * 
     * 
     */
    public List<Object> getActionOrScript() {
        if (actionOrScript == null) {
            actionOrScript = new ArrayList<Object>();
        }
        return this.actionOrScript;
    }

    /**
     * Gets the value of the inputOrOutputOrDefault property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inputOrOutputOrDefault property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInputOrOutputOrDefault().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InputNodeType }
     * {@link OutputNodeType }
     * {@link DefaultNodeType }
     * {@link FunctionNodeType }
     * {@link GetUserInputNodeType }
     * {@link GotoNodeType }
     * {@link IfNodeType }
     * {@link RandomNodeType }
     * {@link SearchNodeType }
     * {@link FolderNodeType }
     * {@link ConceptNodeType }
     * 
     * 
     */
    public List<ChatflowNode> getInputOrOutputOrDefault() {
        if (inputOrOutputOrDefault == null) {
            inputOrOutputOrDefault = new ArrayList<ChatflowNode>();
        }
        return this.inputOrOutputOrDefault;
    }

    /**
     * Obtém o valor da propriedade autoLearnVariations.
     * 
     * @return
     *     possible object is
     *     {@link FolderNodeType.AutoLearnVariations }
     *     
     */
    public FolderNodeType.AutoLearnVariations getAutoLearnVariations() {
        return autoLearnVariations;
    }

    /**
     * Define o valor da propriedade autoLearnVariations.
     * 
     * @param value
     *     allowed object is
     *     {@link FolderNodeType.AutoLearnVariations }
     *     
     */
    public void setAutoLearnVariations(FolderNodeType.AutoLearnVariations value) {
        this.autoLearnVariations = value;
    }

    /**
     * Obtém o valor da propriedade label.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Define o valor da propriedade label.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Obtém o valor da propriedade selectionType.
     * 
     * @return
     *     possible object is
     *     {@link SelectionTypeType }
     *     
     */
    public SelectionTypeType getSelectionType() {
        return selectionType;
    }

    /**
     * Define o valor da propriedade selectionType.
     * 
     * @param value
     *     allowed object is
     *     {@link SelectionTypeType }
     *     
     */
    public void setSelectionType(SelectionTypeType value) {
        this.selectionType = value;
    }


    /**
     * <p>Classe Java de anonymous complex type.
     * 
     * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="item" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "item"
    })
    public static class AutoLearnVariations {

        protected List<String> item;

        /**
         * Gets the value of the item property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getItem() {
            if (item == null) {
                item = new ArrayList<String>();
            }
            return this.item;
        }

    }

}
