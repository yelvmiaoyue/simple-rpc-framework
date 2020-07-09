package priv.patrick.rpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class ResponseHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);
    private PendingRequest pendingRequest;

    public ResponseHandler(PendingRequest pendingRequest) {
        this.pendingRequest = pendingRequest;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse rpcResponse = (RpcResponse) msg;
        CompletableFuture<Object> future = pendingRequest.remove(rpcResponse.getId());
        if (null != future) {
            future.complete(rpcResponse.getResponse());
        } else {
            log.warn("响应已超时,{}", rpcResponse);
        }
    }
}
