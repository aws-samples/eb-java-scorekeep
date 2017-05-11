package scorekeep;

import java.util.Set;
import java.util.HashSet;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable( tableName = TableNames.SESSION_TABLE )
public class Session {

  private String id;
  private String owner;
  private String name;
  private Set<String> users;
  private Set<String> games;

  public Session() {
  }

  public Session(String id) {
    this.id = id;
  }

  @DynamoDBHashKey(attributeName="id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  @DynamoDBAttribute(attributeName="owner")  
  public String getOwner() {
    return owner;
  }
  public void setOwner(String owner) {
    this.owner = owner;
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
  
  @DynamoDBAttribute(attributeName="games")
  public Set<String> getGames() {
    return games;
  }
  public void setGames(Set<String> games) {
    this.games = games;
  }
  public void clearGames() {
    this.games = null;
  }
  public void addGame(String game) {
    if ( games == null ) {
      games = new HashSet<String>();
    }
    games.add(game);
  }
}