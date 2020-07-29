package priv.patrick.rpc.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import priv.patrick.rpc.RpcCommonService;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Patrick_zhou
 */
public class ReconnectHandler extends ChannelInboundHandlerAdapter {
    private final Bootstrap bootstrap;
    private InetSocketAddress address;
    private URI uri;

    public ReconnectHandler(URI uri, Bootstrap bootstrap, InetSocketAddress address) {
        this.bootstrap = bootstrap;
        this.address = address;
        this.uri = uri;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        synchronized (bootstrap) {
            //当程序关闭时会触发此方法，不判断shutting down 会导致定时任务报错,停顿100ms让eventloopgroup切换到关闭状态
            Thread.sleep(100);
            if (ctx.channel().isActive() || bootstrap.config().group().isShuttingDown()) {
                return;
            }
            System.out.println("连接断开");
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
            scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> {
                ChannelFuture connect = bootstrap.connect(address);
                try {
                    boolean success = connect.await(5000);
                    if (success) {
                        System.out.println("重连成功");
                        RpcCommonService.updateChannel(uri, connect.channel());
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    scheduledThreadPoolExecutor.shutdown();
                }
            }, 0, 30, TimeUnit.SECONDS);
        }
    }
}
