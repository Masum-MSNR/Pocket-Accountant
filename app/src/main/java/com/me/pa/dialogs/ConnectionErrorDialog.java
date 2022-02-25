package com.me.pa.dialogs;

import static com.me.pa.others.Functions.dp2px;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.me.pa.R;

public class ConnectionErrorDialog extends AppCompatDialogFragment {

    Context context;

    public ConnectionErrorDialog(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(),R.style.Dialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_no_connectivity, null);
        final Dialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopAnimation;
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.width = dp2px(context, 250);
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        window.setContentView(view);

        return dialog;
    }

}
