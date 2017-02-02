package scorekeep;

import java.lang.Character;
import java.lang.Math;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicTacToe {
  private static final Logger logger = LoggerFactory.getLogger("TicTacToe");

  public static String move(String oldState, String moveText) {
    // 0: user, 1-9: board 
    // String old = "XNNNNNNNNN";
    // 0: turn, 1: space
    // String mov = "X3";
    char[] oldchar = oldState.toCharArray();
    char[] movchar = moveText.toCharArray();
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
    // new = "ONNXNNNNNN" 
    // check for victory
    // - convert state to integer
    int stateInt = toInt(oldchar, movchar[0]);
    logger.warn("state int: " + stateInt);
    // - compare
    boolean win = checkWin(stateInt);

    
    String newState = new String(oldchar);
    return newState;
  }

  public static int toInt(char[] state, char turn) {
    int out = 0;
    int len = state.length;
    for ( int i = 1; i <= len; i++ ){
      if ( state[len-i] == turn) {
        out += java.lang.Math.pow( 2, i-1 );
      }
    }
    return out;
  }

  public static boolean checkWin(int state) {
    int[] winningStates = {7,56,73,84,146,273,292,448};
    for ( int i = 0; i < 8; i++ ){
      int combinedState = winningStates[i] & state;
      if ( combinedState == winningStates[i]) {
        logger.warn("winning state: " + state);
        logger.warn("matches: " + winningStates[i]);
        return true;
      }
    }
    return false;
  }
}
