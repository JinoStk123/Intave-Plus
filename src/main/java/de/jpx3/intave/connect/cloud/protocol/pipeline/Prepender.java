package de.jpx3.intave.connect.cloud.protocol.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class Prepender extends MessageToByteEncoder<ByteBuf> {
  @Override
  protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
    int i = msg.readableBytes();
    out.writeByte(-1);
    out.writeInt(i);
    out.writeBytes(msg, msg.readerIndex(), i);
  }
}
