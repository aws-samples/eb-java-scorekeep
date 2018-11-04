package scorekeep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rolit {
    private static final Logger logger = LoggerFactory.getLogger(Rolit.class);

    public static Rules getRules() {
        String id = "Rolit";
        String name = "Rolit";
        String[] categories = {"head to head", "quick"};
        Integer[] users = {2, 3, 4};
        Integer teams = 0;
        String[] phases = {"Move"};
        String[] moves = {"Roll"};
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("X");
        for (int i = 0; i < 64; i++) {
            stringBuilder.append(" ");
        }
        String initialState = stringBuilder.toString();
        Rules tictactoe = new Rules().setId(id)
                .setName(name)
                .setCategories(categories)
                .setUsers(users)
                .setTeams(teams)
                .setPhases(phases)
                .setMoves(moves)
                .setInitialState(initialState);
        return tictactoe;
    }
}
