package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.subscriber.Subscriber;
import com.skillbox.cryptobot.subscriber.SubscriberRepository;
import com.skillbox.cryptobot.utils.TextUtil;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработка команды отмены подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class UnsubscribeCommand implements IBotCommand {

  private SubscriberRepository repository;


  @Override
  public String getCommandIdentifier() {
    return "unsubscribe";
  }

  @Override
  public String getDescription() {
    return "Отменяет подписку пользователя";
  }

  @Override
  public void processMessage(AbsSender absSender, Message message, String[] arguments) {
    SendMessage answer = new SendMessage();
    answer.setChatId(message.getChatId());
    try {
      Subscriber subscriber = repository.findSubscriberByChatId(message.getChatId());
      if (!(subscriber == null) && (
          Objects.equals(subscriber.getUser_id(), message.getFrom().getId())
              && subscriber.getChat_id().equals(message.getChatId()))
          && subscriber.getPrice() == null) {
        answer.setText("Подписка отсутствует");
      }
      if (!(subscriber == null) && (
          Objects.equals(subscriber.getUser_id(), message.getFrom().getId())
              && subscriber.getChat_id().equals(message.getChatId())) && !(subscriber.getPrice()
          == null)) {
        subscriber.setPrice(null);
        repository.save(subscriber);
        answer.setText("Подписка отменена");
      }
      if (subscriber == null) {
        answer.setText("Вы ещё не зарегистрированы! Пожалуйста, выполните коменду /start");
      }
      absSender.execute(answer);
    } catch (Exception e) {
      log.error("Ошибка возникла в /subscribe методе", e);
    }
  }
}