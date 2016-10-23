package net.itransformer.cli_discovery_launcher;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManagerFactory;
import net.itransformers.filebasedprojectmanager.FileBasedProjectManager;
import net.itransformers.filebasedprojectmanager.FileBasedProjectManagerFactory;
import net.itransformers.idiscover.api.NetworkDiscoverer;
import net.itransformers.idiscover.api.NetworkDiscovererFactory;
import net.itransformers.idiscover.api.VersionManager;
import net.itransformers.idiscover.api.VersionManagerFactory;
import net.itransformers.idiscover.api.models.network.Node;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by vasko on 9/30/2016.
 */
public class CliDiscoveryLauncher {
    static Logger logger = Logger.getLogger(CliDiscoveryLauncher.class);

    public static void main(String[] args) throws MalformedURLException {
        logger.debug("iDiscover v2. gearing up");
        System.setProperty("networkaddress.cache.ttl", "0");
        System.setProperty("networkaddress.cache.negative.ttl", "0");


        Options options = new Options();
        Option projectPathOption = new Option("p", "projectPath", true, "Path to your project folder");
        projectPathOption.setRequired(true);
        options.addOption(projectPathOption);

        Option newProjectOption = new Option("n", "newProject", true, "If specified a new project will be created in projectPath");
        newProjectOption.setRequired(false);
        options.addOption(newProjectOption);

        Option deleteProject = new Option("d", "deleteProject", true, "If specified a the project in projectPath will be deleted");
        newProjectOption.setRequired(false);
        options.addOption(deleteProject);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());

            String usage=
                    "\n" +
                    "  Step 1: Create a new project. netTransformer will create your project and will discover your network in it.\n" +
                    "  The project folder is important sicne we will store your discovered data,connection details and\n"+
                    "  discovery resources in it. Each discovery run will create a new \"version\" of your network into your\n"+
                    "  projectPath folder.\n"+
                    "  java -jar netTransformer.jar --newProject=y --projectPath=[Path to your desired project folder].\"\n"+
                    "\n" +
                    "  Step 2: Run again as many times as you wish netTransformer. It will discover your network and will store \n" +
                    "  the discovered data, in a new version folder into your projectPath folder.\n"+
                    "  java -jar netTransformer.jar --projectPath=[Path to your already created project folder].\n+" +
                    "\n" +
                    "  Step 3: Delete your netTransformer project.\n" +
                    "  java -jar netTransformer.jar --deleteProject=y --projectPath=[Path to your already created project folder].\n";
            ;

            formatter.printHelp(200,"java -jar netTransformer.jar","netTransformer is an open source network discovery tool provided by Nikolay Milovanov and Vasil Yordanov " +
                    "from http://itransformers.net.",options,usage);

            System.exit(1);
            return;
        }




        String newProjectFlag = cmd.getOptionValue("newProject");

        String projectPath = cmd.getOptionValue("projectPath");


        String deleteProject1 = cmd.getOptionValue("deleteProject");


        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:cliDiscoveryLauncher/cliDiscoveryLauncher.xml");
        ctx.refresh();
        ctx.load("classpath:fileBasedProjectManager/fileBasedProjectManager.xml");
        FileBasedProjectManagerFactory fileBasedProjectManagerFactory = ctx.getBean("fileBasedProjectManagerFactory", FileBasedProjectManagerFactory.class);
        String baseDir = System.getProperty("base.dir");
        if (baseDir == null) {
            baseDir = ".";
            System.setProperty("base.dir", baseDir);
        }
        Map<String, String> parameters = new HashMap<>();
        parameters.put("baseDir", baseDir);
        FileBasedProjectManager projectManager = fileBasedProjectManagerFactory.createProjectManager(parameters);


        if (newProjectFlag!=null ){
            if (projectPath==null){
                System.out.println("Project path is not specified!!!");
                return;

            }
            File project = new File(projectPath);

            if (project.exists()){
                System.out.println("Project path folder already exists. Please specify an empty folder!!!");
                return;

            }else{
                logger.info("Creating a new project in "+projectPath+"!!!");
                project.mkdir();
            }


            projectManager.createProject("projectTemplates/netTransformer.pfl",new File(projectPath).getAbsolutePath());


        }
        if (deleteProject1!=null){
            if (projectPath==null){
                System.out.println("Project path is not specified!!!");
                return;

            }
            projectManager.deleteProject(new File(projectPath).getAbsolutePath());
            return;

        }

        if (projectPath == null) {
            File cwd = new File(".");
            System.out.println("Project path is not specified. Will use current dir: " + cwd.getAbsolutePath());
            projectPath = cwd.getAbsolutePath();
        }
        System.setProperty("base.dir",new File(projectPath).getAbsolutePath());

        System.out.println("_________" + System.getProperty("base.dir") + "_________");



        NetworkDiscovererFactory discovererFactory = ctx.getBean("networkDiscoveryFactory", NetworkDiscovererFactory.class);
        VersionManagerFactory versionManagerFactory = ctx.getBean("versionManagerFactory", VersionManagerFactory.class);
        Map<String, String> props = new HashMap<>();
        props.put("projectPath", projectPath);
        VersionManager versionManager = versionManagerFactory.createVersionManager("dir", props);
        String version = versionManager.createVersion();
        props.put("version", version);
        NetworkDiscoverer networkDiscoverer = discovererFactory.createNetworkDiscoverer("parallel", props);


        networkDiscoverer.addNetworkDiscoveryListeners(result -> {
            Map<String, Node> nodes = result.getNodes();
            for (String node : nodes.keySet()) {
                System.out.println("Discovered node: " + node);
            }
        });
        ConnectionDetailsManagerFactory factory = ctx.getBean("connectionManagerFactory",
                ConnectionDetailsManagerFactory .class);

        ConnectionDetailsManager connectionDetailsManager = factory.createConnectionDetailsManager("csv", props);
        Map<String, ConnectionDetails> connectionDetails = connectionDetailsManager.getConnections();
        networkDiscoverer.startDiscovery(new HashSet<>(connectionDetails.values()));
    }
}
