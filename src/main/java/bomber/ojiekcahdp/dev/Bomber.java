package bomber.ojiekcahdp.dev;

import bomber.ojiekcahdp.dev.bomber.Attack;
import bomber.ojiekcahdp.dev.bomber.Callback;
import com.google.common.io.Files;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Bomber implements Callback {

    public static Bomber bomber;
    private static File file;
    public static Bot bot;

    public static void main(String[] args) {

        TelegramBotsApi botsApi = null;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = new Bot();
            Bomber.bot = bot;
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        file = new File("users.txt");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        bomber = new Bomber();
        //bomber.startAttack(0, "9780236247", 3);

    }

    public static boolean hasUserContains(String id) {
        String text = null;
        try {
            text = Files.toString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.contains(id);
    }

    public static void addUser(String id) {
        try {
            Files.append("\n" + id, file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String[] phoneCodes = {"7", "380", ""};

    @Override
    public void onAttackEnd() {
    }

    @Override
    public void onAttackStart(int serviceCount, int numberOfCycles) {
    }

    @Override
    public void onProgressChange(int progress) {
    }


    public void startAttack(int countryCode, String phoneNumber, int numberOfCyclesNum, long clientId, Update update) {
        System.out.println(countryCode + " " + phoneNumber + " " + numberOfCyclesNum);
        Attack attack = new Attack(this, phoneCodes[countryCode], phoneNumber, numberOfCyclesNum,
                new ArrayList<>(), clientId, update);

        attack.start();
    }

}
