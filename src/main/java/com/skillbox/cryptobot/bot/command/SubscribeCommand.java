package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.CryptoCurrencyService;
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
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class SubscribeCommand implements IBotCommand {

  private final CryptoCurrencyService service;
  private SubscriberRepository repository;

  @Override
  public String getCommandIdentifier() {
    return "subscribe";
  }

  @Override
  public String getDescription() {
    return "Подписывает пользователя на стоимость биткоина";
  }

  @Override
  public void processMessage(AbsSender absSender, Message message, String[] arguments) {
    Subscriber subscriber = repository.findSubscriberByChatId(message.getChatId());
    SendMessage answer = new SendMessage();
    answer.setChatId(message.getChatId());
    try {
      if (arguments.length != 1
          || !(arguments[0].matches("[0-9]+(,[0-9]{1,3})?"))) {
        answer.setText("Желаемая цена указывается в формате 12345,678 или 12345");
        absSender.execute(answer);
      } else {
        if (!(subscriber == null) && (
            Objects.equals(subscriber.getUser_id(), message.getFrom().getId())
                || subscriber.getChat_id().equals(message.getChatId()))) {
          subscriber.setPrice(Double.valueOf(arguments[0].replace(",", ".")));
          repository.save(subscriber);
          answer.setText(
              "Текущая цена биткоина " + TextUtil.toString(service.getBitcoinPrice())
                  + " USD.\n"
                  + "Новая подписка создана на стоимость " + arguments[0]);
          absSender.execute(answer);
        } else {
          answer.setText(
              "Вы ещё не зарегистрированы! Пожалуйста, выполните коменду /start");
          absSender.execute(answer);
        }
      }
    } catch (Exception e) {
      log.error("Ошибка возникла в /subscribe методе", e);
    }
  }
}