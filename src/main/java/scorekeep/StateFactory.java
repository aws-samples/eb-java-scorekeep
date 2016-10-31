package scorekeep;
import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.lang.Exception;

public class StateFactory {
  private SecureRandom random = new SecureRandom();
  private final HashMap<String, State> allStates = new HashMap<String, State>(1);
  private StateModel model = new StateModel();

  public StateFactory(){
  }

  public State newState(String sessionId, String gameId, String stateText) throws SessionNotFoundException, GameNotFoundException {
    String id = new BigInteger(40, random).toString(32).toUpperCase();
    State state = new State(id, sessionId, gameId, stateText);
    model.saveState(state);
    return state;
  }

  public State getState(String sessionId, String gameId, String stateId) throws SessionNotFoundException, StateNotFoundException {
    return model.loadState(stateId);
  }

  public List<State> getStates(String sessionId, String gameId) throws SessionNotFoundException, GameNotFoundException {
    return model.loadStates(sessionId, gameId);
  }
}