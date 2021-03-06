package cmu.edu.ini.practicum.dlanapp;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {
    // configs
    static final String serverAddr = "http://34.67.182.249";
    private static final String adminPr = "0x31b7c0ca2d8c19d050b5375d066ad974b1eb2cbaf080f9f58ae24fe45ccdd70a";
    private static final String dappTokenAddr = "0xE4fC5F51269641BA65d538d5567517250b2F5390";
    private static final String dlanCoreAddr = "0xaE7F1947640FF06F49f72b78fCFfBeBAB764A278";
    private static final int chainPort = 7545;

    private static final Credentials credentials = Credentials.create(adminPr);
    private static DappToken dappToken;
    private static DlanCore dlanCore;
    static int balance;

    public static Credentials getCredentials() {
        return credentials;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initContracts();
        findViewById(R.id.depBtn).setOnClickListener(v -> new DepositDialog(this).show(getSupportFragmentManager(), "deposit dialog"));
        findViewById(R.id.exitBtn).setOnClickListener(v -> new ExitTask().execute());
        findViewById(R.id.refreshBtn).setOnClickListener(v -> new RefreshTask(this).execute(credentials.getAddress()));

        new RefreshTask(this).execute(credentials.getAddress());

        registerReceiver(new WifiReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    private void initContracts() {
        Web3j web3 = Web3j.build(new HttpService(serverAddr + ":" + chainPort));
        dappToken = DappToken.load(dappTokenAddr, web3, credentials, new DefaultGasProvider());
        dlanCore = DlanCore.load(dlanCoreAddr, web3, credentials, new DefaultGasProvider());
    }

    public static void doDeposit(BigInteger value) throws Exception {
        dappToken.approve(dlanCoreAddr, value).send();
        dlanCore.deposit(value).send();
    }

    public static void doExit() throws Exception {
        PayIntentService.shouldContinue = false;
        int amount = WebUtils.getNftBalance(credentials.getAddress());
        dlanCore.start_exit(BigInteger.valueOf(amount)).send();
    }

    int getDlanBalance() {
        try {
            return dappToken.balanceOf(credentials.getAddress()).send().intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    static void updateNftBalance() {
        balance = WebUtils.getNftBalance(credentials.getAddress());
    }
}
