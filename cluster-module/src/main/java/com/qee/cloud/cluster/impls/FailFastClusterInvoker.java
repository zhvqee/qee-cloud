package com.qee.cloud.cluster.impls;

import com.qee.cloud.cluster.Directory;
import com.qee.cloud.loadbalance.LoadBalance;
import org.qee.cloud.common.extentions.ExtensionLoader;
import org.qee.cloud.rpc.api.InvocationHandler;
import org.qee.cloud.rpc.api.Invoker;
import org.qee.cloud.rpc.api.Result;

import java.util.List;

public class FailFastClusterInvoker<T> implements Invoker<T> {

    private Directory<T> directory;

    public FailFastClusterInvoker(Directory<T> directory) {
        this.directory = directory;
    }

    @Override
    public Class<T> getInterface() {
        return directory.getInterface();
    }

    @Override
    public Result invoke(InvocationHandler invocationHandler) {
        LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getAdaptiveExtension();
        List<Invoker<T>> invokers = directory.list(invocationHandler);
        invokers.get(0).getUrl().addParameter("loadbalance", "random");
        Invoker<T> invoker = loadBalance.select(invokers, invokers.get(0).getUrl());
        return invoker.invoke(invocationHandler);
    }
}
