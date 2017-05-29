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
 * <p>Classe Java per LF_IsInternal complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="LF_IsInternal">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.org/LogicalExpressions}LogicalFunction">
 *       &lt;sequence>
 *         &lt;element name="FieldOf" type="{http://www.example.org/LogicalExpressions}LF_FieldOf"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LF_IsInternal", propOrder = {
    "fieldOf"
})
public class LFIsInternal
    extends LogicalFunction
{

    @XmlElement(name = "FieldOf", required = true)
    protected LFFieldOf fieldOf;

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

}
