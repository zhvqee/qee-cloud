package org.qee.cloud.rpc.api;

import org.qee.cloud.common.model.URL;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: qee-cloud
 * @Package: org.qee.cloud.registry.api
 * @ClassName: RegistryCenterService
 * @Description:
 * @Date: 2021/11/23 3:12 下午
 * @Version: 1.0
 */
public class RegistryCenterService {

    private static List<URL> registryList = new ArrayList<>();

    public static List<URL> getRegistriesUrls() {
        return registryList;
    }

    public static void addRegistryUrl(URL url) {
        registryList.add(url);
    }
}
