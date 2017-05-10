package scorekeep;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Saves state objects to DynamoDB
    Loads state objects from DynamoDB
    Loads all state objects for a game
**/
public class StateModel {
  /** AWS SDK credentials. */
  private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
        .build();
  private DynamoDBMapper mapper = new DynamoDBMapper(client);
  private final SessionModel sessionModel = new SessionModel();
  private final GameModel gameModel = new GameModel();

  public void saveState(State state) throws SessionNotFoundException, GameNotFoundException {
    // check session
    String sessionId = state.getSession();
    String gameId = state.getGame();
    if (sessionModel.loadSession(sessionId) == null ) {
      throw new SessionNotFoundException(sessionId);
    }
    if (gameModel.loadGame(gameId) == null ) {
      throw new GameNotFoundException(gameId);
    }
    mapper.save(state);
  }

  public State loadState(String stateId) throws StateNotFoundException {
    State state = mapper.load(State.class, stateId);
    if ( state == null ) {
      throw new StateNotFoundException(stateId);
    }
    return state;
  }

  public List<State> loadStates(String sessionId, String gameId) throws SessionNotFoundException, GameNotFoundException {
    if ( sessionModel.loadSession(sessionId) == null ) {
      throw new SessionNotFoundException(sessionId);
    }
    if ( gameModel.loadGame(gameId) == null ) {
      throw new GameNotFoundException(gameId);
    }
    Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
    attributeValues.put(":val1", new AttributeValue().withS(gameId));

    Map<String, String> attributeNames = new HashMap<String, String>();
    attributeNames.put("#key1", "game");

    DynamoDBQueryExpression<State> queryExpression = new DynamoDBQueryExpression<State>()
        .withIndexName("game-index")
        .withExpressionAttributeValues(attributeValues)
        .withExpressionAttributeNames(attributeNames)
        .withKeyConditionExpression("#key1 = :val1")
        .withConsistentRead(false);

    List<State> gameStates = mapper.query(State.class, queryExpression);
    return gameStates;
  }

  public void deleteState(String stateId) throws StateNotFoundException {
    State state = mapper.load(State.class, stateId);
    if ( state == null ) {
      throw new StateNotFoundException(stateId);
    }
    mapper.delete(state);
  }
}