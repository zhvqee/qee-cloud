package org.qee.cloud.registry.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.qee.cloud.common.exceptions.RegistryException;
import org.qee.cloud.common.model.URL;
import org.qee.cloud.common.utils.Asserts;
import org.qee.cloud.registry.api.NotifyListener;
import org.qee.cloud.registry.api.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ZookeeperRegistry implements Registry {

    public String ROOT = "/cloud/zookeeper";

    private String PROVIDERS = "/providers";

    private String CONSUMERS = "/consumers";

    private String SEPARATOR = "/";

    private final ConcurrentHashMap<String, Object> persistentExistNodePath = new ConcurrentHashMap<>();

    private final Object NULL_OBJECT = new Object();

    private CuratorFramework curatorFramework;

    public ZookeeperRegistry(URL url) {
        Asserts.assertTrue(url.getProtocol().equals("zookeeper"), RegistryException.class, "连接注册中心zookeeper,url协议错误:" + url);
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(url.getHost() + ":" + url.getPort())
                .retryPolicy(new RetryUntilElapsed(5000, 200))
                .sessionTimeoutMs(200000)
                .build();
        curatorFramework.start();
    }


    //provider://127.0.0.1:20881/org.qee.service.DemoService:*:*

    ///cloud/zookeeper/provider/org.qee.service.DemoService:*:*/127.0.0.1:20881
    @Override
    public void register(URL url) {
        try {
            recursiveCreateNode(ROOT + PROVIDERS, false);
        } catch (Exception e) {
            throw new RegistryException("连接注册中心异常,url:" + url, e);
        }
        //org.qee.service.DemoService:*:*
        String interfaceService = url.getInterfaceGroupVersion();
        String hostDomain = url.getHostDomain();

        try {
            recursiveCreateNode(ROOT + PROVIDERS + SEPARATOR + interfaceService + SEPARATOR + hostDomain, true);
        } catch (Exception e) {
            throw new RegistryException("注册服务异常,url:" + url, e);
        }

    }


    @Override
    public void unregister(URL url) {
        // TODO: 2021/11/28   待开发
    }

    //consumer://127.0.0.1:20881/org.qee.service.DemoService:*:*

    //provider watch: cloud/zookeeper/provider/org.qee.service.DemoService:*:*
    @Override
    public void subscribe(URL url, NotifyListener listener) {
        try {
            recursiveCreateNode(ROOT + CONSUMERS, false);
        } catch (Exception e) {
            throw new RegistryException("连接注册中心异常,url:" + url, e);
        }
        String hostDomain = url.getHostDomain();
        String interfaceProviderVersion = url.getInterfaceGroupVersion();

        try {
            List<URL> providerList = new ArrayList<>();
            recursiveCreateNode(ROOT + CONSUMERS + SEPARATOR + interfaceProviderVersion + SEPARATOR + hostDomain, true);
            String parentPath = ROOT + PROVIDERS + SEPARATOR + interfaceProviderVersion;
            curatorFramework.getChildren().usingWatcher(new CuratorWatcherImpl(parentPath, url, providerList, curatorFramework)).forPath(parentPath);

            // listener;
            if (listener != null) {
                listener.notify(providerList);
            }
        } catch (Exception e) {
            throw new RegistryException("连接注册中心异常,url:" + url, e);
        }

    }

    @Override
    public void unsubscribe(URL url, NotifyListener listener) {
        // TODO: 2021/11/28  待开发
    }

    // org.qee.service.DemoService:*:*/127.0.0.1:20881
    public void recursiveCreateNode(String path, boolean ephemeral) throws Exception {
        if (!ephemeral) {
            if (persistentExistNodePath.containsKey(path)) {
                return;
            }
            if (curatorFramework.checkExists().forPath(path) != null) {
                persistentExistNodePath.put(path, NULL_OBJECT);
                return;
            }
        }
        String parentPath = path.substring(0, path.lastIndexOf("/"));
        if (StringUtils.isNoneBlank(parentPath)) {
            recursiveCreateNode(parentPath, false);
        }
        CreateMode createMode = CreateMode.EPHEMERAL;
        if (!ephemeral) {
            createMode = CreateMode.PERSISTENT;
        }
        if (curatorFramework.checkExists().forPath(path) == null) {
            curatorFramework.create().withMode(createMode).forPath(path);
            System.out.println("注册节点:" + path);
        }
        if (!ephemeral) {
            persistentExistNodePath.put(path, NULL_OBJECT);
        }


    }


    public void setData(String path, String value) throws Exception {
        curatorFramework.setData().forPath(path, value.getBytes());
    }

    public String getData(String path) throws Exception {
        return new String(curatorFramework.getData().forPath(path));
    }

    public void close() {
        curatorFramework.close();
    }


}
