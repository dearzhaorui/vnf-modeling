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
 * <p>Classe Java per LO_Implies complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="LO_Implies">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.org/LogicalExpressions}LogicalOperator">
 *       &lt;sequence>
 *         &lt;element name="AntecedentExpression" type="{http://www.example.org/LogicalExpressions}ExpressionObject"/>
 *         &lt;element name="ConsequentExpression" type="{http://www.example.org/LogicalExpressions}ExpressionObject"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LO_Implies", propOrder = {
    "antecedentExpression",
    "consequentExpression"
})
public class LOImplies
    extends LogicalOperator
{

    @XmlElement(name = "AntecedentExpression", required = true)
    protected ExpressionObject antecedentExpression;
    @XmlElement(name = "ConsequentExpression", required = true)
    protected ExpressionObject consequentExpression;

    /**
     * Recupera il valore della proprietà antecedentExpression.
     * 
     * @return
     *     possible object is
     *     {@link ExpressionObject }
     *     
     */
    public ExpressionObject getAntecedentExpression() {
        return antecedentExpression;
    }

    /**
     * Imposta il valore della proprietà antecedentExpression.
     * 
     * @param value
     *     allowed object is
     *     {@link ExpressionObject }
     *     
     */
    public void setAntecedentExpression(ExpressionObject value) {
        this.antecedentExpression = value;
    }

    /**
     * Recupera il valore della proprietà consequentExpression.
     * 
     * @return
     *     possible object is
     *     {@link ExpressionObject }
     *     
     */
    public ExpressionObject getConsequentExpression() {
        return consequentExpression;
    }

    /**
     * Imposta il valore della proprietà consequentExpression.
     * 
     * @param value
     *     allowed object is
     *     {@link ExpressionObject }
     *     
     */
    public void setConsequentExpression(ExpressionObject value) {
        this.consequentExpression = value;
    }

}
