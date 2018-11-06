package scorekeep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
        stringBuilder.append("0");
        for (int i = 0; i < 65; i++) {
            if (i == 28)
                stringBuilder.append('0');
            else if (i == 29)
                stringBuilder.append('1');
            else if (i == 36)
                stringBuilder.append('2');
            else if (i == 37)
                stringBuilder.append('3');
            else
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

        char[][] gameMatrix = makeMatrix(oldchar);

        int cellId = 0;

        if (movchar.length == 2) {
            cellId = Integer.parseInt(moveText.substring(1));
        } else if (movchar.length == 3) {
            cellId = Integer.parseInt(moveText.substring(1));
        }

        int i = (cellId - 1) / 8;
        int j = (cellId - 1) % 8;

        if (!isMoveAllowed(gameMatrix, i, j)) {
            logger.error("You can not move here");
            return new String(oldchar);
        }

        List<Pair<Integer, Integer>> availableMoves = getAvailableMoves(gameMatrix, movchar[0]);

        if (availableMoves.size() != 0 && !availableMoves.contains(new Pair<>(i, j))) {
            logger.error("You can not choose these cell");
            return new String(oldchar);
        }

        if (availableMoves.size() != 0) {
            gameMatrix = moveAndChange(gameMatrix, i, j, movchar[0]);
            gameMatrix[i][j] = movchar[0];
            oldchar = makeCharArrayFromMatrix(oldchar[0], oldchar[1], gameMatrix);
        } else {
            oldchar[cellId + 1] = movchar[0];
        }

        if (movchar[0] == oldchar[0]) {
            if (movchar[0] == '0') {
                oldchar[0] = '1';
            } else if (movchar[0] == '1') {
                oldchar[0] = '0';
            }
        } else {
            logger.error("Not your turn");
            return new String(oldchar);
        }

        // check for victory
        int winner = checkWin(oldchar);
        if (winner != -1) {
            oldchar[1] = Integer.toString(winner).charAt(0);
            oldchar[0] = Integer.toString(winner).charAt(0);
        }
        return new String(oldchar);
    }

    public static List<Pair<Integer, Integer>> getAvailableMoves(char[][] matrix, char turn) {
        List<Pair<Integer, Integer>> res = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (matrix[i][j] == ' ' && isMoveAllowed(matrix, i, j)) {
                    if (isAvailable(matrix, i, j, turn))
                        res.add(new Pair<>(i, j));
                }
            }
        }
        return res;
    }

    public static boolean isAvailable(char[][] matrix, int x, int y, char turn) {
        int length = matrix.length;
        int n = 0;
        for (int i = x + 1; i < length; i++) {
            if (matrix[i][y] == ' ') {
                break;
            }
            if (matrix[i][y] == turn) {
                if (n > 0)
                    return true;
                break;
            }
            if (matrix[i][y] != turn) {
                n++;
            }
        }
        n = 0;
        for (int i = x - 1; i >= 0; i--) {
            if (matrix[i][y] == ' ') {
                break;
            }
            if (matrix[i][y] == turn) {
                if (n > 0)
                    return true;
                break;
            }
            if (matrix[i][y] != turn) {
                n++;
            }
        }
        n = 0;
        for (int i = y + 1; i < length; i++) {
            if (matrix[x][i] == ' ') {
                break;
            }
            if (matrix[x][i] == turn) {
                if (n > 0)
                    return true;
                break;
            }
            if (matrix[x][i] != turn) {
                n++;
            }
        }
        n = 0;
        for (int i = y - 1; i >= 0; i--) {
            if (matrix[x][i] == ' ') {
                break;
            }
            if (matrix[x][i] == turn) {
                if (n > 0)
                    return true;
                break;
            }
            if (matrix[x][i] != turn) {
                n++;
            }
        }
        n = 0;
        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--) {
            if (matrix[i][j] == ' ') {
                break;
            }
            if (matrix[i][j] == turn) {
                if (n > 0)
                    return true;
                break;
            }
            if (matrix[i][j] != turn) {
                n++;
            }
        }
        n = 0;
        for (int i = x + 1, j = y - 1; i < length && j >= 0; i++, j--) {
            if (matrix[i][j] == ' ') {
                break;
            }
            if (matrix[i][j] == turn) {
                if (n > 0)
                    return true;
                break;
            }
            if (matrix[i][j] != turn) {
                n++;
            }
        }
        n = 0;
        for (int i = x - 1, j = y + 1; i >= 0 && j < length; i--, j++) {
            if (matrix[i][j] == ' ') {
                break;
            }
            if (matrix[i][j] == turn) {
                if (n > 0)
                    return true;
                break;
            }
            if (matrix[i][j] != turn) {
                n++;
            }
        }
        n = 0;
        for (int i = x + 1, j = y + 1; i < length && j < length; i++, j++) {
            if (matrix[i][j] == ' ') {
                break;
            }
            if (matrix[i][j] == turn) {
                if (n > 0)
                    return true;
                break;
            }
            if (matrix[i][j] != turn) {
                n++;
            }
        }
        return false;
    }

    public static char[][] moveAndChange(char[][] matrix, int x, int y, char turn) {
        int length = matrix.length;
        int n = 0;
        List<Pair<Integer, Integer>> reColor = new ArrayList<>();
        List<Pair<Integer, Integer>> reColorLocal = new ArrayList<>();
        for (int i = x + 1; i < length; i++) {
            if (matrix[i][y] == ' ') {
                break;
            }
            if (matrix[i][y] == turn) {
                if (n > 0) {
                    reColor.addAll(reColorLocal);
                }
                break;
            }
            if (matrix[i][y] != turn) {
                reColorLocal.add(new Pair<>(i, y));
                n++;
            }
        }

        reColorLocal.clear();
        n = 0;
        for (int i = x - 1; i >= 0; i--) {
            if (matrix[i][y] == ' ') {
                break;
            }
            if (matrix[i][y] == turn) {
                if (n > 0) {
                    reColor.addAll(reColorLocal);
                }
                break;
            }
            if (matrix[i][y] != turn) {
                reColorLocal.add(new Pair<>(i, y));
                n++;
            }
        }

        reColorLocal.clear();
        n = 0;
        for (int i = y + 1; i < length; i++) {
            if (matrix[x][i] == ' ') {
                break;
            }
            if (matrix[x][i] == turn) {
                if (n > 0) {
                    reColor.addAll(reColorLocal);
                }
                break;
            }
            if (matrix[x][i] != turn) {
                reColorLocal.add(new Pair<>(x, i));
                n++;
            }
        }

        reColorLocal.clear();
        n = 0;
        for (int i = y - 1; i >= 0; i--) {
            if (matrix[x][i] == ' ') {
                break;
            }
            if (matrix[x][i] == turn) {
                if (n > 0) {
                    reColor.addAll(reColorLocal);
                }
                break;
            }
            if (matrix[x][i] != turn) {
                reColorLocal.add(new Pair<>(x, i));
                n++;
            }
        }

        reColorLocal.clear();
        n = 0;
        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--) {
            if (matrix[i][j] == ' ') {
                break;
            }
            if (matrix[i][j] == turn) {
                if (n > 0){
                    reColor.addAll(reColorLocal);
                }
                break;
            }
            if (matrix[i][j] != turn) {
                reColorLocal.add(new Pair<>(i, j));
                n++;
            }
        }

        reColorLocal.clear();
        n = 0;
        for (int i = x + 1, j = y - 1; i < length && j >= 0; i++, j--) {
            if (matrix[i][j] == ' ') {
                break;
            }
            if (matrix[i][j] == turn) {
                if (n > 0){
                    reColor.addAll(reColorLocal);
                }
                break;
            }
            if (matrix[i][j] != turn) {
                reColorLocal.add(new Pair<>(i, j));
                n++;
            }
        }

        reColorLocal.clear();
        n = 0;
        for (int i = x - 1, j = y + 1; i >= 0 && j < length; i--, j++) {
            if (matrix[i][j] == ' ') {
                break;
            }
            if (matrix[i][j] == turn) {
                if (n > 0){
                    reColor.addAll(reColorLocal);
                }
                break;
            }
            if (matrix[i][j] != turn) {
                reColorLocal.add(new Pair<>(i, j));
                n++;
            }
        }

        reColorLocal.clear();
        n = 0;
        for (int i = x + 1, j = y + 1; i < length && j < length; i++, j++) {
            if (matrix[i][j] == ' ') {
                break;
            }
            if (matrix[i][j] == turn) {
                if (n > 0){
                    reColor.addAll(reColorLocal);
                }
                break;
            }
            if (matrix[i][j] != turn) {
                reColorLocal.add(new Pair<>(i, j));
                n++;
            }
        }

        for (Pair<Integer, Integer> pair: reColor) {
            int i = pair.getKey();
            int j = pair.getValue();
            matrix[i][j] = turn;
        }

        return matrix;
    }

    public static char[][] makeMatrix(char[] state) {
        char[][] matrix = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                matrix[i][j] = state[(i * 8) + j + 2];
            }
        }
        return matrix;
    }

    public static char[] makeCharArrayFromMatrix(char c1, char c2, char[][] matrix) {
        char[] res = new char[66];
        res[0] = c1;
        res[1] = c2;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                res[(i * 8) + j + 2] = matrix[i][j];
            }
        }
        return res;
    }

    private static boolean isMoveAllowed(char[][] matrix, int i, int j) {
        int length = matrix.length;
        if (isInRange1(i - 1, length) && matrix[i - 1][j] != ' ') {
            return true;
        }
        if (isInRange1(j - 1, length) && matrix[i][j - 1] != ' ') {
            return true;
        }
        if (isInRange1(i + 1, length) && matrix[i + 1][j] != ' ') {
            return true;
        }
        if (isInRange1(j + 1, length) && matrix[i][j + 1] != ' ') {
            return true;
        }
        if (isInRange(i - 1, j - 1, length) && matrix[i - 1][j - 1] != ' ') {
            return true;
        }
        if (isInRange(i + 1, j + 1, length) && matrix[i + 1][j + 1] != ' ') {
            return true;
        }
        if (isInRange(i - 1, j + 1, length) && matrix[i - 1][j + 1] != ' ') {
            return true;
        }
        if (isInRange(i + 1, j - 1, length) && matrix[i + 1][j - 1] != ' ') {
            return true;
        }
        return false;
    }

    private static boolean isInRange(int i, int j, int length) {
        return i >= 0 && i < length && j >= 0 && j < length;
    }

    private static boolean isInRange1(int i, int length) {
        return i >= 0 && i < length;
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
            if (state[i] == '0') {
                playersScores[0]++;
            }
            if (state[i] == '1') {
                playersScores[1]++;
            }
            if (state[i] == '2') {
                playersScores[2]++;
            }
            if (state[i] == '3') {
                playersScores[3]++;
            }
        }
        int max = playersScores[0];
        int winner = 0;
        for (int i = 1; i < 4; i++) {
            if (max < playersScores[i]) {
                max = playersScores[i];
                winner = i;
            }
        }
        return winner;
    }
}
