package scorekeep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rolit {
    private static final Logger logger = LoggerFactory.getLogger(Rolit.class);

    public static Rules getRules() {
        String id = "Rolit";
        String name = "Rolit";
        String[] categories = {"head to head", "quick"};
        Integer[] users = {2};
        Integer teams = 0;
        String[] phases = {"Move"};
        String[] moves = {"Roll"};
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1");
        for (int i = 0; i < 65; i++) {
            stringBuilder.append(" ");
        }
        String initialState = stringBuilder.toString();
        Rules rolit = new Rules().setId(id)
                .setName(name)
                .setCategories(categories)
                .setUsers(users)
                .setTeams(teams)
                .setPhases(phases)
                .setMoves(moves)
                .setInitialState(initialState);
        return rolit;
    }

    /* State is a string with one character that indicates the current turn
     * (X or O), or win state (A or B). The remaining 9 characters map to spaces
     * on the game board.
     *        1 2 3
     *        4 5 6
     *        7 8 9
     * A move is a two character string that indicates the moving player (X or O)
     * and the space on the game board. After applying the move, the function
     * converts the game state of the current player into an integer and compares
     * it against known win states to check for victory.
     */
    public static String move(String oldState, String moveText) {
        // current state in char[]
        char[] oldchar = oldState.toCharArray();
        // move in char[]
        char[] movchar = moveText.toCharArray();
        // validate move and update state
        if (oldchar[1] != ' ') {
            logger.error("Game is ended");
            return new String(oldchar);
        }

        if (movchar[0] == oldchar[0]) {
            oldchar[Character.getNumericValue(movchar[1])] = movchar[0];
            if (movchar[0] == '1') {
                oldchar[0] = '2';
            } else if (movchar[0] == '2') {
                oldchar[0] = '1';
            }
        } else {
            logger.error("Not your turn");
        }

        char[][] gameMatrix = makeMatrix(oldchar);

        int cellId = 0;

        if (movchar.length == 2){
            cellId = Integer.parseInt(moveText.substring(1));
        } else if (movchar.length == 3) {
            cellId = Integer.parseInt(moveText.substring(1));
        }

        

        // check for victory
        int winner = checkWin(oldchar);
        if (winner != -1) {
            oldchar[1] = Integer.toString(winner).charAt(0);
            oldchar[0] = Integer.toString(winner).charAt(0);
        }
        String newState = new String(oldchar);
        return newState;
    }

    public static char[][] makeMatrix(char[] state) {
        char[][] matrix = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                matrix[i][j] = state[i * (j + 1) + j + 2];
            }
        }
        return matrix;
    }

    public static int checkWin(char[] state) {
        boolean isAllOccupied = true;
        for (int i = 2; i < 66; i++) {
            if (state[i] == ' ') {
                isAllOccupied = false;
                break;
            }
        }
        if (!isAllOccupied)
            return -1;
        int playersScores[] = new int[4];
        for (int i = 2; i < 66; i++) {
            if (state[i] == '1') {
                playersScores[0]++;
            }
            if (state[i] == '2') {
                playersScores[1]++;
            }
            if (state[i] == '3') {
                playersScores[2]++;
            }
            if (state[i] == '4') {
                playersScores[3]++;
            }
        }
        int max = playersScores[0];
        int winner = 1;
        for (int i = 1; i < 4; i++) {
            if (max < playersScores[i]) {
                max = playersScores[i];
                winner = i + 1;
            }
        }
        return winner;
    }
}
