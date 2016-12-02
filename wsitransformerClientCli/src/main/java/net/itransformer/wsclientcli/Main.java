package net.itransformer.wsclientcli;

import net.itransformer.wsclient.connectionDetails.ConnectionDetailsManagerStub;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import net.itransformers.utils.ProjectConstants;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
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

        Option projectTypeOption = new Option("t", "type", true, "The type of project you are creating. Supported types are [\"+ ProjectConstants.snmpProjectType+\", \"+ProjectConstants.bgpDiscovererProjectType+\", \"+ProjectConstants.freeGraphProjectType+\"]");
        projectTypeOption.setRequired(false);
        options.addOption(projectTypeOption);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

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

        }

        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:wsitransformerClient/wsitransformerClient.xml");
        ctx.refresh();

        ConnectionDetailsManager connectionDetailsManagerStub = ctx.getBean("networkDiscoveryFactory", ConnectionDetailsManager.class);
        Map<String, ConnectionDetails> connDetails = connectionDetailsManagerStub.getConnections();
        for (String key : connDetails.keySet()) {
            System.out.println(key);
        }
    }
}
