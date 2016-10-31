package scorekeep;
import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;

public class RulesFactory {
  private SecureRandom random = new SecureRandom();
  private final HashMap<String, Rules> allRules = new HashMap<String, Rules>(1);

  public RulesFactory(){
    String id = "101";
    String name = "Liars Dice";
    String[] categories = { "dice", "deception" };
    Integer[] users = { 2, 3, 4, 5, 6 };
    Integer teams = 0;
    String[] phases = { "Move", "Challenge" };
    String[] moves = { "Roll", "Challenge" };
    String initialState = "each player has six dice";
    Rules testRules = new Rules(id, name, categories, users, teams, phases, moves, initialState);

    allRules.put(testRules.getId(), testRules);

    String id2 = "102";
    String name2 = "Tic Tac Toe";
    String[] categories2 = { "head to head", "quick" };
    Integer[] users2 = { 2 };
    Integer teams2 = 0;
    String[] phases2 = { "Move" };
    String[] moves2 = { "Roll" };
    String initialState2 = "XNNNNNNNNN";
    testRules = new Rules(id2, name2, categories2, users2, teams2, phases2, moves2, initialState2);

    allRules.put(testRules.getId(), testRules);
  }

  public Rules getRules(String id) {
    return allRules.get(id);
  }

  public Collection<Rules> getAllRules() {
    return allRules.values();
  }
}