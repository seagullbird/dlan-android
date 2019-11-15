package cmu.edu.ini.practicum.dlanapp;
import android.os.AsyncTask;

import java.math.BigInteger;

public class DepositTask extends AsyncTask<Integer, Void, Void> {
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
}