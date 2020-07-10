package priv.patrick.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.SmartLifecycle;
import priv.patrick.rpc.RpcCommonService;
import priv.patrick.rpc.nameservice.NameService;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@SpringBootApplication
@Slf4j
public class ServerApplication implements SmartLifecycle {

    private boolean isRunning = false;
    private int serverPort;
    private URI localUri;
    private RpcCommonService rpcCommonService;

    public ServerApplication(@Value("${rpc.port}") int serverPort) {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ip = inetAddress.getHostAddress();
            this.localUri = new URI("rpc://" + ip + ":" + serverPort);
            this.rpcCommonService=RpcCommonService.getInstance();
            this.serverPort=serverPort;
        } catch (Exception e) {
            log.error("获取本机ip失败");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void start() {
        log.info("服务开始启动。");
        try {
            //注册服务
            rpcCommonService.registerAllServices(localUri);
            //启动服务端
            rpcCommonService.startServer(serverPort);
        } catch (Exception e) {
            log.error(e.toString());
        }
        isRunning = true;
    }

    @Override
    public void stop() {
        isRunning = false;
        rpcCommonService.close();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }


}
