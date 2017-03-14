package com.yang.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yang.demo.activity.GetActivity;
import com.yang.demo.activity.PostActivity;
import com.yang.demo.activity.RxGetActivity;
import com.yang.demo.activity.RxPostActivity;
import com.yang.demo.entity.MainEntity;
import com.yang.demo.R;
import com.yang.demo.activity.DownloadActivity;
import com.yang.demo.activity.UploadActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yangyang on 2017/2/17.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private ArrayList<MainEntity> mList;
    private Context mContext;

    public MainAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(ArrayList<MainEntity> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("test", "onBindViewHolder called position : " + position);
        final MainEntity entity = mList.get(position);

        holder.title.setText(entity.getTitle());
        holder.desc.setText(entity.getDesc());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (entity.getType()) {
                    case 1:
                        Intent intent = new Intent(mContext, GetActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(mContext, PostActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(mContext, DownloadActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(mContext, UploadActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(mContext, RxGetActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(mContext, RxPostActivity.class);
                        mContext.startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.desc)
        TextView desc;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
