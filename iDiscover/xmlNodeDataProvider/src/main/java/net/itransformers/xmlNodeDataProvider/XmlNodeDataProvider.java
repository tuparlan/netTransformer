package net.itransformers.xmlNodeDataProvider;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import net.itransformers.idiscover.api.DiscoveryResult;
import net.itransformers.idiscover.api.models.graphml.*;
import net.itransformers.idiscover.api.models.node_data.DiscoveredDeviceData;
import net.itransformers.idiscover.api.models.node_data.RawDeviceData;
import net.itransformers.utils.JaxbMarshalar;
import net.itransformers.utils.ProjectConstants;
import net.itransformers.utils.blueprints_patch.MyGraphMLReader;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;

/**
 * Created by niau on 9/26/16.
 */
public class XmlNodeDataProvider implements DiscoveryResult {
    static Logger logger = Logger.getLogger(XmlNodeDataProvider.class);

    String projectPath;
    File networkPath;



    public XmlNodeDataProvider(String projectPath) {
        this.projectPath = projectPath;
        networkPath = new File(projectPath+File.separator+ ProjectConstants.networkDirName);
    }



    @Override
    public List<String> getDiscoveredVersions() {
        return Arrays.asList(networkPath.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        }));
    }

    @Override
    public List<String> getDiscoveredNodes(String version) {
        File versionDir = new File(networkPath+File.separator+version);
        ArrayList<String> list = new ArrayList<String>();

        if (versionDir.exists()){
            File undirectedGraphmls = new File(versionDir+File.separator+ProjectConstants.nodesListFileName);
            Scanner s = null;
            try {
                s = new Scanner(undirectedGraphmls);
                while (s.hasNext()){
                    list.add(s.next());
                }
                s.close();
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            }

        }else {
            System.out.println(versionDir+File.separator+ProjectConstants.nodesListFileName +" "+ "does not exist");
            logger.info(versionDir + File.separator + ProjectConstants.nodesListFileName + " " + "does not exist");
        }
        return list;
    }

    @Override
    public String getCreationDate(String version) {
        return null;
    }

    @Override
    public GraphmlGraph getNetwork(String version) {
        File versionDir = new File(networkPath+File.separator+version);
        if (versionDir.exists()){
            File graphmlFile = new File(versionDir+File.separator+ProjectConstants.undirectedGraphmlDirName+File.separator+ProjectConstants.networkGraphmlFileName);

            if (graphmlFile.exists()){

                return loadGraphml(graphmlFile.getAbsolutePath());

            }   else {
                logger.info(graphmlFile.getAbsolutePath() +" "+ "does not exist");

                return null;
            }

        }   else {
            logger.info(versionDir.getAbsolutePath() +" "+ "does not exist");

            return null;
        }
    }

    @Override
    public RawDeviceData getRawData(String version, String nodeId) {
        File versionDir = new File(networkPath+File.separator+version);

        if (versionDir.exists()){
            File rawDataFile = new File(versionDir+File.separator+ProjectConstants.rawDataDirName+File.separator+nodeId+".xml");

            if (rawDataFile.exists()){

                try {
                    return loadRawData(rawDataFile.getAbsolutePath());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }

            } else {
                logger.info(rawDataFile.getAbsolutePath() +" "+ "does not exist");
            }

        } else {
            logger.info(versionDir.getAbsolutePath() +" "+ "does not exist");

        }


        return null;
    }

    private RawDeviceData loadRawData(String absolutePath) throws IOException {


        FileInputStream is = new FileInputStream(absolutePath);
        byte[] data = new byte[is.available()];
        is.read(data);
        return new RawDeviceData(data);
    }

    @Override
    public GraphmlNode getNode(String version, String nodeId) {
        File versionDir = new File(networkPath+File.separator+version);
        if (versionDir.exists()){
            File graphmlFile = new File(versionDir+File.separator+ProjectConstants.undirectedGraphmlDirName+File.separator+ProjectConstants.graphmlDataPrefix+ProjectConstants.graphmlDataPrefix+nodeId+".graphml");

            if (graphmlFile.exists()){

                GraphmlGraph graphmlGraph = loadGraphml(graphmlFile.getAbsolutePath());
                for (GraphmlNode node : graphmlGraph.getGraphmlNodes()) {
                   if (node.getId().equals(nodeId)){
                       return node;
                   }
                }
            }else{
                logger.info(graphmlFile.getAbsolutePath() +" "+ "does not exist");
                return null;
            }

        }
        logger.info(versionDir.getAbsolutePath() +" "+ "does not exist");

        return null;

        }

    @Override
    public DiscoveredDeviceData getDeviceHierarchicalModel(String version, String nodeId) {
        File versionDir = new File(networkPath + File.separator + version);
        if (versionDir.exists()) {
            File deviceDataFile = new File(versionDir + File.separator + ProjectConstants.deviceDataDirName + File.separator + ProjectConstants.deviceDataPrefix  + nodeId + ".xml");

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(deviceDataFile);
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
                e.printStackTrace();
            }

            try {
                return JaxbMarshalar.unmarshal(DiscoveredDeviceData.class, fis);
            } catch (JAXBException e) {
                logger.error(e.getMessage(), e);
                e.printStackTrace();
            }

        } else{
            logger.info(versionDir.getAbsolutePath() +" "+ "does not exist");
        }
        return null;

    }


    private Graph loadGraphmlInTinkerGraph(String filePath){

        Graph graph = new TinkerGraph();
        MyGraphMLReader reader2 = new MyGraphMLReader(graph);
        try {
            reader2.inputGraph(filePath);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return graph;
    }

    private GraphmlGraph loadGraphml(String filePath){


        GraphmlGraph graphmlGraph = new GraphmlGraph();

        List<GraphmlEdge> graphmlEdges = new ArrayList<>();
        List<GraphmlNode> graphmlNodes = new ArrayList<>();

        Graph graph = loadGraphmlInTinkerGraph(filePath);

        for (Vertex vertex: graph.getVertices()){
            GraphmlNode node = new GraphmlNode();
            List<GraphmlNodeData> graphmlNodeDataList = new ArrayList<>();
            node.setId(vertex.getId().toString());
            Set<String> keys = vertex.getPropertyKeys();
            for (String key : keys) {
                graphmlNodeDataList.add(new GraphmlNodeData(key,vertex.getProperty(key)));
            }
            node.setGraphmlNodeDataList(graphmlNodeDataList);
            graphmlNodes.add(node);

        }
        for (Edge edge: graph.getEdges()){

            GraphmlEdge graphmlEdge = new GraphmlEdge();
            List<GraphmlEdgeData> graphmlEdgeDataList = new ArrayList<>();
            graphmlEdge.setId(edge.getId().toString());

            Vertex vertexIn = edge.getVertex(Direction.IN);

            graphmlEdge.setFromNode(vertexIn.getId().toString());
            Vertex vertexOut = edge.getVertex(Direction.OUT);

            graphmlEdge.setToNode(vertexOut.getId().toString());


            Set<String> keys = edge.getPropertyKeys();
            for (String key : keys) {
                graphmlEdgeDataList.add(new GraphmlEdgeData(key,edge.getProperty(key)));
            }
            graphmlEdge.setGraphmlEdgeDataList(graphmlEdgeDataList);
            graphmlEdges.add(graphmlEdge);


        }

        graphmlGraph.setGraphmlEdges(graphmlEdges);
        graphmlGraph.setGraphmlNodes(graphmlNodes);
        return graphmlGraph;

    }




}
