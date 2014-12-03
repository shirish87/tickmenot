package com.buggycoder.tickmenot.ui;

import android.app.Fragment;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buggycoder.tickmenot.R;
import com.buggycoder.tickmenot.model.WhatsappNotif;
import com.buggycoder.tickmenot.ui.adapter.NotifListAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static android.app.LoaderManager.LoaderCallbacks;

public class NotifListFragment extends Fragment implements LoaderCallbacks<List<WhatsappNotif>> {

    public static final String TAG = "NotifListFragment";

    protected LinearLayoutManager mLayoutManager;
    protected NotifListAdapter mAdapter;
    protected Loader<List<WhatsappNotif>> mListLoader;

    @InjectView(R.id.notifList)
    RecyclerView mRecyclerView;

    @InjectView(R.id.emptyView)
    TextView mEmptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notif_list, container, false);
        ButterKnife.inject(this, rootView);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NotifListAdapter();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                checkAdapterIsEmpty();
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        Timber.d("onCreateView");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.d("onActivityCreated");
        mListLoader = getLoaderManager().initLoader(0, null, this);
        mListLoader.forceLoad();
    }

    @Override
    public Loader<List<WhatsappNotif>> onCreateLoader(int id, Bundle args) {
        Timber.d("onCreateLoader");
        return new NotifListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<WhatsappNotif>> loader, List<WhatsappNotif> data) {
        Timber.d("onLoadFinished");
        mAdapter.setData(data);
        scrollToBottom();
    }

    @Override
    public void onLoaderReset(Loader<List<WhatsappNotif>> loader) {
        Timber.d("onLoaderReset");
        mAdapter.setData(null);
    }

    private void checkAdapterIsEmpty () {
        if (mAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public void scrollToBottom() {
        if (mAdapter.getItemCount() > 0) {
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    public void updateNotifs(List<WhatsappNotif> notifs) {
        Timber.d("refresh list");
        mListLoader.forceLoad();
    }

    private static class NotifListLoader extends AsyncTaskLoader<List<WhatsappNotif>> {

        public NotifListLoader(Context context) {
            super(context);
            Timber.d("NotifListLoader");
        }

        @Override
        public List<WhatsappNotif> loadInBackground() {
            Timber.d("loadInBackground");
            return WhatsappNotif.list();
        }
    }
}
