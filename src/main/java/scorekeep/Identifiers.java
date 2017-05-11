package scorekeep;

import java.security.SecureRandom;
import java.math.BigInteger;

public class Identifiers {
  private static final SecureRandom secureRandom = new SecureRandom();

  public static String random() {
    return new BigInteger(40, secureRandom).toString(32).toUpperCase();
  }

}