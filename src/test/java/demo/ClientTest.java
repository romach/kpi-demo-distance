package demo;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

public class ClientTest {

  private Client client;
  private Api api;

  @Before
  public void init() {
    api = mock(Api.class);
    client = new Client(api);
  }

  @Test
  public void postMessages() throws UnsupportedEncodingException {
    String text = "msg1\nmsg2\nmsg3";
    ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes("UTF-8"));

    doReturn(Observable.just("123")).when(api)
        .sendMessage(notNull(Api.Message.class));
    TestSubscriber<String> subscriber = new TestSubscriber<String>();

    client.postMessages(in)
        .subscribe(subscriber);

    verify(api, times(3)).sendMessage(notNull(Api.Message.class));
    subscriber.assertReceivedOnNext(Arrays.asList("123", "123", "123"));
  }

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
