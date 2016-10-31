package scorekeep;

import java.lang.Character;
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
    String newState = new String(oldchar);
    return newState;
  }
}
