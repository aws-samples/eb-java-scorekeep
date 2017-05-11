package scorekeep;
import java.util.*;
import java.lang.Exception;

public class StateFactory {
  private final StateModel model = new StateModel();

  public StateFactory(){
  }

  public State newState(String sessionId, String gameId, String stateText, Set<String> turn) throws SessionNotFoundException, GameNotFoundException {
    String id = Identifiers.random();
    State state = new State(id, sessionId, gameId, stateText, turn);
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