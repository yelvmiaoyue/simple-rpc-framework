package priv.patrick.rpc.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import priv.patrick.rpc.transport.codec.Encoder;
import priv.patrick.rpc.transport.codec.Decoder;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class NettyClient implements Closeable {
    List<Channel> channels = new ArrayList<>();
    private NioEventLoopGroup nioEventLoopGroup;
    private Bootstrap bootstrap;
    private PendingRequest pendingRequest;
    private final long DEFAULT_CONNECTION_TIMEOUT= 10000;

    public NettyClient(PendingRequest pendingRequest) {
        this.pendingRequest = pendingRequest;
    }

    public synchronized Channel createChannel(InetSocketAddress address) {
        if (bootstrap == null) {
            this.init();
        }
        ChannelFuture channelFuture = bootstrap.connect(address);
        try {
            if(!channelFuture.await(DEFAULT_CONNECTION_TIMEOUT)){
                throw new RuntimeException("无法连接到目标地址" + address.toString());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Channel channel = channelFuture.channel();
        if (channel == null || !channel.isActive()) {
            throw new RuntimeException("无法连接到目标地址" + address.toString());
        }
        channels.add(channel);
        return channel;
    }

    private void init() {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        this.nioEventLoopGroup = eventLoopGroup;
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldPrepender(2, 0, false))
                                .addLast(new Encoder())
                                .addLast(new LengthFieldBasedFrameDecoder(32767, 0, 2, 0, 2))
                                .addLast(new Decoder())
                                .addLast(new ResponseHandler(pendingRequest));
                    }
                });
        this.bootstrap = bootstrap;
    }


    @Override
    public void close() {
        for (Channel channel : channels) {
            if (null != channel) {
                channel.close();
            }
        }
        if (nioEventLoopGroup != null) {
            nioEventLoopGroup.shutdownGracefully();
        }
    }
}
