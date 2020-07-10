package priv.patrick.rpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.patrick.rpc.stub.Argument;
import priv.patrick.rpc.stub.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public class RequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Map<String, Object> serviceMap;

    public RequestHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        log.info("接收到请求：{}", request);
        RpcResponse response = this.handle(request);
        ctx.writeAndFlush(response);
    }

    private RpcResponse handle(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        response.setId(request.getId());
        Object instance = serviceMap.get(request.getInterfaceName());
        if (instance == null) {
            log.error("不存在该服务：{}", request);
            return null;
        }
        try {
            Class<?>[] types = Arrays.stream(request.getArguments()).map(Argument::getType).toArray(Class<?>[]::new);
            Method method = instance.getClass().getMethod(request.getMethodName(), types);
            Object[] args = Arrays.stream(request.getArguments()).map(Argument::getValue).toArray(Object[]::new);
            Object result = method.invoke(instance, args);
            response.setResponse(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("调用方法报错：{}，入参{}", e.toString(), request);
            return null;
        }
        return response;
    }
}
