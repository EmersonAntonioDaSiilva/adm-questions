//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementa��o de Refer�ncia (JAXB) de Bind XML, v2.2.8-b130911.1802 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modifica��es neste arquivo ser�o perdidas ap�s a recompila��o do esquema de origem. 
// Gerado em: 2016.08.03 �s 03:49:06 PM BRT 
//


package br.com.afirmanet.questions.dialog;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de defaultNodeType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conte�do esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="defaultNodeType">
 *   &lt;complexContent>
 *     &lt;extension base="{}chatflowNode">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;group ref="{}commonChatflowNodePropertiesChilds"/>
 *         &lt;/choice>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;group ref="{}commonChatflowNodeChilds"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "defaultNodeType", propOrder = {
    "actionOrScript",
    "inputOrOutputOrDefault"
})
public class DefaultNodeType
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
        @XmlElement(name = "folder", type = FolderNodeType.class)
    })
    protected List<ChatflowNode> inputOrOutputOrDefault;

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
     * 
     * 
     */
    public List<ChatflowNode> getInputOrOutputOrDefault() {
        if (inputOrOutputOrDefault == null) {
            inputOrOutputOrDefault = new ArrayList<ChatflowNode>();
        }
        return this.inputOrOutputOrDefault;
    }

}
