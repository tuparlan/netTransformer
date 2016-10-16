package net.itransformers.idiscover.v2.core.listeners.neighbor;

import net.itransformers.idiscover.networkmodelv2.DiscoveredDevice;
import net.itransformers.idiscover.api.NodeDiscoveryResult;
import net.itransformers.idiscover.api.NodeNeighboursDiscoveryListener;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.GraphmlRenderer;
import net.itransformers.idiscover.api.models.graphml.GraphmlEdge;
import net.itransformers.idiscover.api.models.graphml.GraphmlNode;
import net.itransformers.idiscover.v2.core.listeners.neighbor.device.AliasResolver;
import net.itransformers.idiscover.v2.core.listeners.neighbor.device.DeviceToGraphml;
import net.itransformers.idiscover.api.models.network.Node;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vasko on 16.06.16.
 */


public class SnmpNodeNeighbourDiscoveryListener implements NodeNeighboursDiscoveryListener {
    static Logger logger = Logger.getLogger(SnmpNodeNeighbourDiscoveryListener.class);
    String labelDirName;
    String graphmlDirName;
    String projectPath;
    String velocityTemplate;
    GraphmlRenderer graphmlRenderer;


    public SnmpNodeNeighbourDiscoveryListener(){
          graphmlRenderer = new GraphmlRenderer();
    }

    public SnmpNodeNeighbourDiscoveryListener(String labelDirName, String graphmlDirName, String projectPath, String velocityTemplate) {
        this.labelDirName = labelDirName;
        this.graphmlDirName = graphmlDirName;
        this.projectPath = projectPath;
        this.velocityTemplate = velocityTemplate;
        this.graphmlRenderer = new GraphmlRenderer();
    }

    @Override
    public void handleNodeNeighboursDiscovered(Node node, NodeDiscoveryResult nodeDiscoveryResult) {

        File baseDir = new File(projectPath,labelDirName);
        File graphmlDir = new File(baseDir, graphmlDirName);
        if (!graphmlDir.exists()) graphmlDir.mkdir();

        String nodeFileName = node.getId();

        String discoveredIPv4Address = (String) nodeDiscoveryResult.getDiscoveredData().get("discoveredIPv4Address");


        DiscoveredDevice discoveredDevice = (DiscoveredDevice) nodeDiscoveryResult.getDiscoveredData().get("DiscoveredDevice");

        Map<String, String> subnetDetails = (Map<String, String>) nodeDiscoveryResult.getDiscoveredData().get("subnetDetails");

        String icmpStatus = (String) nodeDiscoveryResult.getDiscoveredData().get("icmpStatus");

        String dnsFQDN =   (String) nodeDiscoveryResult.getDiscoveredData().get("FQDN");
        String dnsPQDN =   (String) nodeDiscoveryResult.getDiscoveredData().get("PQDN");

        HashMap<String, Object> params = new HashMap<>();
        ArrayList<GraphmlNode> graphmlNodes = new ArrayList<>();
        List<GraphmlEdge> graphmlEdges = new ArrayList<>();
        GraphmlNode mainNode = new GraphmlNode(node.getId(), node.getId());
        Map<String, String> mainNodeGraphmlDatas =  new HashMap<>();

        if(icmpStatus!=null){
            //We got an icmpDevice

            mainNodeGraphmlDatas.put("icmpStatus",icmpStatus);
            mainNodeGraphmlDatas.put("discoveredIPv4Address",discoveredIPv4Address);
        }

        if (discoveredDevice!=null) {

            //We got an Snmp Device
            mainNodeGraphmlDatas.put("snmpStatus","REACHABLE");
            mainNodeGraphmlDatas.put("discoveredIPv4Address",discoveredIPv4Address);
            Map<String, String> snmpNodeData = getSnmpMainNode(discoveredDevice);
            mainNodeGraphmlDatas.putAll(snmpNodeData);
            DeviceToGraphml deviceToGraphml = new DeviceToGraphml(node, discoveredDevice);
            graphmlNodes.addAll(deviceToGraphml.getSubnetNodes());
            graphmlNodes.addAll(deviceToGraphml.getNonSubnetNeighbours());
            graphmlEdges = deviceToGraphml.getSubnetEdgesToMainNode();
            graphmlEdges.addAll(deviceToGraphml.getEdgesToNeighbours());

        }
        if (subnetDetails!=null){

            Map<String, String> subnetNodeData = getSubnetMainNode(subnetDetails);
            String bogonSubnetMarker =  (String) nodeDiscoveryResult.getDiscoveredData().get("bogon");
            String privateSubnetMarker = (String) nodeDiscoveryResult.getDiscoveredData().get("private");

            if (bogonSubnetMarker!=null && bogonSubnetMarker.equals("YES"))
                subnetNodeData.put("bogon","YES");
            else
                subnetNodeData.put("bogon","NO");

            if (privateSubnetMarker!=null && privateSubnetMarker.equals("YES"))
                subnetNodeData.put("private","YES");
            else
                subnetNodeData.put("private", "NO");

            mainNodeGraphmlDatas.putAll(subnetNodeData);

            List<GraphmlNode> subnetNeighbourNodes = new ArrayList<>();
//
            List<GraphmlEdge> subnetNeighbourEdges = new ArrayList<>();

            for (Node subnetNeighbour : node.getNeighbours()) {

                AliasResolver aliasResolver = new AliasResolver(node,subnetNeighbour.getId(), null, null);
                String neighbourId = aliasResolver.getNeighbourIdFromAliases();
                GraphmlNode subnetNeighbourNode;

                if (neighbourId==null) neighbourId = subnetNeighbour.getId();

                subnetNeighbourNode = new GraphmlNode(neighbourId, neighbourId);

                subnetNeighbourNodes.add(subnetNeighbourNode);
                EdgeIdGenerator edgeIdGenerator = new EdgeIdGenerator(neighbourId,node.getId(),neighbourId,node.getId());
                GraphmlEdge subnetNeighbourEdge = edgeIdGenerator.createEdge();


                subnetNeighbourEdges.add(subnetNeighbourEdge);
            }

            graphmlNodes.addAll(subnetNeighbourNodes);
            graphmlEdges.addAll(subnetNeighbourEdges);
            String subnetIpAddress = subnetDetails.get("ipAddress");
            String subnetPrefixMask = subnetDetails.get("subnetPrefixMask");

            nodeFileName = subnetIpAddress+"--"+subnetPrefixMask;
        }

        if (dnsFQDN!=null){
            mainNodeGraphmlDatas.put("fqdn",dnsFQDN);

            mainNodeGraphmlDatas.put("pqdn",dnsPQDN);
        }

        if (icmpStatus==null && dnsFQDN==null && discoveredDevice ==null && subnetDetails==null)
            return;

        mainNode.setGraphmlNodeData(mainNodeGraphmlDatas);
        graphmlNodes.add(mainNode);
        params.put("nodes", graphmlNodes);
        params.put("graphDirection", "undirected");
        params.put("edges", graphmlEdges);

        try {
            String projectName = new File(projectPath).getName();
            if (projectName.equals("."))
                projectName = new File(new File(projectPath).getParent()).getName();
            params.put("project",projectName);
            params.put("version",baseDir.getCanonicalFile().getName());
        } catch (IOException e) {
            logger.error(e);
        }

        String graphml = null;
        try {
            logger.info("Starting to render graphml for node "+ node.getId());
            graphml = graphmlRenderer.render(velocityTemplate, params);
            logger.info("Finishing to render graphml for node " + node.getId());

        } catch (Exception e) {

            logger.trace(e.getMessage());
        }
        logger.trace(graphml);



        final String fileName = "node-" + nodeFileName+ ".graphml";
        final File nodeFile = new File(graphmlDir,fileName);


        try {
            FileUtils.writeStringToFile(nodeFile, graphml);

            File undirectedGraphmls = new File(graphmlDir.getParent(),"undirected"+".graphmls");
            if (!undirectedGraphmls.exists()){

                undirectedGraphmls.createNewFile();

            }
            FileWriter writer = new FileWriter(undirectedGraphmls,true);


            writer.append(String.valueOf(fileName)).append("\n");

            writer.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private Map<String, String> getSubnetMainNode(Map<String,String> subnetParams) {
        Map<String, String> graphmlNodeData = new HashMap<>();


        String procotocolType = subnetParams.get("protocolType");
        if (procotocolType.equals("IPv4")) {
            graphmlNodeData.put("ipv4Forwarding", "YES");
            graphmlNodeData.put("ipv6Forwarding", "NO");

        }else {
            graphmlNodeData.put("ipv4Forwarding", "NO");
            graphmlNodeData.put("ipv6Forwarding", "YES");
        }

        String ipAddress = subnetParams.get("ipAddress");
        graphmlNodeData.put("ipAddress",ipAddress);

        String subnetPrefixMask = subnetParams.get("subnetPrefixMask");
        graphmlNodeData.put("subnetPrefixMask",subnetPrefixMask);

        return graphmlNodeData;
    }


    private Map<String, String> getSnmpMainNode(DiscoveredDevice discoveredDevice) {

        Map<String, String> graphmlNodeMetaDatas = new HashMap<>();

        for (Map.Entry<String, String> entry : discoveredDevice.getParams().entrySet()) {
            logger.trace("MainNodeParm: " + entry.getKey() + "|" + entry.getValue());

            graphmlNodeMetaDatas.put(entry.getKey(), entry.getValue());

        }
        return graphmlNodeMetaDatas;
    }




    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }

    public String getGraphmlDirName() {
        return graphmlDirName;
    }

    public void setGraphmlDirName(String graphmlDirName) {
        this.graphmlDirName = graphmlDirName;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getVelocityTemplate() {
        return velocityTemplate;
    }

    public void setVelocityTemplate(String velocityTemplate) {
        this.velocityTemplate = velocityTemplate;
    }



}
