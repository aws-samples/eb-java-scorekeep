package scorekeep;

import java.util.concurrent.atomic.AtomicLong;
import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.lang.Long;
import java.lang.NumberFormatException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.SecureRandom;
import java.math.BigInteger;

@RestController
@RequestMapping(value="/api/state/{sessionId}/{gameId}")
public class StateController {
  private SecureRandom random = new SecureRandom();
  private StateFactory stateFactory = new StateFactory();
  private StateModel model = new StateModel();
  private GameController gameController = new GameController();

  /* GET /state/SESSION/GAME */
  @RequestMapping(method=RequestMethod.GET)
  public List<State> getStates(@PathVariable String sessionId, @PathVariable String gameId) throws SessionNotFoundException, GameNotFoundException {
    return stateFactory.getStates(sessionId, gameId);
  }
  /* GET /state/SESSION/GAME/STATE */
  @RequestMapping(value="/{stateId}", method=RequestMethod.GET)
  public State getState(@PathVariable String sessionId, @PathVariable String gameId, @PathVariable String stateId) throws SessionNotFoundException, GameNotFoundException, StateNotFoundException {
    return stateFactory.getState(sessionId, gameId, stateId);
  }
}
