package net.itransformers.parameterfactoryapi;

import net.itransformers.parameterfactoryapi.model.ParamFactoriesType;
import net.itransformers.parameterfactoryapi.model.ParamFactoryElementType;
import net.itransformers.parameterfactoryapi.model.ParamFactoryType;

import java.util.Map;

/**
 * Created by niau on 1/9/17.
 */
public interface ParameterFactoryManger {

//    void createParameterFactoryElementTypes(String name, String paramClass, Map<String, String> params);
//    void createParameterFactory(String name, List<ParameterFactoryElement> parameterFactoryElements);
    ParameterFactory getParameterFactory(String name);
    Map<String,ParameterFactory> getParameterFactories();

    ParamFactoriesType getParamFactoryTypes();

    ParamFactoryType getParamFactoryType(String name);

    ParamFactoryElementType getParamFactoryElementType(String name, String type);


//    void deleteParameterFactory(String name);

//    void createParameterFactoryElement(String name, String type, Map<String,String> params);
//    void deleteParameterFactoryElement(String name, String type);
//    ParameterFactoryElement getParameterFactoryElement(String name, String type);
//    void updateParameterFactoryElement(String name, String type, Map<String,String> params);

}
