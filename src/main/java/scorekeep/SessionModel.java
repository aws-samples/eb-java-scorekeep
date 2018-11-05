package scorekeep;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

import java.util.List;

public class SessionModel {

  public void saveSession(Session session) {
    try {
      Application.mapper.save(session);
    } catch (Exception e) {
      throw e;
    }
  }

  public Session loadSession(String sessionId) throws SessionNotFoundException {
    Session session = Application.mapper.load(Session.class, sessionId);
    if ( session == null ) {
      throw new SessionNotFoundException(sessionId);
    }
    return session;
  }

  public List<Session> loadSessions(){
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
    List<Session> scanResult = Application.mapper.scan(Session.class, scanExpression);
    return scanResult;
  }

  public void deleteSession(String sessionId) throws SessionNotFoundException {
    Session session = Application.mapper.load(Session.class, sessionId);
    if ( session == null ) {
      throw new SessionNotFoundException(sessionId);
    }
    Application.mapper.delete(session);
  }
}