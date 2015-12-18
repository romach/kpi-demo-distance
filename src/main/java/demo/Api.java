package demo;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

import java.util.List;

public interface Api {

  @POST("/message/new")
  Observable<String> sendMessage(@Body Message message);

  @GET("/messages")
  Observable<List<Message>> listMessages();

  class Message {
    final String clientId;
    final String user;
    final String text;

    public Message(String clientId, String user, String text) {
      this.clientId = clientId;
      this.user = user;
      this.text = text;
    }
  }

}
