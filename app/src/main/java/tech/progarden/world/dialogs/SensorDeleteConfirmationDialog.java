package tech.progarden.world.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import tech.progarden.world.R;

/**
 * Created by 1 on 1/28/2016.
 */
public class SensorDeleteConfirmationDialog {
    AlertDialog.Builder builder;

    public SensorDeleteConfirmationDialog(Context context) {
        builder = new AlertDialog.Builder(context);

        // Use the Builder class for convenient dialog construction
        builder.setTitle(R.string.delete_confirmation_title);
        builder.setMessage(R.string.delete_confirmation_question);
    }

    public void setPositiveButtonListener(DialogInterface.OnClickListener listener) {
        builder.setPositiveButton(R.string.yes, listener);
    }

    public void setNegativeButtonListener(DialogInterface.OnClickListener listener) {
        builder.setNegativeButton(R.string.cancel, listener);
    }

    public Dialog create() {
        // Create the AlertDialog object and return
        return builder.create();
    }
}
