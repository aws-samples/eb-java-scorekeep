package scorekeep;

public class UserPool {

  private String poolId;
  private String clientId;

  public UserPool() {
  }

  public UserPool(String poolId, String clientId) {
    this.poolId = poolId;
    this.clientId = clientId;
  }

  public String getPoolId() {
    return poolId;
  }
  public void setPoolId(String poolId) {
    this.poolId = poolId;
  }

  public String getClientId() {
    return clientId;
  }
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

}