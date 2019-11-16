package cmu.edu.ini.practicum.dlanapp;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class WebUtils {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String operatorServiceAddr = MainActivity.operatorServiceAddr;
    private static final String aaaServiceAddr = MainActivity.aaaServiceAddr;

    static int getUserBalance(String address) {
        Request request = new Request.Builder()
                .url(operatorServiceAddr + "/balance?address=" + address)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Integer.parseInt(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static boolean isAAARouter(String ip) {
        Request request = new Request.Builder()
                .url(aaaServiceAddr + "/check-router?router=" + ip)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return Boolean.parseBoolean(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static void sendTransaction(String address) {
        JSONObject data;
        try {
            data = new JSONObject();
            data.put("bal", MainActivity.balance);
            data.put("signature", genSignature(MainActivity.balance));
            data.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        System.out.println(data);
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

    private static String genSignature(int a) {
        String msgHash = Hash.sha3(TypeEncoder.encode(new Uint256(BigInteger.valueOf(a))));
        Sign.SignatureData sigData = Sign.signPrefixedMessage(Numeric.hexStringToByteArray(msgHash), MainActivity.getCredentials().getEcKeyPair());
        ByteBuffer buff = ByteBuffer.wrap(new byte[65]);
        buff.put(sigData.getR());
        buff.put(sigData.getS());
        buff.put(sigData.getV());
        final byte[] sig = buff.array();
        return new String(Hex.encodeHex(sig));
    }
}
