package priv.patrick.rpc.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import priv.patrick.rpc.serialize.SerializeUtils;

import java.util.List;

/**
 * @author Patrick_zhou
 */
public class Decoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] msg = new byte[in.readableBytes()];
        in.readBytes(msg);
        Object deserialize = SerializeUtils.deserialize(msg);
        out.add(deserialize);
    }
}
