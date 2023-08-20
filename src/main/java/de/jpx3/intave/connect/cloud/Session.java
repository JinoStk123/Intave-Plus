package de.jpx3.intave.connect.cloud;

import de.jpx3.intave.connect.cloud.protocol.Packet;
import de.jpx3.intave.connect.cloud.protocol.ProtocolSpecification;
import de.jpx3.intave.connect.cloud.protocol.Shard;
import de.jpx3.intave.connect.cloud.protocol.listener.Clientbound;
import de.jpx3.intave.connect.cloud.protocol.listener.Serverbound;
import de.jpx3.intave.connect.cloud.protocol.pipeline.HandshakeReceiver;
import de.jpx3.intave.connect.cloud.protocol.pipeline.PacketCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.security.Key;
import java.util.ArrayDeque;
import java.util.Queue;

import static de.jpx3.intave.connect.cloud.protocol.Direction.CLIENTBOUND;
import static de.jpx3.intave.connect.cloud.protocol.Direction.SERVERBOUND;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class Session {
  private Shard shard;
  private Cloud cloud;
  private Channel channel;
  private final ProtocolSpecification protocol = new ProtocolSpecification();
  private final Queue<Packet<Serverbound>> pendingOutgoing = new ArrayDeque<>();
  private final Queue<Packet<Clientbound>> pendingIncoming = new ArrayDeque<>();

//  private Key rsaKey;
//  private Key aesKey;

  public Session(Shard shard, Cloud cloud) {
    this.shard = shard;
    this.cloud = cloud;
  }

  public void init() {
    EventLoopGroup group = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap()
      .group(group)
      .channel(NioSocketChannel.class)
      .handler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
          ch.pipeline()
            .addLast("timeout", new ReadTimeoutHandler(30))
//            .addLast("decompression", new Decompression(256))
//            .addLast("compression", new Compression(256))
            .addLast("codec", new PacketCodec(protocol, CLIENTBOUND))
            .addLast("processor", new HandshakeReceiver(Session.this))
          ;
        }
      });

    try {
      // todo replace with actual cloud address
      boolean connected = bootstrap.connect("localhost", 2024).addListener(future -> {
        if (!future.isSuccess()) {
          future.cause().printStackTrace();
          return;
        }
        channel = ((ChannelFuture) future).channel();
        channel.closeFuture().addListener(future2 -> {
          System.out.println("Connection closed");
          group.shutdownGracefully();
        });
      }).await(10, SECONDS);
      if (!connected) {
        System.out.println("Failed to connect to cloud service");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void sendPacket(Packet<Serverbound> packet) {
    if (channel == null || !channel.isActive()) {
      pendingOutgoing.add(packet);
      return;
    }
    while (!pendingOutgoing.isEmpty()) {
      channel.writeAndFlush(pendingOutgoing.poll());
    }
    channel.writeAndFlush(packet);
  }

  public void receivePacketLater(Packet<Clientbound> packet) {
    pendingIncoming.add(packet);
  }

  public Queue<Packet<Clientbound>> pendingIncoming() {
    return pendingIncoming;
  }

  public void setProcessor(ChannelHandler handler) {
    pipeline().replace("processor", "processor", handler);
  }

  public ChannelPipeline pipeline() {
    return channel.pipeline();
  }

  public Shard shard() {
    return shard;
  }

  public void close() {
    channel.close();
  }

  public boolean canSend(Packet<Serverbound> packet) {
    return protocol.packetAvailable(SERVERBOUND, packet.name());
  }
}
