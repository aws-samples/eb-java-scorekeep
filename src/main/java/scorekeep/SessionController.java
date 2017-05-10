package scorekeep;

import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(value="/api/session")
public class SessionController {
  private final SessionFactory sessionFactory = new SessionFactory();
  private final SessionModel model = new SessionModel();

  /* POST /session */
  @RequestMapping(method=RequestMethod.POST)
  public Session newSession() {
    Session session = sessionFactory.newSession();
    return session;
  }
  /* PUT /session/SESSION */
  @RequestMapping(value="/{sessionId}", method=RequestMethod.PUT)
  public Session updateSession(@PathVariable String sessionId, @RequestBody Session session) {
    model.saveSession(session);
    return session;
  }
  /* GET /session */
  @RequestMapping(method=RequestMethod.GET)
  public List<Session> getSessions() {
    return sessionFactory.getSessions();
  }
  /* GET /session/SESSION/ */
  @RequestMapping(value="/{sessionId}",method=RequestMethod.GET)
  public Session getSession(@PathVariable String sessionId) throws SessionNotFoundException {
    return sessionFactory.getSession(sessionId);
  }
  /* DELETE /session/SESSION/ */
  @RequestMapping(value="/{sessionId}",method=RequestMethod.DELETE)
  public void deleteSession(@PathVariable String sessionId) throws SessionNotFoundException {
    model.deleteSession(sessionId);
  }
  /* PUT /session/SESSION/owner/USER */
  @RequestMapping(value="/{sessionId}/owner/{ownerId}",method=RequestMethod.PUT)
  public Session setOwner(@PathVariable String sessionId, @PathVariable String ownerId) throws SessionNotFoundException {
    Session session = sessionFactory.getSession(sessionId);
    session.setOwner(ownerId);
    model.saveSession(session);
    return session;
  }
  /* PUT /session/SESSION/game/GAME */
  @RequestMapping(value="/{sessionId}/game/{gameId}", method=RequestMethod.PUT)
  public void setSessionGame(@PathVariable String sessionId, @PathVariable String gameId) throws SessionNotFoundException, GameNotFoundException {
    Session session = sessionFactory.getSession(sessionId);
    session.addGame(gameId);
    model.saveSession(session);
  }
}
