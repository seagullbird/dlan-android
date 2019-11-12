package cmu.edu.ini.practicum.dlanapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import org.web3j.abi.datatypes.BytesType;
import org.web3j.abi.datatypes.NumericType;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.abi.TypeEncoder;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String adminPr = "0x31b7c0ca2d8c19d050b5375d066ad974b1eb2cbaf080f9f58ae24fe45ccdd70a";
    private static final String operatorPr = "0x1987bdd860a52d9a5766cd39dbd2ad2265351bb15c8e8167a822d483a2960224";
    private static final String dappTokenAddr = "0xE4fC5F51269641BA65d538d5567517250b2F5390";
    private static final String dlanCoreAddr = "0xaE7F1947640FF06F49f72b78fCFfBeBAB764A278";
    private static final Credentials credentials = Credentials.create(adminPr);
    private static final String url = "http://10.0.2.2:7545";
    private static final Web3j web3 = Web3j.build(new HttpService(url));
    private static DappToken dappToken;
    private static DlanCore dlanCore;

    // shared
//    private static byte[] sigBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Temporary: https://stackoverflow.com/questions/6343166/how-do-i-fix-android-os-networkonmainthreadexception#comment13707824_9289190
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initWeb3j();

        findViewById(R.id.depBtn).setOnClickListener(MainActivity::depositClicked);
        findViewById(R.id.sendBtn).setOnClickListener(MainActivity::sendClicked);
        findViewById(R.id.exitBtn).setOnClickListener(MainActivity::exitClicked);
        findViewById(R.id.chaBtn).setOnClickListener(MainActivity::challengeClicked);
    }

    private void initWeb3j() {
        dappToken = DappToken.load(dappTokenAddr, web3, credentials, new DefaultGasProvider());
        dlanCore = DlanCore.load(dlanCoreAddr, web3, credentials, new DefaultGasProvider());
    }

    private static void depositClicked(View v) {
        // deposit 500 DlanToken
        BigInteger value = BigInteger.valueOf(500L);
        try {
            dappToken.approve(dlanCoreAddr, value).send();
            dlanCore.deposit(value).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendClicked(View v) {
        // Spend 200 DlanToken
//        int a = 200;
//        byte[] msgHash = Hash.sha3(BigInteger.valueOf(a).toByteArray());
//
//        Sign.SignatureData sig = Sign.signPrefixedMessage(msgHash, credentials.getEcKeyPair());
//
//        ByteBuffer buff = ByteBuffer.wrap(new byte[65]);
//        buff.put(sig.getR());
//        buff.put(sig.getS());
//        buff.put(sig.getV());
//        sigBytes = buff.array();
//        System.out.println(Numeric.toHexString(sigBytes));
        Uint256 a = new Uint256(BigInteger.valueOf(200));
        String msgHash = Hash.sha3(TypeEncoder.encode(a));
        Sign.SignatureData sigData = Sign.signPrefixedMessage(Numeric.hexStringToByteArray(msgHash), credentials.getEcKeyPair());

        ByteBuffer buff = ByteBuffer.wrap(new byte[65]);
        buff.put(sigData.getR());
        buff.put(sigData.getS());
        buff.put(sigData.getV());
        byte[] sig = buff.array();
    }

    private static void exitClicked(View v) {
        // try to exit with (500 - 100) = 400 DlanToken
        try {
            dlanCore.start_exit(BigInteger.valueOf(100)).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void challengeClicked(View v) {
        Uint256 a = new Uint256(BigInteger.valueOf(200));
        String msgHash = Hash.sha3(TypeEncoder.encode(a));
        Sign.SignatureData sigData = Sign.signPrefixedMessage(Numeric.hexStringToByteArray(msgHash), credentials.getEcKeyPair());

        ByteBuffer buff = ByteBuffer.wrap(new byte[65]);
        buff.put(sigData.getR());
        buff.put(sigData.getS());
        buff.put(sigData.getV());
        byte[] sig = buff.array();

        DlanCore operatorDlanCore = DlanCore.load(dlanCoreAddr, web3, Credentials.create(operatorPr), new DefaultGasProvider());
        try {
            operatorDlanCore.challenge(credentials.getAddress(), BigInteger.valueOf(200), sig).send();
//            byte[] result = operatorDlanCore.test(BigInteger.valueOf(200)).send();
//            System.out.println(Numeric.toHexString(result));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
