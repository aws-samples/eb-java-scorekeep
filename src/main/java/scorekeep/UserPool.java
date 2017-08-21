package scorekeep;

public class UserPool {

  private String poolId;
  private String clientId;
  private String identityPoolId;
  private String region;

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

  public String getIdentityPoolId() {
    return identityPoolId;
  }
  public void setIdentityPoolId(String identityPoolId) {
    this.identityPoolId = identityPoolId;
  }

  public String getRegion() {
    return region;
  }
  public void setRegion(String region) {
    this.region = region;
  }
}