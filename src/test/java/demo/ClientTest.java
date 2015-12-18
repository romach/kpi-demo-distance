package demo;

import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

public class ClientTest {

  @Test
  public void inputMessages() throws IOException {
    String text = "msg1\nmsg2\nmsg3";
    ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes("UTF-8"));
    Observable<String> messages = Client.inputMessages(in);
    TestSubscriber<String> subscriber = new TestSubscriber<String>();
    messages.subscribe(subscriber);

    subscriber.assertCompleted();
    subscriber.assertReceivedOnNext(Arrays.asList("msg1", "msg2", "msg3"));
  }

}
