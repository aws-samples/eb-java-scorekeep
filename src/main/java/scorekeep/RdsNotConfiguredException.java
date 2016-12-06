package scorekeep;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.SERVICE_UNAVAILABLE, reason="RDS database is not attached. Attach an RDS database.")
public class RdsNotConfiguredException extends Exception {

}
