//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2016.09.02 alle 07:14:49 PM CEST 
//


package it.polito.nfdev.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per LU_Packet complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="LU_Packet">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.org/LogicalExpressions}LogicalUnit">
 *       &lt;attribute name="ETH_SRC" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ETH_DST" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="IP_SRC" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="IP_DST" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="PORT_SRC" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="PORT_DST" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TRANSPORT_PROTOCOL" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="APPLICATION_PROTOCOL" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="L7DATA" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LU_Packet")
public class LUPacket
    extends LogicalUnit
{

    @XmlAttribute(name = "ETH_SRC")
    protected String ethsrc;
    @XmlAttribute(name = "ETH_DST")
    protected String ethdst;
    @XmlAttribute(name = "IP_SRC")
    protected String ipsrc;
    @XmlAttribute(name = "IP_DST")
    protected String ipdst;
    @XmlAttribute(name = "PORT_SRC")
    protected String portsrc;
    @XmlAttribute(name = "PORT_DST")
    protected String portdst;
    @XmlAttribute(name = "TRANSPORT_PROTOCOL")
    protected String transportprotocol;
    @XmlAttribute(name = "APPLICATION_PROTOCOL")
    protected String applicationprotocol;
    @XmlAttribute(name = "L7DATA")
    @XmlSchemaType(name = "anySimpleType")
    protected String l7DATA;

    /**
     * Recupera il valore della proprietà ethsrc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getETHSRC() {
        return ethsrc;
    }

    /**
     * Imposta il valore della proprietà ethsrc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setETHSRC(String value) {
        this.ethsrc = value;
    }

    /**
     * Recupera il valore della proprietà ethdst.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getETHDST() {
        return ethdst;
    }

    /**
     * Imposta il valore della proprietà ethdst.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setETHDST(String value) {
        this.ethdst = value;
    }

    /**
     * Recupera il valore della proprietà ipsrc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIPSRC() {
        return ipsrc;
    }

    /**
     * Imposta il valore della proprietà ipsrc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIPSRC(String value) {
        this.ipsrc = value;
    }

    /**
     * Recupera il valore della proprietà ipdst.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIPDST() {
        return ipdst;
    }

    /**
     * Imposta il valore della proprietà ipdst.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIPDST(String value) {
        this.ipdst = value;
    }

    /**
     * Recupera il valore della proprietà portsrc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPORTSRC() {
        return portsrc;
    }

    /**
     * Imposta il valore della proprietà portsrc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPORTSRC(String value) {
        this.portsrc = value;
    }

    /**
     * Recupera il valore della proprietà portdst.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPORTDST() {
        return portdst;
    }

    /**
     * Imposta il valore della proprietà portdst.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPORTDST(String value) {
        this.portdst = value;
    }

    /**
     * Recupera il valore della proprietà transportprotocol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTRANSPORTPROTOCOL() {
        return transportprotocol;
    }

    /**
     * Imposta il valore della proprietà transportprotocol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTRANSPORTPROTOCOL(String value) {
        this.transportprotocol = value;
    }

    /**
     * Recupera il valore della proprietà applicationprotocol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAPPLICATIONPROTOCOL() {
        return applicationprotocol;
    }

    /**
     * Imposta il valore della proprietà applicationprotocol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAPPLICATIONPROTOCOL(String value) {
        this.applicationprotocol = value;
    }

    /**
     * Recupera il valore della proprietà l7DATA.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getL7DATA() {
        return l7DATA;
    }

    /**
     * Imposta il valore della proprietà l7DATA.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setL7DATA(String value) {
        this.l7DATA = value;
    }

}
