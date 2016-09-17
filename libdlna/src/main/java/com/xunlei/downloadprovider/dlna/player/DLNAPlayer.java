package com.xunlei.downloadprovider.dlna.player;

/**
 * Created by Stephan on 2016/9/16.
 */

import android.content.*;
import android.os.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.support.model.*;
import com.xunlei.downloadprovider.dlna.core.*;
import com.xunlei.downloadprovider.dlna.util.*;
import android.text.*;


public class DLNAPlayer implements IDLNAPlayer
{
    private static final int MSG_QUERY_INFO_START = 0;
    private static final int MSG_QUERY_INFO_STOP = 1;
    private static final int QUERY_INFO_INTERVAL = 1000;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PREPARING = 1;
    private static final int VIDEO_COMPLETE_MIN_TIME = 10000;
    private Context mContext;
    private DLNAController.ControllerCallback mControllerCallback;
    private int mCurrentState;
    private DLNAController mDlnaController;
    private Handler mMainHandler;
    private Callback mPlayerCallback;
    private PositionInfo mPositionInfo;
    private volatile boolean mRunnable;
    private TransportInfo mTransportInfo;
    private String mUri;

    public DLNAPlayer(final Context mContext) {
        this.mRunnable = false;
        this.mCurrentState = 0;
        this.mMainHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(final Message message) {
                if (message.what == 0) {
                    DLNAPlayer.this.mDlnaController.actionGetPositionInfo();
                    DLNAPlayer.this.mDlnaController.actionGetTransportInfo();
                    this.sendEmptyMessageDelayed(0, 1000L);
                }
            }
        };
        this.mControllerCallback = new DLNAController.ControllerCallback() {
            @Override
            public void onControllerDisconnected(final Device device) {
                DLNAPlayer.access$9(DLNAPlayer.this, false);
                if (DLNAPlayer.this.mPlayerCallback != null) {
                    DLNAPlayer.this.mPlayerCallback.onError(DLNAPlayer.this.mDlnaController, 0, -1);
                }
            }

            @Override
            public void onGetCurrentTransportActionsResult(final boolean b, final TransportAction[] array) {
            }

            @Override
            public void onGetDeviceCapabilitiesResult(final boolean b, final DeviceCapabilities deviceCapabilities) {
            }

            @Override
            public void onGetMediaInfoResult(final boolean b, final MediaInfo mediaInfo) {
            }

            @Override
            public void onGetPositionInfoResult(final boolean b, final PositionInfo positionInfo) {
                if (b) {
                    DLNAPlayer.access$8(DLNAPlayer.this, positionInfo);
                }
            }

            @Override
            public void onGetTransportInfoResult(final boolean b, final TransportInfo transportInfo) {
                if (b) {
                    DLNAPlayer.access$5(DLNAPlayer.this, transportInfo);
                    if (DLNAPlayer.this.mTransportInfo != null) {
                        final TransportState currentTransportState = DLNAPlayer.this.mTransportInfo.getCurrentTransportState();
                        if (currentTransportState != null && currentTransportState != TransportState.CUSTOM && currentTransportState != TransportState.TRANSITIONING) {
                            if (currentTransportState == TransportState.PLAYING) {
                                if (DLNAPlayer.this.mCurrentState != 0) {
                                    DLNAPlayer.this.updateState(3);
                                    if (DLNAPlayer.this.mPlayerCallback != null) {
                                        DLNAPlayer.this.mPlayerCallback.onInfo(DLNAPlayer.this.mDlnaController, 0, -1);
                                    }
                                }
                            }
                            else if (currentTransportState == TransportState.PAUSED_PLAYBACK) {
                                if (DLNAPlayer.this.mCurrentState != 0) {
                                    DLNAPlayer.this.updateState(4);
                                    if (DLNAPlayer.this.mPlayerCallback != null) {
                                        DLNAPlayer.this.mPlayerCallback.onInfo(DLNAPlayer.this.mDlnaController, 1, -1);
                                    }
                                }
                            }
                            else if (currentTransportState == TransportState.STOPPED && DLNAPlayer.this.mCurrentState != 0 && DLNAPlayer.this.mCurrentState != 2) {
                                if (DLNAPlayer.this.mCurrentState == 3) {
                                    final int currentPosition = DLNAPlayer.this.getCurrentPosition();
                                    final int duration = DLNAPlayer.this.getDuration();
                                    if (currentPosition > 0 && duration > 0 && Math.abs(duration - currentPosition) < 10000) {
                                        DLNAPlayer.this.stopQueryInfo();
                                        DLNAPlayer.this.updateState(0);
                                        if (DLNAPlayer.this.mPlayerCallback != null) {
                                            DLNAPlayer.this.mPlayerCallback.onCompletion(DLNAPlayer.this.mDlnaController);
                                        }
                                    }
                                    else {
                                        DLNAPlayer.this.stopQueryInfo();
                                        DLNAPlayer.this.updateState(0);
                                        if (DLNAPlayer.this.mPlayerCallback != null) {
                                            DLNAPlayer.this.mPlayerCallback.onError(DLNAPlayer.this.mDlnaController, 2, -1);
                                        }
                                    }
                                }
                                else {
                                    DLNAPlayer.this.stopQueryInfo();
                                    DLNAPlayer.this.updateState(0);
                                    if (DLNAPlayer.this.mPlayerCallback != null) {
                                        DLNAPlayer.this.mPlayerCallback.onError(DLNAPlayer.this.mDlnaController, 2, -1);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onGetVolumeDBRangeResult(final boolean b, final int n, final int n2) {
            }

            @Override
            public void onGetVolumeResult(final boolean b, final int n) {
            }

            @Override
            public void onPauseResult(final boolean b) {
                if (b && DLNAPlayer.this.mPlayerCallback != null) {
                    DLNAPlayer.this.mPlayerCallback.onInfo(DLNAPlayer.this.mDlnaController, 1, -1);
                }
            }

            @Override
            public void onPlayResult(final boolean b) {
                if (b && DLNAPlayer.this.mPlayerCallback != null) {
                    DLNAPlayer.this.mPlayerCallback.onInfo(DLNAPlayer.this.mDlnaController, 0, -1);
                }
            }

            @Override
            public void onSeekResult(final boolean b) {
                if (DLNAPlayer.this.mPlayerCallback != null) {
                    DLNAPlayer.this.mPlayerCallback.onSeekResult(b);
                }
            }

            @Override
            public void onSetAVTransportURIResult(final boolean b) {
                DLNAPlayer.this.stopQueryInfo();
                if (b) {
                    DLNAPlayer.this.updateState(2);
                    if (DLNAPlayer.this.mPlayerCallback != null) {
                        DLNAPlayer.this.mPlayerCallback.onPrepared(DLNAPlayer.this.mDlnaController);
                    }
                    DLNAPlayer.this.startQueryInfo();
                }
                else {
                    DLNAPlayer.this.updateState(0);
                    if (DLNAPlayer.this.mPlayerCallback != null) {
                        DLNAPlayer.this.mPlayerCallback.onError(DLNAPlayer.this.mDlnaController, 1, -1);
                    }
                }
            }

            @Override
            public void onSetVolumeResult(final boolean b) {
            }

            @Override
            public void onStopResult(final boolean b) {
                if (b) {
                    DLNAPlayer.this.stopQueryInfo();
                    DLNAPlayer.this.updateState(0);
                    if (DLNAPlayer.this.mPlayerCallback != null) {
                        DLNAPlayer.this.mPlayerCallback.onInfo(DLNAPlayer.this.mDlnaController, 2, -1);
                    }
                }
            }
        };
        this.mContext = mContext;
    }

    static /* synthetic */ void access$5(final DLNAPlayer dlnaPlayer, final TransportInfo mTransportInfo) {
        dlnaPlayer.mTransportInfo = mTransportInfo;
    }

    static /* synthetic */ void access$8(final DLNAPlayer dlnaPlayer, final PositionInfo mPositionInfo) {
        dlnaPlayer.mPositionInfo = mPositionInfo;
    }

    static /* synthetic */ void access$9(final DLNAPlayer dlnaPlayer, final boolean mRunnable) {
        dlnaPlayer.mRunnable = mRunnable;
    }

    private void startQueryInfo() {
        if (this.isRunnable() && this.mCurrentState != 0) {
            this.mMainHandler.removeMessages(1);
            this.mMainHandler.sendEmptyMessage(0);
        }
    }

    private void stopQueryInfo() {
        this.mMainHandler.removeMessages(0);
        this.mMainHandler.sendEmptyMessage(1);
    }

    private void updateState(final int mCurrentState) {
        this.mCurrentState = mCurrentState;
    }

    @Override
    public int getCurrentPosition() {
        int n = 0;
        if (this.isRunnable() && this.mCurrentState != 0) {
            int n2 = 0;
            if (this.mPositionInfo != null) {
                n2 = (int)(this.mPositionInfo.getTrackElapsedSeconds() * 1000L);
            }
            if ((n = n2) < 0) {
                return 0;
            }
        }
        return n;
    }

    @Override
    public int getDuration() {
        int n = 0;
        if (this.isRunnable() && this.mCurrentState != 0) {
            int n2 = 0;
            if (this.mPositionInfo != null) {
                n2 = (int)(this.mPositionInfo.getTrackDurationSeconds() * 1000L);
            }
            if ((n = n2) < 0) {
                return 0;
            }
        }
        return n;
    }

    @Override
    public boolean isPlaying() {
        return this.mCurrentState == 3;
    }

    public boolean isRunnable() {
        return this.mRunnable;
    }

    @Override
    public boolean pause() {
        return this.isRunnable() && this.mCurrentState != 0 && this.mDlnaController.actionPause();
    }

    @Override
    public boolean play() {
        return this.isRunnable() && this.mCurrentState != 0 && this.mDlnaController.actionPlay();
    }

    @Override
    public void release() {
        this.stopQueryInfo();
        this.stop();
        this.mRunnable = false;
        this.updateState(0);
        if (this.mDlnaController != null) {
            this.mDlnaController.release();
        }
    }

    public void releaseNotStop() {
        this.stopQueryInfo();
        this.mRunnable = false;
        this.updateState(0);
        if (this.mDlnaController != null) {
            this.mDlnaController.release();
        }
    }

    @Override
    public boolean seekTo(final int n) {
        return this.isRunnable() && this.mCurrentState != 0 && this.mDlnaController.actionSeek(n);
    }

    public void setCallback(final Callback mPlayerCallback) {
        this.mPlayerCallback = mPlayerCallback;
    }

    public boolean setControlDevice(final Device device) throws IllegalArgumentException, NullPointerException {
        if (device == null) {
            throw new NullPointerException("DLNAPlayer ControlDevice is null");
        }
        if (DLNAEngine.getInstance(this.mContext).getController() == null) {
            throw new NullPointerException("DLNAPlayer DlnaController is null");
        }
        this.mDlnaController = DLNAEngine.getInstance(this.mContext).getController();
        this.mPositionInfo = null;
        return this.mRunnable = this.mDlnaController.controlDevice(device, this.mControllerCallback);
    }

    @Override
    public void setDataSource(String dlnaUri) throws IllegalStateException, IllegalArgumentException {
        if (!this.isRunnable()) {
            throw new IllegalStateException("DlnaPlayer is not runnable");
        }
        dlnaUri = DlnaUtil.getDlnaUri(this.mContext, dlnaUri);
        if (TextUtils.isEmpty((CharSequence)dlnaUri)) {
            throw new IllegalArgumentException("DlnaPlayer uri is not valid");
        }
        this.mUri = dlnaUri;
        if (!this.mDlnaController.actionSetAVTransportURI(this.mUri)) {
            throw new IllegalArgumentException("DlnaPlayer setUri is not valid");
        }
        this.updateState(1);
        if (this.mPlayerCallback != null) {
            this.mPlayerCallback.onPreparing(this.mDlnaController);
        }
    }

    @Override
    public boolean stop() {
        return this.isRunnable() && this.mCurrentState != 0 && this.mDlnaController.actionStop();
    }
}
