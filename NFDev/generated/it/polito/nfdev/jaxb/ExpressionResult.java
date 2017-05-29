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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ExpressionResult complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="ExpressionResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TableSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DataDriven" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="TableFields" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="Node" type="{http://www.example.org/LogicalExpressions}LU_Node"/>
 *           &lt;element name="Packet" type="{http://www.example.org/LogicalExpressions}LU_Packet"/>
 *           &lt;element name="Time" type="{http://www.example.org/LogicalExpressions}LU_Time"/>
 *         &lt;/choice>
 *         &lt;element name="LogicalExpressionResult" type="{http://www.example.org/LogicalExpressions}ExpressionObject" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExpressionResult", propOrder = {
    "tableSize",
    "dataDriven",
    "tableFields",
    "nodeOrPacketOrTime",
    "logicalExpressionResult"
})
public class ExpressionResult {

    @XmlElement(name = "TableSize")
    protected int tableSize;
    @XmlElement(name = "DataDriven")
    protected boolean dataDriven;
    @XmlElement(name = "TableFields")
    protected List<String> tableFields;
    @XmlElements({
        @XmlElement(name = "Node", type = LUNode.class),
        @XmlElement(name = "Packet", type = LUPacket.class),
        @XmlElement(name = "Time", type = LUTime.class)
    })
    protected List<LogicalUnit> nodeOrPacketOrTime;
    @XmlElement(name = "LogicalExpressionResult", required = true)
    protected List<ExpressionObject> logicalExpressionResult;

    /**
     * Recupera il valore della proprietà tableSize.
     * 
     */
    public int getTableSize() {
        return tableSize;
    }

    /**
     * Imposta il valore della proprietà tableSize.
     * 
     */
    public void setTableSize(int value) {
        this.tableSize = value;
    }

    /**
     * Recupera il valore della proprietà dataDriven.
     * 
     */
    public boolean isDataDriven() {
        return dataDriven;
    }

    /**
     * Imposta il valore della proprietà dataDriven.
     * 
     */
    public void setDataDriven(boolean value) {
        this.dataDriven = value;
    }

    /**
     * Gets the value of the tableFields property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tableFields property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTableFields().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTableFields() {
        if (tableFields == null) {
            tableFields = new ArrayList<String>();
        }
        return this.tableFields;
    }

    /**
     * Gets the value of the nodeOrPacketOrTime property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeOrPacketOrTime property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeOrPacketOrTime().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LUNode }
     * {@link LUPacket }
     * {@link LUTime }
     * 
     * 
     */
    public List<LogicalUnit> getNodeOrPacketOrTime() {
        if (nodeOrPacketOrTime == null) {
            nodeOrPacketOrTime = new ArrayList<LogicalUnit>();
        }
        return this.nodeOrPacketOrTime;
    }

    /**
     * Gets the value of the logicalExpressionResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the logicalExpressionResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLogicalExpressionResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExpressionObject }
     * 
     * 
     */
    public List<ExpressionObject> getLogicalExpressionResult() {
        if (logicalExpressionResult == null) {
            logicalExpressionResult = new ArrayList<ExpressionObject>();
        }
        return this.logicalExpressionResult;
    }

}
