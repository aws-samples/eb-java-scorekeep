package scorekeep;

import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable( tableName = TableNames.STATE_TABLE )
public class State {

  private String id;
  private String game;
  private String session;
  private String state;
  private Set<String> turn;

  public State() {
  }

  public State(String id, String session, String game, String state, Set<String> turn) {
    this.id = id;
    this.session = session;
    this.game = game;
    this.state = state;
    this.turn = turn;
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

  @DynamoDBAttribute(attributeName="state")  
  public String getState() {
    return state;
  }
  public void setState(String stateId) {
    this.state = stateId;
  }

  @DynamoDBAttribute(attributeName="turn")
  public Set<String> getTurn() {
    return turn;
  }
  public void setTurn(Set<String> turn) {
    this.turn = turn;
  }
}