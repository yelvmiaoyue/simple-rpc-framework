package priv.patrick.rpc;

import io.netty.channel.Channel;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.patrick.rpc.nameservice.NameService;
import priv.patrick.rpc.spi.ServiceLoaderUtils;
import priv.patrick.rpc.stub.AbstractStub;
import priv.patrick.rpc.stub.StubFactory;
import priv.patrick.rpc.transport.ServiceInfo;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * RPC通用接口
 *
 * @author Patrick_zhou
 */
public class RpcCommonService {
    private static final Logger log = LoggerFactory.getLogger(RpcCommonService.class);

    public static Map<URI, Channel> channelMap = new ConcurrentHashMap<>();

    private static Map<Class<?>, AbstractStub> stubMap = new ConcurrentHashMap<>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final String NAMESERVICE_URI = "jdbc:mysql://localhost:3306/study?user=root&password=123456";
    private final NameService nameService = getNameService(URI.create(NAMESERVICE_URI));

    /**
     * 生成目标stub
     *
     * @param serviceName stub实现的接口class对象
     * @param <T>         接口类型
     */
    public <T> T getStub(Class<T> serviceName) {
        if (serviceName == null) {
            log.error("service对象为空。");
            return null;
        }

        AbstractStub stub;
        readLock.lock();
        try {
            stub = stubMap.get(serviceName);
        } finally {
            readLock.unlock();
        }
        if (stub == null) {
            writeLock.lock();
            try {
                //再次验证
                stub = stubMap.get(serviceName);

                if (stub == null) {
                    String service = serviceName.getCanonicalName();
                    List<URI> uris = nameService.lookupService(service);
                    if (CollectionUtils.isEmpty(uris)) {
                        log.error("目标服务当前不可达。");
                        return null;
                    }
                    ServiceInfo serviceInfo = new ServiceInfo(uris);
                    stub = StubFactory.createStub(serviceInfo, serviceName);
                    stubMap.put(serviceName, stub);
                }
            } finally {
                writeLock.unlock();
            }
        }
        return (T) stub;
    }

    /**
     * 获取注册中心引用
     *
     * @param uri 连接地址
     */
    public NameService getNameService(URI uri) {
        NameService nameService = ServiceLoaderUtils.load(NameService.class);
        nameService.init(uri.toString());
        return nameService;
    }

    public void startServer() {

    }
}
