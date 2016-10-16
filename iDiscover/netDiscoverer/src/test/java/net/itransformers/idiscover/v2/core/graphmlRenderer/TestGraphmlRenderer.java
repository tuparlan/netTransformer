package net.itransformers.idiscover.v2.core.graphmlRenderer;

import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.GraphmlRenderer;
import net.itransformers.idiscover.api.models.graphml.GraphmlNode;
import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestGraphmlRenderer
{
    private final String baseDir = (String) System.getProperties().get("user.dir");
 @Test
    public void testRender( )
        throws Exception
    {

        GraphmlRenderer graphmlRenderer = new GraphmlRenderer();
        HashMap<String,Object> params = new HashMap<>();

        ArrayList<GraphmlNode> graphmlNodes = new ArrayList<>();
        params.put("nodes",graphmlNodes);
        params.put("graphDirection","undirected");
        params.put("project","test");
        params.put("version","version1");
        {
            GraphmlNode graphmlNode = new GraphmlNode("R1", "router1");

            Map<String, String> graphmlNodeDataList = graphmlNode.getGraphmlNodeData();

            graphmlNodeDataList.put("deviceName", "R1");
            graphmlNodeDataList.put("deviceModel", "cisco2911");
            graphmlNodeDataList.put("deviceType", "CISCO");
            graphmlNodeDataList.put("discoveredIPv4Address", "1.1.1.1");
            graphmlNodeDataList.put("ipv4Forwarding", "YES");
            graphmlNodeDataList.put("totalInterfaceCount", "27");

            graphmlNodes.add(graphmlNode);
        }
        {
            GraphmlNode graphmlNode = new GraphmlNode("R2", "router2");

            Map<String, String> graphmlNodeDataList = graphmlNode.getGraphmlNodeData();

            graphmlNodeDataList.put("deviceName", "R2");
            graphmlNodeDataList.put("deviceModel", "cisco2911");
            graphmlNodeDataList.put("deviceType", "CISCO");
            graphmlNodeDataList.put("discoveredIPv4Address", "2.2.2.2");
            graphmlNodeDataList.put("ipv4Forwarding", "YES");
            graphmlNodeDataList.put("totalInterfaceCount", "12");

            graphmlNodes.add(graphmlNode);
        }
        String graphml = graphmlRenderer.render("netDiscoverer/velocity/snmpGraphmlTemplate.vm",params);

       // FileUtils.writeStringToFile(new File(baseDir + "/" + "iDiscover/src/test/resources/expectedGraphml.graphml"), graphml);
        File file = new File(baseDir + "/" + "iDiscover/netDiscoverer/src/test/resources/graphmlRenderer/expectedGraphml.graphml");
        System.out.println("graphml Path: "+file.getAbsoluteFile());
        Assert.assertEquals(FileUtils.readFileToString(file), graphml);
    }
}