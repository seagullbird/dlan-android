package cmu.edu.ini.practicum.dlanapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class DepositDialog extends AppCompatDialogFragment {
    @SuppressLint("StaticFieldLeak")
    private final MainActivity mainActivity;

    DepositDialog(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setTitle("Deposit").setView(input).setPositiveButton("OK", (dialog, which) -> new DepositTask(mainActivity).execute(Integer.parseInt(input.getText().toString())));
        return builder.create();
    }
}
