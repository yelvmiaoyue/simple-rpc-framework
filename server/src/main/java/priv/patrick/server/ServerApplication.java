package priv.patrick.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.SmartLifecycle;
import priv.patrick.rpc.RpcCommonService;
import priv.patrick.rpc.nameservice.NameService;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
@Slf4j
public class ServerApplication implements SmartLifecycle {

    private boolean isRunning = false;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void start() {
        log.info("服务开始启动。");
        RpcCommonService rpcCommonService = new RpcCommonService();
        NameService nameService;
        try {
            nameService = rpcCommonService.getNameService(new URI("jdbc:mysql://localhost:3306/study?user=root&password=123456"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        isRunning = true;

    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }


}
