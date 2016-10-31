package scorekeep;

import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.SecureRandom;
import java.math.BigInteger;

@RestController
@RequestMapping(value="/user")
public class UserController {
  private SecureRandom random = new SecureRandom();
  private UserFactory factory = new UserFactory();
  private UserModel model = new UserModel();

  /* POST /user */
  @RequestMapping(method=RequestMethod.POST)
  public User newUser() {
    User user = factory.newUser();
    return user;
  }
  /* PUT /user/USER */
  @RequestMapping(value="/{userId}", method=RequestMethod.PUT)
  public User updateUser(@PathVariable String userId, @RequestBody User user) {
    model.saveUser(user);
    return user;
  }
  /* GET /user */
  @RequestMapping(method=RequestMethod.GET)
  public List<User> getUsers() {
    return factory.getUsers();
  }
  /* GET /user/USER */
  @RequestMapping(value="/{userId}",method=RequestMethod.GET)
  public User getUser(@PathVariable String userId) throws UserNotFoundException {
    return factory.getUser(userId);
  }
  /* DELETE /user/USER */
  @RequestMapping(value="/{userId}",method=RequestMethod.DELETE)
  public void deleteUser(@PathVariable String userId) throws UserNotFoundException {
    model.deleteUser(userId);
  }
}
