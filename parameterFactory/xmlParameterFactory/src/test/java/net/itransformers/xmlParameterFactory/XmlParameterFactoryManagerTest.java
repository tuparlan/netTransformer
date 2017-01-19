package net.itransformers.xmlParameterFactory;

import net.itransformers.parameterfactoryapi.ParameterFactory;
import net.itransformers.parameterfactoryapi.ParameterFactoryManagerFactory;
import net.itransformers.parameterfactoryapi.ParameterFactoryManger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

/**
 * Created by niau on 1/12/17.
 */
public class XmlParameterFactoryManagerTest {

    ParameterFactoryManger xmlParameterFactoryManager;

    private final String baseDir = (String) System.getProperties().get("user.dir");

    @Before
    public void setUp() throws Exception {

       String xmlParameterFactoryFile = baseDir+ File.separator+"parameterFactory/xmlParameterFactory/src/test/java/resources/xmlParameterFactoryTests/param-factory.xml";
        String xmlParameterFactoryTypesFile = baseDir+ File.separator+"parameterFactory/xmlParameterFactory/src/test/java/resources/xmlParameterFactoryTests/param-factory-config.xml";
        ParameterFactoryManagerFactory parameterFactoryManagerFactory = new XmlParameterManagerFactory(xmlParameterFactoryFile,xmlParameterFactoryTypesFile);

        xmlParameterFactoryManager = parameterFactoryManagerFactory.createParameterFactorysManager();
    }

    @Test
    public void testCreateParameterFactoryElementTypes() throws Exception {

    }

    @Test
    public void testGetParameterFactoryElementTypes() throws Exception {

    }

//    @Test
//    public void testCreateParameterFactory() throws Exception {
//        List<ParameterFactoryElement> parameterFactoryElementList = new ArrayList<>();
//        xmlParameterFactoryManager.createParameterFactory("mynew",parameterFactoryElementList);
//
//        Assert.assertEquals(xmlParameterFactoryManager.getParameterFactories().size(), 23);
//
//
//    }

    @Test
    public void testGetParameterFactory() throws Exception {

        ParameterFactory parameterFactory = xmlParameterFactoryManager.getParameterFactory("configureHostname");
        Assert.assertEquals(parameterFactory.getElements().size(),3);


    }

//    @Test
//    public void testDeleteParameterFactory() throws Exception {
//        xmlParameterFactoryManager.deleteParameterFactory("configureHostname");
//        Assert.assertEquals(xmlParameterFactoryManager.getParameterFactories().size(), 21);
//
//    }

    @Test
    public void testGetParameterFactories() throws Exception {

     Map<String,ParameterFactory> parameterFactoryMap = xmlParameterFactoryManager.getParameterFactories();

     Assert.assertEquals(parameterFactoryMap.size(),22);

    }
}