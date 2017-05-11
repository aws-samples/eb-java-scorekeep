package scorekeep;

public class Rules {

  private String id;
  private String name;
  private String[] categories;
  private Integer[] users;
  private Integer teams;
  private String[] phases;
  private String[] moves;
  private String initialState;

  public Rules() {
  };

  public String getId() {
    return id;
  }
  public Rules setId(String id){
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }
  public Rules setName(String name){
    this.name = name;
    return this;
  }

  public String[] getCategories() {
    return categories;
  }
  public Rules setCategories(String[] categories){
    this.categories = categories;
    return this;
  }

  public Integer[] getUsers() {
    return users;
  }
  public Rules setUsers(Integer[] users){
    this.users = users;
    return this;
  }

  public Integer getTeams() {
    return teams;
  }
  public Rules setTeams(Integer teams){
    this.teams = teams;
    return this;
  }

  public String[] getPhases() {
    return phases;
  }
  public Rules setPhases(String[] phases){
    this.phases = phases;
    return this;
  }

  public String[] getMoves() {
    return moves;
  }
  public Rules setMoves(String[] moves){
    this.moves = moves;
    return this;
  }

  public String getInitialState() {
    return initialState;
  }
  public Rules setInitialState(String initialState){
    this.initialState = initialState;
    return this;
  }
}