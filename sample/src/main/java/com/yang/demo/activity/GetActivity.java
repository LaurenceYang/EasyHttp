package com.yang.demo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.demo.R;
import com.yang.easyhttp.EasyHttpClient;
import com.yang.easyhttp.cache.EasyCacheType;
import com.yang.easyhttp.callback.EasyStringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangyang on 2017/2/17.
 */
public class GetActivity extends AppCompatActivity {
    @BindView(R.id.url)
    EditText urlView;
    @BindView(R.id.go)
    Button go;
    @BindView(R.id.body)
    TextView body;

    ProgressDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_main);

        ButterKnife.bind(this);

        dialog = new ProgressDialog(this);
    }

    @OnClick(R.id.go)
    public void go() {
        Editable url = urlView.getText();

        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "url is empty", Toast.LENGTH_SHORT);
            return;
        }

//        EasyHttpClient.get(url.toString(), new EasyStringCallback() {
        EasyHttpClient.get(url.toString(), EasyCacheType.CACHE_TYPE_SHORT, new EasyStringCallback() {
            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onFinish() {
                dialog.cancel();
            }

            @Override
            public void onSuccess(String content) {
                body.setText(content);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                body.setText(content + "\n" +error.toString());
            }
        });
    }
}
