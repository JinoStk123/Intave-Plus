package de.jpx3.intave.event.service;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.event.service.transaction.TransactionCallBackData;
import de.jpx3.intave.event.service.transaction.TransactionFeedbackCallback;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

public final class TransactionFeedbackService extends PacketAdapter {
  public final static short TRANSACTION_MIN_CODE = Short.MIN_VALUE;
  public final static short TRANSACTION_MAX_CODE = -16370;
  private final static ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

  public TransactionFeedbackService(Plugin plugin) {
    super(plugin, PacketType.Play.Client.TRANSACTION);
    protocolManager.addPacketListener(this);
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
    Player player = event.getPlayer();
    Checkable checkable = CheckableRegistry.checkableOf(player);

    if (checkable == null) {
      return;
    }
    short transactionCode = event.getPacket().getShorts().readSafely(0);
    if (transactionCode <= TRANSACTION_MAX_CODE) {
      TransactionCallBackData<?> transactionResponse = checkable.transactionFeedbackMap().get(transactionCode);
      if (transactionResponse == null) {
        return;
      }
      try {
        transactionResponse.transactionFeedbackCallback().success(
          player,
          convertInstanceOfObject(transactionResponse.obj())
        );
      } catch (Exception e) {
        e.printStackTrace();
      }
      checkable.transactionFeedbackMap().remove(transactionCode);
    }
  }

  private <T> T convertInstanceOfObject(Object o) {
    try {
      //noinspection unchecked
      return (T) o;
    } catch (ClassCastException e) {
      return null;
    }
  }

  public <T> void requestPong(Player player, T target, TransactionFeedbackCallback<T> callback) {
    Short id = acquireNewId(player, target, callback);
    if (id != null) {
      sendTransactionPacket(player, id);
    }
  }

  private <T> Short acquireNewId(Player player, T obj, TransactionFeedbackCallback<T> callback) {
    Checkable checkable = CheckableRegistry.checkableOf(player);
    if (checkable == null) {
      return null;
    }
    short transactionCounter = ++checkable.transactionCounter;
    if (transactionCounter >= TRANSACTION_MAX_CODE) {
      transactionCounter = TRANSACTION_MIN_CODE;
    }
    TransactionCallBackData<T> transactionCallBackData = new TransactionCallBackData<>(callback, obj);
    checkable.transactionFeedbackMap().put(transactionCounter, transactionCallBackData);
    return transactionCounter;
  }

  private void sendTransactionPacket(Player receiver, short id) {
    PacketContainer transactionPacket = protocolManager.createPacket(PacketType.Play.Server.TRANSACTION);
    transactionPacket.getIntegers().write(0, 0);
    transactionPacket.getShorts().write(0, id);
    transactionPacket.getBooleans().write(0, false);
    try {
      protocolManager.sendServerPacket(receiver, transactionPacket);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}