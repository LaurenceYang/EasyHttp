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
import com.yang.demo.entity.PostEntity;
import com.yang.easyhttp.request.EasyRequestParams;
import com.yang.easyhttprx.RxEasyHttp;
import com.yang.easyhttprx.converter.RxEasyCustomConverter;

import org.reactivestreams.Subscription;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;

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

        RxEasyHttp.post(url, params, new RxEasyCustomConverter<PostEntity>() {
                    @Override
                    public void doNothing() {
                        // 防止范型类型擦除引起范型类型不能正确获取问题.
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<PostEntity>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                        dialog.show();
                    }

                    @Override
                    public void onNext(PostEntity entity) {
                        Toast.makeText(RxPostActivity.this, "提交成功", Toast.LENGTH_LONG).show();
                        result.setText("status : " + entity.getStatus() + "\n" +
                                "message : " + entity.getMessage());

                    }

                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(RxPostActivity.this, "提交失败", Toast.LENGTH_LONG).show();
                        result.setText(t.getMessage());
                        dialog.cancel();
                    }

                    @Override
                    public void onComplete() {
                        dialog.cancel();
                    }
                });
    }


}