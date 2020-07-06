package priv.patrick.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.patrick.rpc.RpcCommonService;
import priv.patrick.rpc.nameservice.NameService;
import priv.patrick.service.HelloService;

import java.net.URI;

public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private static final String NAMESERVICE_URI = "jdbc:mysql://localhost:3306/study?user=root&password=123456";

    public static void main(String[] args) throws Exception {
        RpcCommonService rpcCommonService = new RpcCommonService();
        NameService nameService = rpcCommonService.getNameService(new URI(NAMESERVICE_URI));
        test1(nameService);
    }

    private static void test1(NameService nameService) {
        String serviceName = HelloService.class.getCanonicalName();
        URI uri = nameService.lookupService(serviceName);
        log.info("本次调用服务：{}，地址：{}", serviceName, uri);
        HelloService helloService = RpcCommonService.getStub(uri, HelloService.class);
        Integer result = helloService.add(1, 2);
        log.info("收到响应: {}", result);
    }
}
