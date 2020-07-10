package priv.patrick.rpc.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.patrick.rpc.transport.codec.Decoder;
import priv.patrick.rpc.transport.codec.Encoder;

import java.io.Closeable;
import java.util.Map;

public class NettyServer implements Closeable {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    private NioEventLoopGroup acceptGroup;
    private NioEventLoopGroup workerGroup;
    private Channel channel;
    private Map<String, Object> serviceMap;

    public NettyServer(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public void start(int serverPort) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        acceptGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(5);
        bootstrap.group(acceptGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldPrepender(2, 0, false))
                                .addLast(new Encoder())
                                .addLast(new LengthFieldBasedFrameDecoder(32767, 0, 2, 0, 2))
                                .addLast(new Decoder())
                                .addLast(new RequestHandler(serviceMap));
                    }
                });
        try {
            channel = bootstrap.bind(serverPort).sync().channel();
            log.info("开始监听端口：{}", serverPort);
        } catch (InterruptedException e) {
            log.error("启动失败。{}", e.toString());
        }
    }

    @Override
    public void close() {
        if (acceptGroup != null) {
            acceptGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (channel != null) {
            channel.close();
        }
    }
}
