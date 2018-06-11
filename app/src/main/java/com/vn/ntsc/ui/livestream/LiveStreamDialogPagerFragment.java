package com.vn.ntsc.ui.livestream;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.nankai.designlayout.bottomnavigation.FragmentEmpty;
import com.vn.ntsc.R;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.livestream.MediaConnection;

import org.webrtc.StatsReport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nankai on 12/27/2017.
 */

public class LiveStreamDialogPagerFragment extends DialogFragment {

    public static final String TAG = LiveStreamDialogPagerFragment.class.getSimpleName();
    //----------------------------------------------------------------
    //------------------------ Variable ------------------------------
    //----------------------------------------------------------------
    private static final String BUNDLE_MODE = "bundle.model";

    ViewPager viewpagerContainer;
    TextView encoderStatView;
    ViewPagerAdapter adapter;
//    private CpuMonitor cpuMonitor;
    @UserLiveStreamService.Mode
    int mode;

    //----------------------------------------------------------------
    //------------------------ Instance ------------------------------
    //----------------------------------------------------------------
    public static LiveStreamDialogPagerFragment newInstance(@UserLiveStreamService.Mode int mode) {
        Bundle args = new Bundle();
        LiveStreamDialogPagerFragment fragment = new LiveStreamDialogPagerFragment();
        args.putInt(BUNDLE_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    //----------------------------------------------------------------
    //------------------------ life cycle ----------------------------
    //----------------------------------------------------------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog_live_stream_pager, container, true);
        viewpagerContainer = view.findViewById(R.id.fragment_dialog_live_stream_pager_container);
        encoderStatView = view.findViewById(R.id.fragment_dialog_live_stream_pager_encoder_stat_call);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        encoderStatView.setVisibility(View.GONE);
        mode = getArguments().getInt(BUNDLE_MODE);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        viewpagerContainer.setAdapter(adapter);
        if (mode == UserLiveStreamService.Mode.VIEW)
            adapter.addFragment(FragmentEmpty.newInstance(), LiveStreamViewerFragment.newInstance());
        else
            adapter.addFragment(FragmentEmpty.newInstance(), LiveStreamPlayerFragment.newInstance());

        viewpagerContainer.setCurrentItem(1);
        //At the same time the interface changed to resize has reached the soft keyboard pop-up LiveFragment will not follow the move
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.MainDialog) { //set the style, the best code here or with me, we do not change
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                getActivity().onBackPressed();
            }
        };
        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (int i = 0; i < adapter.getCount(); i++) {
            adapter.getItem(i).onActivityResult(requestCode, resultCode, data);
        }
    }

//    public void setCpuMonitor(CpuMonitor cpuMonitor) {
//        this.cpuMonitor = cpuMonitor;
//    }

    public void updateEncoderStatistics(StatsReport[] reports) {
        if (encoderStatView.getVisibility() == View.GONE)
            encoderStatView.setVisibility(View.VISIBLE);
        LogUtils.i(TAG, "-------------- update encoder statistics --------------" + reports.length);
        StringBuilder encoderStat = new StringBuilder(128);
        String fps = null;

        for (StatsReport report : reports) {
            LogUtils.i(TAG, "|-------------- type: " + report.type + " --------------" + reports.length);
            LogUtils.i(TAG, "|StatsReport: \ntype: " + report.type + "\nid: " + report.id + "\nvalues: " + report.values.length);
            if (report.values.length > 0) {
                StatsReport.Value[] values = report.values;
                for (StatsReport.Value value : values) {
                    LogUtils.i(TAG, "|StatsReport.Value: \nname: " + value.name + "\nvalue: " + value.value);
                }
            }
            LogUtils.i(TAG, "|--------------------------------------------------" + reports.length);

            if (report.type.equals("ssrc") && report.id.contains("ssrc") && report.id.contains("send")) {
                // Send video statistics.
                Map<String, String> reportMap = getReportMap(report);
                String trackId = reportMap.get("googTrackId");
                if (trackId != null && trackId.contains(MediaConnection.VIDEO_TRACK_ID)) {
                    fps = reportMap.get("googFrameRateSent");
                }
            }
        }
        if (fps != null) {
            encoderStat.append("Fps:  ").append(fps).append("\n");
        }
//        if (cpuMonitor != null) {
//            encoderStat.append("CPU%: ")
//                    .append(cpuMonitor.getCpuUsageCurrent())
//                    .append("/")
//                    .append(cpuMonitor.getCpuUsageAverage())
//                    .append(". Freq: ")
//                    .append(cpuMonitor.getFrequencyScaleAverage());
//        }
        encoderStatView.setText(encoderStat.toString());

    }

    private Map<String, String> getReportMap(StatsReport report) {
        Map<String, String> reportMap = new HashMap<String, String>();
        for (StatsReport.Value value : report.values) {
            reportMap.put(value.name, value.value);
        }
        return reportMap;
    }

    //----------------------------------------------------------------
    //------------------------ Inner class ---------------------------
    //----------------------------------------------------------------
    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment... fragments) {
            if (fragments == null)
                return;
            if (fragments.length > 0) {
                if (this.fragments == null)
                    this.fragments = new ArrayList<>();
                else
                    this.fragments.clear();

                List<Fragment> fragmentsNews = new ArrayList<>();
                fragmentsNews.addAll(Arrays.asList(fragments));
                this.fragments.addAll(fragmentsNews);
                notifyDataSetChanged();
            }
        }
    }
}
