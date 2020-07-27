package priv.patrick.rpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CompletableFuture;

/**
 * @author Patrick_zhou
 */
public class ResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private PendingRequest pendingRequest;

    public ResponseHandler(PendingRequest pendingRequest) {
        this.pendingRequest = pendingRequest;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        CompletableFuture<Object> future = pendingRequest.remove(rpcResponse.getId());
        if (null != future) {
            future.complete(rpcResponse.getResponse());
        }
    }
}
