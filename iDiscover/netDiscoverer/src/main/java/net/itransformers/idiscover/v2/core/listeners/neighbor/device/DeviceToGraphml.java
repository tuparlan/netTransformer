package net.itransformers.idiscover.v2.core.listeners.neighbor.device;

import net.itransformers.idiscover.api.models.graphml.GraphmlEdge;
import net.itransformers.idiscover.api.models.graphml.GraphmlNode;
import net.itransformers.idiscover.api.models.network.Node;
import net.itransformers.idiscover.core.Subnet;
import net.itransformers.idiscover.networkmodelv2.DeviceNeighbour;
import net.itransformers.idiscover.networkmodelv2.DiscoveredDevice;
import net.itransformers.idiscover.networkmodelv2.DiscoveredInterface;
import net.itransformers.idiscover.v2.core.listeners.neighbor.EdgeIdGenerator;
import org.apache.log4j.Logger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by niau on 9/2/16.
 */
public class DeviceToGraphml {
    static Logger logger = Logger.getLogger(DeviceToGraphml.class);

    DiscoveredDevice device;
    Node node;

    public DeviceToGraphml(Node node, DiscoveredDevice device) {
        this.device = device;
        this.node = node;
    }


    public List<GraphmlNode> getSubnetNodes(){
        Set<Subnet> subnetSet =  device.getDeviceSubnetsFromActiveInterfaces();
        List<GraphmlNode> graphmlSubnetNodes     = new ArrayList<>();
        for (Subnet subnet : subnetSet) {
            GraphmlNode subnetNode = new GraphmlNode(subnet.getName());
            subnetNode.setLabel(subnet.getName());
            subnetNode.setGraphmlNodeData(getGraphmlSubnetNodeMetaData(subnet));
            graphmlSubnetNodes.add(subnetNode);
        }
        return graphmlSubnetNodes;
    }

    public List<GraphmlNode> getNonSubnetNeighbours(){
        List<GraphmlNode> neighbourNodes     = new ArrayList<>();

        List<DeviceNeighbour> deviceNeighbours =  device.getDeviceNeighbours();
        for (DeviceNeighbour deviceNeighbour : deviceNeighbours) {


//            String neighbourName = deviceNeighbour.getId();
            String neighbourIpAddress = deviceNeighbour.getIpAddress();
            String neighbourHostName = deviceNeighbour.getNeighbourHostName();
            String neighbourMac = deviceNeighbour.getNeighbourMac();

            AliasResolver aliasResolver = new AliasResolver(node,neighbourHostName, neighbourIpAddress, neighbourMac);
            String neighbourId = aliasResolver.getNeighbourIdFromAliases();

            if (neighbourId==null) {
                logger.info("Can't find neighbour id for: " + deviceNeighbour);
                continue;
            }

            GraphmlNode graphmlNode = new GraphmlNode(neighbourId, neighbourId);

            neighbourNodes.add(graphmlNode);


        }

        return neighbourNodes;
    }




    public List<GraphmlEdge> getSubnetEdgesToMainNode(){
        List <GraphmlEdge> graphmlEdges = new ArrayList<>();

        Set<Subnet> subnetSet =  device.getDeviceSubnetsFromActiveInterfaces();
        for (Subnet subnet : subnetSet) {
            //TODO this has to create edges with id from the local ip address and the subnet.
            EdgeIdGenerator edgeIdGenerator = new EdgeIdGenerator(device.getName(), subnet.getName(),device.getName(), subnet.getName());

            GraphmlEdge graphmlEdge = edgeIdGenerator.createEdge();
            graphmlEdge.setGraphmlEdgeData(getGraphmlSubnetEdgeMetaData(subnet));
            graphmlEdges.add(graphmlEdge);

        }
        return graphmlEdges;

    }


    public List<GraphmlEdge> getEdgesToNeighbours(){
        List <GraphmlEdge> graphmlEdges = new ArrayList<>();

        Set<Subnet> subnetSet =  device.getDeviceSubnetsFromActiveInterfaces();
        //List<DeviceNeighbour> deviceNeighbours = device.getDeviceNeighbours();


        for (DiscoveredInterface devInterface : device.getInterfaceList()) {
            String localMac = devInterface.getParams().get("ifPhysAddress");


//            List<DiscoveredIPv4Address> discoveredIPv4Addresses = devInterface.getiPv4AddressList();


            for (DeviceNeighbour deviceNeighbour : devInterface.getNeighbours()) {


                String neighbourIpAddress = deviceNeighbour.getNeighbourIpAddress();
                String neighbourHostName = deviceNeighbour.getNeighbourHostName();
                String neighbourMac = deviceNeighbour.getNeighbourMac();

                AliasResolver aliasResolver = new AliasResolver(node,neighbourHostName, neighbourIpAddress, neighbourMac);
                String neighbourId = aliasResolver.getNeighbourIdFromAliases();


                if (neighbourId == null)
                    continue;

                boolean neighbourInSubnet = false;

                if (neighbourIpAddress != null && !neighbourIpAddress.isEmpty()) {
                    for (Subnet subnet : subnetSet) {
                        if (subnet.contains(neighbourIpAddress)) {
                            EdgeIdGenerator edgeIdGenerator = new EdgeIdGenerator(neighbourId, subnet.getName(), neighbourIpAddress, subnet.getIpAddress());

                            GraphmlEdge graphmlEdge = edgeIdGenerator.createEdge();
                            graphmlEdge.setGraphmlEdgeData(getGraphmlDirectNeighbourEdgeMetaData(deviceNeighbour));

                            boolean edgeAlreadyDefined = false;
                            for (GraphmlEdge edge : graphmlEdges) {
                                if (edge.getId().equals(graphmlEdge.getId())) {
                                    edgeAlreadyDefined = true;
                                    int index = graphmlEdges.indexOf(edge);
                                    logger.info(graphmlEdge + "already exists");


                                    Map<String, String> existingGraphmlEdgeDatas = edge.getGraphmlEdgeData();
                                    Map<String, String> newGraphmlEdgeDatas = graphmlEdge.getGraphmlEdgeData();
                                    edge.setGraphmlEdgeData(combineEdgeMetaDatas(existingGraphmlEdgeDatas, newGraphmlEdgeDatas));


                                    graphmlEdges.set(index, edge);

                                }
                            }
                            if (!edgeAlreadyDefined) {
                                graphmlEdges.add(graphmlEdge);
                            }


                            neighbourInSubnet = true;
                            break;
                        }
                    }
                }

                if (!neighbourInSubnet) {
                    EdgeIdGenerator edgeIdGenerator = new EdgeIdGenerator(neighbourId, node.getId(), neighbourId, node.getId(), localMac, neighbourMac);

                    GraphmlEdge graphmlEdge = edgeIdGenerator.createEdge();
                    graphmlEdge.setGraphmlEdgeData(getGraphmlDirectNeighbourEdgeMetaData(deviceNeighbour));

                    boolean edgeAlreadyDefined = false;
                    for (GraphmlEdge edge : graphmlEdges) {
                        if (edge.getId().equals(graphmlEdge.getId())) {
                            edgeAlreadyDefined = true;
                            int index = graphmlEdges.indexOf(edge);
                            logger.info(graphmlEdge + "already exists");


                            Map<String, String> existingGraphmlEdgeDatas = edge.getGraphmlEdgeData();
                            Map<String, String> newGraphmlEdgeDatas = graphmlEdge.getGraphmlEdgeData();


                            edge.setGraphmlEdgeData(combineEdgeMetaDatas(existingGraphmlEdgeDatas, newGraphmlEdgeDatas));

                            graphmlEdges.set(index, edge);

                        }
                    }
                    if (!edgeAlreadyDefined) {
                        graphmlEdges.add(graphmlEdge);
                    }

                }

            }
        }
            //Handle logicalDataNeighbours

            for (DeviceNeighbour deviceNeighbour : device.getLogicalDeviceData().getDeviceNeighbourList()) {

                String neighbourIpAddress = deviceNeighbour.getNeighbourIpAddress();
                String neighbourHostName = deviceNeighbour.getNeighbourHostName();
                String neighbourMac = deviceNeighbour.getNeighbourMac();

                AliasResolver aliasResolver = new AliasResolver(node,neighbourHostName, neighbourIpAddress, neighbourMac);
                String neighbourId = aliasResolver.getNeighbourIdFromAliases();


                if (neighbourId==null)
                    continue;

                boolean neighbourInSubnet = false;


                if(neighbourIpAddress!=null&& !neighbourIpAddress.isEmpty()) {
                    for (Subnet subnet : subnetSet) {
                        if (subnet.contains(neighbourIpAddress)) {
                            EdgeIdGenerator edgeIdGenerator = new EdgeIdGenerator(neighbourId, subnet.getName(),neighbourIpAddress,subnet.getIpAddress());

                            GraphmlEdge graphmlEdge = edgeIdGenerator.createEdge();
                            graphmlEdge.setGraphmlEdgeData(getGraphmlDirectNeighbourEdgeMetaData(deviceNeighbour));

                            boolean edgeAlreadyDefined = false;
                            for (GraphmlEdge edge : graphmlEdges){
                                if (edge.getId().equals(graphmlEdge.getId())){
                                    edgeAlreadyDefined = true;
                                    int index = graphmlEdges.indexOf(edge);
                                    logger.info (graphmlEdge +"already exists" );

                                    Map<String, String> existingGraphmlEdgeDatas = edge.getGraphmlEdgeData();
                                    Map<String, String> newGraphmlEdgeDatas = graphmlEdge.getGraphmlEdgeData();

                                    edge.setGraphmlEdgeData(combineEdgeMetaDatas(existingGraphmlEdgeDatas,newGraphmlEdgeDatas));

                                    graphmlEdges.set(index,edge);

                                }
                            }
                            if (!edgeAlreadyDefined) {
                                graphmlEdges.add(graphmlEdge);
                            }


                            neighbourInSubnet = true;
                            break;
                        }

                    }
                }

                if (!neighbourInSubnet){

                    EdgeIdGenerator edgeIdGenerator = new EdgeIdGenerator(neighbourId, node.getId(),neighbourId,node.getId());
                    GraphmlEdge graphmlEdge = edgeIdGenerator.createEdge();
                    graphmlEdge.setGraphmlEdgeData(getGraphmlDirectNeighbourEdgeMetaData(deviceNeighbour));

                    boolean edgeAlreadyDefined = false;
                    for (GraphmlEdge edge : graphmlEdges) {
                        if (edge.getId().equals(graphmlEdge.getId())) {
                            edgeAlreadyDefined = true;
                            int index = graphmlEdges.indexOf(edge);
                            logger.info(graphmlEdge + "already exists");
                            edge.setGraphmlEdgeData(combineEdgeMetaDatas(edge.getGraphmlEdgeData(),graphmlEdge.getGraphmlEdgeData()));
                            graphmlEdges.set(index, edge);

                        }
                    }
                    if (!edgeAlreadyDefined) {
                        graphmlEdges.add(graphmlEdge);
                    }
                }
            }


        return graphmlEdges;

    }

    private Map<String, String> combineEdgeMetaDatas(Map<String, String> existingGraphmlEdgeDatas,Map<String, String> newGraphmlEdgeDatas) {

        HashMap<String, String> combined = new HashMap<String, String>();

        for (Map.Entry<String, String> existingGraphmlEdgeData : existingGraphmlEdgeDatas.entrySet()) {
            String key = existingGraphmlEdgeData.getKey();

            String value = existingGraphmlEdgeData.getValue();
            if (combined.get(key) == null) {
                combined.put(key, value);

            }
        }

        for (Map.Entry<String, String>  newGraphmlEdgeData: newGraphmlEdgeDatas.entrySet()) {
            String key = newGraphmlEdgeData.getKey();

            String value =  newGraphmlEdgeData.getValue();

            if (combined.get(key) == null) {
                combined.put(key, value);
            } else{
                String oldValue = combined.get(key);

                if (oldValue.equals(value))
                    continue;

                StringTokenizer oldValueTockens = new StringTokenizer(oldValue,",");

                Set<String> combinedSet = new HashSet<>();

                while(oldValueTockens.hasMoreTokens()){
                    String token = oldValueTockens.nextToken();
                    combinedSet.add(token);
                }

                StringTokenizer newValueTockens = new StringTokenizer(value,",");

                while(newValueTockens.hasMoreTokens()){
                    String token = newValueTockens.nextToken();
                    combinedSet.add(token);
                }

                StringBuilder finalString = new StringBuilder();
                for(String s: combinedSet){
                    finalString.append(s).append(",");
                }


                if (!value.equals(oldValue))
                     combined.put(newGraphmlEdgeData.getKey(),finalString.toString());
             }
            }


        Map<String, String> finalDatas = new HashMap<>();

        for (Map.Entry<String,String> entry: combined.entrySet()){
            finalDatas.put(entry.getKey(),entry.getValue());
        }

        return finalDatas;
    }




    private Map<String, String> getGraphmlDirectNeighbourEdgeMetaData(DeviceNeighbour neighbour ) {

        Map<String, String> graphmlEdgeDatas = new HashMap<>();
        if (neighbour.getNeighbourIpAddress()!=null && !neighbour.getNeighbourIpAddress().isEmpty())
            graphmlEdgeDatas.put("ipLink","YES");
        else
            graphmlEdgeDatas.put("ipLink","NO");


       String neighbourIpAddress = neighbour.getIpAddress();

        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(neighbourIpAddress);
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        if (inetAddress instanceof Inet4Address) {
            graphmlEdgeDatas.put("ipv4Forwarding","YES");
            graphmlEdgeDatas.put("ipv6Forwarding","NO");

        }else{
            graphmlEdgeDatas.put("ipv6Forwarding","YES");
            graphmlEdgeDatas.put("ipv4Forwarding","NO");
        }
        String discoveryMethod= neighbour.getParameters().get("Discovery Method");
        graphmlEdgeDatas.put("discoveryMethod",discoveryMethod);

        if (discoveryMethod.equals("CDP")||discoveryMethod.equals("LLDP")||discoveryMethod.equals("MAC")){
            graphmlEdgeDatas.put("dataLink","YES");
        }    else {
            graphmlEdgeDatas.put("dataLink","NO");

        }

        return graphmlEdgeDatas;

    }


    private Map<String, String> getGraphmlSubnetNeighbourEdgeMetaData(Subnet subnet, DeviceNeighbour neighbour ) {

        Map<String, String> graphmlEdgeDatas = new HashMap<>();
        if (neighbour.getNeighbourIpAddress()!=null && !neighbour.getNeighbourIpAddress().isEmpty())
            graphmlEdgeDatas.put("ipLink","true");
        else
            graphmlEdgeDatas.put("ipLink","false");

        switch (subnet.getSubnetProtocolType()) {
            case "IPv4":
                graphmlEdgeDatas.put("ipv4Forwarding","YES");
                graphmlEdgeDatas.put("ipv6Forwarding","NO");
                break;
            case "IPv6":
                graphmlEdgeDatas.put("ipv6Forwarding","YES");
                graphmlEdgeDatas.put("ipv4Forwarding","NO");
                break;
        }
        String discoveryMethod= neighbour.getParameters().get("Discovery Method");
        graphmlEdgeDatas.put("discoveryMethod",discoveryMethod);

        if (discoveryMethod.equals("CDP")||discoveryMethod.equals("LLDP")||discoveryMethod.equals("MAC")){
            graphmlEdgeDatas.put("dataLink","true");
        }    else {
            graphmlEdgeDatas.put("dataLink","false");

        }

        return graphmlEdgeDatas;

    }


    private Map<String, String> getGraphmlSubnetNodeMetaData(Subnet subnet){

        Map<String, String> graphmlNodeDatas = new HashMap<>();

        graphmlNodeDatas.put("ipAddress",subnet.getIpAddress());
        switch (subnet.getSubnetProtocolType()){
            case "IPv4":
                graphmlNodeDatas.put("ipv4Forwarding","YES");
                graphmlNodeDatas.put("ipv6Forwarding","NO");
                break;
            case "IPv6":
                graphmlNodeDatas.put("ipv6Forwarding","YES");
                graphmlNodeDatas.put("ipv4Forwarding","NO");
                break;
        }


        graphmlNodeDatas.put("subnetPrefixMask",subnet.getSubnetPrefixMask());
        graphmlNodeDatas.put("subnetMask",subnet.getsubnetMask());
        graphmlNodeDatas.put("deviceType","subnet");
        graphmlNodeDatas.put("port",subnet.getLocalInterface());


        return graphmlNodeDatas;

    }

    private Map<String, String> getGraphmlSubnetEdgeMetaData(Subnet subnet){
        Map<String, String> graphmlEdgeDatas = new HashMap<>();
        graphmlEdgeDatas.put("ipLink","YES");

        graphmlEdgeDatas.put("dataLink","YES");

        String protocolType= subnet.getSubnetProtocolType();
        switch (protocolType){
            case "IPv4":
                graphmlEdgeDatas.put("ipv4Forwarding","YES");
                graphmlEdgeDatas.put("ipv6Forwarding","NO");
                break;
            case "IPv6":
                graphmlEdgeDatas.put("ipv6Forwarding","YES");
                graphmlEdgeDatas.put("ipv4Forwarding","NO");
                break;
        }

        graphmlEdgeDatas.put("discoveryMethod",subnet.getSubnetDiscoveryMethods());

        return graphmlEdgeDatas;

    }




}
