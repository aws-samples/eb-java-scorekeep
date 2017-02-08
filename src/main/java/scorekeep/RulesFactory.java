package scorekeep;
import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;

public class RulesFactory {
  private SecureRandom random = new SecureRandom();
  private final HashMap<String, Rules> allRules = new HashMap<String, Rules>(1);

  public RulesFactory(){
    Rules tictactoe = TicTacToe.getRules();
    allRules.put(tictactoe.getId(), tictactoe);
  }

  public Rules getRules(String id) {
    return allRules.get(id);
  }

  public Collection<Rules> getAllRules() {
    return allRules.values();
  }
}