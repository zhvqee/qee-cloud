package org.qee.cloud.common.exceptions;

public class RegistryException extends CloudException {
    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }
}
