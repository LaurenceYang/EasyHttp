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
import com.yang.easyhttp.callback.EasyStringCallback;
import com.yang.easyhttp.request.EasyRequestParams;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangyang on 2017/2/17.
 */
public class PostActivity extends AppCompatActivity {
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

        EasyHttpClient.post("http://book.2345.com/app/index.php?c=version&a=feedback",
                params,
                new EasyStringCallback() {
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
                        Toast.makeText(PostActivity.this, "提交成功", Toast.LENGTH_LONG);
                        result.setText(content);
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        Toast.makeText(PostActivity.this, "提交失败", Toast.LENGTH_LONG);
                        result.setText(content + "\n" + error.getMessage());
                    }
                }
        );
    }

}