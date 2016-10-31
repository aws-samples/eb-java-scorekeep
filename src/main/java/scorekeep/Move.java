package scorekeep;

import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable( tableName = Constants.MOVE_TABLE )
public class Move {

  private String id;
  private String game;
  private String session;
  private String user;
  private String move;

  public Move() {
  }

  public Move(String id, String session, String game, String user, String move) {
    this.id = id;
    this.session = session;
    this.game = game;
    this.user = user;
    this.move = move;
  }

  @DynamoDBHashKey(attributeName="id")
  public String getId() {
    return id;
  }
  public void setId(String id){
    this.id = id;
  }

  @DynamoDBIndexHashKey(globalSecondaryIndexName="game-index",attributeName="game")
  public String getGame() {
    return game;
  }
  public void setGame(String gameId){
    this.game = gameId;
  }

  @DynamoDBAttribute(attributeName="session")  
  public String getSession() {
    return session;
  }
  public void setSession(String sessionId) {
    this.session = sessionId;
  }

  @DynamoDBAttribute(attributeName="user")  
  public String getUser() {
    return user;
  }
  public void setUser(String userId) {
    this.user = userId;
  }

  @DynamoDBAttribute(attributeName="move")  
  public String getMove() {
    return move;
  }
  public void setMove(String moveId) {
    this.move = moveId;
  }


}