package org.qee.cloud.rpc;

import java.util.Arrays;

public class InvokerInvocationHandler<T> implements InvocationHandler {

    private Invoker<T> invoker;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    public InvokerInvocationHandler(Invoker<T> invoker, String methodName, Object[] arguments) {
        this.invoker = invoker;
        this.methodName = methodName;
        this.arguments = arguments;
        Class<?>[] clzzArr = null;
        if (arguments == null || arguments.length == 0) {
            clzzArr = new Class[0];
        } else {
            clzzArr = Arrays.stream(arguments).map(Object::getClass).toArray(Class[]::new);
        }
        setParameterTypes(clzzArr);
    }

    public InvokerInvocationHandler(Invoker<T> invoker) {
        this.invoker = invoker;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
}