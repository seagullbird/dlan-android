package cmu.edu.ini.practicum.dlanapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.Map;

public class RefreshTask extends AsyncTask<String, Void, Map.Entry<Integer, Integer>> {
    @SuppressLint("StaticFieldLeak")
    private final MainActivity mainActivity;

    RefreshTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Map.Entry<Integer, Integer> doInBackground(String... address) {
        return new AbstractMap.SimpleEntry<>(WebUtils.getNftBalance(address[0]), mainActivity.getDlanBalance());
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onPostExecute(Map.Entry<Integer, Integer> balances) {
        TextView nftBalanceTextView = mainActivity.findViewById(R.id.curNftBalanceView);
        int nftBalance = balances.getKey();
        MainActivity.balance = nftBalance;
        nftBalanceTextView.setText("Current nft Balance: " + nftBalance);
        TextView dlanBalanceTextView = mainActivity.findViewById(R.id.curDlanBalanceView);
        dlanBalanceTextView.setText("Current dlan Balance: " + balances.getValue());
    }
}
