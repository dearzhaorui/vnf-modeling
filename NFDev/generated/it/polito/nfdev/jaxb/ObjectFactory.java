//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2016.09.02 alle 07:14:49 PM CEST 
//


package it.polito.nfdev.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.polito.nfdev.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Result_QNAME = new QName("http://www.example.org/LogicalExpressions", "Result");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.polito.nfdev.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExpressionResult }
     * 
     */
    public ExpressionResult createExpressionResult() {
        return new ExpressionResult();
    }

    /**
     * Create an instance of {@link LOGreaterThanOrEqual }
     * 
     */
    public LOGreaterThanOrEqual createLOGreaterThanOrEqual() {
        return new LOGreaterThanOrEqual();
    }

    /**
     * Create an instance of {@link LONot }
     * 
     */
    public LONot createLONot() {
        return new LONot();
    }

    /**
     * Create an instance of {@link LOGreaterThan }
     * 
     */
    public LOGreaterThan createLOGreaterThan() {
        return new LOGreaterThan();
    }

    /**
     * Create an instance of {@link LFSend }
     * 
     */
    public LFSend createLFSend() {
        return new LFSend();
    }

    /**
     * Create an instance of {@link LOImplies }
     * 
     */
    public LOImplies createLOImplies() {
        return new LOImplies();
    }

    /**
     * Create an instance of {@link LFRecv }
     * 
     */
    public LFRecv createLFRecv() {
        return new LFRecv();
    }

    /**
     * Create an instance of {@link LOOr }
     * 
     */
    public LOOr createLOOr() {
        return new LOOr();
    }

    /**
     * Create an instance of {@link LUTime }
     * 
     */
    public LUTime createLUTime() {
        return new LUTime();
    }

    /**
     * Create an instance of {@link LFIsInternal }
     * 
     */
    public LFIsInternal createLFIsInternal() {
        return new LFIsInternal();
    }

    /**
     * Create an instance of {@link LOAnd }
     * 
     */
    public LOAnd createLOAnd() {
        return new LOAnd();
    }

    /**
     * Create an instance of {@link LOLessThan }
     * 
     */
    public LOLessThan createLOLessThan() {
        return new LOLessThan();
    }

    /**
     * Create an instance of {@link LOEquals }
     * 
     */
    public LOEquals createLOEquals() {
        return new LOEquals();
    }

    /**
     * Create an instance of {@link LUPacket }
     * 
     */
    public LUPacket createLUPacket() {
        return new LUPacket();
    }

    /**
     * Create an instance of {@link ExpressionObject }
     * 
     */
    public ExpressionObject createExpressionObject() {
        return new ExpressionObject();
    }

    /**
     * Create an instance of {@link LOLessThanOrEqual }
     * 
     */
    public LOLessThanOrEqual createLOLessThanOrEqual() {
        return new LOLessThanOrEqual();
    }

    /**
     * Create an instance of {@link LFMatchEntry }
     * 
     */
    public LFMatchEntry createLFMatchEntry() {
        return new LFMatchEntry();
    }

    /**
     * Create an instance of {@link LFFieldOf }
     * 
     */
    public LFFieldOf createLFFieldOf() {
        return new LFFieldOf();
    }

    /**
     * Create an instance of {@link LOExist }
     * 
     */
    public LOExist createLOExist() {
        return new LOExist();
    }

    /**
     * Create an instance of {@link LUNode }
     * 
     */
    public LUNode createLUNode() {
        return new LUNode();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExpressionResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.org/LogicalExpressions", name = "Result")
    public JAXBElement<ExpressionResult> createResult(ExpressionResult value) {
        return new JAXBElement<ExpressionResult>(_Result_QNAME, ExpressionResult.class, null, value);
    }

}
