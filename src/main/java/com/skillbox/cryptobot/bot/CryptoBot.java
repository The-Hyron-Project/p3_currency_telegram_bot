package com.skillbox.cryptobot.bot;

import com.skillbox.cryptobot.notifications.CollectData;
import com.skillbox.cryptobot.subscriber.Subscriber;
import com.skillbox.cryptobot.utils.TextUtil;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Service
@Slf4j
public class CryptoBot extends TelegramLongPollingCommandBot {

  @Value("${telegram.bot.notify.delay.value}")
  int time;
  @Value("${telegram.bot.notify.delay.unit}")
  String unit;
  private final String botUsername;
  static List<Subscriber> subList;
  Double bitPrice;

  public CryptoBot(
      @Value("${telegram.bot.token}") String botToken,
      @Value("${telegram.bot.username}") String botUsername,
      List<IBotCommand> commandList
  ) {
    super(botToken);
    this.botUsername = botUsername;

    commandList.forEach(this::register);
  }

  @Override
  public String getBotUsername() {
    return botUsername;
  }

  //    Метод обработки сообщений, не вляющих командой
  @Override
  public void processNonCommandUpdate(Update update) {
    SendMessage sm = SendMessage.builder()
        .chatId(update.getMessage().getFrom().getId())
        .text("Не является командой.\n"
            + "Поддерживаемые команды:\n"
            + "/start - начало работы с чат-ботом\n"
            + "/get_price - получить стоимость биткоина\n"
            + "/subscribe ХХХ - оформить подписку на определённый курс биткоина (ХХХ - желаемый курс).\n"
            + "Вы получите уведомление, когда курс биткоина опустится ниже указанного значения\n"
            + "/get_subscription - получить информацию о уже оформленной подписке\n"
            + "/unsubscribe - отмена подписки")
        .build();
    try {
      execute(sm);
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }

  //  Класс генерирует новый сервис расписаний
  private final ScheduledExecutorService scheduler =
      Executors.newScheduledThreadPool(1);

  //  Метод отпарвки уведомлений
  @Bean
  public void notificationSender() {
    Runnable sendNotifications =
        () -> {
          subList = CollectData.getFittingUsers();
          if (!(subList == null) && !subList.isEmpty()) {
            bitPrice = CollectData.getBitPrice();
            subList.stream()
                .forEach(subscriber -> {
                  SendMessage sm = SendMessage.builder()
                      .chatId(subscriber.getUser_id().toString())
                      .text("Пора покупать, стоимость биткоина " + TextUtil.toString(bitPrice))
                      .build();
                  try {
                    execute(sm);
                  } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                  }
                });
          }
        };
    ScheduledFuture<?> notificationHandle =
        scheduler.scheduleAtFixedRate(sendNotifications, 0, time, TimeUnit.valueOf(unit));
  }
}
