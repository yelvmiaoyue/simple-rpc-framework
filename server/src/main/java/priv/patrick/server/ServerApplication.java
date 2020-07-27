package priv.patrick.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.SmartLifecycle;
import priv.patrick.rpc.RpcCommonService;

import java.net.InetAddress;
import java.net.URI;

/**
 * @author Patrick_zhou
 */
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
            this.rpcCommonService = RpcCommonService.getInstance();
            this.serverPort = serverPort;
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
            rpcCommonService.startServer(localUri, serverPort);
            isRunning = true;
        } catch (Exception e) {
            log.error(e.toString());
            isRunning = false;
        }
    }

    @Override
    public void stop() {
        rpcCommonService.close();
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }


}
