//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2016.09.02 alle 07:14:49 PM CEST 
//


package it.polito.nfdev.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ExpressionObject complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="ExpressionObject">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="And" type="{http://www.example.org/LogicalExpressions}LO_And"/>
 *         &lt;element name="Or" type="{http://www.example.org/LogicalExpressions}LO_Or"/>
 *         &lt;element name="Not" type="{http://www.example.org/LogicalExpressions}LO_Not"/>
 *         &lt;element name="Equal" type="{http://www.example.org/LogicalExpressions}LO_Equals"/>
 *         &lt;element name="GreaterThan" type="{http://www.example.org/LogicalExpressions}LO_Greater_Than"/>
 *         &lt;element name="GreaterThanOrEqual" type="{http://www.example.org/LogicalExpressions}LO_Greater_Than_or_Equal"/>
 *         &lt;element name="LessThan" type="{http://www.example.org/LogicalExpressions}LO_Less_Than"/>
 *         &lt;element name="LessThanOrEqual" type="{http://www.example.org/LogicalExpressions}LO_Less_Than_or_Equal"/>
 *         &lt;element name="Implies" type="{http://www.example.org/LogicalExpressions}LO_Implies"/>
 *         &lt;element name="Exist" type="{http://www.example.org/LogicalExpressions}LO_Exist"/>
 *         &lt;element name="Send" type="{http://www.example.org/LogicalExpressions}LF_Send"/>
 *         &lt;element name="Recv" type="{http://www.example.org/LogicalExpressions}LF_Recv"/>
 *         &lt;element name="MatchEntry" type="{http://www.example.org/LogicalExpressions}LF_MatchEntry"/>
 *         &lt;element name="FieldOf" type="{http://www.example.org/LogicalExpressions}LF_FieldOf"/>
 *         &lt;element name="IsInternal" type="{http://www.example.org/LogicalExpressions}LF_IsInternal"/>
 *         &lt;element name="Param" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExpressionObject", propOrder = {
    "and",
    "or",
    "not",
    "equal",
    "greaterThan",
    "greaterThanOrEqual",
    "lessThan",
    "lessThanOrEqual",
    "implies",
    "exist",
    "send",
    "recv",
    "matchEntry",
    "fieldOf",
    "isInternal",
    "param"
})
public class ExpressionObject {

    @XmlElement(name = "And")
    protected LOAnd and;
    @XmlElement(name = "Or")
    protected LOOr or;
    @XmlElement(name = "Not")
    protected LONot not;
    @XmlElement(name = "Equal")
    protected LOEquals equal;
    @XmlElement(name = "GreaterThan")
    protected LOGreaterThan greaterThan;
    @XmlElement(name = "GreaterThanOrEqual")
    protected LOGreaterThanOrEqual greaterThanOrEqual;
    @XmlElement(name = "LessThan")
    protected LOLessThan lessThan;
    @XmlElement(name = "LessThanOrEqual")
    protected LOLessThanOrEqual lessThanOrEqual;
    @XmlElement(name = "Implies")
    protected LOImplies implies;
    @XmlElement(name = "Exist")
    protected LOExist exist;
    @XmlElement(name = "Send")
    protected LFSend send;
    @XmlElement(name = "Recv")
    protected LFRecv recv;
    @XmlElement(name = "MatchEntry")
    protected LFMatchEntry matchEntry;
    @XmlElement(name = "FieldOf")
    protected LFFieldOf fieldOf;
    @XmlElement(name = "IsInternal")
    protected LFIsInternal isInternal;
    @XmlElement(name = "Param")
    protected String param;

    /**
     * Recupera il valore della proprietà and.
     * 
     * @return
     *     possible object is
     *     {@link LOAnd }
     *     
     */
    public LOAnd getAnd() {
        return and;
    }

    /**
     * Imposta il valore della proprietà and.
     * 
     * @param value
     *     allowed object is
     *     {@link LOAnd }
     *     
     */
    public void setAnd(LOAnd value) {
        this.and = value;
    }

    /**
     * Recupera il valore della proprietà or.
     * 
     * @return
     *     possible object is
     *     {@link LOOr }
     *     
     */
    public LOOr getOr() {
        return or;
    }

    /**
     * Imposta il valore della proprietà or.
     * 
     * @param value
     *     allowed object is
     *     {@link LOOr }
     *     
     */
    public void setOr(LOOr value) {
        this.or = value;
    }

    /**
     * Recupera il valore della proprietà not.
     * 
     * @return
     *     possible object is
     *     {@link LONot }
     *     
     */
    public LONot getNot() {
        return not;
    }

    /**
     * Imposta il valore della proprietà not.
     * 
     * @param value
     *     allowed object is
     *     {@link LONot }
     *     
     */
    public void setNot(LONot value) {
        this.not = value;
    }

    /**
     * Recupera il valore della proprietà equal.
     * 
     * @return
     *     possible object is
     *     {@link LOEquals }
     *     
     */
    public LOEquals getEqual() {
        return equal;
    }

    /**
     * Imposta il valore della proprietà equal.
     * 
     * @param value
     *     allowed object is
     *     {@link LOEquals }
     *     
     */
    public void setEqual(LOEquals value) {
        this.equal = value;
    }

    /**
     * Recupera il valore della proprietà greaterThan.
     * 
     * @return
     *     possible object is
     *     {@link LOGreaterThan }
     *     
     */
    public LOGreaterThan getGreaterThan() {
        return greaterThan;
    }

    /**
     * Imposta il valore della proprietà greaterThan.
     * 
     * @param value
     *     allowed object is
     *     {@link LOGreaterThan }
     *     
     */
    public void setGreaterThan(LOGreaterThan value) {
        this.greaterThan = value;
    }

    /**
     * Recupera il valore della proprietà greaterThanOrEqual.
     * 
     * @return
     *     possible object is
     *     {@link LOGreaterThanOrEqual }
     *     
     */
    public LOGreaterThanOrEqual getGreaterThanOrEqual() {
        return greaterThanOrEqual;
    }

    /**
     * Imposta il valore della proprietà greaterThanOrEqual.
     * 
     * @param value
     *     allowed object is
     *     {@link LOGreaterThanOrEqual }
     *     
     */
    public void setGreaterThanOrEqual(LOGreaterThanOrEqual value) {
        this.greaterThanOrEqual = value;
    }

    /**
     * Recupera il valore della proprietà lessThan.
     * 
     * @return
     *     possible object is
     *     {@link LOLessThan }
     *     
     */
    public LOLessThan getLessThan() {
        return lessThan;
    }

    /**
     * Imposta il valore della proprietà lessThan.
     * 
     * @param value
     *     allowed object is
     *     {@link LOLessThan }
     *     
     */
    public void setLessThan(LOLessThan value) {
        this.lessThan = value;
    }

    /**
     * Recupera il valore della proprietà lessThanOrEqual.
     * 
     * @return
     *     possible object is
     *     {@link LOLessThanOrEqual }
     *     
     */
    public LOLessThanOrEqual getLessThanOrEqual() {
        return lessThanOrEqual;
    }

    /**
     * Imposta il valore della proprietà lessThanOrEqual.
     * 
     * @param value
     *     allowed object is
     *     {@link LOLessThanOrEqual }
     *     
     */
    public void setLessThanOrEqual(LOLessThanOrEqual value) {
        this.lessThanOrEqual = value;
    }

    /**
     * Recupera il valore della proprietà implies.
     * 
     * @return
     *     possible object is
     *     {@link LOImplies }
     *     
     */
    public LOImplies getImplies() {
        return implies;
    }

    /**
     * Imposta il valore della proprietà implies.
     * 
     * @param value
     *     allowed object is
     *     {@link LOImplies }
     *     
     */
    public void setImplies(LOImplies value) {
        this.implies = value;
    }

    /**
     * Recupera il valore della proprietà exist.
     * 
     * @return
     *     possible object is
     *     {@link LOExist }
     *     
     */
    public LOExist getExist() {
        return exist;
    }

    /**
     * Imposta il valore della proprietà exist.
     * 
     * @param value
     *     allowed object is
     *     {@link LOExist }
     *     
     */
    public void setExist(LOExist value) {
        this.exist = value;
    }

    /**
     * Recupera il valore della proprietà send.
     * 
     * @return
     *     possible object is
     *     {@link LFSend }
     *     
     */
    public LFSend getSend() {
        return send;
    }

    /**
     * Imposta il valore della proprietà send.
     * 
     * @param value
     *     allowed object is
     *     {@link LFSend }
     *     
     */
    public void setSend(LFSend value) {
        this.send = value;
    }

    /**
     * Recupera il valore della proprietà recv.
     * 
     * @return
     *     possible object is
     *     {@link LFRecv }
     *     
     */
    public LFRecv getRecv() {
        return recv;
    }

    /**
     * Imposta il valore della proprietà recv.
     * 
     * @param value
     *     allowed object is
     *     {@link LFRecv }
     *     
     */
    public void setRecv(LFRecv value) {
        this.recv = value;
    }

    /**
     * Recupera il valore della proprietà matchEntry.
     * 
     * @return
     *     possible object is
     *     {@link LFMatchEntry }
     *     
     */
    public LFMatchEntry getMatchEntry() {
        return matchEntry;
    }

    /**
     * Imposta il valore della proprietà matchEntry.
     * 
     * @param value
     *     allowed object is
     *     {@link LFMatchEntry }
     *     
     */
    public void setMatchEntry(LFMatchEntry value) {
        this.matchEntry = value;
    }

    /**
     * Recupera il valore della proprietà fieldOf.
     * 
     * @return
     *     possible object is
     *     {@link LFFieldOf }
     *     
     */
    public LFFieldOf getFieldOf() {
        return fieldOf;
    }

    /**
     * Imposta il valore della proprietà fieldOf.
     * 
     * @param value
     *     allowed object is
     *     {@link LFFieldOf }
     *     
     */
    public void setFieldOf(LFFieldOf value) {
        this.fieldOf = value;
    }

    /**
     * Recupera il valore della proprietà isInternal.
     * 
     * @return
     *     possible object is
     *     {@link LFIsInternal }
     *     
     */
    public LFIsInternal getIsInternal() {
        return isInternal;
    }

    /**
     * Imposta il valore della proprietà isInternal.
     * 
     * @param value
     *     allowed object is
     *     {@link LFIsInternal }
     *     
     */
    public void setIsInternal(LFIsInternal value) {
        this.isInternal = value;
    }

    /**
     * Recupera il valore della proprietà param.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParam() {
        return param;
    }

    /**
     * Imposta il valore della proprietà param.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParam(String value) {
        this.param = value;
    }

}
