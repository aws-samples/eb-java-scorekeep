package scorekeep;
import java.util.*;
import java.lang.Exception;

public class SessionFactory {
  private final SessionModel model = new SessionModel();

  public SessionFactory(){
  }

  public Session newSession() {
    String id = Identifiers.random();
    Session session = new Session(id);
    model.saveSession(session);
    return session;
  }

  public Session getSession(String sessionId) throws SessionNotFoundException {
    return model.loadSession(sessionId);
  }

  public List<Session> getSessions() {
    return model.loadSessions();
  }
}