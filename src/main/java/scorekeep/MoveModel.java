package scorekeep;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.regions.Regions;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.Exception;
import java.lang.Throwable;

public class MoveModel {
  /** AWS SDK credentials. */
  private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
        .withRegion(Regions.fromName(System.getenv("AWS_REGION")))
        .build();
  private DynamoDBMapper mapper = new DynamoDBMapper(client);
  private SessionModel sessionModel = new SessionModel();
  private GameModel gameModel = new GameModel();

  public void saveMove(Move move) throws SessionNotFoundException, GameNotFoundException {
    // check session
    String sessionId = move.getSession();
    String gameId = move.getGame();
    try {
      if (sessionModel.loadSession(sessionId) == null ) {
        throw new SessionNotFoundException(sessionId);
      }
      if (gameModel.loadGame(gameId) == null ) {
        throw new GameNotFoundException(gameId);
      }
      mapper.save(move);
    } catch (Exception e) {
      throw e;
    }
  }

  public Move loadMove(String moveId) throws MoveNotFoundException {
    Move move = mapper.load(Move.class, moveId);
    if ( move == null ) {
      throw new MoveNotFoundException(moveId);
    }
    return move;
  }

  public List<Move> loadMoves(String sessionId, String gameId) throws SessionNotFoundException, GameNotFoundException {
    if ( sessionModel.loadSession(sessionId) == null ) {
      throw new SessionNotFoundException(sessionId);
    }
    if ( gameModel.loadGame(gameId) == null ) {
      throw new GameNotFoundException(gameId);
    }
    Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    eav.put(":val1", new AttributeValue().withS(gameId));

    Map<String, String> ean = new HashMap<String, String>();
    ean.put("#key1", "game");

    DynamoDBQueryExpression<Move> queryExpression = new DynamoDBQueryExpression<Move>()
    .withIndexName("game-index")
    .withExpressionAttributeValues(eav)
    .withExpressionAttributeNames(ean)
    .withKeyConditionExpression("#key1 = :val1")
    .withConsistentRead(false);

    List<Move> gameMoves = mapper.query(Move.class, queryExpression);
    return gameMoves;
  }

  public void deleteMove(String moveId) throws MoveNotFoundException {
    Move move = mapper.load(Move.class, moveId);
    if ( move == null ) {
      throw new MoveNotFoundException(moveId);
    }
    mapper.delete(move);
  }
}