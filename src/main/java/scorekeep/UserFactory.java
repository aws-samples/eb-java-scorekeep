package scorekeep;
import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.lang.Exception;

public class UserFactory {
  private SecureRandom random = new SecureRandom();
  private UserModel model = new UserModel();

  public UserFactory(){
  }

  public User newUser() {
    String id = new BigInteger(40, random).toString(32).toUpperCase();
    User user = new User(id);
    model.saveUser(user);
    return user;
  }

  public User getUser(String userId) throws UserNotFoundException {
    return model.loadUser(userId);
  }

  public List<User> getUsers() {
    return model.loadUsers();
  }
}