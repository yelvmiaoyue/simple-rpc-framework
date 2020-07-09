package priv.patrick.rpc.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.concurrent.EventExecutorGroup;
import priv.patrick.rpc.serialize.SerializeUtils;
import priv.patrick.rpc.transport.RpcResponse;

import java.util.List;

public class Decoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] msg=new byte[in.readableBytes()];
        in.readBytes(msg);
        out.add(SerializeUtils.deserialize(msg));
    }
}
