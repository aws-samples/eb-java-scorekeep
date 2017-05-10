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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(value="/api/game/{sessionId}")
/** Routes for game CRUD 
    Use GameFactory methods to create new games and load existing games
    Use Game class methods to set fields on Game objects
    Use GameModel to persist updated Game objects to DynamoDB
**/
public class GameController {
  private final GameFactory gameFactory = new GameFactory();
  private final RulesFactory rulesFactory = new RulesFactory();
  private final StateFactory stateFactory = new StateFactory();
  private final GameModel model = new GameModel();
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  /* GET /game/SESSION/ */
  @RequestMapping(method=RequestMethod.GET)
  public List<Game> getGames(@PathVariable String sessionId) throws SessionNotFoundException {
    return gameFactory.getGames(sessionId);
  }
  /* POST /game/SESSION/ */
  @RequestMapping(method=RequestMethod.POST)
  public Game newGame(@PathVariable String sessionId) throws SessionNotFoundException, GameNotFoundException {
    logger.info("Creating game");
    return gameFactory.newGame(sessionId);
  }
  /*  GET /game/SESSION/GAME */
  @RequestMapping(value="/{gameId}", method=RequestMethod.GET)
  public Game getGame(@PathVariable String sessionId, @PathVariable String gameId) throws SessionNotFoundException, GameNotFoundException {
    return gameFactory.getGame(sessionId, gameId);
  }
  /*  PUT /game/SESSION/GAME */
  @RequestMapping(value="/{gameId}", method=RequestMethod.PUT)
  public Game updateGame(@PathVariable String sessionId, @PathVariable String gameId, @RequestBody Game game) throws SessionNotFoundException, GameNotFoundException {
    model.saveGame(game);
    return game;
  }
  /*  DELETE /game/SESSION/GAME */
  @RequestMapping(value="/{gameId}",method=RequestMethod.DELETE)
  public void deleteGame(@PathVariable String sessionId, @PathVariable String gameId) throws GameNotFoundException {
    model.deleteGame(sessionId, gameId);
  }
  /*  GET /game/SESSION/GAME/name */
  @RequestMapping(value="/{gameId}/name", method=RequestMethod.GET)
  public String getGameName(@PathVariable String sessionId, @PathVariable String gameId) throws SessionNotFoundException, GameNotFoundException {
    return gameFactory.getGame(sessionId, gameId).getName();
  }
  /*  PUT /game/SESSION/GAME/name/NAME */
  @RequestMapping(value="/{gameId}/name/{name}", method=RequestMethod.PUT)
  public void setGameName(@PathVariable String sessionId, @PathVariable String gameId, @PathVariable String name) throws SessionNotFoundException, GameNotFoundException {
    Game game = gameFactory.getGame(sessionId, gameId);
    game.setName(name);
    model.saveGame(game);
  }
  /*  GET /game/SESSION/GAME/rules */
  @RequestMapping(value="/{gameId}/rules", method=RequestMethod.GET)
  public String getGameRules(@PathVariable String sessionId, @PathVariable String gameId) throws SessionNotFoundException, GameNotFoundException {
    return gameFactory.getGame(sessionId, gameId).getRules();
  }
  /*  PUT /game/SESSION/GAME/rules/RULES */
  @RequestMapping(value="/{gameId}/rules/{rulesId}",method=RequestMethod.PUT)
  public void setGameRules(@PathVariable String sessionId, @PathVariable String gameId, @PathVariable String rulesId) throws SessionNotFoundException, GameNotFoundException {
    Game game = gameFactory.getGame(sessionId, gameId);
    logger.info("setting rules");
    game.setRules(rulesId);
    String initialState = rulesFactory.getRules(rulesId).getInitialState();
    logger.info("initialState: " + initialState);
    // create new state with initial state
    State state = stateFactory.newState(sessionId, gameId, initialState, game.getUsers());
    game.setState(state.getId());
    model.saveGame(game);
  }
  /*  GET /game/SESSION/GAME/user */
  @RequestMapping(value="/{gameId}/user", method=RequestMethod.GET)
  public Set<String> getGameUsers(@PathVariable String sessionId, @PathVariable String gameId) throws SessionNotFoundException, GameNotFoundException {
    return gameFactory.getGame(sessionId, gameId).getUsers();
  }
  /*  PUT /game/SESSION/GAME/user/USER */
  @RequestMapping(value="/{gameId}/user/{user}", method=RequestMethod.PUT)
  public void setGameUser(@PathVariable String sessionId, @PathVariable String gameId, @PathVariable String user) throws SessionNotFoundException, GameNotFoundException {
    Game game = gameFactory.getGame(sessionId, gameId);
    game.setUser(user);
    model.saveGame(game);
  }
  /*  POST /game/SESSION/GAME/users */
  @RequestMapping(value="/{gameId}/users", method=RequestMethod.POST)
  public void setGameUsers(@PathVariable String sessionId, @PathVariable String gameId, @RequestBody Set<String> users) throws SessionNotFoundException, GameNotFoundException {
    Game game = gameFactory.getGame(sessionId, gameId);
    game.setUsers(users);
    model.saveGame(game);
  }
  /** PUT /game/SESSION/GAME/starttime/STARTTIME **/
  @RequestMapping(value="/{gameId}/starttime/{startTime}",method=RequestMethod.PUT)
  public void setStartTime(@PathVariable String sessionId, @PathVariable String gameId, @PathVariable String startTime) throws SessionNotFoundException, GameNotFoundException, NumberFormatException {
    Game game = gameFactory.getGame(sessionId, gameId);
    Long seconds = Long.parseLong(startTime);
    Date date = new Date(seconds);
    logger.info("Setting start time.");
    game.setStartTime(date);
    logger.info("Start time: " + game.getStartTime());
    model.saveGame(game);
  }
  /** PUT /game/SESSION/GAME/endtime/ENDTIME **/
  @RequestMapping(value="/{gameId}/endtime/{endTime}",method=RequestMethod.PUT)
  public void setEndTime(@PathVariable String sessionId, @PathVariable String gameId, @PathVariable String endTime) throws SessionNotFoundException, GameNotFoundException, NumberFormatException {
    Game game = gameFactory.getGame(sessionId, gameId);
    Long seconds = Long.parseLong(endTime);
    Date date = new Date(seconds);
    game.setEndTime(date);
    model.saveGame(game);
  }
  /** PUT /game/SESSION/GAME/move/MOVE **/
  @RequestMapping(value="/{gameId}/move/{moveId}",method=RequestMethod.PUT)
  public void setGameMove(@PathVariable String sessionId, @PathVariable String gameId, @PathVariable String moveId) throws SessionNotFoundException, GameNotFoundException {
    Game game = gameFactory.getGame(sessionId, gameId);
    game.setMove(moveId);
    model.saveGame(game);
  }
  /** PUT /game/SESSION/GAME/state/STATE **/
  @RequestMapping(value="/{gameId}/state/{stateId}",method=RequestMethod.PUT)
  public void setGameState(@PathVariable String sessionId, @PathVariable String gameId, @PathVariable String stateId) throws SessionNotFoundException, GameNotFoundException {
    Game game = gameFactory.getGame(sessionId, gameId);
    game.setState(stateId);
    model.saveGame(game);
  }
}
