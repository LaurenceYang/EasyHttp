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
import com.yang.easyhttp.request.EasyRequestParams;
import com.yang.easyhttprx.RxEasyHttp;

import org.reactivestreams.Subscription;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Response;

/**
 * Created by yangyang on 2017/2/17.
 */
public class RxPostActivity extends AppCompatActivity {
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.result)
    TextView result;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_main);
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);
    }

    @OnClick(R.id.submit)
    public void submit() {
        Editable content = comment.getText();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "comment is empty", Toast.LENGTH_SHORT);
            return;
        }

        EasyRequestParams params = new EasyRequestParams();
        params.put("content", content.toString());

        String url = "http://book.km.com/app/index.php?c=version&a=feedback";

        RxEasyHttp.post(url, params)
                .map(new Function<Response, String>() {
                    @Override
                    public String apply(@NonNull Response response) throws Exception {
                        return response.body().string();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<String>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                        dialog.show();
                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(RxPostActivity.this, "提交成功", Toast.LENGTH_LONG).show();
                        result.setText(s);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(RxPostActivity.this, "提交失败", Toast.LENGTH_LONG).show();
                        result.setText(t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dialog.cancel();
                    }
                });
    }

}