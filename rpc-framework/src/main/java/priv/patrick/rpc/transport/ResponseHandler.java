package priv.patrick.rpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class ResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);
    private PendingRequest pendingRequest;

    public ResponseHandler(PendingRequest pendingRequest) {
        this.pendingRequest = pendingRequest;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        CompletableFuture<Object> future = pendingRequest.remove(rpcResponse.getId());
        if (null != future) {
            future.complete(rpcResponse.getResponse());
        } else {
            log.warn("响应已超时,{}", rpcResponse);
        }
    }
}
