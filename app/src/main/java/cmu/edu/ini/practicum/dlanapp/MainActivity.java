package cmu.edu.ini.practicum.dlanapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.codec.binary.Hex;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private static final String adminPr = "0x31b7c0ca2d8c19d050b5375d066ad974b1eb2cbaf080f9f58ae24fe45ccdd70a";
    private static final String dappTokenAddr = "0xE4fC5F51269641BA65d538d5567517250b2F5390";
    private static final String dlanCoreAddr = "0xaE7F1947640FF06F49f72b78fCFfBeBAB764A278";
    private static final Credentials credentials = Credentials.create(adminPr);
    private static final String chainUrl = "http://10.0.2.2:7545";
    private static DappToken dappToken;
    private static DlanCore dlanCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initContracts();
        findViewById(R.id.depBtn).setOnClickListener(v -> new DepositDialog().show(getSupportFragmentManager(), "deposit dialog"));
        findViewById(R.id.exitBtn).setOnClickListener(v -> new ExitTask().execute());
        findViewById(R.id.refreshBtn).setOnClickListener(v -> new RefreshTask(this).execute());
    }


    private void initContracts() {
        Web3j web3 = Web3j.build(new HttpService(chainUrl));
        dappToken = DappToken.load(dappTokenAddr, web3, credentials, new DefaultGasProvider());
        dlanCore = DlanCore.load(dlanCoreAddr, web3, credentials, new DefaultGasProvider());
    }


    private static void sendClicked(View v) {
        // Spend 200 DlanToken
        Uint256 a = new Uint256(BigInteger.valueOf(200));
        String msgHash = Hash.sha3(TypeEncoder.encode(a));
        Sign.SignatureData sigData = Sign.signPrefixedMessage(Numeric.hexStringToByteArray(msgHash), credentials.getEcKeyPair());

        ByteBuffer buff = ByteBuffer.wrap(new byte[65]);
        buff.put(sigData.getR());
        buff.put(sigData.getS());
        buff.put(sigData.getV());
        byte[] sig = buff.array();
        String sigStr = Hex.encodeHexString(sig);
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
