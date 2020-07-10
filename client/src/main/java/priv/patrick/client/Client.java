package priv.patrick.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.patrick.rpc.RpcCommonService;
import priv.patrick.service.HelloService;

public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private static final RpcCommonService rpcCommonService = RpcCommonService.getInstance();

    public static void main(String[] args) {
        try {
            test1();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rpcCommonService.close();
        }
    }

    private static void test1() {
        HelloService helloService = rpcCommonService.getStub(HelloService.class);
        Integer result = helloService.add(1, 2);
        log.info("收到响应: {}", result);
    }

}
