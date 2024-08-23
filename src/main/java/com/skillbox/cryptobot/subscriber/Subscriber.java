package com.skillbox.cryptobot.subscriber;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Subscribers")
public class Subscriber {

  //  Объект пользователяы
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "uuid")
  private UUID uuid;
  @Column(name = "user_id")
  private Long user_id;
  @Column(name = "chat_id")
  private Long chat_id;
  @Column(name = "subscription_price")
  private Double price;

  public Subscriber() {
  }

  public Subscriber(Long user_id, Long chat_id, Double price) {
    this.user_id = user_id;
    this.chat_id = chat_id;
    this.price = price;
  }
}
