package priv.patrick.rpc;

import io.netty.channel.Channel;
import org.apache.commons.collections4.CollectionUtils;
import priv.patrick.rpc.nameservice.NameService;
import priv.patrick.rpc.spi.ServiceLoaderUtils;
import priv.patrick.rpc.stub.AbstractStub;
import priv.patrick.rpc.stub.ServiceInfo;
import priv.patrick.rpc.stub.StubFactory;
import priv.patrick.rpc.transport.NettyClient;
import priv.patrick.rpc.transport.NettyServer;
import priv.patrick.rpc.transport.PendingRequest;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
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
public class RpcCommonService implements Closeable {
    private static RpcCommonService instance;

    static {
        try {
            instance = new RpcCommonService();
        } catch (SQLException e) {
            throw new RuntimeException("rpc service init fail !" + e.toString());
        }
    }

    private static Map<URI, Channel> channelMap;
    private static Map<String, Object> serviceMap;
    private static Map<Class<?>, AbstractStub> stubMap;
    private static final String NAMESERVICE_URI = "jdbc:mysql://localhost:3306/study?user=root&password=123456";
    private static NameService nameService;
    private static PendingRequest pendingRequest;
    private static NettyClient client;
    private static NettyServer server;

    private static final ReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();
    private static final Lock READ_LOCK = READ_WRITE_LOCK.readLock();
    private static final Lock WRITE_LOCK = READ_WRITE_LOCK.writeLock();

    private RpcCommonService() throws SQLException {
        channelMap = new ConcurrentHashMap<>();
        serviceMap = new HashMap<>();
        stubMap = new ConcurrentHashMap<>();
        nameService = createNameService(URI.create(NAMESERVICE_URI));
        pendingRequest = new PendingRequest();
        client = new NettyClient(pendingRequest);
        server = new NettyServer(serviceMap);
    }

    /**
     * 获取单例对象
     */
    public static RpcCommonService getInstance() {
        return instance;
    }

    /**
     * 生成目标stub
     *
     * @param serviceName stub实现的接口class对象
     * @param <T>         接口类型
     */
    public <T> T getStub(Class<T> serviceName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        if (serviceName == null) {
            return null;
        }

        AbstractStub stub;
        READ_LOCK.lock();
        try {
            stub = stubMap.get(serviceName);
        } finally {
            READ_LOCK.unlock();
        }
        if (stub == null) {
            WRITE_LOCK.lock();
            try {
                //再次验证
                stub = stubMap.get(serviceName);

                if (stub == null) {
                    String service = serviceName.getCanonicalName();
                    List<URI> uris = nameService.lookupService(service);
                    if (CollectionUtils.isEmpty(uris)) {
                        throw new RuntimeException("没有可达目标地址。");
                    }
                    ServiceInfo serviceInfo = new ServiceInfo(uris, pendingRequest);
                    stub = StubFactory.createStub(serviceInfo, serviceName);
                    stubMap.put(serviceName, stub);
                }
            } finally {
                WRITE_LOCK.unlock();
            }
        }
        return (T) stub;
    }

    /**
     * 创建注册中心
     *
     * @param uri 连接地址
     */
    private NameService createNameService(URI uri) throws SQLException {
        NameService nameService = ServiceLoaderUtils.load(NameService.class);
        nameService.init(uri.toString());
        return nameService;
    }

    /**
     * 服务端启动
     *
     * @param localUri
     * @param serverPort 服务端监听端口
     */
    public void startServer(URI localUri, int serverPort) throws InterruptedException, URISyntaxException, ClassNotFoundException {
        this.registerAllServices(localUri);
        server.start(serverPort);
    }

    /**
     * 获取uri对应的channel
     *
     * @param uri 目标服务地址
     */
    public static Channel getChannel(URI uri) throws InterruptedException {
        Channel channel;
        READ_LOCK.lock();
        try {
            channel = channelMap.get(uri);
        } finally {
            READ_LOCK.unlock();
        }
        if (channel == null) {
            WRITE_LOCK.lock();
            try {
                channel = channelMap.get(uri);

                if (channel == null) {
                    channel = client.createChannel(uri, new InetSocketAddress(uri.getHost(), uri.getPort()));
                    channelMap.put(uri, channel);
                }
            } finally {
                WRITE_LOCK.unlock();
            }
        }
        return channel;
    }

    /**
     * 服务端注册
     *
     * @param uri 服务端地址
     */
    public synchronized void registerAllServices(URI uri) throws URISyntaxException, ClassNotFoundException {
        URL resource = this.getClass().getClassLoader().getResource("META-INF/services");
        if (resource == null) {
            return;
        }
        File file = new File(resource.toURI());
        File[] childFiles = file.listFiles();
        if (childFiles == null || childFiles.length == 0) {
            return;
        }

        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                continue;
            }
            String fullClassName = childFile.getPath();
            String className = fullClassName.substring(fullClassName.indexOf("META-INF\\services") + 18);
            //注册到注册中心
            nameService.registerService(className, uri);
            //注册本地实例，供 RequestHandler使用
            Object instance = ServiceLoaderUtils.load(Class.forName(className));
            serviceMap.put(className, instance);
        }
    }

    /**
     * 重连时更新map
     */
    public static void updateChannel(URI uri, Channel channel) {
        channelMap.put(uri, channel);
    }

    @Override
    public void close() {
        try {
            nameService.close();
        } catch (Exception ignored) {
        }
        server.close();
        client.close();
    }
}
