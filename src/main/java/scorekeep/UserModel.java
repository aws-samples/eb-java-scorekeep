package scorekeep;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

import java.util.List;

public class UserModel {
  /** AWS SDK credentials. */

  public void saveUser(User user) {
    try {
      Application.mapper.save(user);
    } catch (Exception e) {
      throw e;
    }
  }

  public User loadUser(String userId) throws UserNotFoundException {
    User user = Application.mapper.load(User.class, userId);
    if ( user == null ) {
      throw new UserNotFoundException(userId);
    }
    return user;
  }

  public List<User> loadUsers(){
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
    List<User> scanResult = Application.mapper.scan(User.class, scanExpression);
    return scanResult;
  }

  public void deleteUser(String userId) throws UserNotFoundException {
    User user = Application.mapper.load(User.class, userId);
    if ( user == null ) {
      throw new UserNotFoundException(userId);
    }
    Application.mapper.delete(user);
  }
}