package com.rhphere.mini.rpc.provider.registry;

public class RegistryFactory {

    private static volatile RegistryService registryService;

    public static RegistryService getInstance(String registryAddress, RegistryType type) throws Exception {

        if (null == registryService) {
            synchronized (RegistryFactory.class) {
                if (null == registryService) {
                    if (type == RegistryType.ZOOKEEPER) {
                        registryService = new ZookeeperRegistryService(registryAddress);
                    } else if (type == RegistryType.EUREKA) {
                        registryService = new EurekaRegistryService(registryAddress);
                    }
                }
            }
        }
        return registryService;
    }
}
