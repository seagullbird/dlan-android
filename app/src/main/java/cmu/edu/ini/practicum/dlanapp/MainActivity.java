package cmu.edu.ini.practicum.dlanapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;

import androidx.appcompat.app.AppCompatActivity;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {
    // configs
    private static final String adminPr = "0xaf750f912e497f746459387f1826c66297ba80a02bad7960c316cff3e80dbc04";
    private static final String dappTokenAddr = "0xcC3a6f76dB1745c5387b6eBf69b6b1CA0441090f";
    private static final String dlanCoreAddr = "0xCE7001904DfF8adF14C92243306BCb39879fEb7A";
    private static final String chainUrl = "http://172.29.95.175:7545";
    static final String operatorServiceAddr = "http://172.29.95.175:5000";
    static final String aaaServiceAddr = "http://localhost";

    private static final Credentials credentials = Credentials.create(adminPr);
    private static DappToken dappToken;
    private static DlanCore dlanCore;
    private PayTask payTask;
    private BroadcastReceiver wifiConnReceiver;

    public void setBalance(int balance) {
        this.balance = balance;
    }

    private int balance;

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiConnReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initContracts();
        findViewById(R.id.depBtn).setOnClickListener(v -> new DepositDialog().show(getSupportFragmentManager(), "deposit dialog"));
        findViewById(R.id.exitBtn).setOnClickListener(v -> new ExitTask().execute());
        findViewById(R.id.refreshBtn).setOnClickListener(v -> new RefreshTask(this).execute(credentials.getAddress()));

        new RefreshTask(this).execute(credentials.getAddress());
        // listen to WiFi status
        wifiConnReceiver = new BroadcastReceiver() {
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
                        String ip = Formatter.formatIpAddress(wifiInfo.getIpAddress());
                        if (WebUtils.isAAARouter(ip)) {
                            payTask = new PayTask(credentials, balance);
                            payTask.execute();
                        }
                    } else {
                        try {
                            payTask.cancel(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiConnReceiver, filter);
    }


    private void initContracts() {
        Web3j web3 = Web3j.build(new HttpService(chainUrl));
        dappToken = DappToken.load(dappTokenAddr, web3, credentials, new DefaultGasProvider());
        dlanCore = DlanCore.load(dlanCoreAddr, web3, credentials, new DefaultGasProvider());
    }

    public static void doDeposit(BigInteger value) throws Exception {
        dappToken.approve(dlanCoreAddr, value).send();
        dlanCore.deposit(value).send();
    }

    public static void doExit() throws Exception {
        int amount = WebUtils.getUserBalance(credentials.getAddress());
        dlanCore.start_exit(BigInteger.valueOf(amount)).send();
    }
}
