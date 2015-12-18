package demo;

import retrofit.RestAdapter;
import rx.Observable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static retrofit.RestAdapter.LogLevel.NONE;

public class Client {

  private final Api api;

  Client(Api api) {
    this.api = api;
  }

  static Observable<String> inputMessages(InputStream in) {
    return Observable.create(subscriber -> {
      try {
        BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;
        while ((line = r.readLine()) != null) {
          subscriber.onNext(line);
        }
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    });
  }

  Observable<String> postMessages(InputStream in) {
    return inputMessages(in)
        .map(text -> new Api.Message(UUID.randomUUID().toString(), "roman", text))
        .concatMap(msg ->
                api
                    .sendMessage(msg)
                    .doOnError(e -> System.err.println(e.getMessage()))
                    .retryWhen(attempts ->
                            attempts
                                .zipWith(Observable.range(1, 8), (attemptNumber, i) -> i)
                                .flatMap(i -> {
                                  int sec = 1 << i;
                                  System.out.println("Attempt " + i + ", delay " + sec + "seconds");
                                  return Observable.timer(sec, SECONDS);
                                })
                                .doOnTerminate(() -> System.out.println("I gave up"))
                    )
        );
  }

  public static void main(String[] args) {
    RestAdapter adapter = new RestAdapter.Builder()
        .setEndpoint("http://localhost:8080")
        .setLogLevel(NONE)
        .build();
    Api api = adapter.create(Api.class);

    Client client = new Client(api);
    client.postMessages(System.in)
        .concatMap((id) -> api.listMessages())
        .concatMap(Observable::from)
        .subscribe(
            message -> {
              System.out.println(message.user + ": " + message.text);
            },
            error -> {
              System.err.println("ERR: " + error.getMessage());
              System.exit(1);
            }
        );
  }

}
