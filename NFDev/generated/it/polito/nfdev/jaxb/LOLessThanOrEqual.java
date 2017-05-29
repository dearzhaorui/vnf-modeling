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
 * <p>Classe Java per LO_Less_Than_or_Equal complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="LO_Less_Than_or_Equal">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.org/LogicalExpressions}LogicalOperator">
 *       &lt;sequence>
 *         &lt;element name="LeftExpression" type="{http://www.example.org/LogicalExpressions}ExpressionObject"/>
 *         &lt;element name="RightExpression" type="{http://www.example.org/LogicalExpressions}ExpressionObject"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LO_Less_Than_or_Equal", propOrder = {
    "leftExpression",
    "rightExpression"
})
public class LOLessThanOrEqual
    extends LogicalOperator
{

    @XmlElement(name = "LeftExpression", required = true)
    protected ExpressionObject leftExpression;
    @XmlElement(name = "RightExpression", required = true)
    protected ExpressionObject rightExpression;

    /**
     * Recupera il valore della proprietà leftExpression.
     * 
     * @return
     *     possible object is
     *     {@link ExpressionObject }
     *     
     */
    public ExpressionObject getLeftExpression() {
        return leftExpression;
    }

    /**
     * Imposta il valore della proprietà leftExpression.
     * 
     * @param value
     *     allowed object is
     *     {@link ExpressionObject }
     *     
     */
    public void setLeftExpression(ExpressionObject value) {
        this.leftExpression = value;
    }

    /**
     * Recupera il valore della proprietà rightExpression.
     * 
     * @return
     *     possible object is
     *     {@link ExpressionObject }
     *     
     */
    public ExpressionObject getRightExpression() {
        return rightExpression;
    }

    /**
     * Imposta il valore della proprietà rightExpression.
     * 
     * @param value
     *     allowed object is
     *     {@link ExpressionObject }
     *     
     */
    public void setRightExpression(ExpressionObject value) {
        this.rightExpression = value;
    }

}
