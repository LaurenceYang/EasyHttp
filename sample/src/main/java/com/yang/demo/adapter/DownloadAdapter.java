package com.yang.demo.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.demo.entity.DownloadEntity;
import com.yang.demo.R;
import com.yang.easyhttp.download.EasyDownloadManager;
import com.yang.easyhttp.download.EasyDownloadTask;
import com.yang.easyhttp.download.EasyDownloadTaskListener;
import com.yang.easyhttp.download.EasyTaskEntity;
import com.yang.easyhttp.download.EasyTaskStatus;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yangyang on 2017/2/17.
 */
public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
    private ArrayList<DownloadEntity> mList;
    private Context mContext;
    private EasyDownloadManager mDownloadManger;

    public DownloadAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(ArrayList<DownloadEntity> list) {
        mList = list;
        notifyDataSetChanged();
        mDownloadManger = EasyDownloadManager.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.download_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DownloadEntity entity = mList.get(position);
        final String url = entity.getUrl();
        String taskId = entity.getHashCode();
        final EasyDownloadTask task = mDownloadManger.getTask(taskId);

        holder.itemView.setTag(url);

        if (task == null) {
            holder.button.setText(R.string.start);
            holder.progressTxt.setText("0");
            holder.progress.setProgress(0);
        } else {
            EasyTaskEntity easyTaskEntity = task.getTaskEntity();
            final int status = easyTaskEntity.getTaskStatus();
            responseUIListener(task, holder);

            String progress = getPercent(easyTaskEntity.getCompletedSize(), easyTaskEntity.getTotalSize());

            switch (status) {
                case EasyTaskStatus.TASK_STATUS_INIT:
                    boolean isPause = mDownloadManger.isPauseTask(easyTaskEntity.getTaskId());
                    boolean isFinish = mDownloadManger.isFinishTask(easyTaskEntity.getTaskId());
                    holder.button.setText(isFinish ? R.string.delete : !isPause ? R.string.start : R.string.resume);
                    holder.progress.setProgress(Integer.parseInt(progress));
                    holder.progressTxt.setText(progress);
                    break;
                case EasyTaskStatus.TASK_STATUS_QUEUE:
                    holder.button.setText(R.string.queue);
                    holder.progress.setProgress(Integer.parseInt(progress));
                    holder.progressTxt.setText(progress);
                    break;
                case EasyTaskStatus.TASK_STATUS_CONNECTING:
                    holder.button.setText(R.string.connecting);
                    holder.progress.setProgress(Integer.parseInt(progress));
                    holder.progressTxt.setText(progress);
                    break;
                case EasyTaskStatus.TASK_STATUS_DOWNLOADING:
                    holder.button.setText(R.string.pause);
                    holder.progress.setProgress(Integer.parseInt(progress));
                    holder.progressTxt.setText(progress);
                    break;
                case EasyTaskStatus.TASK_STATUS_PAUSE:
                    holder.button.setText(R.string.resume);
                    holder.progress.setProgress(Integer.parseInt(progress));
                    holder.progressTxt.setText(progress);
                    break;
                case EasyTaskStatus.TASK_STATUS_FINISH:
                    holder.button.setText(R.string.delete);
                    holder.progress.setProgress(100);
                    holder.progressTxt.setText(progress);
                    break;
                case EasyTaskStatus.TASK_STATUS_REQUEST_ERROR:
                case EasyTaskStatus.TASK_STATUS_STORAGE_ERROR:
                    holder.button.setText(R.string.retry);
                    holder.progress.setProgress(Integer.parseInt(progress));
                    holder.progressTxt.setText(progress);
                    break;
            }
        }

        holder.title.setText(entity.getTitle());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskId = String.valueOf(holder.itemView.getTag().hashCode());
                EasyDownloadTask task = mDownloadManger.getTask(taskId);

                if (task == null) {
                    task = new EasyDownloadTask(new EasyTaskEntity.Builder().taskId(taskId).downloadUrl(url).build());
                    responseUIListener(task, holder);
                    mDownloadManger.addTask(task);
                } else {
                    responseUIListener(task, holder);
                    EasyTaskEntity easyTaskEntity = task.getTaskEntity();
                    final int status = easyTaskEntity.getTaskStatus();
                    responseUIListener(task, holder);

                    switch (status) {
                        case EasyTaskStatus.TASK_STATUS_QUEUE:
                            mDownloadManger.pauseTask(task);
                            break;
                        case EasyTaskStatus.TASK_STATUS_INIT:
                            mDownloadManger.addTask(task);
                            break;
                        case EasyTaskStatus.TASK_STATUS_CONNECTING:
                            mDownloadManger.pauseTask(task);
                            break;
                        case EasyTaskStatus.TASK_STATUS_DOWNLOADING:
                            mDownloadManger.pauseTask(task);
                            break;
                        case EasyTaskStatus.TASK_STATUS_CANCEL:
                            mDownloadManger.addTask(task);
                            break;
                        case EasyTaskStatus.TASK_STATUS_PAUSE:
                            mDownloadManger.resumeTask(task);
                            break;
                        case EasyTaskStatus.TASK_STATUS_FINISH:
                            mDownloadManger.cancelTask(task);
                            break;
                        case EasyTaskStatus.TASK_STATUS_REQUEST_ERROR:
                        case EasyTaskStatus.TASK_STATUS_STORAGE_ERROR:
                            mDownloadManger.addTask(task);
                            break;
                    }

                }
            }
        });
    }

    private void responseUIListener(EasyDownloadTask task, final ViewHolder holder) {
        final EasyTaskEntity taskEntity = task.getTaskEntity();

        task.setDownloadTaskListener(new EasyDownloadTaskListener() {
            @Override
            public void onQueue(EasyDownloadTask downloadTask) {
                if (holder.itemView.getTag().equals(taskEntity.getDownloadUrl())) {
                    holder.button.setText(R.string.queue);
                }
            }

            @Override
            public void onConnecting(EasyDownloadTask downloadTask) {
                if (holder.itemView.getTag().equals(taskEntity.getDownloadUrl())) {
                    holder.button.setText(R.string.connecting);
                }
            }

            @Override
            public void onDownloading(EasyDownloadTask downloadTask) {
                if (holder.itemView.getTag().equals(taskEntity.getDownloadUrl())) {
                    if (holder.itemView.getTag().equals(taskEntity.getDownloadUrl())) {
                        holder.button.setText(R.string.pause);
                        holder.progress.setProgress(Integer.parseInt(getPercent(taskEntity.getCompletedSize(), taskEntity.getTotalSize())));
                        holder.progressTxt.setText(getPercent(taskEntity.getCompletedSize(), taskEntity.getTotalSize()));
                    }
                }
            }

            @Override
            public void onPause(EasyDownloadTask downloadTask) {
                if (holder.itemView.getTag().equals(taskEntity.getDownloadUrl())) {
                    holder.button.setText(R.string.resume);
                }
            }

            @Override
            public void onCancel(EasyDownloadTask downloadTask) {
                if (holder.itemView.getTag().equals(taskEntity.getDownloadUrl())) {
                    holder.button.setText(R.string.start);
                    holder.progressTxt.setText("0");
                    holder.progress.setProgress(0);
                }
            }

            @Override
            public void onFinish(EasyDownloadTask downloadTask) {
                if (holder.itemView.getTag().equals(taskEntity.getDownloadUrl())) {
                    holder.button.setText(R.string.delete);
                }
            }

            @Override
            public void onError(EasyDownloadTask downloadTask, int code) {
                if (holder.itemView.getTag().equals(taskEntity.getDownloadUrl())) {
                    holder.button.setText(R.string.retry);

                    switch (code) {
                        case EasyTaskStatus.TASK_STATUS_REQUEST_ERROR:
                            Toast.makeText(mContext, R.string.request_error, Toast.LENGTH_SHORT).show();
                            break;
                        case EasyTaskStatus.TASK_STATUS_STORAGE_ERROR:
                            Toast.makeText(mContext, R.string.storage_error, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return  mList != null ? mList.size() : 0;
    }

    private String getPercent(long completed, long total) {
        if (total > 0) {
            double fen = ((double)completed/(double)total) * 100;
            DecimalFormat df = new DecimalFormat("0");
            return df.format(fen);
        }

        return "0";
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.progress)
        ProgressBar progress;
        @BindView(R.id.progress_txt)
        TextView progressTxt;
        @BindView(R.id.button)
        Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
