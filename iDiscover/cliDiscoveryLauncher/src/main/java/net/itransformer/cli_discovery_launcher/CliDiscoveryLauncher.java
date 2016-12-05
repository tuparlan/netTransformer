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
import net.itransformers.utils.ProjectConstants;
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
        Option projectPathOption = new Option("b", "baseDir", true, "Path to your projects folder");
        projectPathOption.setRequired(true);
        options.addOption(projectPathOption);

        Option discoverNetwork = new Option("d", "discover", true, "Trigger a new network discovery run");
        discoverNetwork.setRequired(false);
        options.addOption(discoverNetwork);

        Option projectNameOption = new Option("n", "name", true, "The name of your project");
        projectNameOption.setRequired(true);
        options.addOption(projectNameOption);

        Option newProjectOption = new Option("c", "create", true, "If specified a new project will be created in projectPath");
        newProjectOption.setRequired(false);
        options.addOption(newProjectOption);

        Option projectTypeOption = new Option("t", "type", true, "The type of project you are creating. Supported types are [\"+ ProjectConstants.snmpProjectType+\", \"+ProjectConstants.bgpDiscovererProjectType+\", \"+ProjectConstants.freeGraphProjectType+\"]");
        projectTypeOption.setRequired(false);
        options.addOption(projectTypeOption);

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
                    "  java -jar netTransformer.jar --create=y --name=myProject --type=["+ ProjectConstants.snmpProjectType+", "+ProjectConstants.bgpDiscovererProjectType+", "+ProjectConstants.freeGraphProjectType+"] --baseDir=[Path to your projects folder].\"\n"+
                    "\n" +
                    "  Step 2: Run again as many times as you wish netTransformer. It will discover your network and will store \n" +
                    "  the discovered data, in a new version folder into your projectPath folder.\n"+
                    "  java -jar netTransformer.jar --name=myProject --discover=y --baseDir=[Path to your already created project folder].\n+" +
                    "\n" +
                    "  Step 3: Delete your netTransformer project.\n" +
                    "  java -jar netTransformer.jar --deleteProject=y --name=MyProject --baseDir=[Path to your projects folder].\n";
            ;

            formatter.printHelp(200,"java -jar netTransformer.jar","netTransformer is an open source network discovery tool provided by Nikolay Milovanov and Vasil Yordanov " +
                    "from http://itransformers.net.",options,usage);

            System.exit(1);
            return;
        }



        String discover = cmd.getOptionValue("discover");

        String createProject = cmd.getOptionValue("create");
        String projectName = cmd.getOptionValue("name");

        String baseDir = cmd.getOptionValue("baseDir");
        if (baseDir == null) {
            String systemBaseDir = System.getProperty("base.dir");
            if (systemBaseDir == null){
                baseDir = ".";
                System.setProperty("base.dir", baseDir);
            }else{
                baseDir=systemBaseDir;

            }

        }

        String deleteProject1 = cmd.getOptionValue("deleteProject");
        String projectType = cmd.getOptionValue("type");
        if (projectType == null){
            System.out.println("ProjectType is empty. Setting it to "+ProjectConstants.snmpProjectType);
            projectType = ProjectConstants.snmpProjectType;
        }
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:cliDiscoveryLauncher/cliDiscoveryLauncher.xml");
        ctx.load("classpath:fileBasedProjectManager/fileBasedProjectManager.xml");
        ctx.refresh();

        FileBasedProjectManagerFactory fileBasedProjectManagerFactory = ctx.getBean("projectManagerFactory", FileBasedProjectManagerFactory.class);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("baseDir", baseDir);
        FileBasedProjectManager projectManager = fileBasedProjectManagerFactory.createProjectManager(parameters);

        if (deleteProject1!=null){
            if (projectName==null){
                System.out.println("Project name is not specified!!!");
                return;

            }
            projectManager.deleteProject(projectName);
            return;

        }
        if (createProject!=null ){
            if (projectName == null){
                projectName = projectManager.randomProjectNameGenerator(baseDir);
                System.out.println("Project name is not specified generating a random one!!!");
            }
            File project = new File(baseDir,projectName);

            if (project.exists()){
                System.out.println("There is already a project with that name. Please specify another one!!!");
                return;

            }else{
                logger.info("Creating the project folder for \""+projectName+"\" in "+baseDir+"!!!");
                project.mkdir();
            }

            if (ProjectConstants.bgpDiscovererProjectType.equalsIgnoreCase(projectType) || ProjectConstants.snmpProjectType.equals(projectType)|| ProjectConstants.freeGraphProjectType.equals(projectType)){
                projectManager.createProject(projectName,projectType);
                System.out.println("Project \""+projectName+"\" created successfully in \""+baseDir+"\"");
            }else{
                System.out.println("Unknown project type!!!");
                return;

            }

        }


        if (projectName == null) {
            System.out.println("Project name is not specified. Will use current folder!!!");
            return;
        }

        String projectPath = new File(baseDir,projectName).getAbsolutePath();
        System.setProperty("base.dir",new File(projectName).getAbsolutePath());

        if ("y".equalsIgnoreCase(discover)) {

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
                    ConnectionDetailsManagerFactory.class);

//            ConnectionDetailsManager connectionDetailsManager = factory.createConnectionDetailsManager("csv", props);
//            Map<String, ConnectionDetails> connectionDetails = connectionDetailsManager.getConnections();
            networkDiscoverer.startDiscovery();
        }



    }
}
