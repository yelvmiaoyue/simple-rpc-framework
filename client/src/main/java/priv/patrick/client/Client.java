package priv.patrick.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.patrick.model.po.User;
import priv.patrick.rpc.RpcCommonService;
import priv.patrick.service.HelloService;
import priv.patrick.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private static final RpcCommonService rpcCommonService = RpcCommonService.getInstance();

    public static void main(String[] args) {
        try {
            test1();
            test2();
            Thread.sleep(12000);
            test3();
            test4();

        } catch (Exception e) {
            log.error(e.toString());
        } finally {
//            rpcCommonService.close();
        }
    }

    private static void test1() throws Exception {
        HelloService helloService = rpcCommonService.getStub(HelloService.class);
        Integer result = helloService.add(1, 2);
        log.info("1+2 收到响应: {}", result);
    }

    private static void test2() throws Exception {
        HelloService helloService = rpcCommonService.getStub(HelloService.class);
        Map<String, Object> map = helloService.getMap();
        log.info("get map 收到响应: {}", map);
    }

    private static void test3() throws Exception {
        UserService userService = rpcCommonService.getStub(UserService.class);
        List<User> users = userService.listUsers(Arrays.asList(3, 4));
        log.info("listUsers 收到响应: {}", users);
    }

    private static void test4() throws Exception {
        UserService userService = rpcCommonService.getStub(UserService.class);
        User user = userService.getUser(2);
        log.info("getUser 收到响应: {}", user);
    }
}
