package com.skillbox.cryptobot.subscriber;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

  //  Репозиторий хранения пользователей
  @Query(value = "SELECT * FROM Subscribers where chat_id = ?1", nativeQuery = true)
  Subscriber findSubscriberByChatId(Long id);

  @Query(value = "SELECT * FROM Subscribers where subscription_price > ?1", nativeQuery = true)
  List<Subscriber> findSubscriberByPrice(Double subscriptionPrice);
}
