package com.skillbox.cryptobot.notifications;

import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.subscriber.Subscriber;
import com.skillbox.cryptobot.subscriber.SubscriberRepository;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectData {

  //  Класс сбора данных для отпарвки уведомлений
  @Value("${telegram.bot.collect.delay.value}")
  int time;
  @Value("${telegram.bot.collect.delay.unit}")
  String unit;
  private final CryptoCurrencyService service;
  @NonNull
  private SubscriberRepository repository;
  static Double bitPrice;
  static List<Subscriber> subList;

  private final ScheduledExecutorService scheduler =
      Executors.newScheduledThreadPool(1);

  @Bean
  public void dataCollection() {
    Runnable collection = () -> {
      try {
        bitPrice = service.getBitcoinPrice();
        fittingUsers();
      } catch (Exception e) {
        log.error("Ошибка возникла в запросе цены биткоина", e);
      }
    };
    ScheduledFuture<?> collectionHandle =
        scheduler.scheduleAtFixedRate(collection, 0, time, TimeUnit.valueOf(unit));
  }

  public void fittingUsers() {
    subList = repository.findSubscriberByPrice(bitPrice);
  }

  public static List<Subscriber> getFittingUsers() {
    return subList;
  }

  public static Double getBitPrice() {
    return bitPrice;
  }

}
