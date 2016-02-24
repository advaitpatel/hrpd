package edu.depaul.csc595.jarvis.reminders.ui.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.depaul.csc595.jarvis.R;
import edu.depaul.csc595.jarvis.reminders.adapters.ReminderAdapter;
import edu.depaul.csc595.jarvis.reminders.database.DatabaseHelper;
import edu.depaul.csc595.jarvis.reminders.enums.RemindersType;
import edu.depaul.csc595.jarvis.reminders.models.Reminder;

/**
 * Created by Advait on 18-02-2016.
 */
public class TabFragment extends Fragment {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.empty_view)
    TextView mEmptyText;

    private Activity mActivity;
    private ReminderAdapter mReminderAdapter;
    private List<Reminder> mReminderList;
    private RemindersType mRemindersType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_custom_reminder_fragment_tabs, container, false);
        mActivity = getActivity();
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(messageReceiver, new IntentFilter("BROADCAST_REFRESH"));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ButterKnife.bind(this, view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);

        mRemindersType = (RemindersType) this.getArguments().get("TYPE");
        if (mRemindersType == RemindersType.INACTIVE) {
            mEmptyText.setText(getResources().getString(R.string.no_inactive));
        }

        mReminderList = getListData();
        mReminderAdapter = new ReminderAdapter(mActivity, R.layout.activity_custom_reminder_item_notification_list, mReminderList);
        mRecyclerView.setAdapter(mReminderAdapter);

        if (mReminderAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        }
    }

    public List<Reminder> getListData() {
        DatabaseHelper database = DatabaseHelper.getInstance(mActivity.getApplicationContext());
        List<Reminder> reminderList = database.getNotificationList(mRemindersType);
        database.close();
        return reminderList;
    }

    public void updateList() {
        mReminderList.clear();
        mReminderList.addAll(getListData());
        mReminderAdapter.notifyDataSetChanged();

        if (mReminderAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateList();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }
}