package com.example.tsunehitokita.jogrecord;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class WifiConfirmDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    private int mTitle;
    private int mMessage;

    public static WifiConfirmDialogFragment newInstance(int title , int message) {
        WifiConfirmDialogFragment fragment = new WifiConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, title);
        args.putInt(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        if (getArguments() != null) {
            mTitle = getArguments().getInt(ARG_TITLE);
            mMessage = getArguments().getInt(ARG_MESSAGE);
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setNegativeButton(R.string.alert_dialog_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //do nothing
                            }
                        }
                )
                .setPositiveButton(R.string.alert_dialog_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((MapsActivity) getActivity()).wifiOff();
                            }
                        }
                )
                .create();
    }

    public void show(FragmentManager fragmentManager, String dialog) {

    }
}