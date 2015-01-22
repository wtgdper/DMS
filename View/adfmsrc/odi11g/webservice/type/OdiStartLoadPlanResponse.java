
package odi11g.webservice.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="executionInfo" type="{xmlns.oracle.com/odi/OdiInvoke/}OdiStartLoadPlanType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "executionInfo"
})
@XmlRootElement(name = "OdiStartLoadPlanResponse")
public class OdiStartLoadPlanResponse {

    @XmlElement(required = true)
    protected OdiStartLoadPlanType executionInfo;

    /**
     * Gets the value of the executionInfo property.
     * 
     * @return
     *     possible object is
     *     {@link OdiStartLoadPlanType }
     *     
     */
    public OdiStartLoadPlanType getExecutionInfo() {
        return executionInfo;
    }

    /**
     * Sets the value of the executionInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link OdiStartLoadPlanType }
     *     
     */
    public void setExecutionInfo(OdiStartLoadPlanType value) {
        this.executionInfo = value;
    }

}
