package cn.djzhao.firmonitor.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.djzhao.firmonitor.R;
import cn.djzhao.firmonitor.db.AppListItem;

/**
 * Created by djzhao on 19/02/24.
 */
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<AppListItem> items;
    private static int selected;

    private OnAppListItemClickListener itemClickListener = null;
    private OnAppListItemDelClickListener delClickListener = null;

    public AppListAdapter(List<AppListItem> items, OnAppListItemClickListener itemClickListener, OnAppListItemDelClickListener delClickListener) {
        this.items = items;
        this.itemClickListener = itemClickListener;
        this.delClickListener = delClickListener;
    }

    /**
     * 列表项目点击回调接口
     */
    public interface OnAppListItemClickListener {
        void onItemClick(View view, int positon);
    }

    /**
     * 列表项目删除按钮点击回调接口
     */
    public interface OnAppListItemDelClickListener {
        void onDelClick(View view, int positon);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout layout;
        ImageView appIcon;
        TextView short_url;
        TextView appName;
        TextView updateTime;
        TextView appVersion;
        ImageView platform;
        ImageView delBtn;
        ImageView newIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.list_item_layout);
            appIcon = itemView.findViewById(R.id.list_item_icon);
            short_url = itemView.findViewById(R.id.list_item_short);
            appName = itemView.findViewById(R.id.list_item_name);
            updateTime = itemView.findViewById(R.id.list_item_time);
            appVersion = itemView.findViewById(R.id.list_item_version);
            platform = itemView.findViewById(R.id.list_item_platform_icon);
            delBtn = itemView.findViewById(R.id.list_item_delete_btn);
            newIcon = itemView.findViewById(R.id.list_item_new);

        }

        @Override
        public void onClick(View v) {
            selected = getAdapterPosition();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.content_list_item, parent, false);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AppListItem item = items.get(position);

        String platformStr = item.getPlatform();
        if ("android".equalsIgnoreCase(platformStr)) {
            holder.platform.setImageResource(R.drawable.android);
        } else {
            holder.platform.setImageResource(R.drawable.ios);
        }
        Glide.with(mContext).load(Uri.parse(item.getAppIconUrl())).into(holder.appIcon);
        holder.short_url.setText(item.getShort_url());
        holder.appName.setText(item.getAppName());
        holder.appVersion.setText(item.getAppVersion());
        holder.updateTime.setText(item.getUpdateTime());
        if (item.isNew()) {
            holder.newIcon.setImageResource(R.drawable.new_update);
        }

        holder.layout.setOnClickListener(this);
        holder.delBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_item_layout:
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, selected);
                }
                break;
            case R.id.list_item_delete_btn:
                if (delClickListener != null) {
                    delClickListener.onDelClick(v, selected);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItemClickListener(OnAppListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setDelClickListener(OnAppListItemDelClickListener delClickListener) {
        this.delClickListener = delClickListener;
    }
}
