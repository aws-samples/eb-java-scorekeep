package scorekeep;

import java.lang.Character;
import java.lang.Math;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicTacToe {
  private static final Logger logger = LoggerFactory.getLogger(TicTacToe.class);

  public static Rules getRules() {
    String id = "TicTacToe";
    String name = "Tic Tac Toe";
    String[] categories = { "head to head", "quick" };
    Integer[] users = { 2 };
    Integer teams = 0;
    String[] phases = { "Move" };
    String[] moves = { "Roll" };
    String initialState = "X         ";
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
    if ( movchar[0] == oldchar[0] ) {
      oldchar[Character.getNumericValue(movchar[1])] = movchar[0];
      if ( movchar[0] == 'X' ) {
        oldchar[0] = 'O';
      } else if ( movchar[0] == 'O') {
        oldchar[0] = 'X';
      }
    } else {
      logger.error("Not your turn");
    }
    // convert state to integer
    int stateInt = toInt(oldchar, movchar[0]);
    logger.info("state int: " + stateInt);
    // check for victory
    boolean win = checkWin(stateInt);
    if ( win ) {
      if ( movchar[0] == 'X') {
        oldchar[0] = 'A';
      } else {
        oldchar[0] = 'B';
      }
    }
    String newState = new String(oldchar);
    return newState;
  }

  /* Convert a string game state to an integer by treating it as a binary
   * number where spaces occupied by the player are '1' and all other spaces
   * are '0'.
   */
  public static int toInt(char[] state, char turn) {
    int out = 0;
    int len = state.length;
    for ( int i = 1; i <= len; i++ ){
      if ( state[len-i] == turn) {
        out += Math.pow( 2, i-1 );
      }
    }
    return out;
  }

  /* Compare an integer game state against known winning states for tic tac
   * toe. For example, X can win with three Xs on the bottom row:
   *        0 X 0
   *          O
   *        X X X
   * Xs state in binary is 010000111, 135 in decimal. 135 is a bitwise match
   * for 000000111, 7 in decimal, one of the 8 winning states:
   *      111000000  448      010010010  146
   *      000111000  56       001001001  73
   *      000000111  7        100010001  273
   *      100100100  292      001010100  84
   */
  public static boolean checkWin(int state) {
    int[] winningStates = {7,56,73,84,146,273,292,448};
    for ( int i = 0; i < 8; i++ ){
      int combinedState = winningStates[i] & state;
      if ( combinedState == winningStates[i]) {
        logger.info("winning state: " + state);
        logger.info("matches: " + winningStates[i]);
        return true;
      }
    }
    return false;
  }
}
