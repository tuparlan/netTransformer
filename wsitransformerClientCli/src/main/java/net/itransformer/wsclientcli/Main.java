package net.itransformer.wsclientcli;

import net.itransformer.wsclient.connectionDetails.ConnectionDetailsManagerStub;
import net.itransformer.wsclient.iDiscover.NetworkDiscovererStub;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import net.itransformers.idiscover.api.NetworkDiscoverer;
import net.itransformers.idiscover.api.VersionManager;
import net.itransformers.utils.ProjectConstants;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * Created by vasko on 12/2/2016.
 */
public class Main {
    static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws MalformedURLException {
        logger.debug("iDiscover v2. gearing up");
        Options options = new Options();

        Option projectTypeOption = new Option("v", "version", true, "version");
        projectTypeOption.setRequired(false);
        options.addOption(projectTypeOption);

        Option commandOption = new Option("c", "command", true, "command");
        commandOption.setRequired(true);
        options.addOption(commandOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());

            String usage=
                    "\n" +
                            "TODO\n";
            ;

            formatter.printHelp(200,"java -jar netTransformer.jar"," " +
                    "from http://itransformers.net.",options,usage);
            return;
        }
        String version = cmd.getOptionValue("v");
        if (version == null) {version = "";}
        String command = cmd.getOptionValue("c");
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:wsitransformerClient/wsitransformerClient.xml");

        BeanDefinition versionBeanDefinition = BeanDefinitionBuilder.
                rootBeanDefinition(String.class)
                .addConstructorArgValue(version).getBeanDefinition();

        ctx.registerBeanDefinition("version",versionBeanDefinition);
        ctx.refresh();

        if (command.equals("createVersion")) {
            VersionManager versionManager = ctx.getBean("versionManagerStub", VersionManager.class);
            System.out.println("Created version="+versionManager.createVersion());
        } else if (command.equals("startDiscovery")){
            NetworkDiscoverer networkDiscoverer = ctx.getBean("networkDiscovererStub", NetworkDiscoverer.class);
            networkDiscoverer.startDiscovery();
        } else if (command.equals("stopDiscovery")){
            NetworkDiscoverer networkDiscoverer = ctx.getBean("networkDiscovererStub", NetworkDiscoverer.class);
            networkDiscoverer.stopDiscovery();
        } else if (command.equals("pauseDiscovery")){
            NetworkDiscoverer networkDiscoverer = ctx.getBean("networkDiscovererStub", NetworkDiscoverer.class);
            networkDiscoverer.pauseDiscovery();
        } else if (command.equals("resumeDiscovery")){
            NetworkDiscoverer networkDiscoverer = ctx.getBean("networkDiscovererStub", NetworkDiscoverer.class);
            networkDiscoverer.resumeDiscovery();
        } else if (command.equals("getDiscoveryStatus")){
            NetworkDiscoverer networkDiscoverer = ctx.getBean("networkDiscovererStub", NetworkDiscoverer.class);
            NetworkDiscoverer.Status status = networkDiscoverer.getDiscoveryStatus();
            System.out.println("Discovery status: "+status);
        } else if (command.equals("getConnections")){
            ConnectionDetailsManager connectionDetailsManagerStub = ctx.getBean("connectionDetailsManagerStub", ConnectionDetailsManager.class);
            Map<String, ConnectionDetails> connDetails = connectionDetailsManagerStub.getConnections();
            for (String key : connDetails.keySet()) {
                System.out.println(key+ ": "+connDetails.get(key));
            }
        }
    }
}
