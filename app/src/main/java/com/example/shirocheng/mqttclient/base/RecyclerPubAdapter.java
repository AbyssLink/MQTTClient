package com.example.shirocheng.mqttclient.base;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.view.OnMoveAndSwipedListener;
import com.example.shirocheng.mqttclient.bean.Publishing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerPubAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnMoveAndSwipedListener {

    private final int TYPE_NORMAL = 1;
    private Context context;
    private List<Publishing> mItems;
    private int color = 0;
    private View parentView;         // todo: 修复parentview onResume时为null
    private onItemDismissListener listener;

    public RecyclerPubAdapter(Context context) {
        this.context = context;
        mItems = new ArrayList();
    }

    public void setItems(List<Publishing> data) {
        this.mItems.clear();
        this.mItems.addAll(data);
        notifyDataSetChanged();
    }

    public void addItem(int position, Publishing insertData) {
        mItems.add(position, insertData);
        notifyItemInserted(position);
    }

    public void addItems(List<Publishing> data) {
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

            if (color == 1) {
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_green)));
            } else if (color == 2) {
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_yellow)));
            } else if (color == 3) {
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_red)));
            } else if (color == 4) {
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
            } else {
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_blue)));
            }

            // 设置视图内容
            Publishing pub = mItems.get(i);
            if (pub != null) {
                ((RecyclerViewHolder) viewHolder).tv_name.setText(pub.getName());
                ((RecyclerViewHolder) viewHolder).tv_topic.setText(pub.getTopic());
                if (pub.getTopic() != null) {
                    ((RecyclerViewHolder) viewHolder).ic_active.setColorFilter(context.getResources().getColor(R.color.google_green));
                } else {
                    ((RecyclerViewHolder) viewHolder).ic_active.setColorFilter(context.getResources().getColor(R.color.google_red));
                }
            }

            recyclerViewHolder.mView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ControlActivity.class);
                intent.putExtra("color", color);
                intent.putExtra("id", String.valueOf(pub.getId()));
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation
                        ((Activity) context, recyclerViewHolder.rela_round, "shareView").toBundle());
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        Publishing s = mItems.get(position);
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

    public interface onItemDismissListener {
        void onDeleteData(int position);
    }


    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private RelativeLayout rela_round;
        private TextView tv_name;
        private TextView tv_topic;
        private ImageView ic_active;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            rela_round = itemView.findViewById(R.id.rela_round);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_topic = itemView.findViewById(R.id.tv_topic);
            ic_active = itemView.findViewById(R.id.ic_active);
        }
    }

}