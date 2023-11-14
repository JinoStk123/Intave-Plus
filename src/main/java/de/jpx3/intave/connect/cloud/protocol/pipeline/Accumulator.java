package de.jpx3.intave.connect.cloud.protocol.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public final class Accumulator extends ByteToMessageDecoder {
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    in.markReaderIndex();
    if (!in.isReadable()) {
      in.resetReaderIndex();
      return;
    }
    // packet header
    if (in.readableBytes() < 5) {
      // reset and await full packet content
      in.resetReaderIndex();
      return;
    }
    byte markerBit = in.readByte();
    if (markerBit == -1) {
      int length = in.readInt();
      if (in.readableBytes() < length) {
        // reset and await full packet content
        in.resetReaderIndex();
        return;
      }
      // read full packet content
      out.add(in.readBytes(length));
    }
  }
}