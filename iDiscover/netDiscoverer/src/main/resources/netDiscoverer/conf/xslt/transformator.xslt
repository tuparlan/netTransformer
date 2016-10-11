<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ transformator.xslt
  ~
  ~ This work is free software; you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published
  ~ by the Free Software Foundation; either version 2 of the License,
  ~ or (at your option) any later version.
  ~
  ~ This work is distributed in the hope that it will be useful, but
  ~ WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program; if not, write to the Free Software
  ~ Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
  ~ USA
  ~
  ~ Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
  -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:SnmpForXslt="net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:exslt="http://exslt.org/common"
                extension-element-prefixes="exslt" xmlns:functx="http://www.functx.com"
                xmlns:IPv6formatConvertor="net.itransformers.idiscover.util.IPv6formatConvertor"
                xmlns:InterfaceNeighbor="net.itransformers.idiscover.discoveryhelpers.xml.InterfaceNeighbor">
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    <xsl:param name="ipAddress"/>
    <xsl:param name="status"/>
    <xsl:param name="community-ro"/>
    <xsl:param name="community-rw"/>
    <xsl:param name="timeout"/>
    <xsl:param name="retries"/>
    <xsl:param name="neighbourIPDryRun"/>

    <!--<xsl:include href="utils.xslt"/>-->
    <!--<xsl:include href="discovery-methods.xslt"/>-->
    <!--<xsl:include href="interfaces.xslt"/>-->

    <xsl:variable name="comm" select="$community-ro"/>
    <xsl:variable name="comm2" select="$community-rw"/>
    <xsl:variable name="deviceIPv4Address" select="$ipAddress"/>
    <xsl:variable name="dev_state" select="$status"/>


    <xsl:template match="/">
        <!--
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    This file transforms the raw-device data to the common object oriented model used by snmpDiscovery manager for representing
    different devices architecture.
    The current model consist of a device that has:
     1. Several common device parameters
     2. Several device objects including:
        2.1 Objects that represent device interfaces
        2.1.1 Under some of the interfaces there are objects that represent one or more addresses connfigured under the interface.
        2.2.2 Neighbors found by the snmpDiscovery methods under the particular interface. Each time the neighbor address is identified
         a snmp-get is perform in order to obtain neighbor hostname. Currently the following snmpDiscovery methods are supported:

        2.2.2.1 MAC address table neighbors. This table represent L2 Neighbors in Ethernet network. Those neighbors are identified by
        MAC address and physical interface index. Unfortunately it does not contain neighbor IP address. So to find it a cross check
        is performed against the IPv4 ARP table. Neighbors that could not be obtained are marked with the name "Unknown - MAC addreess".

        2.2.2.2 ARP address table neighbors - Neighbors here are identified by a MAC, IP and interface index. It is important to note
        that those indexes represent in many cases represent device logical interfaces (e.g vlan interfaces).

        2.2.2.3 Cisco Discovery Protocol neighbors - Cisco proprietary snmpDiscovery protocol - one of the most reliable methods for physical
        network topology snmpDiscovery. Supported by most of the Cisco devices but also by some others e.g HP Procurve switches. Note that HP is
        able to see Cisco but Cisco is not able to see HP. The good stuff of that protocol is that it provide information about neighbor
        platform and current device interface pointing to the neighbor.
        2.2.2.4 Local Link Discovery Protocol - IEEE standardized snmpDiscovery protocol. Pretty much same as CDP.Note that LLDP MIB is still
         a draft and therefore might cause some problems.
        2.2.2.5 SLASH30/31- This method use Interface IP address to calculate the IP address on the other side of the point to point link.
        2.2.2.6 Next Hops from routing Table
        2.2.2.7  Next Hops from ipCidrRouteTable
        2.3 Device Logical Data - section that represent data related to the current device unrelated to the physical setup of the device.
        Such might be routing protocol neighbors or device configuration or something else.
        2.3.1 OSPF Neighbors - Neighbors found by OSPF routing protocol.
        2.3.2 BGP Neighbors - Neighbors found by BGP routing protocol.
        -->


        <xsl:variable name="dot1dStpDesignatedRoot"
                      select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dStp/dot1dStpDesignatedRoot"/>
        <xsl:variable name="baseBridgeAddress"
                      select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dBase/dot1dBaseBridgeAddress"/>
        <!--Format hostname. For example if we have R1.test.com it will strip .test.com and will return only R1. This comes form the issue
that there is a bug in CDP and on some Cisco routers we might see the neighbor hostname as R1.test.co so those hostname will mismatch
from the one obtained by snmp or other snmpDiscovery methods.-->
        <xsl:variable name="sysName">
            <xsl:call-template name="return-hostname">
                <xsl:with-param name="hostname-unformated"
                                select="//root/iso/org/dod/internet/mgmt/mib-2/system/sysName"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="hostname">
            <xsl:choose>
                <xsl:when test="$sysName!=''">
                    <xsl:value-of select="$sysName"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="temp">
                        <xsl:call-template name="return-hostname">
                            <xsl:with-param name="hostname-unformated"
                                            select="SnmpForXslt:getName($deviceIPv4Address,$neighbourIPDryRun)"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:value-of select="$temp"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <!--<xsl:message>DEBUG:<xsl:value-of select="$hostname"/></xsl:message>-->
        <xsl:variable name="sysDescr">
            <xsl:value-of select="//root/iso/org/dod/internet/mgmt/mib-2/system/sysDescr"/>
        </xsl:variable>
        <xsl:variable name="sysOr" select="//root/iso/org/dod/internet/mgmt/mib-2/system/sysObjectID"/>
        <xsl:variable name="deviceType">
            <xsl:call-template name="determine-device-Type">
                <xsl:with-param name="sysDescr" select="$sysDescr"/>
                <xsl:with-param name="sysOr" select="$sysOr"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="IPv6">
            <xsl:choose>
                <xsl:when test="//root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6Forwarding = '1'">YES</xsl:when>
                <xsl:when test="count(//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry) > 0">YES</xsl:when>
                <xsl:otherwise>NO</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="firstMacAddress" select="(/root/iso/org/dod/internet/mgmt/mib-2/interfaces/ifTable/ifEntry[ifPhysAddress!='']/ifPhysAddress)[1]"/>
        <xsl:variable name="secondMacAddress" select="(/root/iso/org/dod/internet/mgmt/mib-2/interfaces/ifTable/ifEntry[ifPhysAddress!='']/ifPhysAddress)[2]"/>
        <xsl:variable name="macAddress">
            <xsl:choose>
            <xsl:when test="$firstMacAddress = '00:00:00:00:00:00' or $firstMacAddress=''"><xsl:value-of select="$secondMacAddress"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$firstMacAddress"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="mplsVRF">
            <root1>
                <xsl:for-each
                        select="//root/iso/org/dod/internet/experimental/mplsVpnMIB/mplsVpnObjects/mplsVpnConf/mplsVpnInterfaceConfTable/mplsVpnInterfaceConfEntry/instance">
                    <xsl:variable name="test">
                        <xsl:call-template name="substring-before-last">
                            <xsl:with-param name="substring">.</xsl:with-param>
                            <xsl:with-param name="value" select="substring-after(.,'.')"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="name">
                        <xsl:for-each select="tokenize($test,'\.')">
                            <xsl:value-of select="codepoints-to-string(xs:integer(.))"/>
                        </xsl:for-each>
                    </xsl:variable>
                    <xsl:variable name="index">
                        <xsl:call-template name="substring-after">
                            <xsl:with-param name="substring">
                                <xsl:call-template name="substring-before-last">
                                    <xsl:with-param name="value" select="."/>
                                    <xsl:with-param name="substring">.</xsl:with-param>
                                </xsl:call-template>
                            </xsl:with-param>
                            <xsl:with-param name="value" select="."/>
                        </xsl:call-template>
                    </xsl:variable>
                    <test>
                        <name>
                            <xsl:value-of select="$name"/>
                        </name>
                        <index>
                            <xsl:value-of select="$index"/>
                        </index>
                    </test>
                </xsl:for-each>
            </root1>
        </xsl:variable>
        <!--<key id="hostname" for="node" attr.name="Device Hostname" attr.type="string"/>-->
        <!--<key id="deviceModel" for="node" attr.name="Device Model" attr.type="string"/>-->
        <!--<key id="deviceType" for="node" attr.name="Device Type" attr.type="string"/>-->
        <!--<key id="nodeInfo" for="node" attr.name="Node Information " attr.type="string"/>-->
        <!--<key id="discoveredIPv4Address" for="node" attr.name="Discovered IPv4 Address" attr.type="string"/>-->
        <!--<key id="discoveredState" for="node" attr.name="Discovered State" attr.type="string"/>-->
        <!--<key id="sysLocation" for="node" attr.name="Location by SNMP" attr.type="string"/>-->
        <!--<key id="site" for="node" attr.name="site" attr.type="string"/>-->
        <!--<key id="diff" for="node" attr.name="diff" attr.type="string"/>-->
        <!--<key id="diffs" for="node" attr.name="diffs" attr.type="string"/>-->
        <!--<key id="ipv6Forwarding" for="node" attr.name="IP v6 Forwarding" attr.type="string"/>-->
        <!--<key id="ipv4Forwarding" for="node" attr.name="IP v4 Forwarding" attr.type="string"/>-->
        <!--<key id="subnetPrefix" for="node" attr.name="Subnet Prefix" attr.type="string"/>-->
        <!--<key id="ipProtocolType" for="node" attr.name="IP Protocol Type" attr.type="string"/>-->
        <!--<key id="bgpAS" for="node" attr.name="BGP Autonomous system" attr.type="string"/>-->
        <!--<key id="totalInterfaceCount" for="node" attr.name="Total Interface Count" attr.type="string"/>-->

        <!--<key id="discoveryMethod" for="edge" attr.name="Discovery Method" attr.type="string"/>-->
        <!--<key id="dataLink" for="edge" attr.name="dataLink" attr.type="string"/>-->
        <!--<key id="ipLink" for="edge" attr.name="IP Link" attr.type="string"/>-->
        <!--<key id="MPLS" for="edge" attr.name="MPLS" attr.type="string"/>-->
        <!--<key id="ipv6Forwarding" for="edge" attr.name="IP v6 Forwarding" attr.type="string"/>-->
        <!--<key id="ipv4Forwarding" for="edge" attr.name="IP v4 Forwarding" attr.type="string"/>-->
        <!--<key id="interface" for="edge" attr.name="interface" attr.type="string"/>-->
        <!--<key id="diff" for="edge" attr.name="diff" attr.type="string"/>-->
        <!--<key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>-->
        <!--<key id="encapsulation" for="edge" attr.name="L2 encapsulation" attr.type="string"/>-->
        <!--<key id="speed" for="edge" attr.name="Port Speed" attr.type="string"/>-->


        <DiscoveredDevice>
            <name>
                <xsl:value-of select="$hostname"/>
            </name>
            <!-- Device specific parameters-->
            <parameters>
                <parameter>
                    <name>discoveredState</name>
                    <value>discovered</value>
                </parameter>
                <!--Parameter that contain device sysDescr e.g info about device OS and particular image-->
                <parameter>
                    <name>sysDescr</name>
                    <value>
                        <xsl:value-of select="$sysDescr"/>
                    </value>
                </parameter>
                <parameter>
                    <name>deviceType</name>
                    <value>
                        <xsl:value-of select="$deviceType"/>
                    </value>
                </parameter>
                <!--
                OID that represents exact device model for most of the devices. Once identified the OID shall be identified in the
                VENDOR-PRODUCTS-MIB.
                TODO: Currently only CISCO and Juniper are supported!!!
                -->
                <xsl:variable name="oid">
                    <xsl:value-of select="//root/iso/org/dod/internet/mgmt/mib-2/system/sysObjectID"/>
                </xsl:variable>
                <parameter>
                    <name>deviceModel</name>
                    <value><xsl:choose><xsl:when test="$deviceType='CISCO'"><xsl:value-of select="SnmpForXslt:getSymbolByOid('CISCO-PRODUCTS-MIB', $oid)"/></xsl:when><xsl:when test="$deviceType='JUNIPER'"><xsl:value-of select="SnmpForXslt:getSymbolByOid('JUNIPER-CHASSIS-DEFINES-MIB', $oid)"/></xsl:when><xsl:otherwise>Unknown</xsl:otherwise></xsl:choose></value>
                </parameter>
                <parameter>
                    <name>ipAddress</name>
                    <value>
                        <xsl:value-of select="$deviceIPv4Address"/>
                    </value>
                </parameter>
                <parameter>
                    <name>MacAddress</name>
                    <value><xsl:value-of select="$macAddress"/></value>
                </parameter>
                <parameter>
                    <name>totalInterfaceCount</name>
                    <value><xsl:value-of select="//root/iso/org/dod/internet/mgmt/mib-2/interfaces/ifNumber"/></value>
                </parameter>
                <parameter>
                    <name>ipv6Forwarding</name>
                    <value>
                        <xsl:value-of select="$IPv6"/>
                    </value>
                </parameter>
                <parameter>
                    <name>ipv4Forwarding</name>
                    <value>YES</value>
                </parameter>
                <parameter>
                    <name>deviceModelOid</name>
                    <value><xsl:value-of select="$oid"/></value>
                </parameter>
                <xsl:variable name="bgpAS" select="//root/iso/org/dod/internet/mgmt/mib-2/bgp/bgpLocalAs"/>
                <xsl:if test="$bgpAS !='0' and $bgpAS !='' and not(contains($bgpAS,'days'))">
                    <parameter>
                        <name>bgpASInfo</name>
                        <value>
                            <xsl:value-of select="$bgpAS"/>
                        </value>
                    </parameter>
                </xsl:if>

                <!--Those two addresses has intention to STP process. Some devices do not have STP enables so for those might contain
               some boolshit-->
                <xsl:if test="$baseBridgeAddress!='' and not(contains($baseBridgeAddress,'days'))">
                    <parameter>
                        <name>baseBridgeAddress</name>
                        <value>
                            <xsl:value-of select="$baseBridgeAddress"/>
                        </value>
                    </parameter>
                </xsl:if>
                <xsl:if test="$dot1dStpDesignatedRoot!='' and not(contains($dot1dStpDesignatedRoot,'days'))">

                <parameter>
                    <name>stpDesignatedRoot</name>
                    <value>
                        <xsl:value-of select="$dot1dStpDesignatedRoot"/>
                    </value>
                </parameter>
                </xsl:if>
                <xsl:variable name="sysLocation" select="//root/iso/org/dod/internet/mgmt/mib-2/system/sysLocation"/>
                <parameter>
                    <name>sysLocation</name>
                    <value><xsl:value-of select="$sysLocation"/></value>
                </parameter>
            </parameters>
            <!--Walk over the interface ifTable table.-->
            <xsl:for-each select="//root/iso/org/dod/internet/mgmt/mib-2/interfaces/ifTable/ifEntry">
                <xsl:variable name="ifIndex">
                    <xsl:value-of select="ifIndex"/>
                </xsl:variable>
                <xsl:variable name="ifInstanceIndex" select="instance/@instanceIndex"/>
                <xsl:variable name="ifAdminStatus">
                    <xsl:call-template name="adminStatus">
                        <xsl:with-param name="status" select="ifAdminStatus"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="ifOperStatus">
                    <xsl:call-template name="operStatus">
                        <xsl:with-param name="status" select="ifOperStatus"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="ifName">
                    <xsl:value-of select="/root/iso/org/dod/internet/mgmt/mib-2/ifMIB/ifMIBObjects/ifXTable/ifXEntry[instance=$ifIndex]/ifName"/>
                </xsl:variable>
                <xsl:variable name="ifDescr">
                    <xsl:value-of select="ifDescr"/>
                </xsl:variable>
                <xsl:variable name="ifType">
                    <xsl:value-of select="ifType"/>
                </xsl:variable>
                <xsl:variable name="ifSpeed">
                   <xsl:value-of select="//root/iso/org/dod/internet/mgmt/mib-2/ifMIB/ifMIBObjects/ifXTable/ifXEntry[instance=$ifIndex]/ifHighSpeed"/>
                </xsl:variable>
                <xsl:variable name="ifPhysAddress">
                    <xsl:value-of select="ifPhysAddress"/>
                </xsl:variable>
                <xsl:variable name="IPv4Forwarding">
                    <xsl:choose>
                        <xsl:when test="count(//root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable/ipAddrEntry[ipAdEntIfIndex=$ifIndex])>0">YES</xsl:when>
                        <xsl:otherwise>NO</xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="IPv6Forwarding">
                    <xsl:choose>
                        <xsl:when test="count(//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance)>0">YES</xsl:when>
                        <xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex])>0">YES</xsl:when>
                        <xsl:otherwise>NO</xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="vrfForwarding">
                    <xsl:value-of
                            select="$mplsVRF/root1//test[substring-after(index,'.')= $ifIndex]/name"/>
                </xsl:variable>
                <xsl:message>TRACE:>ifDescr<xsl:value-of select="$ifDescr"/> ifIndex<xsl:value-of select="$ifIndex"/> ifType <xsl:value-of select="$ifType"/></xsl:message>
                <!-- Neighbors and IP addresses are obtained only for the interfaces that are up and running.
If the Admin status is UP and Operational is down the interface is marked as Cable CUT !-->
                        <object>
                            <name>
                                <xsl:value-of select="$ifDescr"/>
                            </name>
                            <objectType>Discovery Interface</objectType>
                            <xsl:call-template name="interfaceParameters">
                                <xsl:with-param name="ifDescr" select="$ifDescr"/>
                                <xsl:with-param name="ifIndex" select="$ifIndex"/>
                                <xsl:with-param name="ifName" select="$ifName"/>
                                <xsl:with-param name="ifType" select="$ifType"/>
                                <xsl:with-param name="ifSped" select="$ifSpeed"/>
                                <xsl:with-param name="ifPhysicalAddress" select="$ifPhysAddress"/>
                                <xsl:with-param name="ifAdminStatus" select="$ifAdminStatus"/>
                                <xsl:with-param name="ifOperStatus" select="$ifOperStatus"/>
                                <xsl:with-param name="IPv4Forwarding" select="$IPv4Forwarding"/>
                                <xsl:with-param name="IPv6Forwarding" select="$IPv6Forwarding"/>
                                <xsl:with-param name="vrfForwarding" select="$vrfForwarding"/>
                            </xsl:call-template>
                            <!--Check for  IPv4 IP addresses-->
                            <xsl:variable name="ipv4Addresses">
                                <ipv4>
                                    <xsl:for-each
                                            select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable/ipAddrEntry[ipAdEntIfIndex=$ifIndex]">
                                        <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                        <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                        <xsl:variable name="subnetBitCount"><xsl:call-template name="subnet-to-bitcount"><xsl:with-param
                                                name="subnet" select="$ipAdEntNetMask"/></xsl:call-template></xsl:variable>
                                        <xsl:variable name="ipPrefix"><xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$subnetBitCount"/></xsl:variable>

                                        <ipv4addr>
                                            <ipAdEntAddr><xsl:value-of select="ipAdEntAddr"/></ipAdEntAddr>
                                            <ipAdEntNetMask><xsl:value-of select="ipAdEntNetMask"/></ipAdEntNetMask>
                                            <ipPrefix><xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$subnetBitCount"/></ipPrefix>
                                            <subnetBitCount><xsl:call-template name="subnet-to-bitcount"><xsl:with-param
                                                    name="subnet" select="$ipAdEntNetMask"/></xsl:call-template></subnetBitCount>
                                            <ipv4Subnet><xsl:value-of select="SnmpForXslt:getSubnetFromPrefix($ipPrefix)"/></ipv4Subnet>
                                            <ipv4SubnetBroadcast><xsl:value-of select="SnmpForXslt:getBroadCastFromPrefix($ipPrefix)"/></ipv4SubnetBroadcast>
                                        </ipv4addr>
                                    </xsl:for-each>
                                </ipv4>
                            </xsl:variable>
                            <xsl:message>TRACE:ipv4Addresses<xsl:value-of select="$ipv4Addresses"/>/<xsl:value-of select="ipAdEntNetMask"/></xsl:message>
                            <xsl:for-each select="$ipv4Addresses/ipv4/ipv4addr">
                                <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                <xsl:variable name="subnetBitCount" select="subnetBitCount"/>
                                <xsl:variable name="ipPrefix" select="ipPrefix"/>
                                <xsl:if test="$ipAdEntAddr !=''">
                                    <object>
                                        <name>
                                            <xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of
                                                select="$subnetBitCount"/>
                                        </name>
                                        <objectType>IPv4 Address</objectType>
                                        <parameters>
                                            <parameter>
                                                <name>IPv4Address</name>
                                                <value>
                                                    <xsl:value-of select="$ipAdEntAddr"/>
                                                </value>
                                            </parameter>
                                            <parameter>
                                                <name>ipSubnetMask</name>
                                                <value>
                                                    <xsl:value-of select="$ipAdEntNetMask"/>
                                                </value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4Subnet</name>
                                                <value><xsl:value-of select="SnmpForXslt:getSubnetFromPrefix($ipPrefix)"/></value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4SubnetPrefix</name>
                                                <value><xsl:value-of select="$subnetBitCount"/></value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4SubnetBroadcast</name>
                                                <value><xsl:value-of select="SnmpForXslt:getBroadCastFromPrefix($ipPrefix)"/></value>
                                            </parameter>
                                        </parameters>
                                    </object>
                                </xsl:if>
                            </xsl:for-each>
                            <!--Check for  IPv6 IP addresses-->
                            <xsl:choose>
                                <xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIp/cIpAddressTable/cIpAddressEntry[cIpAddressIfIndex=$ifIndex]/instance)!=0">
                                    <xsl:for-each
                                            select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIp/cIpAddressTable/cIpAddressEntry[cIpAddressIfIndex=$ifIndex]/instance">
                                        <xsl:variable name="instance" select="substring-after(.,'.')"/>
                                        <xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>

                                        <xsl:variable name="ipv6AddrPfxLength"
                                                      select="substring-after(substring-before(../cIpAddressPrefix,'.'),'.')"/>
                                        <xsl:variable name="ipv6AddrType" select="../cIpAddressType"/>
                                        <xsl:variable name="cIpAddressOrigin" select="../cIpAddressPrefix"/>
                                        <xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>
                                        <xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>
                                        <xsl:message>DEBUG: cIpAddressTable  <xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>

                                        <xsl:call-template name="IPv6">
                                            <xsl:with-param name="ipAdEntAddr" select="IPv6formatConvertor:IPv6Convertor($ipAdEntAddr)"/>
                                            <xsl:with-param name="ipv6AddrPfxLength"
                                                            select="functx:substring-after-last-match($cIpAddressOrigin,'\.')"/>
                                            <xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>
                                            <xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>
                                            <xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>
                                        </xsl:call-template>
                                    </xsl:for-each>
                                </xsl:when>
                                <xsl:otherwise>
                                        <!--<xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex]/cIpv6InterfaceIdentifier) > 0">-->
                                            <!--<xsl:for-each-->
                                                    <!--select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex]/cIpv6InterfaceIdentifier">-->
                                                <!--<xsl:variable name="instance" select="."/>-->
                                                <!--<xsl:variable name="ipAdEntAddr" select="IPv6formatConvertor:IPv6Convertor(.)"/>-->
                                                <!--<xsl:variable name="ipv6AddrPfxLength"-->
                                                              <!--select="../cIpv6InterfaceIdentifierLength"/>-->
                                                <!--<xsl:variable name="ipv6AddrType" select="../cIpAddressType"/>-->
                                                <!--<xsl:variable name="cIpAddressOrigin" select="../cIpAddressPrefix"/>-->
                                                <!--<xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>-->
                                                <!--<xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>-->
                                                <!--<xsl:message>DEBUG: cIpv6InterfaceIfIndex<xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>-->

                                                <!--<xsl:call-template name="IPv6">-->
                                                    <!--<xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>-->
                                                    <!--<xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>-->
                                                    <!--<xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>-->
                                                    <!--<xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>-->
                                                    <!--<xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>-->
                                                <!--</xsl:call-template>-->
                                            <!--</xsl:for-each>-->
                                        <!--</xsl:when>-->
                                            <xsl:for-each
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance">
                                                <xsl:variable name="instance" select="substring-after(.,'.')"/>
                                                <xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>

                                                <!--<xsl:variable name="fr" select="()"/>-->
                                                <!--<xsl:variable name="to" select="(':')"/>-->
                                                <!--<xsl:variable name="ipAddr" select="functx:replace-multi($ipAdEntAddr,$fr,$to)" />-->
                                                <xsl:variable name="ipv6AddrPfxLength" select="../ipv6AddrPfxLength"/>
                                                <xsl:variable name="ipv6AddrType" select="../ipv6AddrType"/>
                                                <xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>
                                                <xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>
                                                <xsl:message>DEBUG: ipv6MIB<xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>

                                                <xsl:call-template name="IPv6">
                                                    <xsl:with-param name="ipAdEntAddr"
                                                                    select="$ipAdEntAddr"/>
                                                    <xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>
                                                    <xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>
                                                    <xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>
                                                    <xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>
                                                </xsl:call-template>

                                            </xsl:for-each>
                                </xsl:otherwise>
                                <!--<xsl:otherwise>-->
                                    <!--<xsl:for-each-->
                                            <!--select="//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance">-->
                                        <!--<xsl:variable name="instance" select="substring-after(.,'.')"/>-->
                                        <!--<xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>-->
                                        <!--&lt;!&ndash;<xsl:variable name="fr" select="()"/>&ndash;&gt;-->
                                        <!--&lt;!&ndash;<xsl:variable name="to" select="(':')"/>&ndash;&gt;-->
                                        <!--&lt;!&ndash;<xsl:variable name="ipAddr" select="functx:replace-multi($ipAdEntAddr,$fr,$to)" />&ndash;&gt;-->
                                        <!--<xsl:variable name="ipv6AddrPfxLength" select="../ipv6AddrPfxLength"/>-->
                                        <!--<xsl:variable name="ipv6AddrType" select="../ipv6AddrType"/>-->
                                        <!--<xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>-->
                                        <!--<xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>-->
                                        <!--<xsl:call-template name="IPv6">-->
                                            <!--<xsl:with-param name="ipAdEntAddr"-->
                                                            <!--select="IPv6formatConvertor:IPv6Convertor($ipAdEntAddr)"/>-->
                                            <!--<xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>-->
                                            <!--<xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>-->
                                            <!--<xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>-->
                                            <!--<xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>-->
                                        <!--</xsl:call-template>-->
                                    <!--</xsl:for-each>-->
                                <!--</xsl:otherwise>-->
                            </xsl:choose>
                            <xsl:variable name="interface-neighbors">
                                <xsl:for-each select="$ipv4Addresses/ipv4/ipv4addr">
                                    <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                    <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                    <xsl:call-template name="SLASH30">
                                        <xsl:with-param name="ipAdEntNetMask" select="$ipAdEntNetMask"/>
                                        <xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>
                                    </xsl:call-template>
                                    <xsl:call-template name="SLASH31">
                                        <xsl:with-param name="ipAdEntNetMask" select="$ipAdEntNetMask"/>
                                        <xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                                <!--Check for NEXT-HOP neighbors-->
                                <xsl:call-template name="nextHop">
                                    <xsl:with-param name="ipRouteTable"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipRouteTable/ipRouteEntry[ipRouteIfIndex=$ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>
                                <!--Check for CIDR-NEXT-HOP neighbors-->
                                <xsl:call-template name="cnextHop">
                                    <xsl:with-param name="ipCidrRouteTable"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipForward/ipCidrRouteTable/ipCidrRouteEntry[ipCidrRouteIfIndex=$ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>
                                <!--Check for ARP neighbors-->
                                <xsl:call-template name="ARP">
                                    <xsl:with-param name="ipNetToMediaIfNeighbors"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipNetToMediaTable/ipNetToMediaEntry[ipNetToMediaIfIndex = $ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>

                                <!--Check for MAC neighbors-->
                                <xsl:variable name="brdPort">
                                    <xsl:value-of
                                            select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dBase/dot1dBasePortTable/dot1dBasePortEntry[dot1dBasePortIfIndex=$ifIndex]/dot1dBasePort"/>
                                </xsl:variable>
                                <xsl:for-each
                                        select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dTp/dot1dTpFdbTable/dot1dTpFdbEntry[dot1dTpFdbPort=$brdPort]">
                                    <xsl:variable name="neighborMACAddress">
                                        <xsl:value-of select="dot1dTpFdbAddress"/>
                                    </xsl:variable>
                                    <xsl:variable name="neighborIPAddress">
                                        <xsl:value-of
                                                select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipNetToMediaTable/ipNetToMediaEntry[ipNetToMediaPhysAddress=$neighborMACAddress][1]/ipNetToMediaNetAddress"/>

                                    </xsl:variable>
                                    <xsl:message>DEBUG: NEIGHBOR MAC: <xsl:copy-of select="$neighborMACAddress"/> </xsl:message>
                                    <xsl:message>DEBUG: NEIGHBOR IP: <xsl:copy-of select="$neighborIPAddress"/> </xsl:message>

                                    <xsl:call-template name="MAC">
                                        <xsl:with-param name="neighborMACAddress" select="dot1dTpFdbAddress"/>

                                        <xsl:with-param name="neighborIPAddress"
                                                        select="$neighborIPAddress"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                                <!--Check for CDP neighbors-->
                                <xsl:call-template name="CDP">
                                    <xsl:with-param name="cdpIfNeighbors"
                                                    select="exslt:node-set(//root/iso/org/dod/internet/private/enterprises/cisco/ciscoMgmt/ciscoCdpMIB/ciscoCdpMIBObjects/cdpCache/cdpCacheTable/cdpCacheEntry[index[@name='cdpCacheIfIndex'] = $ifIndex])"/>
                                </xsl:call-template>
                                <!--Check for LLDP neighbors-->
                                <xsl:call-template name="LLDP">
                                    <xsl:with-param name="lldpIfNeighbors"
                                                    select="//root/iso/std/iso8802/ieee802dot1/ieee802dot1mibs/lldpMIB/lldpObjects/lldpRemoteSystemsData/lldpRemTable/lldpRemEntry/index[@name = 'lldpRemLocalPortNum' and text()=$ifIndex]/../lldpRemSysName"/>
                                </xsl:call-template>
                                <!--Check for Spanning Tree neighbors-->
                                <!--<TEST>brdPort<xsl:value-of select="$brdPort"/>-->
                                <!--</TEST>-->
                                <!--<xsl:for-each-->
                                <!--select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dStp/dot1dStpPortTable/dot1dStpPortEntry[dot1dStpPort=$brdPort]">-->
                                <!--<xsl:variable name="designatedBridge" select="dot1dStpPortDesignatedBridge"/>-->
                                <!--<xsl:choose>-->
                                <!--<xsl:when test="contains($designatedBridge,$baseBridgeAddress)">-->
                                <!--<STP>The other switch is the root <xsl:value-of select="$designatedBridge"/>|<xsl:value-of-->
                                <!--select="$baseBridgeAddress"/>-->
                                <!--</STP>-->
                                <!--</xsl:when>-->
                                <!--<xsl:otherwise>-->
                                <!--<STP>I am the root. The root is <xsl:value-of select="$baseBridgeAddress"/>|-->
                                <!--<xsl:value-of select="$designatedBridge"/>-->
                                <!--</STP>-->
                                <!--</xsl:otherwise>-->
                                <!--</xsl:choose>-->
                                <!--</xsl:for-each>-->
                            </xsl:variable>
                            <xsl:message>DEBUG: NEIGHBORS </xsl:message>
                            <xsl:for-each select="distinct-values($interface-neighbors/object/name)">
                                <xsl:variable name="name1" select="."/>
                                <xsl:message>DEBUG: Name: <xsl:value-of select="$name1"/> </xsl:message>

                                <xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1]/parameters/parameter[name='Neighbor IP Address']/value)">
                                    <xsl:variable name="ipAddress666" select="."/>
                                    <xsl:message>DEBUG: IP: <xsl:value-of select="$ipAddress666"/></xsl:message>
                                    <object>
                                            <name><xsl:value-of select="$name1"/></name>
                                            <objectType>Discovered Neighbor</objectType>
                                            <parameters>
                                                <xsl:variable name="Reachable">
                                                    <xsl:for-each
                                                            select="$interface-neighbors/object[name=$name1]/parameters/parameter[name='Reachable']/value">
                                                        <xsl:value-of select="."/>
                                                    </xsl:for-each>
                                                </xsl:variable>
                                                <!--<Reachable>-->
                                                    <!--<xsl:copy-of select="$Reachable"/>-->
                                                <!--</Reachable>-->
                                                <xsl:choose>
                                                    <xsl:when test="contains($Reachable,'YES')">
                                                        <parameter>
                                                            <name>Reachable</name>
                                                            <value>YES</value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Discovery Method</name>
                                                            <xsl:variable name="discoveryMethods"><xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Discovery Method']/value)"><xsl:value-of select="."/>,</xsl:for-each></xsl:variable>
                                                            <value><xsl:value-of select="functx:substring-before-last-match($discoveryMethods,',')"/></value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor Port</name>
                                                            <value>
                                                                <xsl:value-of select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Port']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor IP Address</name>
                                                            <value>
                                                                <xsl:value-of select="$ipAddress666"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor hostname</name>
                                                            <value>
                                                                <xsl:value-of select="$name1"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor Device Type</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Device Type']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor MAC Address</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor MAC Address']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <parameter>
                                                            <name>Reachable</name>
                                                            <value>NO</value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Discovery Method</name>
                                                            <xsl:variable name="discoveryMethods"><xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Discovery Method']/value)"><xsl:value-of select="."/>,</xsl:for-each></xsl:variable>
                                                            <value><xsl:value-of select="functx:substring-before-last-match($discoveryMethods,',')"/></value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor Port</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Port']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor IP Address</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="$ipAddress666"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor hostname</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="$name1"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor Device Type</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Device Type']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor MAC Address</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor MAC Address']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </parameters>
                                        </object>
                                    </xsl:for-each>

                            </xsl:for-each>
                        </object>

            </xsl:for-each>

            <!--Walk over the ifXTable neighbours and find those that are not alredy in the ifTable-->
            <xsl:for-each select="//root/iso/org/dod/internet/mgmt/mib-2/ifMIB/ifMIBObjects/ifXTable/ifXEntry">
                <xsl:variable name="ifIndex" select="instance"/>
                <xsl:choose>
                    <xsl:when test="not(exists(//root/iso/org/dod/internet/mgmt/mib-2/interfaces/ifTable/ifEntry[ifIndex=$ifIndex]))">
                        <xsl:variable name="ifAdminStatus">
                            <xsl:call-template name="adminStatus">
                                <xsl:with-param name="status" select="ifXAdminStatus"/>
                            </xsl:call-template>
                        </xsl:variable>
                        <xsl:variable name="ifOperStatus">
                            <xsl:call-template name="operStatus">
                                <xsl:with-param name="status" select="ifOperStatus"/>
                            </xsl:call-template>
                        </xsl:variable>
                        <xsl:variable name="ifName">
                            <xsl:value-of select="/root/iso/org/dod/internet/mgmt/mib-2/ifMIB/ifMIBObjects/ifXTable/ifXEntry[instance=$ifIndex]/ifName"/>
                        </xsl:variable>
                        <xsl:variable name="ifDescr">
                            <xsl:value-of select="ifDescr"/>
                        </xsl:variable>
                        <xsl:variable name="ifType">
                            <xsl:value-of select="ifType"/>
                        </xsl:variable>
                        <xsl:variable name="ifSpeed">
                            <xsl:value-of select="//root/iso/org/dod/internet/mgmt/mib-2/ifMIB/ifMIBObjects/ifXTable/ifXEntry[instance=$ifIndex]/ifHighSpeed"/>
                        </xsl:variable>
                        <xsl:variable name="ifPhysAddress">
                            <xsl:value-of select="ifPhysAddress"/>
                        </xsl:variable>
                        <xsl:variable name="IPv4Forwarding">
                            <xsl:choose>
                                <xsl:when test="count(//root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable/ipAddrEntry[ipAdEntIfIndex=$ifIndex])>0">YES</xsl:when>
                                <xsl:otherwise>NO</xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="IPv6Forwarding">
                            <xsl:choose>
                                <xsl:when test="count(//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance)>0">YES</xsl:when>
                                <xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex])>0">YES</xsl:when>
                                <xsl:otherwise>NO</xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="vrfForwarding">
                            <xsl:value-of
                                    select="$mplsVRF/root1//test[substring-after(index,'.')= $ifIndex]/name"/>
                        </xsl:variable>
                        <xsl:message>TRACE:>ifDescr<xsl:value-of select="$ifDescr"/> ifIndex<xsl:value-of select="$ifIndex"/> ifType <xsl:value-of select="$ifType"/></xsl:message>
                        <!-- Neighbors and IP addresses are obtained only for the interfaces that are up and running.
        If the Admin status is UP and Operational is down the interface is marked as Cable CUT !-->
                        <object>
                            <name>
                                <xsl:value-of select="$ifDescr"/>
                            </name>
                            <objectType>Discovery Interface</objectType>
                            <xsl:call-template name="interfaceParameters">
                                <xsl:with-param name="ifDescr" select="$ifDescr"/>
                                <xsl:with-param name="ifIndex" select="$ifIndex"/>
                                <xsl:with-param name="ifName" select="$ifName"/>
                                <xsl:with-param name="ifType" select="$ifType"/>
                                <xsl:with-param name="ifSped" select="$ifSpeed"/>
                                <xsl:with-param name="ifPhysicalAddress" select="$ifPhysAddress"/>
                                <xsl:with-param name="ifAdminStatus" select="$ifAdminStatus"/>
                                <xsl:with-param name="ifOperStatus" select="$ifOperStatus"/>
                                <xsl:with-param name="IPv4Forwarding" select="$IPv4Forwarding"/>
                                <xsl:with-param name="IPv6Forwarding" select="$IPv6Forwarding"/>
                                <xsl:with-param name="vrfForwarding" select="$vrfForwarding"/>
                            </xsl:call-template>
                            <!--Check for  IPv4 IP addresses-->
                            <xsl:variable name="ipv4Addresses">
                                <ipv4>
                                    <xsl:for-each
                                            select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable/ipAddrEntry[ipAdEntIfIndex=$ifIndex]">
                                        <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                        <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                        <xsl:variable name="subnetBitCount"><xsl:call-template name="subnet-to-bitcount"><xsl:with-param
                                                name="subnet" select="$ipAdEntNetMask"/></xsl:call-template></xsl:variable>
                                        <xsl:variable name="ipPrefix"><xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$subnetBitCount"/></xsl:variable>

                                        <ipv4addr>
                                            <ipAdEntAddr><xsl:value-of select="ipAdEntAddr"/></ipAdEntAddr>
                                            <ipAdEntNetMask><xsl:value-of select="ipAdEntNetMask"/></ipAdEntNetMask>
                                            <ipPrefix><xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$subnetBitCount"/></ipPrefix>
                                            <subnetBitCount><xsl:call-template name="subnet-to-bitcount"><xsl:with-param
                                                    name="subnet" select="$ipAdEntNetMask"/></xsl:call-template></subnetBitCount>
                                            <ipv4Subnet><xsl:value-of select="SnmpForXslt:getSubnetFromPrefix($ipPrefix)"/></ipv4Subnet>
                                            <ipv4SubnetBroadcast><xsl:value-of select="SnmpForXslt:getBroadCastFromPrefix($ipPrefix)"/></ipv4SubnetBroadcast>
                                        </ipv4addr>
                                    </xsl:for-each>
                                </ipv4>
                            </xsl:variable>
                            <xsl:message>TRACE:ipv4Addresses<xsl:value-of select="$ipv4Addresses"/>/<xsl:value-of select="ipAdEntNetMask"/></xsl:message>
                            <xsl:for-each select="$ipv4Addresses/ipv4/ipv4addr">
                                <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                <xsl:variable name="subnetBitCount" select="subnetBitCount"/>
                                <xsl:variable name="ipPrefix" select="ipPrefix"/>
                                <xsl:if test="$ipAdEntAddr !=''">
                                    <object>
                                        <name>
                                            <xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of
                                                select="$subnetBitCount"/>
                                        </name>
                                        <objectType>IPv4 Address</objectType>
                                        <parameters>
                                            <parameter>
                                                <name>IPv4Address</name>
                                                <value>
                                                    <xsl:value-of select="$ipAdEntAddr"/>
                                                </value>
                                            </parameter>
                                            <parameter>
                                                <name>ipSubnetMask</name>
                                                <value>
                                                    <xsl:value-of select="$ipAdEntNetMask"/>
                                                </value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4Subnet</name>
                                                <value><xsl:value-of select="SnmpForXslt:getSubnetFromPrefix($ipPrefix)"/></value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4SubnetPrefix</name>
                                                <value><xsl:value-of select="$subnetBitCount"/></value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4SubnetBroadcast</name>
                                                <value><xsl:value-of select="SnmpForXslt:getBroadCastFromPrefix($ipPrefix)"/></value>
                                            </parameter>
                                        </parameters>
                                    </object>
                                </xsl:if>
                            </xsl:for-each>
                            <!--Check for  IPv6 IP addresses-->
                            <xsl:choose>
                                <xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIp/cIpAddressTable/cIpAddressEntry[cIpAddressIfIndex=$ifIndex]/instance)!=0">
                                    <xsl:for-each
                                            select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIp/cIpAddressTable/cIpAddressEntry[cIpAddressIfIndex=$ifIndex]/instance">
                                        <xsl:variable name="instance" select="substring-after(.,'.')"/>
                                        <xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>

                                        <xsl:variable name="ipv6AddrPfxLength"
                                                      select="substring-after(substring-before(../cIpAddressPrefix,'.'),'.')"/>
                                        <xsl:variable name="ipv6AddrType" select="../cIpAddressType"/>
                                        <xsl:variable name="cIpAddressOrigin" select="../cIpAddressPrefix"/>
                                        <xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>
                                        <xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>
                                        <xsl:message>DEBUG: cIpAddressTable  <xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>

                                        <xsl:call-template name="IPv6">
                                            <xsl:with-param name="ipAdEntAddr" select="IPv6formatConvertor:IPv6Convertor($ipAdEntAddr)"/>
                                            <xsl:with-param name="ipv6AddrPfxLength"
                                                            select="functx:substring-after-last-match($cIpAddressOrigin,'\.')"/>
                                            <xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>
                                            <xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>
                                            <xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>
                                        </xsl:call-template>
                                    </xsl:for-each>
                                </xsl:when>
                                <xsl:otherwise>
                                    <!--<xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex]/cIpv6InterfaceIdentifier) > 0">-->
                                    <!--<xsl:for-each-->
                                    <!--select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex]/cIpv6InterfaceIdentifier">-->
                                    <!--<xsl:variable name="instance" select="."/>-->
                                    <!--<xsl:variable name="ipAdEntAddr" select="IPv6formatConvertor:IPv6Convertor(.)"/>-->
                                    <!--<xsl:variable name="ipv6AddrPfxLength"-->
                                    <!--select="../cIpv6InterfaceIdentifierLength"/>-->
                                    <!--<xsl:variable name="ipv6AddrType" select="../cIpAddressType"/>-->
                                    <!--<xsl:variable name="cIpAddressOrigin" select="../cIpAddressPrefix"/>-->
                                    <!--<xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>-->
                                    <!--<xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>-->
                                    <!--<xsl:message>DEBUG: cIpv6InterfaceIfIndex<xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>-->

                                    <!--<xsl:call-template name="IPv6">-->
                                    <!--<xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>-->
                                    <!--<xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>-->
                                    <!--<xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>-->
                                    <!--<xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>-->
                                    <!--<xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>-->
                                    <!--</xsl:call-template>-->
                                    <!--</xsl:for-each>-->
                                    <!--</xsl:when>-->
                                    <xsl:for-each
                                            select="//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance">
                                        <xsl:variable name="instance" select="substring-after(.,'.')"/>
                                        <xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>

                                        <!--<xsl:variable name="fr" select="()"/>-->
                                        <!--<xsl:variable name="to" select="(':')"/>-->
                                        <!--<xsl:variable name="ipAddr" select="functx:replace-multi($ipAdEntAddr,$fr,$to)" />-->
                                        <xsl:variable name="ipv6AddrPfxLength" select="../ipv6AddrPfxLength"/>
                                        <xsl:variable name="ipv6AddrType" select="../ipv6AddrType"/>
                                        <xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>
                                        <xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>
                                        <xsl:message>DEBUG: ipv6MIB<xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>

                                        <xsl:call-template name="IPv6">
                                            <xsl:with-param name="ipAdEntAddr"
                                                            select="$ipAdEntAddr"/>
                                            <xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>
                                            <xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>
                                            <xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>
                                            <xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>
                                        </xsl:call-template>

                                    </xsl:for-each>
                                </xsl:otherwise>
                                <!--<xsl:otherwise>-->
                                <!--<xsl:for-each-->
                                <!--select="//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance">-->
                                <!--<xsl:variable name="instance" select="substring-after(.,'.')"/>-->
                                <!--<xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>-->
                                <!--&lt;!&ndash;<xsl:variable name="fr" select="()"/>&ndash;&gt;-->
                                <!--&lt;!&ndash;<xsl:variable name="to" select="(':')"/>&ndash;&gt;-->
                                <!--&lt;!&ndash;<xsl:variable name="ipAddr" select="functx:replace-multi($ipAdEntAddr,$fr,$to)" />&ndash;&gt;-->
                                <!--<xsl:variable name="ipv6AddrPfxLength" select="../ipv6AddrPfxLength"/>-->
                                <!--<xsl:variable name="ipv6AddrType" select="../ipv6AddrType"/>-->
                                <!--<xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>-->
                                <!--<xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>-->
                                <!--<xsl:call-template name="IPv6">-->
                                <!--<xsl:with-param name="ipAdEntAddr"-->
                                <!--select="IPv6formatConvertor:IPv6Convertor($ipAdEntAddr)"/>-->
                                <!--<xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>-->
                                <!--<xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>-->
                                <!--<xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>-->
                                <!--<xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>-->
                                <!--</xsl:call-template>-->
                                <!--</xsl:for-each>-->
                                <!--</xsl:otherwise>-->
                            </xsl:choose>
                            <xsl:variable name="interface-neighbors">
                                <xsl:for-each select="$ipv4Addresses/ipv4/ipv4addr">
                                    <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                    <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                    <xsl:call-template name="SLASH30">
                                        <xsl:with-param name="ipAdEntNetMask" select="$ipAdEntNetMask"/>
                                        <xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>
                                    </xsl:call-template>
                                    <xsl:call-template name="SLASH31">
                                        <xsl:with-param name="ipAdEntNetMask" select="$ipAdEntNetMask"/>
                                        <xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                                <!--Check for NEXT-HOP neighbors-->
                                <xsl:call-template name="nextHop">
                                    <xsl:with-param name="ipRouteTable"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipRouteTable/ipRouteEntry[ipRouteIfIndex=$ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>
                                <!--Check for CIDR-NEXT-HOP neighbors-->
                                <xsl:call-template name="cnextHop">
                                    <xsl:with-param name="ipCidrRouteTable"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipForward/ipCidrRouteTable/ipCidrRouteEntry[ipCidrRouteIfIndex=$ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>
                                <!--Check for ARP neighbors-->
                                <xsl:call-template name="ARP">
                                    <xsl:with-param name="ipNetToMediaIfNeighbors"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipNetToMediaTable/ipNetToMediaEntry[ipNetToMediaIfIndex = $ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>

                                <!--Check for MAC neighbors-->
                                <xsl:variable name="brdPort">
                                    <xsl:value-of
                                            select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dBase/dot1dBasePortTable/dot1dBasePortEntry[dot1dBasePortIfIndex=$ifIndex]/dot1dBasePort"/>
                                </xsl:variable>
                                <xsl:for-each
                                        select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dTp/dot1dTpFdbTable/dot1dTpFdbEntry[dot1dTpFdbPort=$brdPort]">
                                    <xsl:variable name="neighborMACAddress">
                                        <xsl:value-of select="dot1dTpFdbAddress"/>
                                    </xsl:variable>
                                    <xsl:variable name="neighborIPAddress">
                                        <xsl:value-of
                                                select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipNetToMediaTable/ipNetToMediaEntry[ipNetToMediaPhysAddress=$neighborMACAddress][1]/ipNetToMediaNetAddress"/>

                                    </xsl:variable>
                                    <xsl:message>DEBUG: NEIGHBOR MAC: <xsl:copy-of select="$neighborMACAddress"/> </xsl:message>
                                    <xsl:message>DEBUG: NEIGHBOR IP: <xsl:copy-of select="$neighborIPAddress"/> </xsl:message>

                                    <xsl:call-template name="MAC">
                                        <xsl:with-param name="neighborMACAddress" select="dot1dTpFdbAddress"/>
                                        <!--<xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>-->

                                        <xsl:with-param name="neighborIPAddress"
                                                        select="$neighborIPAddress"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                                <!--Check for CDP neighbors-->
                                <xsl:call-template name="CDP">
                                    <xsl:with-param name="cdpIfNeighbors"
                                                    select="exslt:node-set(//root/iso/org/dod/internet/private/enterprises/cisco/ciscoMgmt/ciscoCdpMIB/ciscoCdpMIBObjects/cdpCache/cdpCacheTable/cdpCacheEntry[index[@name='cdpCacheIfIndex'] = $ifIndex])"/>
                                </xsl:call-template>
                                <!--Check for LLDP neighbors-->
                                <xsl:call-template name="LLDP">
                                    <xsl:with-param name="lldpIfNeighbors"
                                                    select="//root/iso/std/iso8802/ieee802dot1/ieee802dot1mibs/lldpMIB/lldpObjects/lldpRemoteSystemsData/lldpRemTable/lldpRemEntry/index[@name = 'lldpRemLocalPortNum' and text()=$ifIndex]/../lldpRemSysName"/>
                                </xsl:call-template>
                                <!--Check for Spanning Tree neighbors-->
                                <!--<TEST>brdPort<xsl:value-of select="$brdPort"/>-->
                                <!--</TEST>-->
                                <!--<xsl:for-each-->
                                <!--select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dStp/dot1dStpPortTable/dot1dStpPortEntry[dot1dStpPort=$brdPort]">-->
                                <!--<xsl:variable name="designatedBridge" select="dot1dStpPortDesignatedBridge"/>-->
                                <!--<xsl:choose>-->
                                <!--<xsl:when test="contains($designatedBridge,$baseBridgeAddress)">-->
                                <!--<STP>The other switch is the root <xsl:value-of select="$designatedBridge"/>|<xsl:value-of-->
                                <!--select="$baseBridgeAddress"/>-->
                                <!--</STP>-->
                                <!--</xsl:when>-->
                                <!--<xsl:otherwise>-->
                                <!--<STP>I am the root. The root is <xsl:value-of select="$baseBridgeAddress"/>|-->
                                <!--<xsl:value-of select="$designatedBridge"/>-->
                                <!--</STP>-->
                                <!--</xsl:otherwise>-->
                                <!--</xsl:choose>-->
                                <!--</xsl:for-each>-->
                            </xsl:variable>
                            <xsl:message>DEBUG: NEIGHBORS </xsl:message>
                            <xsl:for-each select="distinct-values($interface-neighbors/object/name)">
                                <xsl:variable name="name1" select="."/>
                                <xsl:message>DEBUG: Name: <xsl:value-of select="$name1"/> </xsl:message>

                                <xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1]/parameters/parameter[name='Neighbor IP Address']/value)">
                                    <xsl:variable name="ipAddress666" select="."/>
                                    <xsl:message>DEBUG: IP: <xsl:value-of select="$ipAddress666"/></xsl:message>
                                    <object>
                                        <name><xsl:value-of select="$name1"/></name>
                                        <objectType>Discovered Neighbor</objectType>
                                        <parameters>
                                            <xsl:variable name="Reachable">
                                                <xsl:for-each
                                                        select="$interface-neighbors/object[name=$name1]/parameters/parameter[name='Reachable']/value">
                                                    <xsl:value-of select="."/>
                                                </xsl:for-each>
                                            </xsl:variable>
                                            <!--<Reachable>-->
                                            <!--<xsl:copy-of select="$Reachable"/>-->
                                            <!--</Reachable>-->
                                            <xsl:choose>
                                                <xsl:when test="contains($Reachable,'YES')">
                                                    <parameter>
                                                        <name>Reachable</name>
                                                        <value>YES</value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Discovery Method</name>
                                                        <xsl:variable name="discoveryMethods"><xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Discovery Method']/value)"><xsl:value-of select="."/>,</xsl:for-each></xsl:variable>
                                                        <value><xsl:value-of select="functx:substring-before-last-match($discoveryMethods,',')"/></value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor Port</name>
                                                        <value>
                                                            <xsl:value-of select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Port']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor IP Address</name>
                                                        <value>
                                                            <xsl:value-of select="$ipAddress666"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor hostname</name>
                                                        <value>
                                                            <xsl:value-of select="$name1"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor Device Type</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Device Type']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor MAC Address</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor MAC Address']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <parameter>
                                                        <name>Reachable</name>
                                                        <value>NO</value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Discovery Method</name>
                                                        <xsl:variable name="discoveryMethods"><xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Discovery Method']/value)"><xsl:value-of select="."/>,</xsl:for-each></xsl:variable>
                                                        <value><xsl:value-of select="functx:substring-before-last-match($discoveryMethods,',')"/></value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor Port</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Port']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor IP Address</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="$ipAddress666"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor hostname</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="$name1"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor Device Type</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Device Type']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor MAC Address</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor MAC Address']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </parameters>
                                    </object>
                                </xsl:for-each>

                            </xsl:for-each>
                        </object>

                    </xsl:when>

                </xsl:choose>

            </xsl:for-each>
            <object>
                <name>DeviceLogicalData</name>
                <objectType>DeviceLogicalData</objectType>
                <parameters/>
                <xsl:for-each select="//org/dod/internet/mgmt/mib-2/ospf/ospfNbrTable/ospfNbrEntry">
                    <xsl:call-template name="OSPF">
                        <xsl:with-param name="ospfNbr" select="."/>
                    </xsl:call-template>
                </xsl:for-each>
                <xsl:for-each select="//org/dod/internet/mgmt/mib-2/bgp/bgpPeerTable/bgpPeerEntry">
                    <xsl:call-template name="BGP">
                        <xsl:with-param name="bgpPeer" select="."/>
                    </xsl:call-template>
                </xsl:for-each>
                <xsl:call-template name="IPSEC-Phase1">
                    <xsl:with-param name="cikeTunnelTable" select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoMgmt/ciscoIpSecFlowMonitorMIB/cipSecMIBObjects/cipSecPhaseOne/cikeTunnelTable"/>
                    <xsl:with-param name="sysName"/>
                    <xsl:with-param name="ipv4addresses" select="/root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable"/>
                </xsl:call-template>
                <xsl:call-template name="IPSEC-Phase2">
                    <xsl:with-param name="cipSecTunnelTable" select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoMgmt/ciscoIpSecFlowMonitorMIB/cipSecMIBObjects/cipSecPhaseTwo/cipSecTunnelTable"/>
                    <xsl:with-param name="sysName"/>
                    <xsl:with-param name="ipv4addresses" select="/root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable"/>
                </xsl:call-template>
            </object>
            <object>
                <name>mplsL3VPNs</name>
                <objectType>mplsL3VPNs</objectType>
                <parameters/>

                <xsl:for-each
                        select="//org/dod/internet/experimental/mplsVpnMIB/mplsVpnObjects/mplsVpnConf/mplsVpnVrfTable/mplsVpnVrfEntry">
                    <xsl:variable name="RD" select="mplsVpnVrfRouteDistinguisher"/>
                    <xsl:variable name="instance" select="instance"/>
                    <xsl:variable name="rd_instance" select="substring-after($instance,'.')"/>
                    <object>
                        <name>
                            <xsl:value-of select="$RD"/>
                        </name>
                        <objectType>mplsL3VPN</objectType>
                        <parameters>
                            <parameter>
                                <name>vrfName</name>
                                <value>
                                    <xsl:for-each select="tokenize($rd_instance,'\.')">
                                        <xsl:value-of select="codepoints-to-string(xs:integer(.))"/>
                                        <!--xsl:value-of select="."/-->
                                    </xsl:for-each>
                                </value>
                            </parameter>
                        </parameters>
                        <xsl:for-each
                                select="//org/dod/internet/experimental/mplsVpnMIB/mplsVpnObjects/mplsVpnConf/mplsVpnVrfRouteTargetTable/mplsVpnVrfRouteTargetEntry[contains(instance,$rd_instance)]">
                            <xsl:variable name="rt" select="mplsVpnVrfRouteTarget"/>
                            <object>
                                <name>
                                    <xsl:value-of select="$rt"/>
                                </name>
                                <objectType>RT</objectType>
                                <parameters>
                                    <parameter>
                                        <name>Type</name>
                                        <value>
                                            <xsl:value-of
                                                    select="codepoints-to-string(xs:integer(index[@name='mplsVpnVrfRouteTargetType']))"/>
                                        </value>
                                    </parameter>
                                </parameters>
                            </object>
                        </xsl:for-each>
                    </object>
                </xsl:for-each>
            </object>

                <!--<xsl:for-each-->
                        <!--select="//root/iso/org/dod/internet/private/enterprises/cisco/ciscoMgmt/ciscoIpSecFlowMonitorMIB/cipSecMIBObjects/cipSecPhaseOne/cikeTunnelTable/cikeTunnelEntry">-->
                    <!--<xsl:variable name="localAddress"><xsl:variable name="temp"><xsl:for-each select="tokenize(cikeTunLocalAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>-->
                    <!--</xsl:variable><xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>-->
                    <!--</xsl:variable>-->
                    <!--<xsl:variable name="remoteAddress"><xsl:variable name="temp"><xsl:for-each select="tokenize(cikeTunRemoteAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>-->
                    <!--</xsl:variable>-->
                        <!--<xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>-->
                    <!--</xsl:variable>-->
                    <!--<xsl:variable name="status">-->
                        <!--<xsl:choose>-->
                            <!--<xsl:when test="cikeTunStatus = '1'">UP</xsl:when>-->
                            <!--<xsl:otherwise>DESTROYING</xsl:otherwise>-->
                        <!--</xsl:choose>-->
                    <!--</xsl:variable>-->
                    <!--<xsl:variable name="cikeTunRemoteValue" select="cikeTunRemoteValue"/>-->

                    <!--<xsl:variable name="instance" select="instance"/>-->
                    <!--<object>-->
                        <!--<name>-->
                            <!--<xsl:value-of select="$localAddress"/>-<xsl:value-of select="$remoteAddress"/>-->
                        <!--</name>-->
                        <!--<objectType>Discovered Neighbor</objectType>-->
                        <!--<parameters>-->
                            <!--<parameter>-->
                                <!--<name>Local IP Address</name>-->
                                <!--<value><xsl:value-of select="$localAddress"/></value>-->
                            <!--</parameter>-->
                            <!--<parameter>-->
                                <!--<name>remotelPeer</name>-->
                                <!--<value><xsl:value-of select="$remoteAddress"/></value>-->
                            <!--</parameter>-->
                            <!--<parameter>-->
                                <!--<name>tunnelStatus</name>-->
                                <!--<value><xsl:value-of select="$status"/></value>-->
                            <!--</parameter>-->
                        <!--</parameters>-->

                    <!--</object>-->
                <!--</xsl:for-each>-->

        </DiscoveredDevice>
        <!--</network> -->
    </xsl:template>


    <xsl:variable name="bit8" select="256"/>
    <xsl:variable name="bit7" select="128"/>
    <xsl:variable name="bit6" select="64"/>
    <xsl:variable name="bit5" select="32"/>
    <xsl:variable name="bit4" select="16"/>
    <xsl:variable name="bit3" select="8"/>
    <xsl:variable name="bit2" select="4"/>
    <xsl:variable name="bit1" select="2"/>
    <xsl:variable name="bit0" select="1"/>
    <xsl:template name="ipv6AddrAnycastFlag">
        <xsl:param name="ipv6AddrAnycastFlag"/>
        <xsl:choose>
            <xsl:when test="$ipv6AddrAnycastFlag = '1'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="ipv6AddrType">
        <xsl:param name="type"/>
        <xsl:choose>
            <xsl:when test="$type='1'">stateless</xsl:when>
            <xsl:when test="$type='2'">statefull</xsl:when>
            <xsl:otherwise>unknown</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="adminStatus">
        <xsl:param name="status"/>
        <xsl:choose>
            <xsl:when test="$status='1'">UP</xsl:when>
            <xsl:when test="$status='2'">DOWN</xsl:when>
            <xsl:otherwise>TESTING</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="operStatus">
        <xsl:param name="status"/>
        <xsl:choose>
            <xsl:when test="$status='1'">UP</xsl:when>
            <xsl:when test="$status='2'">DOWN</xsl:when>
            <xsl:when test="$status='3'">TESTING</xsl:when>
            <xsl:when test="$status='4'">UNKNOWN</xsl:when>
            <xsl:when test="$status='5'">DORMANT</xsl:when>
            <xsl:when test="$status='6'">NOTPRESENT</xsl:when>
            <xsl:otherwise>LOWERLAYERDOWN</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="getNeighID">
        <xsl:param name="neighIP"/>
        <xsl:if test="$neighIP!=''">
            <xsl:variable name="temp">
                <xsl:call-template name="return-hostname">
                    <xsl:with-param name="hostname-unformated" select="SnmpForXslt:getName($neighIP, $neighbourIPDryRun)"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="$temp=''">
                    <xsl:value-of select="$neighIP"/>
                </xsl:when>
                <xsl:otherwise><xsl:value-of select="$temp"/></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
    <xsl:template name="substring-before-last">
        <xsl:param name="value"/>
        <xsl:param name="substring"/>
        <xsl:choose>
            <xsl:when test="contains($value,$substring)">
                <xsl:value-of select="substring-before($value,$substring)"/>
                <xsl:variable name="substring-after" select="substring-after($value,$substring)"/>
                <xsl:if test="contains($substring-after,$substring)">
                    <xsl:value-of select="$substring"/>
                </xsl:if>
                <xsl:call-template name="substring-before-last">
                    <xsl:with-param name="value" select="$substring-after"/>
                    <xsl:with-param name="substring" select="$substring"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="substring-before">
        <xsl:param name="value"/>
        <xsl:param name="substring"/>
        <xsl:choose>
            <xsl:when test="contains($value,$substring)">
                <xsl:variable name="substring-before" select="substring-before($value,$substring)"/>
                <xsl:value-of select="$substring-before"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$value"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="substring-after">
        <xsl:param name="value"/>
        <xsl:param name="substring"/>
        <xsl:choose>
            <xsl:when test="contains($value,$substring)">
                <xsl:variable name="substring-after" select="substring-after($value,$substring)"/>
                <xsl:value-of select="$substring-after"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$value"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="return-hostname">
        <xsl:param name="hostname-unformated"/>
        <xsl:call-template name="substring-before">
            <xsl:with-param name="substring">.</xsl:with-param>
            <xsl:with-param name="value">
                <xsl:value-of select="$hostname-unformated"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="Bit">
        <xsl:param name="decimal"/>
        <xsl:param name="bit" select="1"/>
        <xsl:choose>
            <xsl:when test="( $decimal mod ( $bit * 2 ) ) -
                      ( $decimal mod ( $bit     ) )">1</xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="decimal-to-binary">
        <xsl:param name="decimal" select="0"/>
        <xsl:variable name="binary-digit">
            <xsl:call-template name="Bit">
                <xsl:with-param name="decimal" select="$decimal"/>
                <xsl:with-param name="bit" select="$bit7"/>
            </xsl:call-template>
            <xsl:call-template name="Bit">
                <xsl:with-param name="decimal" select="$decimal"/>
                <xsl:with-param name="bit" select="$bit6"/>
            </xsl:call-template>
            <xsl:call-template name="Bit">
                <xsl:with-param name="decimal" select="$decimal"/>
                <xsl:with-param name="bit" select="$bit5"/>
            </xsl:call-template>
            <xsl:call-template name="Bit">
                <xsl:with-param name="decimal" select="$decimal"/>
                <xsl:with-param name="bit" select="$bit4"/>
            </xsl:call-template>
            <xsl:call-template name="Bit">
                <xsl:with-param name="decimal" select="$decimal"/>
                <xsl:with-param name="bit" select="$bit3"/>
            </xsl:call-template>
            <xsl:call-template name="Bit">
                <xsl:with-param name="decimal" select="$decimal"/>
                <xsl:with-param name="bit" select="$bit2"/>
            </xsl:call-template>
            <xsl:call-template name="Bit">
                <xsl:with-param name="decimal" select="$decimal"/>
                <xsl:with-param name="bit" select="$bit1"/>
            </xsl:call-template>
            <xsl:call-template name="Bit">
                <xsl:with-param name="decimal" select="$decimal"/>
                <xsl:with-param name="bit" select="$bit0"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="normalize-space($binary-digit)"/>
    </xsl:template>
    <xsl:template name="ip-to-binary">
        <xsl:param name="ip-address"/>
        <xsl:variable name="octet-1">
            <xsl:value-of select="substring-before($ip-address,'.')"/>
        </xsl:variable>
        <xsl:variable name="octet-2">
            <xsl:value-of select="substring-before(substring-after($ip-address,'.'),'.')"/>
        </xsl:variable>
        <xsl:variable name="octet-3">
            <xsl:value-of select="substring-before(substring-after(substring-after($ip-address,'.'),'.'),'.')"/>
        </xsl:variable>
        <xsl:variable name="octet-4">
            <xsl:value-of select="substring-after(substring-after(substring-after($ip-address,'.'),'.'),'.')"/>
        </xsl:variable>
        <xsl:call-template name="decimal-to-binary">
            <xsl:with-param name="decimal" select="$octet-1"/>
        </xsl:call-template>
        <xsl:call-template name="decimal-to-binary">
            <xsl:with-param name="decimal" select="$octet-2"/>
        </xsl:call-template>
        <xsl:call-template name="decimal-to-binary">
            <xsl:with-param name="decimal" select="$octet-3"/>
        </xsl:call-template>
        <xsl:call-template name="decimal-to-binary">
            <xsl:with-param name="decimal" select="$octet-4"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="get-ip-octet">
        <xsl:param name="binary"/>
        <xsl:variable name="ip-octet">
            <xsl:value-of select="number(substring($binary,1,1))*128+(number(substring($binary,2,1))*64)+(number(substring($binary,3,1))*32)+(number(substring($binary,4,1))*16)+(number(substring($binary,5,1))*8)+(number(substring($binary,6,1))*4)+(number(substring($binary,7,1))*2)+(number(substring($binary,8,1))*1)"/>
        </xsl:variable>
        <xsl:value-of select="$ip-octet"/>
    </xsl:template>
    <xsl:template name="binary-to-ip">
        <xsl:param name="binary-ip-address"/>
        <xsl:variable name="octet-1">
            <xsl:call-template name="get-ip-octet">
                <xsl:with-param name="binary" select="substring($binary-ip-address,1,8)"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="octet-2">
            <xsl:call-template name="get-ip-octet">
                <xsl:with-param name="binary" select="substring($binary-ip-address,9,8)"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="octet-3">
            <xsl:call-template name="get-ip-octet">
                <xsl:with-param name="binary" select="substring($binary-ip-address,17,8)"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="octet-4">
            <xsl:call-template name="get-ip-octet">
                <xsl:with-param name="binary" select="substring($binary-ip-address,25,8)"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="concat($octet-1, '.', $octet-2, '.', $octet-3, '.', $octet-4)"/>
    </xsl:template>
    <xsl:template name="subnet-to-bitcount">
        <xsl:param name="subnet"/>
        <xsl:variable name="subnet-ip">
            <xsl:call-template name="ip-to-binary">
                <xsl:with-param name="ip-address" select="$subnet"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="contains($subnet-ip,'0')">
                <xsl:value-of select="string-length(substring-before($subnet-ip,'0'))"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="string-length($subnet-ip)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="get-network-range">
        <xsl:param name="ip-address"/>
        <xsl:param name="subnet-mask"/>
        <xsl:variable name="zero-ip">00000000000000000000000000000000</xsl:variable>
        <xsl:variable name="bitcount">
            <xsl:call-template name="subnet-to-bitcount">
                <xsl:with-param name="subnet" select="$subnet-mask"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="binary-ip">
            <xsl:call-template name="ip-to-binary">
                <xsl:with-param name="ip-address" select="$ip-address"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="network-range-binary">
            <xsl:value-of select="concat(substring($binary-ip,1,$bitcount),substring($zero-ip,$bitcount + 1, string-length($zero-ip) - $bitcount))"/>
        </xsl:variable>
        <xsl:variable name="network-start-ip">
            <xsl:call-template name="binary-to-ip">
                <xsl:with-param name="binary-ip-address" select="$network-range-binary"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="concat($network-start-ip,'/',$bitcount)"/>
    </xsl:template>
    <xsl:template name="return-neighbor-params">
        <xsl:param name="neighborIP"/>
        <xsl:param name="neighborHostname"/>

        <!--Get Neighbor hostname and format it-->
        <parameter>
            <name>Neighbor IP Address</name>
            <value>
                <xsl:value-of select="$neighborIP"/>
            </value>
        </parameter>
        <parameter>
            <name>Neighbor hostname</name>
            <value>
                <xsl:value-of select="$neighborHostname"/>
            </value>
        </parameter>
        <!--Get Neighbor systemDescription-->

        <parameter>
            <name>Neighbor Device Type</name>
            <value><xsl:value-of select="SnmpForXslt:getDeviceType($neighborIP,$neighbourIPDryRun)"/></value>
        </parameter>
    </xsl:template>
    <xsl:template name="determine-device-Type">
        <xsl:param name="sysDescr"/>
        <xsl:param name="sysOr"/>

        <xsl:choose>
            <xsl:when test="contains($sysOr, '1.3.6.1.4.1.4526')">NETGEAR</xsl:when>
            <xsl:when test="contains($sysDescr, 'Cisco')">CISCO</xsl:when>
            <xsl:when test="contains($sysDescr, 'Linux')">LINUX</xsl:when>
            <xsl:when test="contains($sysDescr, 'Huawei')">HUAWEI</xsl:when>
            <xsl:when test="contains($sysDescr, 'Juniper')">JUNIPER</xsl:when>
            <xsl:when test="contains($sysDescr, 'Riverstone')">RIVERSTONE</xsl:when>
            <xsl:when test="contains($sysDescr, 'SevOne')">SEVONE</xsl:when>
            <xsl:when test="contains($sysDescr, 'Tellabs')">TELLABS</xsl:when>
            <xsl:when test="contains($sysDescr, 'ProCurve')">HP</xsl:when>
            <xsl:when test="contains($sysDescr, 'Windows')">WINDOWS</xsl:when>
            <xsl:otherwise>UNKNOWN</xsl:otherwise>
        </xsl:choose>
        <!--xsl:when test="$sysDescr!=''">UNKNOWN</xsl:when-->
    </xsl:template>
    <xsl:template name="determine-ifType">
        <xsl:param name="ifType"/>

        <xsl:choose>
            <xsl:when test="$ifType='1'">other</xsl:when>
            <xsl:when test="$ifType='6'">ethernetCsmacd</xsl:when>
            <xsl:when test="$ifType='23'">ppp</xsl:when>
            <xsl:when test="$ifType='24'">softwareLoopback</xsl:when>
            <xsl:when test="$ifType='39'">sonet</xsl:when>
            <xsl:when test="$ifType='32'">frameRelay</xsl:when>
            <xsl:when test="$ifType='135'">l2vlan</xsl:when>
            <xsl:when test="$ifType='136'">l3ipvlan</xsl:when>
            <xsl:when test="$ifType='142'">ipForward</xsl:when>
            <xsl:when test="$ifType='171'">pos</xsl:when>

            <xsl:when test="$ifType='150'">mplsTunnel</xsl:when>
            <xsl:when test="$ifType='166'">mpls</xsl:when>
            <xsl:otherwise>
                <xsl:variable name="oid">1.3.6.1.2.1.30.<xsl:value-of select="$ifType"/></xsl:variable>
                <xsl:value-of select="SnmpForXslt:getSymbolByOid('IANAifType-MIB', $oid)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="ipCidrProtocolResolver">
        <xsl:param name="number"/>
        <xsl:choose>
            <xsl:when test="$number='2'">LOCAL</xsl:when>
            <xsl:when test="$number='3'">STATIC_ROUTE</xsl:when>
            <xsl:when test="$number='4'">ICMP</xsl:when>
            <xsl:when test="$number='5'">EGP</xsl:when>
            <xsl:when test="$number='6'">GGP</xsl:when>
            <xsl:when test="$number='7'">HELLO</xsl:when>
            <xsl:when test="$number='8'">RIP</xsl:when>
            <xsl:when test="$number='9'">ISIS</xsl:when>
            <xsl:when test="$number='10'">ESLS</xsl:when>
            <xsl:when test="$number='11'">IGRP</xsl:when>
            <xsl:when test="$number='12'">BBN</xsl:when>
            <xsl:when test="$number='13'">OSPF</xsl:when>
            <xsl:when test="$number='14'">BGP</xsl:when>
            <xsl:when test="$number='15'">IDPR</xsl:when>
            <xsl:when test="$number='15'">EIGRP</xsl:when>
            <xsl:otherwise>OTHER</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="HexToDecimal">
        <xsl:param name="hexNumber"/>
        <xsl:param name="decimalNumber">0</xsl:param>
        <!-- If there is zero hex digits left, output-->
        <xsl:choose>
            <xsl:when test="$hexNumber">
                <xsl:call-template name="HexToDecimal">
                    <xsl:with-param name="decimalNumber" select="($decimalNumber*16)+number(substring-before(substring-after('00/11/22/33/44/55/66/77/88/99/A10/B11/C12/D13/E14/F15/a10/b11/c12/d13/e14/f15/',substring($hexNumber,1,1)),'/'))"/>
                    <xsl:with-param name="hexNumber" select="substring($hexNumber,2)"/>
                </xsl:call-template>
            </xsl:when>
            <!-- otherwise multiply, and add the next digit, and recurse -->
            <xsl:otherwise>
                <xsl:value-of select="$decimalNumber"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- If it begins with 0x then parse it for sure, else return it -->
    <xsl:template name="asDecimal">
        <xsl:param name="number"/>
        <xsl:choose>
            <xsl:when test="substring($number,1,2)='0x'">
                <xsl:call-template name="HexToDecimal">
                    <xsl:with-param name="hexNumber" select="substring($number,3)"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$number"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:function name="functx:substring-before-last-match" as="xs:string?" xmlns:functx="http://www.functx.com">
        <xsl:param name="arg" as="xs:string?"/>
        <xsl:param name="regex" as="xs:string"/>
        <xsl:sequence select="
   replace($arg,concat('^(.*)',$regex,'.*'),'$1')
 "/>
    </xsl:function>
    <xsl:function name="functx:substring-after-last-match" as="xs:string"
                  xmlns:functx="http://www.functx.com" >
        <xsl:param name="arg" as="xs:string?"/>
        <xsl:param name="regex" as="xs:string"/>

        <xsl:sequence select="
   replace($arg,concat('^.*',$regex),'')
 "/>

    </xsl:function>
    <xsl:function name="functx:decimal-to-hex" as="xs:string">
        <xsl:param name="decimalNumber"/>
        <xsl:variable name="hexDigits" select="'0123456789ABCDEF'"/>
        <xsl:variable name="upperDigits">
            <xsl:if test="$decimalNumber &gt;= 16">
                <xsl:sequence select="string-join(functx:decimal-to-hex(floor($decimalNumber div 16)), '')"/>
            </xsl:if>
        </xsl:variable>
        <xsl:sequence select="string-join(($upperDigits,substring($hexDigits, ($decimalNumber mod 16) + 1, 1)), '')"/>
    </xsl:function>
    <xsl:function name="functx:replace-multi" as="xs:string?"
                  xmlns:functx="http://www.functx.com" >
        <xsl:param name="arg" as="xs:string?"/>
        <xsl:param name="changeFrom" as="xs:string*"/>
        <xsl:param name="changeTo" as="xs:string*"/>

        <xsl:sequence select="
       if (count($changeFrom) > 0)
       then functx:replace-multi(
              replace($arg, $changeFrom[1],
                         functx:if-absent($changeTo[1],'')),
              $changeFrom[position() > 1],
              $changeTo[position() > 1])
       else $arg
     "/>

    </xsl:function>
    <xsl:function name="functx:if-absent" as="item()*"
                  xmlns:functx="http://www.functx.com" >
        <xsl:param name="arg" as="item()*"/>
        <xsl:param name="value" as="item()*"/>

        <xsl:sequence select="
        if (exists($arg))
        then $arg
        else $value
     "/>

    </xsl:function>

    <xsl:function name="functx:substring-before-if-contains" as="xs:string?"
                  xmlns:functx="http://www.functx.com">
        <xsl:param name="arg" as="xs:string?"/>
        <xsl:param name="delim" as="xs:string"/>

        <xsl:sequence select="
   if (contains($arg,$delim))
   then substring-before($arg,$delim)
   else $arg
 "/>

    </xsl:function>


    <xsl:template name="IPv6">
        <xsl:param name="ipAdEntAddr"/>
        <xsl:param name="ipv6AddrPfxLength"/>
        <xsl:param name="ipv6AddrType"/>
        <xsl:param name="ipv6AddrAnycastFlag"/>
        <xsl:param name="ipv6AddrStatus"/>
        <xsl:variable name="ipv6Addr" select="functx:substring-before-last-match($ipAdEntAddr,'.')"/>
        <xsl:variable name="ipv6Prefixtemp" select="concat($ipv6Addr,'/')"/>
        <xsl:variable name="ipv6Prefix" select="concat($ipv6Prefixtemp,$ipv6AddrPfxLength)"/>

        <object>
            <name>
                <xsl:value-of select="functx:substring-before-last-match($ipAdEntAddr,'.')"/>/<xsl:value-of select="$ipv6AddrPfxLength"/>
            </name>
            <objectType>IPv6 Address</objectType>
            <parameters>
                <parameter>
                    <name>IPv6Address</name>
                    <value>
                        <xsl:value-of select="$ipv6Addr"/>
                    </value>
                </parameter>
                <parameter>
                    <name>ipv6AddrPfxLength</name>
                    <value>
                        <xsl:value-of select="$ipv6AddrPfxLength"/>
                    </value>
                </parameter>
                <parameter>
                    <name>ipv6Subnet</name>
                    <value><xsl:value-of select="SnmpForXslt:getSubnetFromPrefix($ipv6Prefix)"/></value>
                </parameter>

                <parameter>
                    <name>ipv6AddrType</name>
                    <value>
                        <xsl:call-template name="ipv6AddrType">
                            <xsl:with-param name="type" select="$ipv6AddrType"/>
                        </xsl:call-template>
                    </value>
                </parameter>
                <parameter>
                    <name>ipv6AddrAnycastFlag</name>
                    <value>
                        <xsl:call-template name="ipv6AddrAnycastFlag">
                            <xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>
                        </xsl:call-template>
                    </value>
                </parameter>
            </parameters>
        </object>
    </xsl:template>
    <xsl:template name="CDP">
        <xsl:param name="cdpIfNeighbors"/>
        <xsl:for-each select="exslt:node-set($cdpIfNeighbors)">
            <xsl:variable name="cdpNeighbor">
                <xsl:call-template name="return-hostname">
                    <xsl:with-param name="hostname-unformated" select="cdpCacheDeviceId"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="cdpNeighborPort" select="cdpCacheDevicePort"/>
            <xsl:variable name="cdpNeighborPlatform" select="cdpCachePlatform"/>
            <xsl:variable name="cdpCachePrimaryMgmtAddrType" select="cdpCachePrimaryMgmtAddrType"/>
            <xsl:variable name="cdpCacheAddressType" select="cdpCacheAddressType"/>
            <xsl:variable name="cdpCacheAddress" select="cdpCacheAddress"/>

            <xsl:variable name="cdpCachePrimaryMgmtAddr" select="cdpCachePrimaryMgmtAddr"/>
            <xsl:variable name="neighborIP">
                <xsl:choose>
                    <xsl:when test="$cdpCachePrimaryMgmtAddrType='1'">
                        <xsl:variable name="temp">
                            <xsl:for-each select="tokenize($cdpCachePrimaryMgmtAddr,':')">
                                <xsl:call-template name="HexToDecimal">
                                    <xsl:with-param name="hexNumber">
                                        <xsl:value-of select="."/>
                                    </xsl:with-param>
                                </xsl:call-template>.</xsl:for-each>
                        </xsl:variable>
                        <xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="temp">
                            <xsl:for-each select="tokenize($cdpCacheAddress,':')">
                                <xsl:call-template name="HexToDecimal">
                                    <xsl:with-param name="hexNumber">
                                        <xsl:value-of select="."/>
                                    </xsl:with-param>
                                </xsl:call-template>.</xsl:for-each>

                        </xsl:variable>
                        <xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>

                    </xsl:otherwise>
                </xsl:choose>

            </xsl:variable>
            <xsl:variable name="neighID">
                <xsl:call-template name="getNeighID">
                    <xsl:with-param name="neighIP" select="$neighborIP"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:if test="$cdpNeighbor !=''">
                <object>
                    <name><xsl:value-of select="$cdpNeighbor"/></name>
                    <objectType>Discovered Neighbor</objectType>
                    <parameters>
                        <parameter>
                            <name>Reachable</name>
                            <value>
                                <xsl:choose>
                                    <xsl:when test="$neighID!='' and $neighID!=$neighborIP">YES</xsl:when>
                                    <xsl:otherwise>NO</xsl:otherwise>
                                </xsl:choose>
                            </value>
                        </parameter>

                        <parameter>
                            <name>Discovery Method</name>
                            <value>CDP</value>
                        </parameter>
                        <parameter>
                            <name>Neighbor Port</name>
                            <value>
                                <xsl:value-of select="$cdpNeighborPort"/>
                            </value>
                        </parameter>
                        <parameter>
                            <name>Neighbor Platform</name>
                            <value>
                                <xsl:value-of select="$cdpNeighborPlatform"/>
                            </value>
                        </parameter>
                        <parameter>
                            <name>Neighbor IP Address</name>
                            <value>
                                <xsl:value-of select="$neighborIP"/>
                            </value>
                        </parameter>
                        <parameter>
                            <name>Neighbor hostname</name>
                            <value>
                                <xsl:value-of select="$cdpNeighbor"/>
                            </value>
                        </parameter>
                        <parameter>
                            <name>Neighbor Device Type</name>
                            <value><xsl:value-of select="SnmpForXslt:getDeviceType($neighborIP, $neighbourIPDryRun)"/></value>
                        </parameter>
                    </parameters>
                </object>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="LLDP">
        <xsl:param name="lldpIfNeighbors"/>
        <xsl:variable name="lldpRemSysName" select="$lldpIfNeighbors/../lldpRemSysName"/>
        <xsl:variable name="lldpNeighbor-rough">
            <xsl:choose>
                <xsl:when test="$lldpRemSysName!=''">
                    <xsl:value-of select="$lldpRemSysName"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$lldpIfNeighbors/../lldpRemChassisId"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="lldpNeighbor">
            <xsl:call-template name="return-hostname">
                <xsl:with-param name="hostname-unformated" select="$lldpNeighbor-rough"/>
            </xsl:call-template>
        </xsl:variable>


        <xsl:variable name="neighID">
            <xsl:call-template name="getNeighID">
                <xsl:with-param name="neighIP" select="$lldpNeighbor-rough"/>
            </xsl:call-template>
        </xsl:variable>


        <xsl:variable name="lldpNeighborPort">
            <xsl:value-of select="$lldpIfNeighbors/../lldpRemPortId"/>
        </xsl:variable>
        <xsl:variable name="lldpNeighborPlatform">
            <xsl:value-of select="$lldpIfNeighbors/../lldpRemSysDesc"/>
        </xsl:variable>
        <xsl:if test="$lldpNeighbor !=''">
            <object>
                <name>
                    <xsl:value-of select="$lldpNeighbor"/>
                </name>
                <objectType>Discovered Neighbor</objectType>
                <parameters>
                    <parameter>
                        <name>Reachable</name>
                        <value>
                            <xsl:choose>
                                <xsl:when test="$neighID!='' and $neighID!=$lldpNeighbor-rough">YES</xsl:when>
                                <xsl:otherwise>NO</xsl:otherwise>
                            </xsl:choose>
                        </value>
                    </parameter>
                    <parameter>
                        <name>Discovery Method</name>
                        <value>LLDP</value>
                    </parameter>
                    <parameter>
                        <name>Neighbor Port</name>
                        <value>
                            <xsl:value-of select="$lldpNeighborPort"/>
                        </value>
                    </parameter>
                    <parameter>
                        <name>Neighbor Platform</name>
                        <value>
                            <xsl:value-of select="$lldpNeighborPlatform"/>
                        </value>
                    </parameter>
                    <xsl:call-template name="return-neighbor-params">
                        <xsl:with-param name="neighborIP"/>
                        <xsl:with-param name="neighborHostname" select="$lldpNeighbor"/>
                    </xsl:call-template>
                </parameters>
            </object>
        </xsl:if>
    </xsl:template>
    <xsl:template name="SLASH31">
        <xsl:param name="ipAdEntNetMask"/>
        <xsl:param name="ipAdEntAddr"/>
        <xsl:if test="$ipAdEntNetMask='255.255.255.254'">
            <xsl:variable name="firstOctets">
                <xsl:call-template name="substring-before-last">
                    <xsl:with-param name="value" select="$ipAdEntAddr"/>
                    <xsl:with-param name="substring">.</xsl:with-param>
                </xsl:call-template>
                <xsl:text>.</xsl:text>
            </xsl:variable>
            <!-- Calculate the other IP address on the Point to Point link -->
            <xsl:variable name="lastOctet" select="number(substring-after($ipAdEntAddr,$firstOctets))"/>
            <xsl:variable name="lastOctetSubnet" select="$lastOctet - ($lastOctet mod 2)"/>
            <xsl:variable name="plusOne" select="concat($firstOctets,number($lastOctetSubnet)+1)"/>
            <xsl:variable name="subnetIP" select="concat($firstOctets,number($lastOctetSubnet))"/>
            <xsl:variable name="otherIp">
                <xsl:choose>
                    <xsl:when test="$ipAdEntAddr = $plusOne">
                        <xsl:value-of select="$subnetIP"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$plusOne"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <object>
                <name><xsl:value-of select="$otherIp"/></name>
                <objectType>Discovered Neighbor</objectType>
                <parameters>

                    <parameter>
                        <name>Discovery Method</name>
                        <value>Slash31</value>
                    </parameter>
                    <parameter>
                        <name>Local IP address</name>
                        <value>
                            <xsl:value-of select="$ipAdEntAddr"/>
                        </value>
                    </parameter>
                    <xsl:call-template name="return-neighbor-params">
                        <xsl:with-param name="neighborIP" select="$otherIp"/>
                        <xsl:with-param name="neighborHostname" />
                    </xsl:call-template>
                </parameters>
            </object>
        </xsl:if>
    </xsl:template>
    <xsl:template name="SLASH30">
        <xsl:param name="ipAdEntNetMask"/>
        <xsl:param name="ipAdEntAddr"/>
        <xsl:if test="$ipAdEntNetMask='255.255.255.252'">
            <xsl:variable name="firstOctets">
                <xsl:call-template name="substring-before-last">
                    <xsl:with-param name="value" select="$ipAdEntAddr"/>
                    <xsl:with-param name="substring">.</xsl:with-param>
                </xsl:call-template>
                <xsl:text>.</xsl:text>
            </xsl:variable>
            <!-- Calculate the other IP address on the Point to Point link -->
            <xsl:variable name="lastOctet" select="number(substring-after($ipAdEntAddr,$firstOctets))"/>
            <xsl:variable name="lastOctetSubnet" select="$lastOctet - ($lastOctet mod 4)"/>
            <xsl:variable name="plusOne" select="concat($firstOctets,number($lastOctetSubnet)+1)"/>
            <xsl:variable name="plusTwo" select="concat($firstOctets,number($lastOctetSubnet)+2)"/>
            <xsl:variable name="otherIp">
                <xsl:choose>
                    <xsl:when test="$ipAdEntAddr = $plusOne">
                        <xsl:value-of select="$plusTwo"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$plusOne"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <!--Slash 30-->

            <object>
                <name><xsl:value-of select="$otherIp"/></name>
                <objectType>Discovered Neighbor</objectType>
                <parameters>

                    <parameter>
                        <name>Discovery Method</name>
                        <value>Slash30</value>
                    </parameter>
                    <parameter>
                        <name>Local IP address</name>
                        <value>
                            <xsl:value-of select="$ipAdEntAddr"/>
                        </value>
                    </parameter>
                    <xsl:message>DEBUG: SLASH30<xsl:value-of select="$otherIp"/>OTHER IP ADDRESS</xsl:message>

                    <xsl:call-template name="return-neighbor-params">
                        <xsl:with-param name="neighborIP" select="$otherIp"/>
                        <xsl:with-param name="neighborHostname"/>

                    </xsl:call-template>


                </parameters>
            </object>
        </xsl:if>
    </xsl:template>
    <xsl:template name="nextHop">
        <xsl:param name="ipRouteTable"/>
        <xsl:param name="sysName"/>
        <xsl:param name="ipv4addresses"/>
        <xsl:for-each select="distinct-values($ipRouteTable/ipRouteNextHop)">
            <xsl:variable name="next-hop-ip" select="."/>
            <xsl:if test="$next-hop-ip!='' and SnmpForXslt:checkBogons($next-hop-ip)=$next-hop-ip and count($ipv4addresses[ipAdEntAddr=$next-hop-ip])=0 and  count($ipv4addresses[ipv4SubnetBroadcast=$next-hop-ip]) = 0">

                <object>
                    <name><xsl:value-of select="$next-hop-ip"/></name>
                    <objectType>Discovered Neighbor</objectType>
                    <parameters>

                        <parameter>
                            <name>Discovery Method</name>
                            <xsl:variable name="test">
                                <xsl:for-each select="distinct-values($ipRouteTable/../ipRouteEntry[ipRouteNextHop = $next-hop-ip]/ipRouteProto)">
                                    <xsl:call-template name="ipCidrProtocolResolver">
                                        <xsl:with-param name="number">
                                            <xsl:value-of select="."/>
                                        </xsl:with-param>
                                    </xsl:call-template>
                                    <xsl:text>,</xsl:text>
                                </xsl:for-each>
                            </xsl:variable>
                            <value>r_<xsl:value-of select="functx:substring-before-last-match($test,'.')"/>
                            </value>
                        </parameter>
                        <xsl:call-template name="return-neighbor-params">
                            <xsl:with-param name="neighborIP" select="$next-hop-ip"/>
                            <xsl:with-param name="neighborHostname"/>
                        </xsl:call-template>
                    </parameters>
                </object>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="cnextHop">
        <xsl:param name="ipCidrRouteTable"/>
        <xsl:param name="sysName"/>
        <xsl:param name="ipv4addresses"/>
        <xsl:variable name="next-hop-ips" select="distinct-values($ipCidrRouteTable/ipCidrRouteNextHop)"/>
        <xsl:for-each select="$next-hop-ips">
            <xsl:variable name="next-hop-ip" select="."/>
            <xsl:if test="SnmpForXslt:checkBogons($next-hop-ip)=$next-hop-ip and count($ipv4addresses[ipAdEntAddr=$next-hop-ip])=0 and  count($ipv4addresses[ipv4SubnetBroadcast=$next-hop-ip]) = 0 ">




                <xsl:if test="not(contains($ipv4addresses,$next-hop-ip))">
                    <object>
                        <name><xsl:value-of select="$next-hop-ip"/></name>
                        <objectType>Discovered Neighbor</objectType>
                        <parameters>
                            <parameter>
                                <name>Discovery Method</name>
                                <xsl:variable name="test">
                                    <xsl:for-each select="distinct-values($ipCidrRouteTable/../ipCidrRouteEntry[ipCidrRouteNextHop = $next-hop-ip]/ipCidrRouteProto)">
                                        <xsl:call-template name="ipCidrProtocolResolver">
                                            <xsl:with-param name="number">
                                                <xsl:value-of select="."/>
                                            </xsl:with-param>
                                        </xsl:call-template>
                                        <xsl:text>,</xsl:text>
                                    </xsl:for-each>
                                </xsl:variable>
                                <value>c_<xsl:value-of select="functx:substring-before-last-match($test,'.')"/>
                                </value>
                            </parameter>
                            <xsl:call-template name="return-neighbor-params">
                                <xsl:with-param name="neighborIP" select="$next-hop-ip"/>
                                <xsl:with-param name="neighborHostname"/>
                            </xsl:call-template>
                        </parameters>
                    </object>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="ARP">
        <xsl:param name="ipNetToMediaIfNeighbors"/>
        <xsl:param name="sysName"/>
        <xsl:param name="ipv4addresses"/>
        <xsl:for-each select="$ipNetToMediaIfNeighbors">
            <xsl:variable name="ipNetToMediaNetAddress">
                <xsl:value-of select="ipNetToMediaNetAddress"/>
            </xsl:variable>
            <xsl:if test="$ipNetToMediaNetAddress">
                <xsl:if test="SnmpForXslt:checkBogons($ipNetToMediaNetAddress)=$ipNetToMediaNetAddress and count($ipv4addresses[ipAdEntAddr=$ipNetToMediaNetAddress])=0 and count($ipv4addresses[ipv4Subnet=$ipNetToMediaNetAddress]) = 0 and  count($ipv4addresses[ipv4SubnetBroadcast=$ipNetToMediaNetAddress]) = 0 ">

                    <!--<xsl:variable name="neighID">-->
                    <!--<xsl:call-template name="getNeighID">-->
                    <!--<xsl:with-param name="neighIP" select="$ipNetToMediaNetAddress"/>-->
                    <!--</xsl:call-template>-->
                    <!--</xsl:variable>-->
                    <xsl:message>DEBUG: ARP<xsl:value-of select="$ipNetToMediaNetAddress"/></xsl:message>
                    <xsl:if test="count($ipv4addresses[ipAdEntAddr=$ipNetToMediaNetAddress])=0">
                        <object>
                            <name><xsl:value-of select="$ipNetToMediaNetAddress"/></name>
                            <objectType>Discovered Neighbor</objectType>
                            <parameters>

                                <parameter>
                                    <name>Discovery Method</name>
                                    <value>ARP</value>
                                </parameter>
                                <parameter>
                                    <name>Neighbor MAC Address</name>
                                    <value>
                                        <xsl:value-of select="ipNetToMediaPhysAddress"/>
                                    </value>
                                </parameter><xsl:call-template name="return-neighbor-params">
                                <xsl:with-param name="neighborIP" select="$ipNetToMediaNetAddress"/>
                                <xsl:with-param name="neighborHostname" />
                            </xsl:call-template></parameters>
                        </object>
                    </xsl:if>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="MAC">
        <xsl:param name="neighborMACAddress"/>
        <xsl:param name="neighborIPAddress"/>
        <xsl:if test="$neighborMACAddress !=''">

            <!--<xsl:variable name="neighID">-->
            <!--<xsl:call-template name="getNeighID">-->
            <!--<xsl:with-param name="neighIP" select="$neighborIPAddress"/>-->
            <!--</xsl:call-template>-->
            <!--</xsl:variable>-->
            <object>
                <objectType>Discovered Neighbor</objectType>
                <xsl:choose>
                    <xsl:when test="$neighborIPAddress!=''">
                        <name><xsl:value-of select="$neighborIPAddress"/></name>
                        <parameters>

                            <parameter>
                                <name>Discovery Method</name>
                                <value>MAC</value>
                            </parameter>
                            <parameter>
                                <name>Neighbor MAC Address</name>
                                <value>
                                    <xsl:value-of select="$neighborMACAddress"/>
                                </value>
                            </parameter>
                            <xsl:call-template name="return-neighbor-params">
                                <xsl:with-param name="neighborIP" select="$neighborIPAddress"/>
                                <xsl:with-param name="neighborHostname" />
                            </xsl:call-template>
                        </parameters>
                    </xsl:when>
                    <xsl:otherwise>
                        <name><xsl:value-of select="$neighborMACAddress"/></name>
                        <parameters>
                            <parameter>
                                <name>Reachable</name>
                                <value>NO</value>
                            </parameter>
                            <parameter>
                                <name>Discovery Method</name>
                                <value>MAC</value>
                            </parameter>
                            <parameter>
                                <name>Neighbor MAC Address</name>
                                <value>
                                    <xsl:value-of select="$neighborMACAddress"/>
                                </value>
                            </parameter>
                            <parameter>
                                <name>Neighbor IP Address</name>
                                <value/>
                            </parameter>
                            <parameter>
                                <name>Neighbor Hostname</name>
                                <value/>
                            </parameter>
                            <parameter>
                                <name>Neighbor Device Type</name>
                                <value>UNKNOWN</value>
                            </parameter>
                        </parameters>
                    </xsl:otherwise>
                </xsl:choose>
            </object>
        </xsl:if>
    </xsl:template>
    <xsl:template name="OSPF">
        <xsl:param name="ospfNbr"/>
        <xsl:variable name="ospfNbrIpAddr">
            <xsl:value-of select="$ospfNbr/ospfNbrIpAddr"/>
        </xsl:variable>
        <xsl:if test="$ospfNbrIpAddr!=''">
            <!--<xsl:variable name="neighID">-->
            <!--<xsl:call-template name="getNeighID">-->
            <!--<xsl:with-param name="neighIP" select="$ospfNbrIpAddr"/>-->
            <!--</xsl:call-template>-->
            <!--</xsl:variable>-->


            <object>

                <name><xsl:value-of select="$ospfNbrIpAddr"/></name>
                <objectType>Discovered Neighbor</objectType>
                <parameters>

                    <parameter>
                        <name>Local IP address</name>
                        <value><xsl:value-of select="$ospfNbr/ospfIfIpAddress"/></value>
                    </parameter>
                    <parameter>
                        <name>OSPF Area</name>
                        <value><xsl:value-of select="$ospfNbr/ospfIfAreaId"/></value>
                    </parameter>
                    <parameter>
                        <name>Discovery Method</name>
                        <value>OSPF</value>
                    </parameter>
                    <xsl:call-template name="return-neighbor-params">
                        <xsl:with-param name="neighborIP" select="$ospfNbrIpAddr"/>
                        <xsl:with-param name="neighborHostname"/>
                    </xsl:call-template>
                </parameters>
            </object>
        </xsl:if>
    </xsl:template>
    <xsl:template name="BGP" match="bgpPeerEntry">
        <xsl:param name="bgpPeer"/>
        <xsl:variable name="bgpPeerRemoteAddr">
            <xsl:value-of select="$bgpPeer/bgpPeerRemoteAddr"/>
        </xsl:variable>
        <xsl:if test="$bgpPeerRemoteAddr!=''">

            <object>
                <name><xsl:value-of select="$bgpPeerRemoteAddr"/></name>
                <objectType>Discovered Neighbor</objectType>
                <parameters>

                    <parameter>
                        <name>Discovery Method</name>
                        <value>BGP</value>
                    </parameter>
                    <parameter>
                        <name>bgpPeerRemoteAs</name>
                        <value>
                            <xsl:value-of select="$bgpPeer/bgpPeerRemoteAs"/>
                        </value>
                    </parameter>
                    <parameter>
                        <name>bgpPeerState</name>
                        <value>
                            <xsl:value-of select="$bgpPeer/bgpPeerState"/>
                        </value>
                    </parameter>
                    <parameter>
                        <name>Local IP address</name>
                        <value><xsl:value-of select="$bgpPeer/bgpPeerLocalAddr"/></value>
                    </parameter>
                    <parameter>
                        <name>bgpPeerAdminStatus</name>
                        <value>
                            <xsl:value-of select="$bgpPeer/bgpPeerAdminStatus"/>
                        </value>
                    </parameter><xsl:call-template name="return-neighbor-params">
                    <xsl:with-param name="neighborIP" select="$bgpPeerRemoteAddr"/>
                    <xsl:with-param name="neighborHostname" />
                </xsl:call-template></parameters>

            </object>
        </xsl:if>
    </xsl:template>

    <xsl:template name="IPSEC-Phase2">
        <xsl:param name="cipSecTunnelTable"/>
        <xsl:param name="sysName"/>
        <xsl:param name="ipv4addresses"/>
        <xsl:for-each select="$cipSecTunnelTable/cipSecTunnelEntry">
            <xsl:copy-of select="."/>

            <xsl:variable name="localAddress"><xsl:variable name="temp"><xsl:for-each select="tokenize(cipSecTunLocalAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>
            </xsl:variable><xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>
            </xsl:variable>
            <xsl:variable name="next-hop-ip"><xsl:variable name="temp"><xsl:for-each select="tokenize(cipSecTunRemoteAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>
            </xsl:variable>
                <xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>
            </xsl:variable>

            <xsl:variable name="status">
                <xsl:choose>
                    <xsl:when test="cipSecTunIkeTunnelAlive = '1'">UP</xsl:when>
                    <xsl:otherwise>DOWN</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:if test="SnmpForXslt:checkBogons($next-hop-ip)=$next-hop-ip and count($ipv4addresses/ipAddrEntry[ipAdEntAddr=$next-hop-ip])=0 and  count($ipv4addresses/ipAddrEntry[ipv4SubnetBroadcast=$next-hop-ip]) = 0">

                <!--<xsl:variable name="neighID">-->
                <!--<xsl:call-template name="getNeighID">-->
                <!--<xsl:with-param name="neighIP" select="$next-hop-ip"/>-->
                <!--</xsl:call-template>-->
                <!--</xsl:variable>     -->

                <xsl:if test="$next-hop-ip!=$sysName">
                    <object>
                        <name><xsl:value-of select="$next-hop-ip"/></name>
                        <objectType>Discovered Neighbor</objectType>
                        <parameters>

                            <parameter>
                                <name>Discovery Method</name>
                                <value>IPSEC-Phase2</value>
                            </parameter>
                            <xsl:call-template name="return-neighbor-params">
                                <xsl:with-param name="neighborIP" select="$next-hop-ip"/>
                                <xsl:with-param name="neighborHostname" />
                            </xsl:call-template>
                        </parameters>
                        <parameter>
                            <name>Local IP address</name>
                            <value><xsl:value-of select="$localAddress"/></value>
                        </parameter>
                        <parameter>
                            <name>Tunnel Status</name>
                            <value><xsl:value-of select="$status"/></value>
                        </parameter>
                    </object>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>

    </xsl:template>


    <xsl:template name="IPSEC-Phase1">
        <xsl:param name="cikeTunnelTable"/>
        <xsl:param name="sysName"/>
        <xsl:param name="ipv4addresses"/>
        <xsl:for-each select="$cikeTunnelTable/cikeTunnelEntry">
            <xsl:copy-of select="."/>

            <xsl:variable name="localAddress"><xsl:variable name="temp"><xsl:for-each select="tokenize(cikeTunLocalAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>
            </xsl:variable><xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>
            </xsl:variable>
            <xsl:variable name="next-hop-ip"><xsl:variable name="temp"><xsl:for-each select="tokenize(cikeTunRemoteAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>
            </xsl:variable>
                <xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>
            </xsl:variable>

            <xsl:variable name="status">
                <xsl:choose>
                    <xsl:when test="cikeTunStatus = '1'">UP</xsl:when>
                    <xsl:otherwise>DOWN</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:if test="SnmpForXslt:checkBogons($next-hop-ip)=$next-hop-ip and count($ipv4addresses/ipAddrEntry[ipAdEntAddr=$next-hop-ip])=0 and  count($ipv4addresses/ipAddrEntry[ipv4SubnetBroadcast=$next-hop-ip]) = 0">

                <xsl:variable name="neighID">
                    <xsl:call-template name="getNeighID">
                        <xsl:with-param name="neighIP" select="$next-hop-ip"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:if test="$neighID!=$sysName">
                    <object>
                        <name><xsl:value-of select="$neighID"/></name>
                        <objectType>Discovered Neighbor</objectType>
                        <parameters>
                            <parameter>
                                <name>Discovery Method</name>
                                <value>IPSEC-Phase1</value>
                            </parameter>
                            <xsl:call-template name="return-neighbor-params">
                                <xsl:with-param name="neighborIP" select="$next-hop-ip"/>
                                <xsl:with-param name="neighborHostname" />
                            </xsl:call-template>
                        </parameters>
                        <parameter>
                            <name>Local IP address</name>
                            <value><xsl:value-of select="$localAddress"/></value>
                        </parameter>
                        <parameter>
                            <name>Tunnel Status</name>
                            <value><xsl:value-of select="$status"/></value>
                        </parameter>
                    </object>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>

    </xsl:template>

    <xsl:template name="interfaceParameters">
        <xsl:param name="ifDescr"/>
        <xsl:param name="ifIndex"/>
        <xsl:param name="ifName"/>
        <xsl:param name="ifType"/>
        <xsl:param name="ifSped"/>
        <xsl:param name="ifAdminStatus"/>
        <xsl:param name="ifOperStatus"/>
        <xsl:param name="IPv4Forwarding"/>
        <xsl:param name="IPv6Forwarding"/>
        <xsl:param name="vrfForwarding"/>
        <xsl:param name="ifPhysicalAddress"/>

        <xsl:variable name="cableCut">
            <xsl:choose>
                <xsl:when test="$ifAdminStatus='UP' and $ifOperStatus='UP'">NO</xsl:when>
                <xsl:when test="$ifAdminStatus='YES' and $ifOperStatus='DOWN'">YES</xsl:when>
                <xsl:when test="$ifAdminStatus='DOWN' and $ifOperStatus='DOWN'">NO</xsl:when>
                <xsl:otherwise>UNKNOWN</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <parameters>
            <parameter>
                <name>ifIndex</name>
                <value>
                    <xsl:value-of select="$ifIndex"/>
                </value>
            </parameter>
            <parameter>
                <name>ifDescr</name>
                <value>
                    <xsl:value-of select="$ifDescr"/>
                </value>
            </parameter>
            <parameter>
                <name>ifName</name>
                <value>
                    <xsl:value-of select="$ifName"/>
                </value>
            </parameter>
            <parameter>
                <name>ifType</name>
                <value>
                    <xsl:call-template name="determine-ifType">
                        <xsl:with-param name="ifType" select="$ifType"/>
                    </xsl:call-template>
                </value>
            </parameter>
            <parameter>
                <name>ifSpeed</name>
                <value><xsl:value-of select="$ifSped"/></value>
            </parameter>
            <parameter>
                <name>ifAdminStatus</name>
                <value>
                    <xsl:value-of select="$ifAdminStatus"/>
                </value>
            </parameter>
            <parameter>
                <name>ifOperStatus</name>
                <value>
                    <xsl:value-of select="$ifOperStatus"/>
                </value>
            </parameter>
            <parameter>
                <name>ifPhysAddress</name>
                <value>
                    <xsl:value-of select="$ifPhysicalAddress"/>
                </value>
            </parameter>
            <parameter>
                <name>CableCut</name>
                <value><xsl:value-of select="$cableCut"/></value>
            </parameter>
            <parameter>
                <name>IPv4Forwarding</name>
                <value>
                    <xsl:value-of select="$IPv4Forwarding"/>
                </value>
            </parameter>
            <parameter>
                <name>IPv6Forwarding</name>
                <value>
                    <xsl:value-of select="$IPv6Forwarding"/>
                </value>
            </parameter>
            <parameter>
                <name>vrfForwarding</name>
                <value>
                    <xsl:value-of select="$vrfForwarding"/>
                </value>
            </parameter>
        </parameters>
    </xsl:template>

</xsl:stylesheet>
