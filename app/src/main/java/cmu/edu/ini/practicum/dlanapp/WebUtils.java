package cmu.edu.ini.practicum.dlanapp;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class WebUtils {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String operatorServiceAddr = "";
    private static final String aaaServiceAddr = "";

    static int getUserBalance(String address) {
        Request request = new Request.Builder()
                .url(operatorServiceAddr + "/balance?user=" + address)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Integer.parseInt(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static boolean isAAARouter(String ssid) {
        Request request = new Request.Builder()
                .url(aaaServiceAddr)
                .build();
        return false;
    }

    static void sendTransaction(JSONObject data) {
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), data.toString());
        Request request = new Request.Builder()
                .url(operatorServiceAddr + "/transaction")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
