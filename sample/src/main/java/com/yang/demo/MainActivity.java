package com.yang.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.yang.demo.entity.MainEntity;
import com.yang.demo.adapter.MainAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private MainAdapter mMainAdapter;

    @BindView(R.id.list)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMainAdapter = new MainAdapter(this);
        mRecyclerView.setAdapter(mMainAdapter);
        mMainAdapter.setData(getEntities());
    }

    private ArrayList<MainEntity> getEntities() {
        ArrayList<MainEntity> list = new ArrayList<>();
        String [][] data = {
                {
                        "1",
                        "Get",
                        "Get 请求",
                },
                {
                        "2",
                        "Post",
                        "Post 请求",
                },
                {
                        "3",
                        "Download",
                        "Download 请求",
                },
                {
                        "4",
                        "Upload",
                        "Upload 请求",
                },
        };

        for (int i = 0; i < data.length; i++) {
            MainEntity entity = new MainEntity();
            entity.setType(Integer.parseInt(data[i][0]));
            entity.setTitle(data[i][1]);
            entity.setDesc(data[i][2]);
            list.add(entity);
        }

        Log.d("test" , "list.length = " + list.size());

        return list;
    }
}
