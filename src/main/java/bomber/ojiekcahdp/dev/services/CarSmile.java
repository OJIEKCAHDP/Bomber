package bomber.ojiekcahdp.dev.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class CarSmile extends JsonService {

    public CarSmile() {
        setUrl("https://api.carsmile.com/");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "okhttp/3.12.1");
        builder.addHeader("authorization", "Bearer null");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("operationName", "enterPhone");
            json.put("variables", new JSONObject().put("phone", getFormattedPhone()));
            json.put("query", "mutation enterPhone($phone: String!) {\n  enterPhone(phone: $phone)\n}\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}
