<?xml version="1.0" encoding="UTF-8"?>


<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" cdata-section-elements="nodeInfo"/>
    <!--xsl:include href="utils.xslt"/-->
    <xsl:template match="/">
        <graphml>
            <graph edgedefault="undirected">
                <!-- data schema -->
                <key id="hostname" for="node" attr.name="hostname" attr.type="string"/>
                <key id="deviceModel" for="node" attr.name="deviceModel" attr.type="string"/>
                <key id="deviceType" for="node" attr.name="deviceType" attr.type="string"/>
                <key id="nodeInfo" for="node" attr.name="nodeInfo" attr.type="string"/>
                <key id="deviceStatus" for="node" attr.name="deviceStatus" attr.type="string"/>
                <key id="ManagementIPAddress" for="node" attr.name="ManagementIPAddress" attr.type="string"/>
                <key id="BGPLocalASInfo" for="node" attr.name="BGPLocalASInfo" attr.type="string"/>
                <key id="ipv6Forwarding" for="node" attr.name="ipv6Forwarding" attr.type="string"/>
                <key id="ipv6Forwarding" for="edge" attr.name="ipv6Forwarding" attr.type="string"/>
                <key id="site" for="node" attr.name="site" attr.type="string"/>
                <key id="name" for="edge" attr.name="name" attr.type="string"/>
                <key id="method" for="edge" attr.name="method" attr.type="string"/>
                <key id="BGPRemoteASInfo" for="edge" attr.name="BGPRemoteASInfo" attr.type="string"/>
                <key id="OSPFAreaInfo" for="edge" attr.name="OSPFAreaInfo" attr.type="string"/>
                <key id="physical" for="edge" attr.name="physical" attr.type="string"/>
                <key id="color" for="edge" attr.name="color" attr.type="string"/>
                <key id="diff" for="node" attr.name="diff" attr.type="string"/>
                <key id="diff" for="edge" attr.name="diff" attr.type="string"/>
                <key id="diffs" for="node" attr.name="diffs" attr.type="string"/>
                <key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>
                <xsl:variable name="nodeID">
                    <xsl:value-of select="/DiscoveredDevice/name"/>
                </xsl:variable>
                <xsl:variable name="root" select="/DiscoveredDevice"/>
                <xsl:variable name="deviceModel">
                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='Device Model']/value"/>
                </xsl:variable>
                <xsl:variable name="deviceType">
                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='Device Type']/value"/>
                </xsl:variable>
                <xsl:variable name="deviceStatus" select="//DiscoveredDevice/parameters/parameter[name='Device State']/value"/>
                <xsl:variable name="siteID">
                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='siteID']/value"/>
                </xsl:variable>
                <xsl:variable name="BGPLocalASInfo">
                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='BGPLocalASInfo']/value"/>
                </xsl:variable>
                <xsl:variable name="ManagementIPAddress">
                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='Management IP Address']/value"/>
                </xsl:variable>
                <xsl:variable name="ipv6Forwarding" select="//DiscoveredDevice/parameters/parameter[name='ipv6Forwarding']/value"/>

                <node>
                    <xsl:attribute name="id"><xsl:value-of select="$nodeID"/></xsl:attribute>
                    <data key="hostname">
                        <xsl:value-of select="$nodeID"/>
                    </data>
                    <data key="deviceModel">
                        <xsl:value-of select="$deviceModel"/>
                    </data>
                    <data key="deviceType">
                        <xsl:value-of select="$deviceType"/>
                    </data>
                    <data key="deviceStatus">
                        <xsl:value-of select="$deviceStatus"/>
                    </data>
                    <data key="BGPLocalASInfo">
                        <xsl:value-of select="$BGPLocalASInfo"/>
                    </data>
                    <data key="ipv6Forwarding">
                        <xsl:value-of select="$ipv6Forwarding"/>
                    </data>
                    <data key="ManagementIPAddress">
                        <xsl:value-of select="$ManagementIPAddress"/>
                    </data>
                    <data key="site">
                        <xsl:value-of select="$siteID"/>
                    </data>

                </node>
                <!--Prepare Edges and remote nodes-->
                <xsl:for-each select="distinct-values(//object[objectType='Discovered Neighbor']/name)">
                    <xsl:variable name="neighID">
                        <xsl:value-of select="."/>
                    </xsl:variable>
                    <xsl:if test="$neighID !=$nodeID and $neighID!='' and not(contains($neighID,'Unknown'))">
                        <node>
                            <xsl:attribute name="id"><xsl:value-of select="$neighID"/></xsl:attribute>
                        </node>
                        <edge>
                            <xsl:attribute name="id"><xsl:value-of select="$nodeID"/><xsl:text> </xsl:text><xsl:value-of select="$neighID"/></xsl:attribute>
                            <xsl:attribute name="source"><xsl:value-of select="$nodeID"/></xsl:attribute>
                            <xsl:attribute name="target"><xsl:value-of select="$neighID"/></xsl:attribute>
                            <xsl:variable name="methods">
                                <xsl:for-each select="$root//object[objectType='Discovered Neighbor' and name=$neighID]/parameters/parameter[name='Discovery Method']">
                                    <xsl:value-of select="value"/>
                                    <xsl:text>,</xsl:text>
                                </xsl:for-each>
                            </xsl:variable>
                            <data>
                                <xsl:attribute name="key">method</xsl:attribute>
                                <xsl:value-of select="$methods"/>
                            </data>
                            <data>

                            </data>
                            <xsl:if test="contains($methods,'BGP')">
                                <data>
                                    <xsl:attribute name="key">BGPRemoteASInfo</xsl:attribute>
                                    <xsl:value-of select="$root//object[objectType='Discovered Neighbor' and name=$neighID]/parameters/parameter[name='Discovery Method' and value='BGP']/../parameter[name='bgpPeerRemoteAs']/value"/>
                                </data>
                            </xsl:if>
                            <xsl:if test="contains($methods,'OSPF')">
                                <data>
                                    <xsl:attribute name="key">OSPFAreaInfo</xsl:attribute>
                                    <!--xsl:value-of select="$root//object[objectType='Discovered Neighbor' and name=$neighID]/parameters/parameter[name='Discovery Method' and value='OSPF']/../parameter[name='ospfAreaID']/value"/-->AREA UNKNOWN</data>
                            </xsl:if>
                        </edge>
                    </xsl:if>
                </xsl:for-each>
            </graph>
        </graphml>
    </xsl:template>
</xsl:stylesheet>