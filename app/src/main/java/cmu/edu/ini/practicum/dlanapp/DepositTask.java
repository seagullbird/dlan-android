package cmu.edu.ini.practicum.dlanapp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;

import java.math.BigInteger;

public class DepositTask extends AsyncTask<Integer, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private final MainActivity mainActivity;

    DepositTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Void doInBackground(Integer... v) {
        BigInteger value = BigInteger.valueOf(v[0]);
        try {
            MainActivity.doDeposit(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        System.out.println("onPostExecute of Deposited");
        if (PayIntentService.shouldContinue) return;
        try {
            System.out.println("Trying to start payment after deposit");
            Context context = mainActivity.getApplicationContext();
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            System.out.println("Connected to " + wifiInfo.getSSID());
            String ip = Formatter.formatIpAddress(wifiManager.getDhcpInfo().gateway);
            Intent startPayIntent = new Intent(context, PayIntentService.class);
            startPayIntent.putExtra("ip", ip);
            context.startService(startPayIntent);
        } catch (Exception e) {
            e.printStackTrace();
            PayIntentService.shouldContinue = false;
        }
    }
}