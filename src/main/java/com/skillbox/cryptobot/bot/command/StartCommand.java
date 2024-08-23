package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.subscriber.Subscriber;
import com.skillbox.cryptobot.subscriber.SubscriberRepository;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * Обработка команды начала работы с ботом
 */
@Service
@AllArgsConstructor
@Slf4j
public class StartCommand implements IBotCommand {

  private SubscriberRepository repository;

  @Override
  public String getCommandIdentifier() {
    return "start";
  }

  @Override
  public String getDescription() {
    return "Запускает бота";
  }

  @Override
  public void processMessage(AbsSender absSender, Message message, String[] arguments) {
    SendMessage answer = new SendMessage();
    answer.setChatId(message.getChatId());

    answer.setText("""
        Привет! Данный бот помогает отслеживать стоимость биткоина.
        Поддерживаемые команды:
         /get_price - получить стоимость биткоина
         /subscribe ХХХ - оформить подписку на определённый курс биткоина (ХХХ - желаемый курс).
         Вы получите уведомление, когда курс биткоина опустится ниже указанного значения
         /get_subscription - получить информацию о уже оформленной подписке
         /unsubscribe - отмена подписки
        """);
    try {
      absSender.execute(answer);
    } catch (TelegramApiException e) {
      log.error("Error occurred in /start command", e);
    }
    Subscriber subscriber = repository.findSubscriberByChatId(message.getChatId());
    if (!(subscriber == null) && (Objects.equals(subscriber.getUser_id(), message.getFrom().getId())
        || subscriber.getChat_id().equals(message.getChatId()))) {
      answer.setText("Вы уже зарегестрированы.");
      try {
        absSender.execute(answer);
      } catch (TelegramApiException e) {
        throw new RuntimeException(e);
      }
    } else {
      subscriber = new Subscriber(message.getFrom().getId(), message.getChatId(), null);
      repository.save(subscriber);
      answer.setText("Вы успешно зарегестрированы!");
      try {
        absSender.execute(answer);
      } catch (TelegramApiException e) {
        throw new RuntimeException(e);
      }
    }

  }
}