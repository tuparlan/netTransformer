package net.itransformers.ws.configurationManager;

import net.itransformers.parameterfactoryapi.ParameterFactory;
import net.itransformers.parameterfactoryapi.ParameterFactoryManagerFactory;
import net.itransformers.parameterfactoryapi.ParameterFactoryManger;
import net.itransformers.parameterfactoryapi.model.ParamFactoryElementType;
import net.itransformers.parameterfactoryapi.model.ParamFactoryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by niau on 1/7/17.
 */

@Controller
@RequestMapping(value="/parameterFactories")
public class ParameterFactoryController implements ServletContextAware {
    final Logger logger = LoggerFactory.getLogger(ParameterFactoryController.class);
    @Resource(name="xmlParameterFactory")
    private ParameterFactoryManagerFactory xmlParameterFactory;
    @Resource(name="baseDir")
    private String baseDir;
    private ServletContext context;

    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    private ParameterFactoryManger getParameterFactoryManger(){
        ParameterFactoryManger parameterFactoryManger =
                (ParameterFactoryManger) context.getAttribute("parameterFactoryManger");
        if (parameterFactoryManger == null) {
            Map<String, String> props = new HashMap<>();
            props.put("projectPath", baseDir);
            parameterFactoryManger = xmlParameterFactory.createParameterFactorysManager(props);
            context.setAttribute("parameterFactoryManger", parameterFactoryManger);
        }
        return parameterFactoryManger;
    }

    @RequestMapping(value = "/{name}", method=RequestMethod.GET)
    @ResponseBody
    ParamFactoryType getParameterFactory(@PathVariable String name){
        return getParameterFactoryManger().getParamFactoryType(name);

    }

    @RequestMapping(value = "/{name}/{type}", method=RequestMethod.GET)
    @ResponseBody
    ParamFactoryElementType getParameterFactoryElement(@PathVariable String name,
                                                @PathVariable String type
                                                ){
        return getParameterFactoryManger().getParamFactoryElementType(name, type);

    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    List<String> getParameterFactories(){
        Map<String,ParameterFactory> parameterFactoryMap =  getParameterFactoryManger().getParameterFactories();
        List<String> paramFactoryNames = new ArrayList<>();

        for (Map.Entry<String,ParameterFactory> entry : parameterFactoryMap.entrySet()) {
            paramFactoryNames.add(entry.getKey());
        }

        return  paramFactoryNames;

    }



}
