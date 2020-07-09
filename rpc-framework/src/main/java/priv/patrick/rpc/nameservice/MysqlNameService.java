package priv.patrick.rpc.nameservice;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick_zhou
 */
public class MysqlNameService implements NameService {
    private static final Logger log = LoggerFactory.getLogger(MysqlNameService.class);
    private Connection connection = null;
    private ServiceProvider localCache = new ServiceProvider();
    private final Object lock = new Object();

    //todo 定期刷新缓存,并通知所有stub更新

    @Override
    public synchronized void registerService(String serviceName, URI uri) {
        ServiceProvider serviceProvider = this.getAllServices();

        List<URI> list = serviceProvider.get(serviceName);
        //包含当前服务信息就返回
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.indexOf(uri) != -1) {
                return;
            }
        }

        //新增服务信息
        String insert = "insert into nameservice(id,service_name,uri) values(default,?,?)";
        try (PreparedStatement sql = connection.prepareStatement(insert)) {
            sql.setString(1, serviceName);
            sql.setString(2, uri.toString());
            sql.executeUpdate();
        } catch (SQLException e) {
            log.error("insert data fail !" + e.getMessage());
        }
    }

    @Override
    public List<URI> lookupService(String serviceName) {
        //查本地缓存
        List<URI> uris = localCache.get(serviceName);
        if (CollectionUtils.isEmpty(uris)) {
            //查数据库，更新缓存
            synchronized (lock) {
                //二次判断，减轻数据库压力
                uris = localCache.get(serviceName);
                if (CollectionUtils.isEmpty(uris)) {
                    localCache = this.getAllServices();
                }
            }
        }

        uris = localCache.get(serviceName);
        if (CollectionUtils.isEmpty(uris)) {
            return null;
        } else {
            return uris;
        }
    }

    private ServiceProvider getAllServices() {
        ServiceProvider serviceProvider = new ServiceProvider();
        String read = "select id,service_name,uri from nameservice";
        try (PreparedStatement sql = connection.prepareStatement(read);
             ResultSet resultSet = sql.executeQuery()) {
            while (resultSet.next()) {
                URI uri = getUriInstance(resultSet.getString("uri"));
                String serviceName = resultSet.getString("service_name");
                serviceProvider.compute(serviceName, (k, v) -> {
                    if (v == null) {
                        v = new ArrayList<>();
                    }
                    v.add(uri);
                    return v;
                });
            }
        } catch (Exception e) {
            log.error("read data fail !" + e.getMessage());
        }
        return serviceProvider;
    }

    private static URI getUriInstance(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            log.error("create URI fail !" + e.getMessage());
            return null;
        }
    }

    @Override
    public void init(String uri) {
        try {
            connection = DriverManager.getConnection(uri);
            localCache = this.getAllServices();
        } catch (SQLException e) {
            log.error("无法连接到数据库。");
        }
    }
}
