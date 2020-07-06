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
    @Value("${nameService.uri}")
    private String nameServiceUri;
    private URI uri;

    public ServerApplication(@Value("${server.port}") int port) {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ip = inetAddress.getHostAddress();
            this.uri = new URI("rpc://" + ip + ":" + port);
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
        RpcCommonService rpcCommonService = new RpcCommonService();
        try {
            NameService nameService = rpcCommonService.getNameService(new URI(nameServiceUri));
            log.info("获取到注册中心");
            //向注册中心注册自己提供的服务
            this.registerAllServices(nameService);
            //启动服务端
            rpcCommonService.startServer();
        } catch (Exception e) {
            log.error(e.toString());
        }
        isRunning = true;
    }

    private void registerAllServices(NameService nameService) {
        try {
            URL resource = this.getClass().getClassLoader().getResource("META-INF/services");
            if (resource == null) {
                log.error("读取服务异常");
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

                nameService.registerService(className, uri);
            }
        } catch (URISyntaxException e) {
            log.error(e.toString());
        }
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
