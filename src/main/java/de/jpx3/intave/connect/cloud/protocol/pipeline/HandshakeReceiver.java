package de.jpx3.intave.connect.cloud.protocol.pipeline;

import de.jpx3.intave.connect.cloud.Session;
import de.jpx3.intave.connect.cloud.protocol.Packet;
import de.jpx3.intave.connect.cloud.protocol.PacketRegistry;
import de.jpx3.intave.connect.cloud.protocol.Token;
import de.jpx3.intave.connect.cloud.protocol.listener.Clientbound;
import de.jpx3.intave.connect.cloud.protocol.packets.ClientboundHelloPacket;
import de.jpx3.intave.connect.cloud.protocol.packets.ServerboundConfirmEncryptionPacket;
import de.jpx3.intave.connect.cloud.protocol.packets.ServerboundHelloPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.net.URISyntaxException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import static de.jpx3.intave.connect.cloud.protocol.Direction.CLIENTBOUND;
import static de.jpx3.intave.connect.cloud.protocol.Direction.SERVERBOUND;

public final class HandshakeReceiver extends ChannelInboundHandlerAdapter implements Clientbound {
  private final Session session;

  public HandshakeReceiver(Session session) {
    this.session = session;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    ServerboundHelloPacket serverHelloPacket = ServerboundHelloPacket.builder()
      .token(session.shard() == null ? new Token(new byte[0], 0) : session.shard().token())
      .supportedEncryptionAlgorithms(Security.getAlgorithms("Cipher").stream().filter(s -> s.startsWith("AES")).collect(Collectors.toList()))
      .supportedEncryptionKeySizes(Collections.singletonList(256))
      .supportedCompressionAlgorithms(Collections.singletonList("GZIP"))
      .supportedHMACAlgorithms(new ArrayList<>(Security.getAlgorithms("Mac")))
      .clientboundProtocol(PacketRegistry.packetSpecsFor(CLIENTBOUND))
      .serverboundProtocol(PacketRegistry.packetSpecsFor(SERVERBOUND))
      .build();
    ctx.writeAndFlush(serverHelloPacket);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object object) {
    Packet<?> packet = (Packet<?>) object;
    if (!(packet instanceof ClientboundHelloPacket)) {
      //noinspection unchecked
      session.receivePacketLater((Packet<Clientbound>) packet);
      return;
    }
    //noinspection unchecked
    ((Packet<Clientbound>) packet).accept(this);
    ctx.writeAndFlush(new ServerboundConfirmEncryptionPacket()).addListener(future -> {
      session.setProcessor(new StandardPacketReceiver(session));
    });
  }

  @Override
  public void onClientHello(ClientboundHelloPacket packet) {
//    PacketRegistry.enterIdAssignment(CLIENTBOUND, packet.clientboundPackets());
//    PacketRegistry.enterIdAssignment(SERVERBOUND, packet.serverboundPackets());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
    throwable.printStackTrace();
    channelHandlerContext.close();
  }
}
