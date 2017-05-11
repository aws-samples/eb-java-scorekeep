package scorekeep;

import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable( tableName = TableNames.MOVE_TABLE )
public class Move {

  private String id;
  private String game;
  private String session;
  private String user;
  private String move;

  public Move() {
  }

  @DynamoDBHashKey(attributeName="id")
  public String getId() {
    return id;
  }
  public Move setId(String id){
    this.id = id;
    return this;
  }

  @DynamoDBIndexHashKey(globalSecondaryIndexName="game-index",attributeName="game")
  public String getGame() {
    return game;
  }
  public Move setGame(String gameId){
    this.game = gameId;
    return this;
  }

  @DynamoDBAttribute(attributeName="session")  
  public String getSession() {
    return session;
  }
  public Move setSession(String sessionId) {
    this.session = sessionId;
    return this;
  }

  @DynamoDBAttribute(attributeName="user")  
  public String getUser() {
    return user;
  }
  public Move setUser(String userId) {
    this.user = userId;
    return this;
  }

  @DynamoDBAttribute(attributeName="move")  
  public String getMove() {
    return move;
  }
  public Move setMove(String moveId) {
    this.move = moveId;
    return this;
  }
}