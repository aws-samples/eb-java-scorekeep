package scorekeep;
import com.amazonaws.services.lambda.invoke.LambdaFunction;

public interface RandomNameService {
  @LambdaFunction(functionName="random-name")
  RandomNameOutput randomName(RandomNameInput input);
}