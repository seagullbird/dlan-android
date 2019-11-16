package cmu.edu.ini.practicum.dlanapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int networkType = intent.getIntExtra(
                ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
        if (ConnectivityManager.TYPE_WIFI == networkType) {
            NetworkInfo networkInfo = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (networkInfo != null && wifiManager != null && networkInfo.isConnected()) {
                // Connected to new WIFI
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                System.out.println("Connected to " + wifiInfo.getSSID());
                String ip = Formatter.formatIpAddress(wifiInfo.getIpAddress());
                if (wifiInfo.getSSID().equals("\"CMU-SECURE\"")) {
                    ip = "73.71.12.184";
                }
                Intent startPayIntent = new Intent(context, PayIntentService.class);
                startPayIntent.putExtra("ip", ip);
                context.startService(startPayIntent);
            } else {
                PayIntentService.shouldContinue = false;
                System.out.println("Disconnected");
            }
        }
    }
}
