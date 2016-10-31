package scorekeep;
import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.lang.Exception;
import java.io.InputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserFactory {
  private SecureRandom random = new SecureRandom();
  private UserModel model = new UserModel();

  public UserFactory(){
  }

  public User newUser() throws IOException {
    String id = new BigInteger(40, random).toString(32).toUpperCase();
    User user = new User(id);
    String name = randomName();
    user.setName(name);
    model.saveUser(user);
    return user;
  }

  public User newUser(String name) throws IOException {
    String id = new BigInteger(40, random).toString(32).toUpperCase();
    User user = new User(id);
    user.setName(name);
    model.saveUser(user);
    return user;
  }

  public String randomName() throws IOException {
    CloseableHttpClient httpclient = HttpClientBuilder.create().build();
    HttpGet httpGet = new HttpGet("http://uinames.com/api/");
    CloseableHttpResponse response = httpclient.execute(httpGet);
    try {
      HttpEntity entity = response.getEntity();
      InputStream inputStream = entity.getContent();
      ObjectMapper mapper = new ObjectMapper();
      Map<String, String> jsonMap = mapper.readValue(inputStream, Map.class);
      String name = jsonMap.get("name");
      EntityUtils.consume(entity);
      return name;
    } finally {
      response.close();
    }
  }

  public User getUser(String userId) throws UserNotFoundException {
    return model.loadUser(userId);
  }

  public List<User> getUsers() {
    return model.loadUsers();
  }
}