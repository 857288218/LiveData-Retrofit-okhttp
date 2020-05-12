package com.example.rjq.myapplication.activity;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.rjq.myapplication.R;
import com.example.rjq.myapplication.entity.User;
import com.example.rjq.myapplication.entity.WanResponse;
import com.example.rjq.myapplication.http.HttpMethods;
import com.example.rjq.myapplication.progress.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.click_me_BN)
    Button clickMeBN;
    @BindView(R.id.result_TV)
    TextView resultTV;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.click_me_BN)
    public void onClick() {
        getMovie();
    }

    //进行网络请求
    private void getMovie() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this, R.style.ActionSheetDialogStyle);
        }
        loadingDialog.show();
        Log.d("current thread", Thread.currentThread().getName());
        HttpMethods.getInstance().login("15620419359", "rjq015").observe(this, new Observer<WanResponse<User>>() {
            @Override
            public void onChanged(@Nullable WanResponse<User> subjects) {
                loadingDialog.dismiss();
                if (subjects != null)
                    resultTV.setText(subjects.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        if (HttpMethods.getInstance().wanResponseCallback != null) {
            HttpMethods.getInstance().wanResponseCallback.cancel();
        }
    }
}
