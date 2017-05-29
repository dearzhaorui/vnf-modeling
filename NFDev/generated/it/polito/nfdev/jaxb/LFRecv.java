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
 * <p>Classe Java per LF_Recv complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="LF_Recv">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.org/LogicalExpressions}LogicalFunction">
 *       &lt;sequence>
 *         &lt;element name="Source" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Destination" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Packet_in" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Time_in" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LF_Recv", propOrder = {
    "source",
    "destination",
    "packetIn",
    "timeIn"
})
public class LFRecv
    extends LogicalFunction
{

    @XmlElement(name = "Source", required = true)
    protected String source;
    @XmlElement(name = "Destination", required = true)
    protected String destination;
    @XmlElement(name = "Packet_in", required = true)
    protected String packetIn;
    @XmlElement(name = "Time_in", required = true)
    protected String timeIn;

    /**
     * Recupera il valore della proprietà source.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Imposta il valore della proprietà source.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Recupera il valore della proprietà destination.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Imposta il valore della proprietà destination.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestination(String value) {
        this.destination = value;
    }

    /**
     * Recupera il valore della proprietà packetIn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPacketIn() {
        return packetIn;
    }

    /**
     * Imposta il valore della proprietà packetIn.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPacketIn(String value) {
        this.packetIn = value;
    }

    /**
     * Recupera il valore della proprietà timeIn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeIn() {
        return timeIn;
    }

    /**
     * Imposta il valore della proprietà timeIn.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeIn(String value) {
        this.timeIn = value;
    }

}
