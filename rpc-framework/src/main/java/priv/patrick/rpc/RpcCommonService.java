package priv.patrick.rpc;

import priv.patrick.rpc.nameservice.NameService;
import priv.patrick.rpc.utils.ServiceLoaderUtils;

import java.net.URI;

/**
 * RPC通用接口
 *
 * @author Patrick_zhou
 */
public class RpcCommonService {
    /**
     * 获取注册中心引用
     *
     * @param uri 连接地址
     * @return
     */
    public NameService getNameService(URI uri) {
        NameService nameService = ServiceLoaderUtils.load(NameService.class);
        nameService.init(uri.toString());
        return nameService;
    }
}
