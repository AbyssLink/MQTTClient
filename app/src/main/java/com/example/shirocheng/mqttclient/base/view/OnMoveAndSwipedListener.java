package com.example.shirocheng.mqttclient.base.view;


/**
 * Interface definition for a callback to be invoked when a view is clicked.
 */
public interface OnMoveAndSwipedListener {

    boolean onItemMove(int fromPosition, int toPosition);

    /**
     * @param position called when item is dismiss
     */
    void onItemDismiss(int position);

}