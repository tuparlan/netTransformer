package net.itransformers.parameterfactoryapi;

/**
 * Created by niau on 1/12/17.
 */
public class ParameterFactoryException extends RuntimeException {
    public ParameterFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterFactoryException(String message) {
        super(message);
    }

}
