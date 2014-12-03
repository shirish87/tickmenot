package com.buggycoder.tickmenot.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buggycoder.tickmenot.R;
import com.buggycoder.tickmenot.model.WhatsappNotif;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class NotifListAdapter extends RecyclerView.Adapter<NotifListAdapter.ViewHolder> {

    private final List<WhatsappNotif> mNotifs;

    public NotifListAdapter() {
        this.mNotifs = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.notif_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        WhatsappNotif notif = mNotifs.get(position);
        viewHolder.sender.setText(notif.sender);
        viewHolder.message.setText(notif.message);
        viewHolder.event.setText(notif.event);
        viewHolder.postTime.setText(notif.getFormattedPostTime());
    }

    @Override
    public int getItemCount() {
        return mNotifs.size();
    }

    public List<WhatsappNotif> getNotifs() {
        return mNotifs;
    }

    public void clear() {
        mNotifs.clear();
    }

    public void setData(List<WhatsappNotif> notifs) {
        clear();

        if (notifs != null) {
            mNotifs.addAll(notifs);
        }

        Timber.d("size: %d", mNotifs.size());
        notifyDataSetChanged();
    }

    public void push(WhatsappNotif notif) {
        mNotifs.add(notif);
        notifyItemInserted(mNotifs.size() - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView sender;
        public final TextView message;
        public final TextView event;
        public final TextView postTime;

        public ViewHolder(View v) {
            super(v);
            sender = (TextView) v.findViewById(R.id.sender);
            message = (TextView) v.findViewById(R.id.message);
            event = (TextView) v.findViewById(R.id.event);
            postTime = (TextView) v.findViewById(R.id.postTime);
        }
    }

}
