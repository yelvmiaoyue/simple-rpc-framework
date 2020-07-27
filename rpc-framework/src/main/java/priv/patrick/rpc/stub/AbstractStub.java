package priv.patrick.rpc.stub;

import io.netty.channel.Channel;
import org.apache.commons.collections4.CollectionUtils;
import priv.patrick.rpc.RpcCommonService;
import priv.patrick.rpc.transport.ResponseFuture;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * stub 抽象模板
 *
 * @author Patrick_zhou
 */
public abstract class AbstractStub {
    private ServiceInfo serviceInfo;

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    protected <T> T invoke(RpcRequest rpcRequest) {
        List<URI> uris = serviceInfo.getUris();
        if (CollectionUtils.isEmpty(uris)) {
            throw new RuntimeException("no available service!");
        }
        //拿到uri对应的channel
        try {
            Channel channel = RpcCommonService.getChannel(this.loadBalance(uris));
            //通过future异步拿到对应请求的响应
            CompletableFuture<Object> result = new CompletableFuture<>();
            serviceInfo.getPendingRequest().put(new ResponseFuture(rpcRequest.getId(), result));
            channel.writeAndFlush(rpcRequest).addListener(future -> {
                if (!future.isSuccess()) {
                    result.completeExceptionally(future.cause());
                    channel.close();
                }
            });
            return (T) result.get();
        } catch (InterruptedException e) {
            throw new RuntimeException("can not get channel!" + e.toString());
        } catch (Exception e) {
            throw new RuntimeException("调用异常:" + rpcRequest + "." + e.toString());
        }
    }

    /**
     * 负载均衡
     *
     * @param uris 可选地址
     */
    private URI loadBalance(List<URI> uris) {
        //随机算法
        return uris.get(ThreadLocalRandom.current().nextInt(uris.size()));
    }
}
