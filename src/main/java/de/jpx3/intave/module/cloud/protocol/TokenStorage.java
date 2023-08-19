package de.jpx3.intave.module.cloud.protocol;

import java.util.ArrayList;
import java.util.List;

public final class TokenStorage {
  private static List<Token> pastTokens = new ArrayList<>();
  private static Token currentToken;

  public static void addToken(Token token) {
    pastTokens.add(token);
    currentToken = token;
  }

  public static Token currentToken() {
    return currentToken;
  }
}
