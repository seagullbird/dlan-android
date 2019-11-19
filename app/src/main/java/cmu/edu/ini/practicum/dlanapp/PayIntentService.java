package cmu.edu.ini.practicum.dlanapp;

import android.app.IntentService;
import android.content.Intent;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class PayIntentService extends IntentService {
    public static volatile boolean shouldContinue = false;
    private final int UNIT_PRICE = 1;
    private final int INTERVAL = 1000;
    private final String address = MainActivity.getCredentials().getAddress();


    public PayIntentService() {
        super("PayIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        String ip = intent.getStringExtra("ip");
        System.out.println(ip);
        shouldContinue = WebUtils.isAAARouter(ip);
        System.out.println("Pay service started");

        MainActivity.updateNftBalance();
        while (MainActivity.balance > 0 && shouldContinue) {
            System.out.println("Current balance " + MainActivity.balance);
            WebUtils.sendTransaction(address);
            MainActivity.balance -= UNIT_PRICE;
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Pay service stopped");
        stopSelf();
    }
}
