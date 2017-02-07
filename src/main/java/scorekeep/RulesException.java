package scorekeep;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Rules move invocation failed.")
public class RulesException extends Exception{
  private String rulesId;
  public RulesException(String rulesId)
  {
    this.rulesId = rulesId;
  }
  public String getRulesId()
  {
    return rulesId;
  }
}