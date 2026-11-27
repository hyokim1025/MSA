package se.magnus.api.event;

import static java.time.ZonedDateTime.now;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public class Event<K, T> {

  public enum Type {
    CREATE,
    DELETE
  }

  private final Type eventType;
  private final K key;
  private final T data;
  private final ZonedDateTime eventCreatedAt;

  public Event() {
    this.eventType = null;
    this.key = null;
    this.data = null;
    this.eventCreatedAt = null;
  }

  public Event(Type eventType, K key, T data) {
    this(eventType, key, data, now());
  }

  // [4.0.9] Jackson 3 은 final 필드를 기본 생성자로 채우지 않으므로 @JsonCreator 생성자로 역직렬화
  @JsonCreator
  public Event(
    @JsonProperty("eventType") Type eventType,
    @JsonProperty("key") K key,
    @JsonProperty("data") T data,
    @JsonProperty("eventCreatedAt") ZonedDateTime eventCreatedAt) {

    this.eventType = eventType;
    this.key = key;
    this.data = data;
    this.eventCreatedAt = eventCreatedAt;
  }

  public Type getEventType() {
    return eventType;
  }

  public K getKey() {
    return key;
  }

  public T getData() {
    return data;
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  public ZonedDateTime getEventCreatedAt() {
    return eventCreatedAt;
  }
}
