package cmu.edu.ini.practicum.dlanapp;

import android.os.AsyncTask;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import okhttp3.OkHttpClient;

public class PayTask extends AsyncTask<Void, Void, Void> {
    private final OkHttpClient client = new OkHttpClient();
    private final Credentials credentials;
    private int balance;
    private final int UNIT_PRICE = 1;
    private final int INTERVAL = 1000;

    PayTask(Credentials credentials, int balance) {
        this.credentials = credentials;
        this.balance = balance;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (balance > 0) {
            sendTransaction();
            balance -= UNIT_PRICE;
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void sendTransaction() {
        JSONObject data = new JSONObject();
        try {
            data.put("bal", balance);
            data.put("signature", genSignature(balance));
            data.put("address", credentials.getAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebUtils.sendTransaction(data);
    }

    private String genSignature(int a) {
        String msgHash = Hash.sha3(TypeEncoder.encode(new Uint256(BigInteger.valueOf(a))));
        Sign.SignatureData sigData = Sign.signPrefixedMessage(Numeric.hexStringToByteArray(msgHash), credentials.getEcKeyPair());
        ByteBuffer buff = ByteBuffer.wrap(new byte[65]);
        buff.put(sigData.getR());
        buff.put(sigData.getS());
        buff.put(sigData.getV());
        byte[] sig = buff.array();
        return Hex.encodeHexString(sig);
    }
}
