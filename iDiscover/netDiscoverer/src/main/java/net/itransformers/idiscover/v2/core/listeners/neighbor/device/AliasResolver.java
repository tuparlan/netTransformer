package net.itransformers.idiscover.v2.core.listeners.neighbor.device;

import net.itransformers.idiscover.api.models.network.Node;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * Created by niau on 10/13/16.
 */
public class AliasResolver {
    static Logger logger = Logger.getLogger(AliasResolver.class);

    Node node;
    String neighbourHostName;
    String neighbourIpAddress;
    String neighbourMac;

    public AliasResolver(Node node,String neighbourHostName, String neighbourIpAddress, String neighbourMac) {
        this.node = node;
        this.neighbourHostName = neighbourHostName;
        this.neighbourIpAddress = neighbourIpAddress;
        this.neighbourMac = neighbourMac;

    }


    private String findNeighbourFromAliases(String key){


        for (Node neighbour : node.getNeighbours()){

            Set<String> neighbourAliases = neighbour.getAliases();
            if (neighbourAliases!=null && neighbourAliases.contains(key)){
                return neighbour.getId();
            }

        }
        return null;

    }


    public String getNeighbourIdFromAliases(){
        String neighbourId;

        if (neighbourIpAddress!=null && !neighbourIpAddress.isEmpty()){
            neighbourId  = findNeighbourFromAliases(neighbourIpAddress);
            if (neighbourId!=null) {
                return neighbourId;
            }
        } else if (neighbourHostName!=null && !neighbourHostName.isEmpty()){

            neighbourId = findNeighbourFromAliases(neighbourHostName);
            if (neighbourId!=null) {
                return neighbourId;
            }

        }  else if (neighbourMac!=null && !neighbourMac.isEmpty()){
            neighbourId = findNeighbourFromAliases(neighbourMac);
            if (neighbourId!=null) {
                return neighbourId;
            }

        } else {
            logger.info("Can't find neighbour id hostName,ipAddress and mac are all null or empty!!!");

        }

        if (neighbourHostName!=null&&!neighbourHostName.isEmpty()) {
            return neighbourHostName;

        }else if (neighbourIpAddress!=null && !neighbourIpAddress.isEmpty()){
            return neighbourIpAddress;
        }else if (neighbourMac!=null && !neighbourMac.isEmpty()){
            return neighbourMac;
        }



        return null;
    }
}
