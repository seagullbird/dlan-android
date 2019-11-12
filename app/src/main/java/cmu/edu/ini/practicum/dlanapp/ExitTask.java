package cmu.edu.ini.practicum.dlanapp;

import android.os.AsyncTask;


class ExitTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            MainActivity.doExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}