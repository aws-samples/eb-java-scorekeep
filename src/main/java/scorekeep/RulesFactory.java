package scorekeep;

import java.util.Collection;
import java.util.HashMap;

public class RulesFactory {
    private final HashMap<String, Rules> allRules = new HashMap<String, Rules>(1);

    public RulesFactory() {
        Rules tictactoe = TicTacToe.getRules();
        Rules rolit = Rolit.getRules();
        allRules.put(tictactoe.getId(), tictactoe);
        allRules.put(rolit.getId(), rolit);
    }

    public Rules getRules(String id) {
        return allRules.get(id);
    }

    public Collection<Rules> getAllRules() {
        return allRules.values();
    }
}