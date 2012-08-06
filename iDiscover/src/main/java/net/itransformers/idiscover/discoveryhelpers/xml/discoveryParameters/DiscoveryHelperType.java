/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.21 at 01:58:29 AM EEST 
//


package net.itransformers.idiscover.discoveryhelpers.xml.discoveryParameters;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for discovery-helperType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="discovery-helperType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="device" type="{}deviceType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="stop-criteria" type="{}stop-criteriaType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "discovery-helperType", propOrder = {
    "device",
    "stopCriteria"
})
public class DiscoveryHelperType {

    protected List<DeviceType> device;
    @XmlElement(name = "stop-criteria", required = true)
    protected StopCriteriaType stopCriteria;

    /**
     * Gets the value of the device property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the device property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDevice().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DeviceType }
     * 
     * 
     */
    public List<DeviceType> getDevice() {
        if (device == null) {
            device = new ArrayList<DeviceType>();
        }
        return this.device;
    }

    /**
     * Gets the value of the stopCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link StopCriteriaType }
     *     
     */
    public StopCriteriaType getStopCriteria() {
        return stopCriteria;
    }

    /**
     * Sets the value of the stopCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link StopCriteriaType }
     *     
     */
    public void setStopCriteria(StopCriteriaType value) {
        this.stopCriteria = value;
    }

}
