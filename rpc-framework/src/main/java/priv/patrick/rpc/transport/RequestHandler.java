package priv.patrick.rpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import priv.patrick.rpc.stub.Argument;
import priv.patrick.rpc.stub.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Patrick_zhou
 */
public class RequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private Map<String, Object> serviceMap;

    public RequestHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        RpcResponse response = this.handle(request);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    private RpcResponse handle(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        response.setId(request.getId());
        Object instance = serviceMap.get(request.getInterfaceName());
        if (instance == null) {
            return null;
        }
        try {
            Class<?>[] types = Arrays.stream(request.getArguments()).map(Argument::getType).toArray(Class<?>[]::new);
            Method method = instance.getClass().getMethod(request.getMethodName(), types);
            Object[] args = Arrays.stream(request.getArguments()).map(Argument::getValue).toArray(Object[]::new);
            Object result = method.invoke(instance, args);
            response.setResponse(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
        return response;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("关闭channel");
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
