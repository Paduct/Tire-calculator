package org.ctavkep.tirecalculator;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

public class InfoDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_info, null);

        return new AlertDialog.Builder(getActivity())
        .setTitle(R.string.app_name)
        .setView(view)
        .setPositiveButton(android.R.string.ok, null)
        .create();
    }
}
