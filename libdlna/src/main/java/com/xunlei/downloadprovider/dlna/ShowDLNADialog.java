package com.xunlei.downloadprovider.dlna;

/**
 * Created by Stephan on 2016/9/16.
 */

import com.xunlei.downloadprovider.dlna.data.*;

import org.fourthline.cling.registry.*;
import org.fourthline.cling.model.meta.*;

import android.os.*;
import java.util.*;
import com.xunlei.downloadprovider.dlna.util.*;
import com.xunlei.downloadprovider.dlna.player.*;

import android.util.Log;
import android.view.animation.*;
import android.app.*;
import android.widget.*;
import android.content.*;
import android.view.*;
import com.xunlei.downloadprovider.dlna.core.*;


public class ShowDLNADialog implements View.OnClickListener
{
    private static final int DEVICE_CONNECTING = 3000;
    private static final int DEVICE_CONNECT_ERROR = 3002;
    private static final int DEVICE_CONNECT_SUCCESS = 3001;
    private static final int DEVICE_SEEK_BACK = 3003;
    private static final int MSG_UPDATE_CONNECT_VIEW = 5;
    private static final int MSG_UPDATE_DIALOG_VIEW = 4;
    private static final int MSG_UPDATE_SPACE_TIME_OUT = 3;
    private static final int MSG_UPDATE_TIME_START = 1;
    private static final int MSG_UPDATE_TIME_STOP = 2;
    private static final int SEEK_TIMES = 3;
    private static final int SEEK_UPDATE_TIME = 1000;
    private static final int SEEK_WAVE = 3;
    private static final int SHOW_LISTVIEW = 2002;
    private static final int SHOW_NO_TARGET = 2000;
    private static final int SHOW_PROGRESS = 2001;
    private static final String TAG = ShowDLNADialog.class.getSimpleName();
    private static int mCurSeekTimes;
    private View dialogDeliver;
    private View dialogView;
    private boolean isEnd;
    private Button mBtnBackToPhonePlay;
    private Context mContext;
    private int mCurPlayPos;
    private DeviceAdapter mDeviceAdapter;
    private List<Device> mDevices;
    private DLNAPlayer mDlnaPlayer;
    private boolean mFirstAddDevice;
    private boolean mIsFirstSearch;
    private ImageView mIvClose;
    private ImageView mIvDlnaConnectedFail;
    private ImageView mIvDlnaConnectedSuccess;
    private ImageView mIvRefresh;
    private OnDLNADialogListener mListener;
    private ListView mLvDeviceDisplay;
    private Handler mMainHandler;
    private TextView mNoTargetDevice;
    private ProgressBar mPbDlnaConnectLoad;
    private ProgressBar mPbDlnaSearchLoad;
    private OnDLNADialogListener.MediaPlayerPlayCMD mPlayCMD;
    private String mRefUrl;
    private RegistryListener mRegistryListener;
    private String mResourceName;
    private RelativeLayout mRlDlnaSearchingDevice;
    private Dialog mShowDLNADialog;
    private TimeOutRunnable mTimeOutRunnable;
    private TextView mTvDlnaDeviceName;
    private TextView mTvDlnaResourceName;
    private TextView mTvShowDlnaConnectState;

    static {
        mCurSeekTimes = 0;
    }

    public ShowDLNADialog(final Context mContext, final OnDLNADialogListener mListener) {
        this.isEnd = false;
        this.mFirstAddDevice = true;
        this.mRegistryListener = new RegistryListener() {
            @Override
            public void afterShutdown() {
            }

            @Override
            public void beforeShutdown(final Registry registry) {
            }

            @Override
            public void localDeviceAdded(final Registry registry, final LocalDevice localDevice) {
            }

            @Override
            public void localDeviceRemoved(final Registry registry, final LocalDevice localDevice) {
            }

            @Override
            public void remoteDeviceAdded(final Registry registry, final RemoteDevice remoteDevice) {
                if (!isEnd) {
                    addDevice(remoteDevice);
                }
            }

            @Override
            public void remoteDeviceDiscoveryFailed(final Registry registry, final RemoteDevice remoteDevice, final Exception ex) {
                if (!isEnd) {
                    mMainHandler.obtainMessage(4, 2000, 0).sendToTarget();
//                    StatReporter.reportDlnaResultNone();
                }
            }

            @Override
            public void remoteDeviceDiscoveryStarted(final Registry registry, final RemoteDevice remoteDevice) {
                if (!isEnd && !mIsFirstSearch) {
                    setFirstSearch(!mIsFirstSearch);
                    mMainHandler.obtainMessage(4, 2001, 0).sendToTarget();
                }
            }

            @Override
            public void remoteDeviceRemoved(final Registry registry, final RemoteDevice remoteDevice) {
                if (!isEnd) {
                    removeDevice(remoteDevice);
                }
            }

            @Override
            public void remoteDeviceUpdated(final Registry registry, final RemoteDevice remoteDevice) {
                if (!isEnd) {
                    addDevice(remoteDevice);
                }
            }
        };
        this.mMainHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(final Message message) {
                Label_0040: {
                    switch (message.what) {
                        case 1: {
                            if (isEnd || mCurPlayPos <= 0 || mDlnaPlayer == null) {
                                break;
                            }
                            final int currentPosition = mDlnaPlayer.getCurrentPosition();
                            int n;
                            if (currentPosition > 0) {
                                n = currentPosition / 1000;
                            }
                            else {
                                n = 0;
                            }
                            final int n2 = mCurPlayPos / 1000;
                            setCurSeekTime(mCurSeekTimes + 1);
                            if (mCurSeekTimes == 1) {
                                if (n != n2) {
                                    mDlnaPlayer.seekTo(n2 * 1000);
                                    this.sendEmptyMessageDelayed(1, 1000L);
                                    return;
                                }
                                break;
                            }
                            else {
                                if ((n > n2 + 3 || n < n2 - 3) && mCurSeekTimes <= 3) {
                                    mDlnaPlayer.seekTo(n2 * 1000);
                                    this.sendEmptyMessageDelayed(1, 1000L);
                                    return;
                                }
                                break;
                            }

                        }
                        case 3: {
                            mMainHandler.obtainMessage(4, 2000, 0).sendToTarget();
                            stopTimeRunner();
//                            StatReporter.reportDlnaResultNone();
                            return;
                        }
                        case 4: {
                            switch (message.arg1) {
                                default: {
                                    return;
                                }
                                case 2000: {
                                    mNoTargetDevice.setVisibility(View.VISIBLE);
                                    mRlDlnaSearchingDevice.setVisibility(View.GONE);
                                    mLvDeviceDisplay.setVisibility(View.GONE);
                                    return;
                                }
                                case 2001: {
                                    mNoTargetDevice.setVisibility(View.GONE);
                                    mRlDlnaSearchingDevice.setVisibility(View.VISIBLE);
                                    mLvDeviceDisplay.setVisibility(View.GONE);
                                    return;
                                }
                                case 2002: {
                                    mNoTargetDevice.setVisibility(View.GONE);
                                    mRlDlnaSearchingDevice.setVisibility(View.GONE);
                                    mLvDeviceDisplay.setVisibility(View.VISIBLE);
                                    return;
                                }
                            }

                        }
                        case 5: {
                            switch (message.arg1) {
                                default: {
                                    return;
                                }
                                case 3000: {
                                    mPbDlnaConnectLoad.setVisibility(View.VISIBLE);
                                    mIvDlnaConnectedSuccess.setVisibility(View.GONE);
                                    mIvDlnaConnectedFail.setVisibility(View.GONE);
                                    mTvShowDlnaConnectState.setText(R.string.tv_show_dlna_connecting_state);
                                    return;
                                }
                                case 3001: {
                                    mPbDlnaConnectLoad.setVisibility(View.GONE);
                                    mIvDlnaConnectedSuccess.setVisibility(View.VISIBLE);
                                    mIvDlnaConnectedFail.setVisibility(View.GONE);
                                    mTvShowDlnaConnectState.setText(R.string.tv_show_dlna_connected_success_state);
                                    mDlnaPlayer.play();
                                    startUpdateTime();
                                    return;
                                }
                                case 3002: {
                                    mPbDlnaConnectLoad.setVisibility(View.GONE);
                                    mIvDlnaConnectedSuccess.setVisibility(View.GONE);
                                    mIvDlnaConnectedFail.setVisibility(View.VISIBLE);
                                    mTvShowDlnaConnectState.setText(R.string.tv_show_dlna_connected_fail_state);

                                    if (message.arg2 == 0) {
                                        stopUpdateTime();
                                        return;
                                    }
                                    if (message.arg2 == 1) {
                                        stopUpdateTime();
                                        return;
                                    }
                                    if (message.arg2 == 2) {
                                        stopUpdateTime();
                                        return;
                                    }
                                    break Label_0040;
                                }
                            }

                        }
                    }
                }
            }
        };
        this.isEnd = false;
        this.mContext = mContext;
        this.mDevices = new ArrayList<Device>();
        this.mListener = mListener;
        this.initDlnaDialog();
    }

    private void setPlayer(final DLNAPlayer dlnaPlayer) {
        mDlnaPlayer = dlnaPlayer;
    }

    private void setFirstSearch( final boolean firstSearch) {
        mIsFirstSearch = firstSearch;
    }

    private void setCurSeekTime(final int curSeekTimes) {
        mCurSeekTimes = curSeekTimes;
    }

    private void addDevice(final Device device) {
        boolean mFirstAddDevice = false;
        if (device != null && !this.mDevices.contains(device) && DlnaUtil.isSupportAVTransportService(device)) {
            this.mDevices.add(device);
            this.mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onListChange(true);
                    }
                }
            });
            this.mDeviceAdapter.updateDevices(this.mDevices);
            if (this.mDeviceAdapter.getCount() > 0) {
                this.mMainHandler.obtainMessage(4, 2002, 0).sendToTarget();
                if (this.mFirstAddDevice) {
                    if (!this.mFirstAddDevice) {
                        mFirstAddDevice = true;
                    }
                    this.mFirstAddDevice = mFirstAddDevice;

                }
            }
        }
    }

    private void dismissLogoutDialog() {
        if (this.mShowDLNADialog != null) {
            try {
                if (this.mShowDLNADialog.isShowing()) {
                    this.mShowDLNADialog.dismiss();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean firstPlayDlna(final Device controlDevice, final String dataSource) {
        try {
            if (this.mDlnaPlayer.setControlDevice(controlDevice)) {
                this.mDlnaPlayer.setCallback(new IDLNAPlayer.Callback() {
                    @Override
                    public void onCompletion(final DLNAController dlnaController) {
                        if (!isEnd) {
                            stopUpdateTime();
                        }
                    }

                    @Override
                    public void onError(final DLNAController dlnaController, final int n, final int n2) {
                        if (!isEnd) {
                            mMainHandler.obtainMessage(5, 3002, n).sendToTarget();
                        }
                    }

                    @Override
                    public void onInfo(final DLNAController dlnaController, final int n, final int n2) {
                        if (!isEnd && n != 0 && n != 1 && n == 2) {
                            stopUpdateTime();
                        }
                    }

                    @Override
                    public void onPrepared(final DLNAController dlnaController) {
                        if (!isEnd) {
                            mMainHandler.sendMessageDelayed(mMainHandler.obtainMessage(5, 3001, 0), 1000L);
                        }
                    }

                    @Override
                    public void onPreparing(final DLNAController dlnaController) {
                        if (!isEnd) {
                            mMainHandler.obtainMessage(5, 3000, 0).sendToTarget();
                        }
                    }

                    @Override
                    public void onSeekResult(final boolean b) {
                        if (!isEnd) {
                            mMainHandler.obtainMessage(5, 3003, 0).sendToTarget();
                        }
                    }
                });
                this.mDlnaPlayer.setDataSource(dataSource);
                return true;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void initDlnaDialog() {
        final LayoutInflater from = LayoutInflater.from(this.mContext);

        this.dialogView = from.inflate(R.layout.dlna_search_result_list_view, null);
        this.mIvClose = (ImageView)this.dialogView.findViewById(R.id.dlna_dialog_close_btn);
        this.mIvRefresh = (ImageView)this.dialogView.findViewById(R.id.dlna_dialog_refresh_btn);
        this.mLvDeviceDisplay = (ListView)this.dialogView.findViewById(R.id.lv_target_device_list);
        this.mNoTargetDevice = (TextView)this.dialogView.findViewById(R.id.tv_no_target_device_text);
        this.mRlDlnaSearchingDevice = (RelativeLayout)this.dialogView.findViewById(R.id.rl_searching_target_device);
        (this.mPbDlnaSearchLoad = (ProgressBar)this.dialogView.findViewById(R.id.pb_dlna_search_view_circle))
                .startAnimation(AnimationUtils.loadAnimation(this.mContext, R.anim.vod_notify_show));
        this.dialogDeliver = from.inflate(R.layout.dlna_search_result_deliver,null);
        this.mIvDlnaConnectedSuccess = (ImageView)this.dialogDeliver.findViewById(R.id.iv_dlna_connected_success);
        this.mIvDlnaConnectedFail = (ImageView)this.dialogDeliver.findViewById(R.id.iv_dlna_connected_fail);
        (this.mPbDlnaConnectLoad = (ProgressBar)this.dialogDeliver.findViewById(R.id.pb_dlna_connect_view_circle))
                .startAnimation(AnimationUtils.loadAnimation(this.mContext, R.anim.vod_notify_show));
        this.mTvShowDlnaConnectState = (TextView)this.dialogDeliver.findViewById(R.id.tv_show_dlna_connect_state);
        this.mTvDlnaDeviceName = (TextView)this.dialogDeliver.findViewById(R.id.tv_dlna_device_name);
        (this.mTvDlnaResourceName = (TextView)this.dialogDeliver.findViewById(R.id.tv_dlna_resource_name)).setMaxLines(1);
        this.mBtnBackToPhonePlay = (Button)this.dialogDeliver.findViewById(R.id.btn_back_to_phone_play);
        this.mDeviceAdapter = new DeviceAdapter(this.mContext);
        this.mLvDeviceDisplay.setAdapter(mDeviceAdapter);
        this.dismissLogoutDialog();
        (this.mShowDLNADialog = new Dialog(this.mContext)).setOwnerActivity((Activity) this.mContext);
        this.mShowDLNADialog.setContentView(this.dialogView);
        this.mShowDLNADialog.setCanceledOnTouchOutside(false);
        this.mShowDLNADialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(final DialogInterface dialogInterface, final int n, final KeyEvent keyEvent) {
                if (n == 4 && keyEvent.getRepeatCount() == 0) {
                    hideDialog();
                    return false;
                }
                return true;
            }
        });
        final WindowManager.LayoutParams attributes = this.mShowDLNADialog.getWindow().getAttributes();
        attributes.alpha = 0.7f;
        this.mShowDLNADialog.getWindow().setAttributes(attributes);
        this.mShowDLNADialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.mLvDeviceDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
                mShowDLNADialog.setContentView(dialogDeliver);
                stopUpdateTime();
                final Device deviceByIndex = mDeviceAdapter.getDeviceByIndex(n);
                mMainHandler.obtainMessage(5, 3000, 0).sendToTarget();
                mTvDlnaResourceName.setText(mResourceName);
                mTvDlnaDeviceName.setText(DlnaUtil.getDeviceDisplayName(deviceByIndex));
                firstPlayDlna(deviceByIndex,mRefUrl);
            }
        });
       /* this.mShowDLNADialog.setRightBtnListener((DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                if (mShowDLNADialog != null) {
                    mShowDLNADialog.dismiss();
                }
            }
        });*/
       /* this.mShowDLNADialog.setLeftBtnListener((DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                if (mShowDLNADialog != null) {
                    mShowDLNADialog.dismiss();
                }
            }
        });*/
        this.mIvClose.setOnClickListener(this);
        this.mIvRefresh.setOnClickListener(this);
        this.mBtnBackToPhonePlay.setOnClickListener(this);
        this.openDlnaEngineDelay();
    }

    private void openDlnaEngineDelay() {
        this.mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setPlayer(new DLNAPlayer(mContext));
                startEngine();
            }
        }, 100L);
    }

    private void refreshDialogView() {
        this.mDeviceAdapter.clearDevice();
        this.mMainHandler.obtainMessage(4, 2001, 0).sendToTarget();
        if (this.mDevices.size() > 0) {
            this.mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDeviceAdapter.updateDevices(mDevices);
                    mMainHandler.obtainMessage(4, 2002, 0).sendToTarget();
                }
            }, 1000L);
            return;
        }
        if (this.mTimeOutRunnable != null) {
            this.mTimeOutRunnable.stop();
        }
        this.mTimeOutRunnable = new TimeOutRunnable();
        this.mMainHandler.postDelayed((Runnable)this.mTimeOutRunnable, 1000L);
    }

    private void removeDevice(final Device device) {
        if (device != null && this.mDevices.contains(device)) {
            this.mDevices.remove(device);
            this.mMainHandler.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onListChange(false);
                    }
                }
            });
            this.mDeviceAdapter.updateDevices(this.mDevices);
        }
    }

    private void startEngine() {
        Log.d(TAG,"start engine");
        DLNAEngine.getInstance(this.mContext).addRegistryListener(this.mRegistryListener);
        DLNAEngine.getInstance(this.mContext).startEngine();
    }

    private void startUpdateTime() {
        this.mMainHandler.removeMessages(2);
        this.mMainHandler.sendEmptyMessageDelayed(1, 1000L);
    }

    private void stopEngine() {
        DLNAEngine.getInstance(this.mContext).removeRegistryListener(this.mRegistryListener);
        DLNAEngine.getInstance(this.mContext).stopEngine();
    }

    private void stopTimeRunner() {
        if (this.mTimeOutRunnable != null) {
            this.mTimeOutRunnable.stop();
            this.mTimeOutRunnable = null;
        }
    }

    private void stopUpdateTime() {
        this.mMainHandler.removeMessages(1);
        mCurSeekTimes = 0;
        this.mMainHandler.sendEmptyMessage(2);
    }

    public void hideDialog() {
        this.stopTimeRunner();
        if (this.mShowDLNADialog != null && this.mShowDLNADialog.isShowing()) {
            this.mCurPlayPos = 0;
            this.mShowDLNADialog.dismiss();
            if (this.mListener != null) {
                this.mListener.onDialogDismiss(true, this.mPlayCMD);
            }
        }
    }

    public boolean isDialogShowing() {
        return this.mShowDLNADialog != null && this.mShowDLNADialog.isShowing();
    }

    public void onClick(final View view) {
        if (view.getId() == R.id.dlna_dialog_close_btn) {
            this.hideDialog();
        }
        else {
            if (view.getId() == R.id.dlna_dialog_refresh_btn) {
                DLNAEngine.getInstance(this.mContext).search();
                this.refreshDialogView();
                return;
            }
            if (view.getId() == R.id.btn_back_to_phone_play) {
                this.hideDialog();
            }
        }
    }

    public void release() {
        this.isEnd = true;
        this.stopTimeRunner();
        this.stopUpdateTime();
        this.stopEngine();
        this.dismissLogoutDialog();
        this.mDevices.clear();
        if (this.mDlnaPlayer != null) {
            this.mDlnaPlayer.releaseNotStop();
        }
    }

    public void showDialog(final OnDLNADialogListener.MediaPlayerPlayCMD mPlayCMD, final String mResourceName, final String mRefUrl, final int mCurPlayPos) {
        this.mPlayCMD = mPlayCMD;
        this.mResourceName = mResourceName;
        this.mRefUrl = mRefUrl;
        this.mCurPlayPos = mCurPlayPos;
        if (this.mShowDLNADialog != null && !this.mShowDLNADialog.isShowing()) {
            this.mShowDLNADialog.setContentView(this.dialogView);
            this.refreshDialogView();
            this.mShowDLNADialog.show();
        }
    }

    class TimeOutRunnable implements Runnable
    {
        private boolean isStop;
        private int mTimeCount;

        TimeOutRunnable() {
            this.mTimeCount = 0;
            this.isStop = false;
        }

        @Override
        public void run() {
            if (!this.isStop && !isEnd) {
                if (this.mTimeCount++ <= 10) {
                    if (mDevices.size() <= 0) {
                        mMainHandler.postDelayed(this, 1000L);
                        return;
                    }
                    stopTimeRunner();
                }
                else if (mDevices.size() == 0) {
                    mMainHandler.sendEmptyMessage(3);
                }
            }
        }

        public void stop() {
            this.isStop = true;
        }
    }
}

