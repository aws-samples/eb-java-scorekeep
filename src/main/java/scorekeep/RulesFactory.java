package scorekeep;
import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;

public class RulesFactory {
  private SecureRandom random = new SecureRandom();
  private final HashMap<String, Rules> allRules = new HashMap<String, Rules>(1);

  public RulesFactory(){
    String id = "TicTacToe";
    String name = "Tic Tac Toe";
    String[] categories = { "head to head", "quick" };
    Integer[] users = { 2 };
    Integer teams = 0;
    String[] phases = { "Move" };
    String[] moves = { "Roll" };
    String initialState = "X         ";
    Rules tictactoe = new Rules(id, name, categories, users, teams, phases, moves, initialState);

    allRules.put(tictactoe.getId(), tictactoe);
  }

  public Rules getRules(String id) {
    return allRules.get(id);
  }

  public Collection<Rules> getAllRules() {
    return allRules.values();
  }
}