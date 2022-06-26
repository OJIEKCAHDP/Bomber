package bomber.ojiekcahdp.dev.services;

import java.io.IOException;

import okhttp3.Call;

public interface Callback extends okhttp3.Callback {

    void onError(Exception e);

    @Override
    default void onFailure(Call call, IOException e) {
        onError(e);
    }
}
