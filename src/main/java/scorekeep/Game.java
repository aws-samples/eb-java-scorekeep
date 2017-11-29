package scorekeep;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Date;
import java.util.ArrayList;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable( tableName = TableNames.GAME_TABLE )
public class Game {

  private String id;
  private String session;
  private String name;
  private Set<String> users;
  private String rules;
  private Date startTime;
  private Date endTime;
  private List<String> states;
  private List<String> moves;

  public Game() {
  }

  public Game(String id, String session) {
    this.id = id;
    this.session = session;
  }

  @DynamoDBHashKey(attributeName="id")
  public String getId() {
    return id;
  }
  public void setId(String id){
    this.id = id;
  }

  @DynamoDBIndexHashKey(globalSecondaryIndexName="session-index",attributeName="session")
  public String getSession() {
    return session;
  }
  public void setSession(String sessionId){
    this.session = sessionId;
  }

  @DynamoDBAttribute(attributeName="name")  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  @DynamoDBAttribute(attributeName="users")
  public Set<String> getUsers() {
    return users;
  }
  public void setUsers(Set<String> users) {
    this.users = users;
  }
  public void setUser(String user) {
    if ( users == null ) {
      users = new HashSet<String>();
    }
    users.add(user);
  }

  @DynamoDBAttribute(attributeName="rules")  
  public String getRules() {
    return rules;
  }
  public void setRules(String rules) {
    this.rules = rules;
  }

  @DynamoDBAttribute(attributeName="startTime")  
  public Date getStartTime() {
    return startTime;
  }
  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  @DynamoDBAttribute(attributeName="endTime")  
  public Date getEndTime() {
    return endTime;
  }
  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  @DynamoDBAttribute(attributeName="states")
  public List<String> getStates() {
    return states;
  }
  public void setStates(List<String> states) {
    this.states = states;
  }
  public void setState(String state) {
    if ( states == null ) {
      states = new ArrayList<String>();
    }
    states.add(state);
  }

  @DynamoDBAttribute(attributeName="moves")
  public List<String> getMoves() {
    return moves;
  }
  public void setMoves(List<String> moves) {
    this.moves = moves;
  }
  public void setMove(String move) {
    if ( moves == null ) {
      moves = new ArrayList<String>();
    }
    moves.add(move);
  }
}