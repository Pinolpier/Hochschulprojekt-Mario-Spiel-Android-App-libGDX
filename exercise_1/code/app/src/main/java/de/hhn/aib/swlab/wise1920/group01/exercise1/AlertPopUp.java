package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.fragment.app.DialogFragment;

public class AlertPopUp extends DialogFragment {

    public AlertDialog onCreateDialog() {

        // Use the Builder class for convenient dialog construction
        Builder builder = new Builder(getActivity());
        builder.setMessage("")
                .setPositiveButton(R.string.app_name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.on_off, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        return builder.create();
    }

}
