package de.jpx3.intave.reflect.hitbox.typeaccess;

import de.jpx3.intave.access.IntaveInternalException;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ServerClassByteResolver {
  public static List<byte[]> pollBytesOf(String filePrefix) {
    URL resource = Bukkit.getServer().getClass().getProtectionDomain().getCodeSource().getLocation();
    String file = resource.getFile();
    file = file.replace("%20", " ");
    return resolveBytesOf(file, filePrefix);
  }

  private static List<byte[]> resolveBytesOf(
    String file,
    String filePrefix
  ) {
    try {
      List<byte[]> bytes = new ArrayList<>();
      ZipFile zipFile = new ZipFile(file);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry zipEntry = entries.nextElement();
        String name = zipEntry.getName();
        if (classFile(name) && name.startsWith(filePrefix)) {
          bytes.add(readBytesOf(zipFile.getInputStream(zipEntry)));
        }
      }
      return bytes;
    } catch (IOException e) {
      throw new IntaveInternalException(e);
    }
  }

  private static boolean classFile(String fileName) {
    return fileName.endsWith(".class");
  }

  private static byte[] readBytesOf(InputStream inputStream) {
    try {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      byte[] data = new byte[16384];
      int nRead;
      while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }
      return buffer.toByteArray();
    } catch (IOException e) {
      throw new IntaveInternalException(e);
    }
  }
}