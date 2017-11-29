package scorekeep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

class TicTacToeTest {
  private static final Logger logger = LoggerFactory.getLogger(TicTacToe.class);

  @Test
  void gameplayTest() {
    Rules rules = TicTacToe.getRules();
    String state = rules.getInitialState();
    List<String> moves = new ArrayList<String>(Arrays.asList("X1", "O3", "X4", "O6", "X7"));
    for (String move : moves) {
      state = TicTacToe.move(state, move);
    }
    assertTrue(TicTacToe.checkWin(TicTacToe.toInt(state.toCharArray(), 'X')));
  }
  @Test
  void checkWinTest() {
    String state = "OX O XO  X";
    assertTrue(TicTacToe.checkWin(TicTacToe.toInt(state.toCharArray(), 'X')));
  }

}