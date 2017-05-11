package scorekeep;
import java.util.*;
import java.lang.Exception;

public class GameFactory {
  private final HashMap<String, Game> allGames = new HashMap<String, Game>(1);
  private final GameModel model = new GameModel();
  private final SessionController sessionController = new SessionController();

  public GameFactory(){
  }

  public Game newGame(String sessionId) throws SessionNotFoundException, GameNotFoundException {
    String gameId = Identifiers.random();
    Game game = new Game(gameId, sessionId);
    model.saveGame(game);
    // Register game to session
    sessionController.setSessionGame(sessionId, gameId);
    return game;
  }

  public Game getGame(String sessionId, String gameId) throws SessionNotFoundException, GameNotFoundException {
    return model.loadGame(gameId);
  }

  public List<Game> getGames(String sessionId) throws SessionNotFoundException {
    return model.loadGames(sessionId);
  }
}