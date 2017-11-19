package scorekeep;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sqs {
  private static final Logger logger = LoggerFactory.getLogger(Sns.class);
  private static AmazonSQS sqsclient = AmazonSQSClientBuilder.standard()
        .build();
  /*
   * Add a message to the queue.
   */
  public static void queueItem(String body) {
    String queueurl = System.getenv("WORKER_QUEUE");
    SendMessageRequest sendMessageRequest = new SendMessageRequest(queueurl, body);
    SendMessageResult sendMessageResult = sqsclient.sendMessage(sendMessageRequest);
    logger.info("Item queued: " + sendMessageResult.getMessageId());
  }

}