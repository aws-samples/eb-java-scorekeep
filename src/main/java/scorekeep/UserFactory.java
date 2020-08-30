package scorekeep;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;
import com.amazonaws.services.lambda.model.ResourceNotFoundException;

public class UserFactory {
  private final UserModel model = new UserModel();
  private AWSLambda lambdaClient = AWSLambdaClientBuilder.standard()
        .build();

  public UserFactory(){
  }

  public User newUser() throws IOException {
    String id = Identifiers.random();
    User user = new User(id);
    String category = "American names";
    String name = randomNameLambda(id, category);
    user.setName(name);
    model.saveUser(user);
    return user;
  }

  public User newUser(String name) throws IOException {
    String id = Identifiers.random();
    User user = new User(id);
    user.setName(name);
    model.saveUser(user);
    return user;
  }

  public String randomName() {
    List<String> names = new ArrayList<String>();
    
    names.add("Billy");
    names.add("Jake");
    names.add("Emma");
    names.add("Ralph");
    names.add("Lucy");
    
    Random random = new Random();
    int index = random.nextInt(names.size());
    String name = names.get(index);

    Sns.sendNotification("Scorekeep user created", "Name: " + name);
    return name;
  }

  public String randomNameLambda(String userid, String category) throws IOException {
    RandomNameService service = LambdaInvokerFactory.build(RandomNameService.class, lambdaClient);
    RandomNameInput input = new RandomNameInput();
    input.setCategory(category);
    input.setUserid(userid);
    /** If this fails, state is set but get state fails*/
    String name ="";
    try { name = service.randomName(input).getName(); }
    catch ( ResourceNotFoundException e) {
      name = randomName();
    }
    return name;
  }

  public User getUser(String userId) throws UserNotFoundException {
    return model.loadUser(userId);
  }

  public List<User> getUsers() {
    return model.loadUsers();
  }
}