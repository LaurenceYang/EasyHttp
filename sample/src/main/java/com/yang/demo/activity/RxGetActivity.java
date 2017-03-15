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
import com.yang.easyhttprx.RxEasyHttp;
import com.yang.easyhttprx.converter.RxEasyStringConverter;

import org.reactivestreams.Subscription;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by yangyang on 2017/2/17.
 */
public class RxGetActivity extends AppCompatActivity {
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

        RxEasyHttp.get(url.toString(), new RxEasyStringConverter())
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        dialog.show();
                        body.setText("");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                        dialog.show();
                        body.setText("");
                    }

                    @Override
                    public void onNext(String response) {
                        body.setText(response);
                    }

                    @Override
                    public void onError(Throwable t) {
                        body.setText(t.toString());
                    }

                    @Override
                    public void onComplete() {
                        dialog.cancel();
                    }
                });
    }
}
