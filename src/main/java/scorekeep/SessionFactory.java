package scorekeep;
import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.lang.Exception;

public class SessionFactory {
  private SecureRandom random = new SecureRandom();
  private SessionModel model = new SessionModel();

  public SessionFactory(){
  }

  public Session newSession() {
    String id = new BigInteger(40, random).toString(32).toUpperCase();
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