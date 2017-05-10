package scorekeep;
import java.util.*;

public class RulesFactory {
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