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
 * <p>Classe Java per LO_Not complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="LO_Not">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.org/LogicalExpressions}LogicalOperator">
 *       &lt;sequence>
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
@XmlType(name = "LO_Not", propOrder = {
    "expression"
})
public class LONot
    extends LogicalOperator
{

    @XmlElement(name = "Expression", required = true)
    protected ExpressionObject expression;

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
