package bomber.ojiekcahdp.dev;

import bomber.ojiekcahdp.dev.bomber.Attack;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.regex.Pattern;

public class Bot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "ojiekcahdp_bomb_bot";
    }

    @Override
    public String getBotToken() {
        return "5024151526:AAFqMwmMFeVLyxS4E59qXfI1hzlDTjDRWYM";
    }

    @Override
    public void onUpdateReceived(Update update) {

        User user = update.getMessage().getFrom();

        if (update.getMessage().getText().startsWith("/attack")) {
            if (!Bomber.hasUserContains(user.getId() + "") && user.getId() != 872447090) {
                sendMessage(update, "К сожалению, у вас нет доступа к этому боту, поскольку он приватный.\nЧтобы получить доступ, вам следует обратиться к @ojiekcandp");
                return;
            }
            String[] args = update.getMessage().getText().split(" ");
            if (args.length < 4) {
                sendMessage(update, "Для того, чтобы запустить атаку на определённый номер от вас требуется несколько аргументов:\nВведите: /attack [number] [cycles] [code]\nГде: \n[number] - номер телефона, на который хотите пустить атаку\n[cycles] - сколько раз повторять атаку\n[code] - код страны (Россия: 0, Украина: 1)");
                return;
            }
            if (!isNumber(args[1])) {
                sendMessage(update, "Вы ошиблись с номером. Проверьте правильность указания. \nЕсть вариант, что я переборщил с регулярными выражениями, если вы уверены в правильности номера - пишите сюда: @ojiekcahdp");
                return;
            }
            if (!isInteger(args[2])) {
                sendMessage(update, "Количество повторений должно быть числом, проверьте, правильность написания");
                return;
            }
            if (!isInteger(args[3])) {
                sendMessage(update, "Код страны должен соответствовать одному из следующих кодов: \nРоссия: 0\nУкраина: 1");
                return;
            }
            int code = Integer.parseInt(args[3]);
            if (code != 0 && code != 1) {
                sendMessage(update, "Код страны должен соответствовать одному из следующих кодов: \nРоссия: 0\nУкраина: 1");
                return;
            }
            if (args[1].equals("+380684540870") && user.getId() != 872447090) {
                sendMessage(update, "А вот на этого чучмека пускать атаку нельзя, сосай");
                return;
            }
            Bomber.bomber.startAttack(code, args[1].replace("+", "").replaceFirst(Bomber.phoneCodes[code], ""), Integer.parseInt(args[2]), user.getId(), update);
            sendMessage(update, "Атака запущена: \nНомер: " + args[1] + "\nСтрана: " + (code == 0 ? "Россия" : "Украина") + "\nПовторения: " + args[2] + "\nId: " + Attack.attacks.get(user.getId()).size());
        } else if (update.getMessage().getText().startsWith("/add")) {
            String[] message = update.getMessage().getText().split(" ");
            if (user.getId() != 872447090) {
                sendMessage(update, "Не получится");
                return;
            }
            Bomber.addUser(message[1]);
            sendMessage(update, "Пользователь добавлен!");
        } else if (update.getMessage().getText().startsWith("/stop")) {
            String[] args = update.getMessage().getText().split(" ");
            if (args.length < 2 || isNumber(args[1])) {
                sendMessage(update, "Для прекращения атаки используйте: /stop [id]");
                return;
            }
            if (Attack.attacks.get(user.getId()).size() < Integer.parseInt(args[1])) {
                sendMessage(update, "У вас нет запущенной атаки с этим id");
                return;
            }
            Attack.attacks.get(user.getId()).get(Integer.parseInt(args[1]) - 1).stopAttack();
            sendMessage(update, "Атака с id " + args[1] + " остановлена");
        } else {
            if (!Bomber.hasUserContains(user.getId() + "") && user.getId() != 872447090) {
                sendMessage(update, "К сожалению, у вас нет доступа к этому боту, поскольку он приватный.\nЧтобы получить доступ, вам следует обратиться к @ojiekcandp");
                return;
            }
            sendMessage(update, "Помощь по боту: \nЗапустить атаку: /attack\nПредварительно закончить атаку /stop\n(Не используйте больше 20 циклов, можете только в том случае, если знаете, что делаете)\n\nРазработчик: @ojiekcahdp");
        }

    }

    private boolean isNumber(String number) {
        return Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$").matcher(number).matches();
    }

    public void sendMessage(Update update, String message) {
        try {
            this.execute(new SendMessage(update.getMessage().getChatId().toString(), message));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean isInteger(String integer) {
        try {
            Integer.parseInt(integer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
