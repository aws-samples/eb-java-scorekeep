package scorekeep;

public class Rules {

  private final String id;
  private final String name;
  private final String[] categories;
  private final Integer[] users;
  private final Integer teams;
  private final String[] phases;
  private final String[] moves;
  private final String initialState;

  public Rules(String id, String name, String[] categories, Integer[] users, Integer teams, String[] phases, String[] moves, String initialState) {
    this.id = id;
    this.name = name;
    this.categories = categories;
    this.users = users;
    this.teams = teams;
    this.phases = phases;
    this.moves = moves;
    this.initialState = initialState;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String[] getCategories() {
    return categories;
  }

  public Integer[] getUsers() {
    return users;
  }

  public Integer getTeams() {
    return teams;
  }

  public String[] getPhases() {
    return phases;
  }

  public String[] getMoves() {
    return moves;
  }

  public String getInitialState() {
    return initialState;
  }
}