//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2016.09.02 alle 07:14:49 PM CEST 
//


package it.polito.nfdev.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per LO_Exist complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="LO_Exist">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.org/LogicalExpressions}LogicalOperator">
 *       &lt;sequence>
 *         &lt;element name="Unit" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="Expression" type="{http://www.example.org/LogicalExpressions}ExpressionObject"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LO_Exist", propOrder = {
    "unit",
    "expression"
})
public class LOExist
    extends LogicalOperator
{

    @XmlElement(name = "Unit", required = true)
    protected List<String> unit;
    @XmlElement(name = "Expression", required = true)
    protected ExpressionObject expression;

    /**
     * Gets the value of the unit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUnit() {
        if (unit == null) {
            unit = new ArrayList<String>();
        }
        return this.unit;
    }

    /**
     * Recupera il valore della proprietà expression.
     * 
     * @return
     *     possible object is
     *     {@link ExpressionObject }
     *     
     */
    public ExpressionObject getExpression() {
        return expression;
    }

    /**
     * Imposta il valore della proprietà expression.
     * 
     * @param value
     *     allowed object is
     *     {@link ExpressionObject }
     *     
     */
    public void setExpression(ExpressionObject value) {
        this.expression = value;
    }

}
