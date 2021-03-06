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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de dialogType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conte�do esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="dialogType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="flow" type="{}flowType"/>
 *         &lt;element name="entities" type="{}entitiesType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="constants" type="{}constantsType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="variables" type="{}variablesType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="translations" type="{}translationsType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="settings" type="{}settingsType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="specialSettings" type="{}specialSettingsType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="reports" type="{}reportsType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="hitnoderules" type="{}hitNodeRulesType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="profilevaluerules" type="{}profileValueRulesType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dialogType", propOrder = {
    "flow",
    "entities",
    "constants",
    "variables",
    "translations",
    "settings",
    "specialSettings",
    "reports",
    "hitnoderules",
    "profilevaluerules"
})
public class DialogType {

    @XmlElement(required = true)
    protected FlowType flow;
    protected List<EntitiesType> entities;
    protected List<ConstantsType> constants;
    protected List<VariablesType> variables;
    protected List<TranslationsType> translations;
    protected List<SettingsType> settings;
    protected List<SpecialSettingsType> specialSettings;
    protected List<ReportsType> reports;
    protected List<HitNodeRulesType> hitnoderules;
    protected List<ProfileValueRulesType> profilevaluerules;

    /**
     * Obt�m o valor da propriedade flow.
     * 
     * @return
     *     possible object is
     *     {@link FlowType }
     *     
     */
    public FlowType getFlow() {
        return flow;
    }

    /**
     * Define o valor da propriedade flow.
     * 
     * @param value
     *     allowed object is
     *     {@link FlowType }
     *     
     */
    public void setFlow(FlowType value) {
        this.flow = value;
    }

    /**
     * Gets the value of the entities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntitiesType }
     * 
     * 
     */
    public List<EntitiesType> getEntities() {
        if (entities == null) {
            entities = new ArrayList<EntitiesType>();
        }
        return this.entities;
    }

    /**
     * Gets the value of the constants property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the constants property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConstants().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConstantsType }
     * 
     * 
     */
    public List<ConstantsType> getConstants() {
        if (constants == null) {
            constants = new ArrayList<ConstantsType>();
        }
        return this.constants;
    }

    /**
     * Gets the value of the variables property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the variables property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVariables().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VariablesType }
     * 
     * 
     */
    public List<VariablesType> getVariables() {
        if (variables == null) {
            variables = new ArrayList<VariablesType>();
        }
        return this.variables;
    }

    /**
     * Gets the value of the translations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the translations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTranslations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TranslationsType }
     * 
     * 
     */
    public List<TranslationsType> getTranslations() {
        if (translations == null) {
            translations = new ArrayList<TranslationsType>();
        }
        return this.translations;
    }

    /**
     * Gets the value of the settings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the settings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSettings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SettingsType }
     * 
     * 
     */
    public List<SettingsType> getSettings() {
        if (settings == null) {
            settings = new ArrayList<SettingsType>();
        }
        return this.settings;
    }

    /**
     * Gets the value of the specialSettings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specialSettings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecialSettings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpecialSettingsType }
     * 
     * 
     */
    public List<SpecialSettingsType> getSpecialSettings() {
        if (specialSettings == null) {
            specialSettings = new ArrayList<SpecialSettingsType>();
        }
        return this.specialSettings;
    }

    /**
     * Gets the value of the reports property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reports property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReports().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReportsType }
     * 
     * 
     */
    public List<ReportsType> getReports() {
        if (reports == null) {
            reports = new ArrayList<ReportsType>();
        }
        return this.reports;
    }

    /**
     * Gets the value of the hitnoderules property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hitnoderules property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHitnoderules().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HitNodeRulesType }
     * 
     * 
     */
    public List<HitNodeRulesType> getHitnoderules() {
        if (hitnoderules == null) {
            hitnoderules = new ArrayList<HitNodeRulesType>();
        }
        return this.hitnoderules;
    }

    /**
     * Gets the value of the profilevaluerules property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the profilevaluerules property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfilevaluerules().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProfileValueRulesType }
     * 
     * 
     */
    public List<ProfileValueRulesType> getProfilevaluerules() {
        if (profilevaluerules == null) {
            profilevaluerules = new ArrayList<ProfileValueRulesType>();
        }
        return this.profilevaluerules;
    }

}
