package scorekeep;
import com.amazonaws.services.lambda.invoke.LambdaFunction;

public interface RandomNameService {
  @LambdaFunction(functionName="random-name-embedded")
  RandomNameOutput randomName(RandomNameInput input);
}