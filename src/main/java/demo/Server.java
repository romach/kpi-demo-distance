package demo;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.server.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

  private static AtomicInteger counter = new AtomicInteger(0);

  static class Message {
    String id;
    String clientId;
    String text;
    String user;
  }

  public static void main(String[] args) {

    Map<String, Message> messages = Collections.synchronizedMap(new LinkedHashMap<>());
    Gson gson = new Gson();

    HttpServer<ByteBuf, ByteBuf> server = RxNetty.createHttpServer(8080, (request, response) -> {
      if (request.getPath().startsWith("/message")) {

        if (request.getHttpMethod().equals(HttpMethod.GET)) {
          response.setStatus(HttpResponseStatus.OK);
          response.writeString(gson.toJson(messages.values()));
          return response.close();
        } else {
          return request.getContent()
              .reduce(new ByteArrayOutputStream(), (buffer, chunk) -> {
                chunk.forEachByte(b -> {
                  buffer.write(b);
                  return true;
                });
                return buffer;
              })
              .map(ByteArrayOutputStream::toByteArray)
              .map(bytes -> {
                try {
                  return new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                  throw new AssertionError(e);
                }
              })
              .map(str -> gson.fromJson(str, Message.class))
              .doOnNext(m -> {
                m.id = UUID.randomUUID().toString();
              })
              .doOnNext(msg -> {
                if (!messages.containsKey(msg.clientId)) {
                  messages.put(msg.clientId, msg);
                }
              })
              .concatMap(m -> {
                System.out.println(counter.get());
                if (counter.get() < 4) {
                  response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                  counter.incrementAndGet();
                } else {
                  response.setStatus(HttpResponseStatus.OK);
                  counter.set(0);
                }
                response.writeString(gson.toJson(m.id));
                return response.close();
              });
        }
      } else {
        response.setStatus(HttpResponseStatus.NOT_FOUND);
        return response.close();
      }
    });

    server.startAndWait();
  }

}
