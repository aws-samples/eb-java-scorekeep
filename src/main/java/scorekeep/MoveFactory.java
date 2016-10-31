package scorekeep;
import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.lang.Exception;

public class MoveFactory {
  private SecureRandom random = new SecureRandom();
  private final HashMap<String, Move> allMoves = new HashMap<String, Move>(1);
  private MoveModel moveModel = new MoveModel();
  private StateModel stateModel = new StateModel();
  private GameController gameController = new GameController();
  private StateController stateController = new StateController();
  private RulesFactory rulesFactory = new RulesFactory(); 

  public MoveFactory(){
  }

  public Move newMove(String sessionId, String gameId, String userId, String moveText) throws SessionNotFoundException, GameNotFoundException, StateNotFoundException {
    String moveId = new BigInteger(40, random).toString(32).toUpperCase();
    String stateId = new BigInteger(40, random).toString(32).toUpperCase();
    Move move = new Move(moveId, sessionId, gameId, userId, moveText);
    // load game state
    Game game = gameController.getGame(sessionId, gameId);
    ArrayList<String> states = game.getStates();
    State oldState = stateController.getState(sessionId, gameId, states.get(states.size() - 1));
    // check turn 
    //   if ( state.getTurn().contains(userId) )
    // load game rules
    //   rules = rulesFactory.getRules(rulesId)
    // apply move
    //   String stateText = rules.move(oldState, move)
    String newStateText = TicTacToe.move(oldState.getState(), moveText);
    // save new game state
    State newState = new State(stateId, sessionId, gameId, newStateText);
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