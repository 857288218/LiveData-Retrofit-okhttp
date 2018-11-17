package com.example.rjq.myapplication.progress;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.example.rjq.myapplication.R;


/**
 * Created by liukun on 16/3/10.
 */
public class ProgressDialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private ProgressDialog pd;
    private LoadingDialog ld;
    private Context context;
    private boolean cancelable;
    private ProgressCancelListener mProgressCancelListener;

    public ProgressDialogHandler(Context context, ProgressCancelListener mProgressCancelListener,
                                 boolean cancelable) {
        super();
        this.context = context;
        this.mProgressCancelListener = mProgressCancelListener;
        this.cancelable = cancelable;
    }

    private void initProgressDialog(){
//        if (pd == null) {
//            pd = new ProgressDialog(context);
////
//            pd.setCancelable(cancelable);
//
//            if (cancelable) {
//                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialogInterface) {
//                        mProgressCancelListener.onCancelProgress();
//                    }
//                });
//            }
//
//            if (!pd.isShowing()) {
//                pd.show();
//            }
//        }
        if (ld == null){
            ld = new LoadingDialog(context, R.style.ActionSheetDialogStyle);
            ld.setCancelable(cancelable);
            if (cancelable){
                ld.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mProgressCancelListener.onCancelProgress();
                    }
                });
            }
            if (!ld.isShowing()){
                ld.show();
            }
        }
    }

    private void dismissProgressDialog(){
//        if (pd != null) {
//            pd.dismiss();
//            pd = null;
//        }
        if (ld != null){
            ld.dismiss();
            ld = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }

}
