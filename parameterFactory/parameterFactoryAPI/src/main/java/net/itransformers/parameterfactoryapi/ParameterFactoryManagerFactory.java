package net.itransformers.parameterfactoryapi;

import java.util.Map;

/**
 * Created by niau on 1/9/17.
 */
public interface ParameterFactoryManagerFactory  {
    ParameterFactoryManger createParameterFactorysManager(Map<String, String> properties);
}
