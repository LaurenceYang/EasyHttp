package com.yang.demo.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.yang.demo.entity.DownloadEntity;
import com.yang.demo.R;
import com.yang.demo.adapter.DownloadAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yangyang on 2017/2/17.
 */
public class DownloadActivity extends AppCompatActivity {
    private DownloadAdapter mDownloadAdapter;

    @BindView(R.id.list)
    RecyclerView mRecyclerView;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_download);
        ButterKnife.bind(this);

        mDownloadAdapter = new DownloadAdapter(this);
        mRecyclerView.setAdapter(mDownloadAdapter);
        mDownloadAdapter.setData(getEntities());
    }

    private ArrayList<DownloadEntity> getEntities() {
        ArrayList<DownloadEntity> list = new ArrayList<>();
        String [][] data = {
                {
                        "天气王",
                        "http://tianqi.2345.com/redirect.php?downloadQr"
                },
                {
                        "阅读王",
                        "http://book.km.com/app/index.php?c=app&a=download",
                },
                {
                        "高清壁纸",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487332054826&di=ad31ae9a0d64c6df1b67ddf995adea96&imgtype=0&src=http%3A%2F%2Fc.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Fd01373f082025aaf2e112fa4f9edab64034f1ab9.jpg",
                },
                {
                        "高清壁纸",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487332099511&di=a59747692ee933615f8d971f78cd5c32&imgtype=0&src=http%3A%2F%2Fi1284.photobucket.com%2Falbums%2Fa577%2Fxia14905029362%2Fdongfang%2Fdesk_cg_581_zps8a07ea53.jpg",
                },
                {
                        "高清壁纸",
                        "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2920785976,3012753148&fm=23&gp=0.jpg"
                },
                {
                        "高清壁纸",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487332167386&di=97d63ccaea664b0d9ba0b6b7d88d9c4f&imgtype=jpg&src=http%3A%2F%2Fimg1.imgtn.bdimg.com%2Fit%2Fu%3D1118135712%2C1857871654%26fm%3D214%26gp%3D0.jpg"
                },
        };

        for (int i = 0; i < data.length; i++) {
            DownloadEntity entity = new DownloadEntity();
            entity.setTitle(data[i][0]);
            String url = data[i][1];
            String hashcode = String.valueOf(url.hashCode());
            entity.setUrl(data[i][1]);
            entity.setHashCode(hashcode);
            list.add(entity);
        }


        return list;
    }
}
