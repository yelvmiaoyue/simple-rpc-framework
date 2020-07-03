package priv.patrick.rpc.nameservice;

import java.net.URI;

/**
 * 注册中心
 *
 * @author Patrick_zhou
 */
public interface NameService {
    /**
     * 注册服务
     *
     * @param serviceName 接口全限定名
     */
    void registerService(String serviceName, URI uri);

    /**
     * 获取服务提供者地址
     *
     * @param serviceName 接口全限定名
     * @return
     */
    URI lookupService(String serviceName);

    /**
     * 初始化
     *
     * @param uri 连接地址
     */
    void init(String uri);

}
