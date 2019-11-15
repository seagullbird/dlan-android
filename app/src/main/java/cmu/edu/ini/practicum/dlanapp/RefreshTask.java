package cmu.edu.ini.practicum.dlanapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.TextView;

public class RefreshTask extends AsyncTask<String, Void, Integer> {
    @SuppressLint("StaticFieldLeak")
    private final MainActivity mainActivity;
    RefreshTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Integer doInBackground(String... address) {

        return WebUtils.getUserBalance(address[0]);
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onPostExecute(Integer balance) {
        TextView balanceTextView = mainActivity.findViewById(R.id.curBalanceView);
        mainActivity.setBalance(balance);
        balanceTextView.setText("Current Balance: " + balance);
    }

}
