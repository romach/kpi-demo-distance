package demo;

import rx.Observable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Client {

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

  public static void main(String[] args) {

  }

}
