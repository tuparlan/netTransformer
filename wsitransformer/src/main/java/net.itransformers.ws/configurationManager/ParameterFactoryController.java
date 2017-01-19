package net.itransformers.ws.configurationManager;

import net.itransformers.parameterfactoryapi.ParameterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created by niau on 1/7/17.
 */

@Controller
@RequestMapping(value="/parameters")
public class ParameterFactoryController {
    final Logger logger = LoggerFactory.getLogger(ParameterFactoryController.class);
    @Resource(name="parameterFactory")
    private ParameterFactory parameterFactory;


}
