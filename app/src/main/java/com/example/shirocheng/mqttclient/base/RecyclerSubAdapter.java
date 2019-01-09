package com.example.shirocheng.mqttclient.base;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.view.OnMoveAndSwipedListener;
import com.example.shirocheng.mqttclient.bean.Subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerSubAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnMoveAndSwipedListener {

    private final int TYPE_NORMAL = 1;
    private Context context;
    private List<Subscription> mItems;
    private int color = 0;
    private View parentView;         // todo: 修复parentview onResume时为null
    private onItemDismissListener listener;
    private onItemClickListener onItemClickListener;

    public RecyclerSubAdapter(Context context) {
        this.context = context;
        mItems = new ArrayList();
    }

    public void setItems(List<Subscription> data) {
        this.mItems.clear();
        this.mItems.addAll(data);
        notifyDataSetChanged();
    }

    public void addItem(int position, Subscription insertData) {
        mItems.add(position, insertData);
        notifyItemInserted(position);
    }

    public void addItems(List<Subscription> data) {
        mItems.addAll(data);
        notifyItemInserted(mItems.size() - 1);
    }

    public void setColor(int color) {
        this.color = color;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentView = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pubsub_linear, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof RecyclerViewHolder) {
            final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) viewHolder;

            // 设置视图内容
            Subscription sub = mItems.get(i);
            if (sub != null) {
                ((RecyclerViewHolder) viewHolder).tv_name.setText(sub.getName());
                ((RecyclerViewHolder) viewHolder).tv_topic.setText(sub.getTopic());
                ((RecyclerViewHolder) viewHolder).rela_round.setBackgroundResource(R.drawable.ic_notifications_none);
            }

            recyclerViewHolder.mView.setOnClickListener(view -> {
                Snackbar.make(parentView, "Start Subscribe", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                if (onItemClickListener != null) {
                    onItemClickListener.onSubscribe(sub);
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        Subscription s = mItems.get(position);
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onItemDismiss(final int position) {

        mItems.remove(position);
        notifyItemRemoved(position);

        Snackbar.make(parentView, context.getString(R.string.item_swipe_dismissed), Snackbar.LENGTH_SHORT)
                .setAction(context.getString(R.string.item_swipe_undo), v -> addItem(position, mItems.get(position))).show();

        // todo: 理解接口回调
        if (listener != null) {
            listener.onDeleteData(position);
        }
    }

    public void setOnItemDismissListener(onItemDismissListener listener) {
        this.listener = listener;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface onItemDismissListener {
        void onDeleteData(int position);
    }

    public interface onItemClickListener {
        void onSubscribe(Subscription sub);
    }


    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private RelativeLayout rela_round;
        private TextView tv_name;
        private TextView tv_topic;
        private TextView tv_msg;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            rela_round = itemView.findViewById(R.id.rela_round);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_topic = itemView.findViewById(R.id.tv_topic);
            tv_msg = itemView.findViewById(R.id.tv_msg);
        }
    }

}