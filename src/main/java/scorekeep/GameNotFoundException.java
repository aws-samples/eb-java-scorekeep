package scorekeep;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Game does not exist.")
public class GameNotFoundException extends Exception{
  private String gameId;
  public GameNotFoundException(String gameId)
  {
    this.gameId = gameId;
  } 
  public String getGameId()
  {
    return gameId;
  }
}