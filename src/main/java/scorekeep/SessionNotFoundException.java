package scorekeep;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Session does not exist.")
public class SessionNotFoundException extends Exception{
  private String sessionId;
  public SessionNotFoundException(String sessionId)
  {
    this.sessionId = sessionId;
  } 
  public String getSessionId()
  {
    return sessionId;
  }
}