package com.xunlei.downloadprovider.dlna.core;

/**
 * Created by Stephan on 2016/9/16.
 */

import org.fourthline.cling.android.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.controlpoint.*;
import android.os.*;
import org.fourthline.cling.support.avtransport.callback.*;
import com.xunlei.downloadprovider.dlna.action.*;
import org.fourthline.cling.support.renderingcontrol.callback.*;
import org.fourthline.cling.model.action.*;
import org.fourthline.cling.model.message.*;
import org.fourthline.cling.support.model.*;
import com.xunlei.downloadprovider.dlna.util.*;
import android.text.*;
import org.fourthline.cling.model.types.*;
import java.util.*;


public class DLNAController
{
    private Service mAVTransportService;
    private AndroidUpnpService mAndroidUpnpService;
    private ControllerCallback mControllerCallback;
    private ActionCallbackFlag mCurrentActionLevelFlag0;
    private ActionCallbackFlag mCurrentActionLevelFlag1;
    private ActionCallbackFlag mCurrentActionLevelFlag2;
    private ActionCallbackFlag mCurrentActionLevelFlag3;
    private ActionCallbackFlag mCurrentActionLevelFlag4;
    private Device mCurrentDevice;
    private Handler mMainHandler;
    private Queue<ActionCallback> mPendingActionsLevel0;
    private Queue<ActionCallback> mPendingActionsLevel1;
    private Queue<ActionCallback> mPendingActionsLevel2;
    private Queue<ActionCallback> mPendingActionsLevel3;
    private Queue<ActionCallback> mPendingActionsLevel4;
    private Service mRenderingControlService;
    private volatile boolean mRunnable;

    public DLNAController(final AndroidUpnpService mAndroidUpnpService) {
        boolean mRunnable = false;
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mRunnable = false;
        this.mPendingActionsLevel0 = new LinkedList<ActionCallback>();
        this.mPendingActionsLevel1 = new LinkedList<ActionCallback>();
        this.mPendingActionsLevel2 = new LinkedList<ActionCallback>();
        this.mPendingActionsLevel3 = new LinkedList<ActionCallback>();
        this.mPendingActionsLevel4 = new LinkedList<ActionCallback>();
        this.mAndroidUpnpService = mAndroidUpnpService;
        if (this.mAndroidUpnpService != null) {
            mRunnable = true;
        }
        this.mRunnable = mRunnable;
    }

    private void completeActionCallback(final ActionCallbackFlag actionCallbackFlag) {
        if (this.isRunnable() && this.mAndroidUpnpService != null && actionCallbackFlag != null) {
            if (actionCallbackFlag != ActionCallbackFlag.GetTransportInfo) {
                final ActionCallbackFlag getPositionInfo = ActionCallbackFlag.GetPositionInfo;
            }
            if (actionCallbackFlag == ActionCallbackFlag.SetAVTransportURI || actionCallbackFlag == ActionCallbackFlag.Stop) {
                if (this.mCurrentActionLevelFlag0 != null && this.mCurrentActionLevelFlag0 == actionCallbackFlag) {
                    this.mCurrentActionLevelFlag0 = null;
                    this.executeActionCallback(this.mPendingActionsLevel0.poll());
                }
            }
            else if (actionCallbackFlag == ActionCallbackFlag.Play || actionCallbackFlag == ActionCallbackFlag.Pause) {
                if (this.mCurrentActionLevelFlag0 == null && this.mPendingActionsLevel0.isEmpty() && this.mCurrentActionLevelFlag1 != null && this.mCurrentActionLevelFlag1 == actionCallbackFlag) {
                    this.mCurrentActionLevelFlag1 = null;
                    this.executeActionCallback(this.mPendingActionsLevel1.poll());
                }
            }
            else if (actionCallbackFlag == ActionCallbackFlag.Seek) {
                if (this.mCurrentActionLevelFlag0 == null && this.mPendingActionsLevel0.isEmpty() && this.mCurrentActionLevelFlag2 != null && this.mCurrentActionLevelFlag2 == actionCallbackFlag) {
                    this.mCurrentActionLevelFlag2 = null;
                    this.executeActionCallback(this.mPendingActionsLevel2.poll());
                }
            }
            else if (actionCallbackFlag == ActionCallbackFlag.GetPositionInfo) {
                if (this.mCurrentActionLevelFlag0 == null && this.mPendingActionsLevel0.isEmpty() && this.mCurrentActionLevelFlag3 != null && this.mCurrentActionLevelFlag3 == actionCallbackFlag) {
                    this.mCurrentActionLevelFlag3 = null;
                    this.executeActionCallback(this.mPendingActionsLevel3.poll());
                }
            }
            else if (actionCallbackFlag == ActionCallbackFlag.GetTransportInfo && this.mCurrentActionLevelFlag0 == null && this.mPendingActionsLevel0.isEmpty() && this.mCurrentActionLevelFlag4 != null && this.mCurrentActionLevelFlag4 == actionCallbackFlag) {
                this.mCurrentActionLevelFlag4 = null;
                this.executeActionCallback(this.mPendingActionsLevel4.poll());
            }
        }
    }

    private ActionCallbackFlag convertActionCallBack2Flag(final ActionCallback actionCallback) {
        ActionCallbackFlag setAVTransportURI = null;
        if (actionCallback != null) {
            if (actionCallback instanceof SetAVTransportURI) {
                setAVTransportURI = ActionCallbackFlag.SetAVTransportURI;
            }
            else {
                if (actionCallback instanceof Play) {
                    return ActionCallbackFlag.Play;
                }
                if (actionCallback instanceof Stop) {
                    return ActionCallbackFlag.Stop;
                }
                if (actionCallback instanceof Pause) {
                    return ActionCallbackFlag.Pause;
                }
                if (actionCallback instanceof Seek) {
                    return ActionCallbackFlag.Seek;
                }
                if (actionCallback instanceof GetPositionInfo) {
                    return ActionCallbackFlag.GetPositionInfo;
                }
                if (actionCallback instanceof GetMediaInfo) {
                    return ActionCallbackFlag.GetMediaInfo;
                }
                if (actionCallback instanceof GetTransportInfo) {
                    return ActionCallbackFlag.GetTransportInfo;
                }
                if (actionCallback instanceof GetCurrentTransportActions) {
                    return ActionCallbackFlag.GetCurrentTransportActions;
                }
                if (actionCallback instanceof GetDeviceCapabilities) {
                    return ActionCallbackFlag.GetDeviceCapabilities;
                }
                if (actionCallback instanceof GetVolumeDBRange) {
                    return ActionCallbackFlag.GetVolumeDBRange;
                }
                if (actionCallback instanceof GetVolume) {
                    return ActionCallbackFlag.GetVolume;
                }
                setAVTransportURI = setAVTransportURI;
                if (actionCallback instanceof SetVolume) {
                    return ActionCallbackFlag.SetVolume;
                }
            }
        }
        return setAVTransportURI;
    }

    private void executeActionCallback(final ActionCallback actionCallback) {
        if (this.isRunnable() && this.mAndroidUpnpService != null && actionCallback != null) {
            final ActionCallbackFlag convertActionCallBack2Flag = this.convertActionCallBack2Flag(actionCallback);
            if (convertActionCallBack2Flag != null) {
                if (convertActionCallBack2Flag != ActionCallbackFlag.GetTransportInfo) {
                    final ActionCallbackFlag getPositionInfo = ActionCallbackFlag.GetPositionInfo;
                }
                if (convertActionCallBack2Flag == ActionCallbackFlag.SetAVTransportURI || convertActionCallBack2Flag == ActionCallbackFlag.Stop) {
                    if (this.mCurrentActionLevelFlag0 == null) {
                        this.mCurrentActionLevelFlag0 = convertActionCallBack2Flag;
                        this.performActionCallback(actionCallback);
                        return;
                    }
                    if (this.mCurrentActionLevelFlag0 == convertActionCallBack2Flag) {
                        this.mPendingActionsLevel0.clear();
                        return;
                    }
                    this.mPendingActionsLevel0.clear();
                    this.mPendingActionsLevel0.add(actionCallback);
                }
                else if (convertActionCallBack2Flag == ActionCallbackFlag.Play || convertActionCallBack2Flag == ActionCallbackFlag.Pause) {
                    if (this.mCurrentActionLevelFlag0 == null && this.mPendingActionsLevel0.isEmpty()) {
                        if (this.mCurrentActionLevelFlag1 == null) {
                            this.mCurrentActionLevelFlag1 = convertActionCallBack2Flag;
                            this.performActionCallback(actionCallback);
                            return;
                        }
                        if (this.mCurrentActionLevelFlag1 == convertActionCallBack2Flag) {
                            this.mPendingActionsLevel1.clear();
                            return;
                        }
                        this.mPendingActionsLevel1.clear();
                        this.mPendingActionsLevel1.add(actionCallback);
                    }
                }
                else if (convertActionCallBack2Flag == ActionCallbackFlag.Seek) {
                    if (this.mCurrentActionLevelFlag0 == null && this.mPendingActionsLevel0.isEmpty()) {
                        if (this.mCurrentActionLevelFlag2 == null) {
                            this.mCurrentActionLevelFlag2 = convertActionCallBack2Flag;
                            this.performActionCallback(actionCallback);
                            return;
                        }
                        this.mPendingActionsLevel2.clear();
                        this.mPendingActionsLevel2.add(actionCallback);
                    }
                }
                else if (convertActionCallBack2Flag == ActionCallbackFlag.GetPositionInfo) {
                    if (this.mCurrentActionLevelFlag0 == null && this.mPendingActionsLevel0.isEmpty()) {
                        if (this.mCurrentActionLevelFlag3 == null) {
                            this.mCurrentActionLevelFlag3 = convertActionCallBack2Flag;
                            this.performActionCallback(actionCallback);
                            return;
                        }
                        this.mPendingActionsLevel3.clear();
                        this.mPendingActionsLevel3.add(actionCallback);
                    }
                }
                else if (convertActionCallBack2Flag == ActionCallbackFlag.GetTransportInfo && this.mCurrentActionLevelFlag0 == null && this.mPendingActionsLevel0.isEmpty()) {
                    if (this.mCurrentActionLevelFlag4 == null) {
                        this.mCurrentActionLevelFlag4 = convertActionCallBack2Flag;
                        this.performActionCallback(actionCallback);
                        return;
                    }
                    this.mPendingActionsLevel4.clear();
                    this.mPendingActionsLevel4.add(actionCallback);
                }
            }
        }
    }

    private void performActionCallback(final ActionCallback actionCallback) {
        if (this.mAndroidUpnpService != null && actionCallback != null) {
            this.mAndroidUpnpService.getControlPoint().execute(actionCallback);
        }
    }

    public boolean actionGetCurrentTransportActions() {
        if (!this.isRunnable() && !this.isSupportAVTransportService()) {
            return false;
        }
        this.executeActionCallback(new GetCurrentTransportActions(this.mAVTransportService) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetCurrentTransportActions);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetCurrentTransportActionsResult(false, null);
                            }
                        }
                    }
                });
            }

            @Override
            public void received(final ActionInvocation actionInvocation, final TransportAction[] array) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetCurrentTransportActions);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetCurrentTransportActionsResult(true, array);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionGetDeviceCapabilities() {
        if (!this.isRunnable() && !this.isSupportAVTransportService()) {
            return false;
        }
        this.executeActionCallback(new GetDeviceCapabilities(this.mAVTransportService) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetDeviceCapabilities);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetDeviceCapabilitiesResult(false, null);
                            }
                        }
                    }
                });
            }

            @Override
            public void received(final ActionInvocation actionInvocation, final DeviceCapabilities deviceCapabilities) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetDeviceCapabilities);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetDeviceCapabilitiesResult(true, deviceCapabilities);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionGetMediaInfo() {
        if (!this.isRunnable() && !this.isSupportAVTransportService()) {
            return false;
        }
        this.executeActionCallback(new GetMediaInfo(this.mAVTransportService) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetMediaInfo);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetMediaInfoResult(false, null);
                            }
                        }
                    }
                });
            }

            @Override
            public void received(final ActionInvocation actionInvocation, final MediaInfo mediaInfo) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetMediaInfo);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetMediaInfoResult(true, mediaInfo);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionGetPositionInfo() {
        if (!this.isRunnable() && !this.isSupportAVTransportService()) {
            return false;
        }
        this.executeActionCallback(new GetPositionInfo(this.mAVTransportService) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetPositionInfo);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetPositionInfoResult(false, null);
                            }
                        }
                    }
                });
            }

            @Override
            public void received(final ActionInvocation actionInvocation, final PositionInfo positionInfo) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetPositionInfo);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetPositionInfoResult(true, positionInfo);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionGetTransportInfo() {
        if (!this.isRunnable() && !this.isSupportAVTransportService()) {
            return false;
        }
        this.executeActionCallback(new GetTransportInfo(this.mAVTransportService) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetTransportInfo);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetTransportInfoResult(false, null);
                            }
                        }
                    }
                });
            }

            @Override
            public void received(final ActionInvocation actionInvocation, final TransportInfo transportInfo) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetTransportInfo);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetTransportInfoResult(true, transportInfo);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionGetVolume() {
        if (!this.isRunnable() && !this.isSupportRenderingControlService()) {
            return false;
        }
        this.executeActionCallback(new GetVolume(this.mRenderingControlService) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetVolume);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetVolumeResult(false, -1);
                            }
                        }
                    }
                });
            }

            @Override
            public void received(final ActionInvocation actionInvocation, final int n) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetVolume);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetVolumeResult(true, n);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionGetVolumeDBRange() {
        if (!this.isRunnable() && !this.isSupportRenderingControlService()) {
            return false;
        }
        this.executeActionCallback(new GetVolumeDBRange(this.mRenderingControlService) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetVolumeDBRange);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetVolumeDBRangeResult(false, -1, -1);
                            }
                        }
                    }
                });
            }

            @Override
            public void received(final ActionInvocation actionInvocation, final int n, final int n2) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.GetVolumeDBRange);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onGetVolumeDBRangeResult(true, n, n2);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionPause() {
        if (!this.isRunnable() && !this.isSupportAVTransportService()) {
            return false;
        }
        this.executeActionCallback(new Pause(this.mAVTransportService) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.Pause);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onPauseResult(false);
                            }
                        }
                    }
                });
            }

            @Override
            public void success(final ActionInvocation actionInvocation) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.Pause);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onPauseResult(true);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionPlay() {
        if (!this.isRunnable() && !this.isSupportAVTransportService()) {
            return false;
        }
        this.executeActionCallback(new Play(this.mAVTransportService) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.Play);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onPlayResult(false);
                            }
                        }
                    }
                });
            }

            @Override
            public void success(final ActionInvocation actionInvocation) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.Play);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onPlayResult(true);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionSeek(final int n) {
        if (!this.isRunnable() && !this.isSupportAVTransportService()) {
            return false;
        }
        this.executeActionCallback(new Seek(this.mAVTransportService, DlnaUtil.convertRelativeTimeTarget(n)) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.Seek);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onSeekResult(false);
                            }
                        }
                    }
                });
            }

            @Override
            public void success(final ActionInvocation actionInvocation) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.Seek);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onSeekResult(true);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionSetAVTransportURI(final String s) {
        if ((!this.isRunnable() && !this.isSupportAVTransportService()) || TextUtils.isEmpty((CharSequence)s)) {
            return false;
        }
        this.executeActionCallback(new SetAVTransportURI(this.mAVTransportService, s) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.SetAVTransportURI);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onSetAVTransportURIResult(false);
                            }
                        }
                    }
                });
            }

            @Override
            public void success(final ActionInvocation actionInvocation) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.SetAVTransportURI);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onSetAVTransportURIResult(true);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionSetVolume(final int n) {
        if ((!this.isRunnable() && !this.isSupportRenderingControlService()) || n < 0) {
            return false;
        }
        this.executeActionCallback(new SetVolume(this.mRenderingControlService, (long)n) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.SetVolume);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onSetVolumeResult(false);
                            }
                        }
                    }
                });
            }

            @Override
            public void success(final ActionInvocation actionInvocation) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.SetVolume);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onSetVolumeResult(true);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean actionStop() {
        if (!this.isRunnable() && !this.isSupportAVTransportService()) {
            return false;
        }
        this.executeActionCallback(new Stop(this.mAVTransportService) {
            @Override
            public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.Stop);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onStopResult(false);
                            }
                        }
                    }
                });
            }

            @Override
            public void success(final ActionInvocation actionInvocation) {
                DLNAController.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (DLNAController.this.isRunnable()) {
                            DLNAController.this.completeActionCallback(ActionCallbackFlag.Stop);
                            if (DLNAController.this.mControllerCallback != null) {
                                DLNAController.this.mControllerCallback.onStopResult(true);
                            }
                        }
                    }
                });
            }
        });
        return true;
    }

    public boolean controlDevice(final Device mCurrentDevice, final ControllerCallback mControllerCallback) {
        if (this.mAndroidUpnpService != null && mCurrentDevice != null) {
            this.mCurrentDevice = mCurrentDevice;
            this.mAVTransportService = mCurrentDevice.findService(new UDAServiceType("AVTransport"));
            this.mRenderingControlService = mCurrentDevice.findService(new UDAServiceType("RenderingControl"));
            this.mControllerCallback = mControllerCallback;
            if (this.mAVTransportService != null) {
                return this.mRunnable = true;
            }
            this.mCurrentDevice = null;
            this.mAVTransportService = null;
            this.mRenderingControlService = null;
            this.mControllerCallback = null;
            this.mRunnable = false;
        }
        if (this.mAndroidUpnpService != null) {}
        return false;
    }

    public Device getCurrentDevice() {
        return this.mCurrentDevice;
    }

    public boolean isRunnable() {
        return this.mRunnable;
    }

    public boolean isSupportAVTransportService() {
        return this.mAVTransportService != null;
    }

    public boolean isSupportRenderingControlService() {
        return this.mRenderingControlService != null;
    }

    public void onLocalDevicesChanged(final List<Device> list) {
    }

    public void onRemoteDevicesChanged(final List<Device> list) {
        if (list != null && this.mCurrentDevice != null && !list.contains(this.mCurrentDevice)) {
            this.release();
        }
    }

    public void release() {
        if (!this.mRunnable) {
            return;
        }
        this.mRunnable = false;
        if (this.mControllerCallback != null) {
            this.mControllerCallback.onControllerDisconnected(this.mCurrentDevice);
        }
        this.mCurrentDevice = null;
        this.mAVTransportService = null;
        this.mRenderingControlService = null;
        this.mCurrentActionLevelFlag0 = null;
        this.mCurrentActionLevelFlag1 = null;
        this.mCurrentActionLevelFlag2 = null;
        this.mCurrentActionLevelFlag3 = null;
        this.mCurrentActionLevelFlag4 = null;
        this.mPendingActionsLevel0.clear();
        this.mPendingActionsLevel1.clear();
        this.mPendingActionsLevel2.clear();
        this.mPendingActionsLevel3.clear();
        this.mPendingActionsLevel4.clear();
    }

    private enum ActionCallbackFlag
    {
        GetCurrentTransportActions("GetCurrentTransportActions", 8),
        GetDeviceCapabilities("GetDeviceCapabilities", 9),
        GetMediaInfo("GetMediaInfo", 6),
        GetPositionInfo("GetPositionInfo", 5),
        GetTransportInfo("GetTransportInfo", 7),
        GetVolume("GetVolume", 11),
        GetVolumeDBRange("GetVolumeDBRange", 10),
        Pause("Pause", 3),
        Play("Play", 1),
        Seek("Seek", 4),
        SetAVTransportURI("SetAVTransportURI", 0),
        SetVolume("SetVolume", 12),
        Stop("Stop", 2);

        private ActionCallbackFlag(final String s, final int n) {
        }
    }

    public interface ControllerCallback
    {
        void onControllerDisconnected(final Device p0);

        void onGetCurrentTransportActionsResult(final boolean p0, final TransportAction[] p1);

        void onGetDeviceCapabilitiesResult(final boolean p0, final DeviceCapabilities p1);

        void onGetMediaInfoResult(final boolean p0, final MediaInfo p1);

        void onGetPositionInfoResult(final boolean p0, final PositionInfo p1);

        void onGetTransportInfoResult(final boolean p0, final TransportInfo p1);

        void onGetVolumeDBRangeResult(final boolean p0, final int p1, final int p2);

        void onGetVolumeResult(final boolean p0, final int p1);

        void onPauseResult(final boolean p0);

        void onPlayResult(final boolean p0);

        void onSeekResult(final boolean p0);

        void onSetAVTransportURIResult(final boolean p0);

        void onSetVolumeResult(final boolean p0);

        void onStopResult(final boolean p0);
    }
}

