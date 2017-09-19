package scorekeep;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.lang.Class;
import java.lang.Thread;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveFactory {
  private static final Logger logger = LoggerFactory.getLogger(MoveFactory.class);
  private final HashMap<String, Move> allMoves = new HashMap<String, Move>(1);
  private final MoveModel moveModel = new MoveModel();
  private final StateModel stateModel = new StateModel();
  private final GameController gameController = new GameController();
  private final StateController stateController = new StateController();
  private final RulesFactory rulesFactory = new RulesFactory();

  public MoveFactory(){
  }

  public Move newMove(String sessionId, String gameId, String userId, String moveText) throws SessionNotFoundException, GameNotFoundException, StateNotFoundException, RulesException {
    String moveId = Identifiers.random();
    String stateId = Identifiers.random();
    Move move = new Move().setId(moveId)
                          .setSession(sessionId)
                          .setGame(gameId)
                          .setUser(userId)
                          .setMove(moveText);
    String newStateText = "";
    // load game state
    Game game = gameController.getGame(sessionId, gameId);
    List<String> states = game.getStates();
    State oldState = stateController.getState(sessionId, gameId, states.get(states.size() - 1));
    Set<String> oldTurn = oldState.getTurn();
    // check turn 
    // if ( oldTurn.contains(userId) {}
    // load game rules
    //   rules = rulesFactory.getRules(rulesId)
    // apply move
    //   String stateText = rules.move(oldState, move)
    Set<String> newTurn = game.getUsers();
    if (newTurn.size() != 1) {
      newTurn.remove(userId);
    }
    String rulesName = game.getRules();
    if ( !rulesName.matches("[a-zA-Z]{1,16}") ) {
      throw new RulesException(rulesName);
    }
    try {
      Class<?> rules = Class.forName("scorekeep." + rulesName);
      Method moveMethod = rules.getMethod("move", String.class, String.class);
      newStateText = (String) moveMethod.invoke(null, oldState.getState(), moveText);
    } catch ( ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) { throw new RulesException(rulesName); }
    // save new game state
    State newState = new State(stateId, sessionId, gameId, newStateText, newTurn);
    // send notification on game end
    if ( newStateText.startsWith("A") || newStateText.startsWith("B")) {
      Thread comm = new Thread() {
        public void run() {
          Sns.sendNotification("Scorekeep game completed", "Winner: " + userId);
        }
      };
      comm.start();
    }
    // register state and move id to game
    gameController.setGameMove(sessionId, gameId, moveId);
    gameController.setGameState(sessionId, gameId, stateId);
    moveModel.saveMove(move);
    stateModel.saveState(newState);
    return move;
  }

  public Move getMove(String sessionId, String gameId, String moveId) throws SessionNotFoundException, MoveNotFoundException {
    return moveModel.loadMove(moveId);
  }

  public List<Move> getMoves(String sessionId, String gameId) throws SessionNotFoundException, GameNotFoundException {
    return moveModel.loadMoves(sessionId, gameId);
  }
}