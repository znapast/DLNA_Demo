package com.xunlei.downloadprovider.dlna.core;

/**
 * Created by Stephan on 2016/9/16.
 */

import org.fourthline.cling.android.*;
import android.os.*;
import org.fourthline.cling.registry.*;
import org.fourthline.cling.model.meta.*;
import java.util.*;
import java.io.*;
//import com.xunlei.downloadprovider.dlnaplugin.*;
import android.content.*;


public class DLNAEngine
{
    private static DLNAEngine mInstance;
    private AndroidUpnpService mAndroidUpnpService;
    private Context mContext;
    private DLNAController mDlnaController;
    private boolean mEngineStarted;
    private List<Device> mLocalDevices;
    private Handler mMainHandler;
    private final RegistryListener mRegistryListenerInner;
    private HashSet<RegistryListener> mRegistryListenerSet;
    private List<Device> mRemoteDevices;
    private final ServiceConnection mServiceConnection;

    static {
        DLNAEngine.mInstance = null;
    }

    private DLNAEngine(final Context mContext) {
        this.mEngineStarted = false;
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mServiceConnection = (ServiceConnection)new ServiceConnection() {
            public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
                if (binder instanceof AndroidUpnpService) {
                    DLNAEngine.access$0(DLNAEngine.this, (AndroidUpnpService)binder);
                    DLNAEngine.this.mAndroidUpnpService.getRegistry().addListener(DLNAEngine.this.mRegistryListenerInner);
                    DLNAEngine.this.search();
                    DLNAEngine.access$3(DLNAEngine.this, new DLNAController(DLNAEngine.this.mAndroidUpnpService));
                }
            }

            public void onServiceDisconnected(final ComponentName componentName) {
                DLNAEngine.this.release();
            }
        };
        this.mRegistryListenerInner = new RegistryListener() {
            @Override
            public void afterShutdown() {
                DLNAEngine.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final Iterator<RegistryListener> iterator = DLNAEngine.this.mRegistryListenerSet.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().afterShutdown();
                        }
                    }
                });
            }

            @Override
            public void beforeShutdown(final Registry registry) {
                DLNAEngine.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final Iterator<RegistryListener> iterator = DLNAEngine.this.mRegistryListenerSet.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().beforeShutdown(registry);
                        }
                    }
                });
            }

            @Override
            public void localDeviceAdded(final Registry registry, final LocalDevice localDevice) {
                DLNAEngine.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final Iterator<RegistryListener> iterator = DLNAEngine.this.mRegistryListenerSet.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().localDeviceAdded(registry, localDevice);
                        }
                        if (!DLNAEngine.this.mLocalDevices.contains(localDevice)) {
                            DLNAEngine.this.mLocalDevices.add(localDevice);
                            if (DLNAEngine.this.mDlnaController != null) {
                                DLNAEngine.this.mDlnaController.onLocalDevicesChanged(DLNAEngine.this.mRemoteDevices);
                            }
                        }
                    }
                });
            }

            @Override
            public void localDeviceRemoved(final Registry registry, final LocalDevice localDevice) {
                DLNAEngine.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final Iterator<RegistryListener> iterator = DLNAEngine.this.mRegistryListenerSet.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().localDeviceRemoved(registry, localDevice);
                        }
                        if (DLNAEngine.this.mLocalDevices.contains(localDevice)) {
                            DLNAEngine.this.mLocalDevices.remove(localDevice);
                            if (DLNAEngine.this.mDlnaController != null) {
                                DLNAEngine.this.mDlnaController.onLocalDevicesChanged(DLNAEngine.this.mRemoteDevices);
                            }
                        }
                    }
                });
            }

            @Override
            public void remoteDeviceAdded(final Registry registry, final RemoteDevice remoteDevice) {
                DLNAEngine.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final Iterator<RegistryListener> iterator = DLNAEngine.this.mRegistryListenerSet.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().remoteDeviceAdded(registry, remoteDevice);
                        }
                        if (!DLNAEngine.this.mRemoteDevices.contains(remoteDevice)) {
                            DLNAEngine.this.mRemoteDevices.add(remoteDevice);
                            if (DLNAEngine.this.mDlnaController != null) {
                                DLNAEngine.this.mDlnaController.onRemoteDevicesChanged(DLNAEngine.this.mRemoteDevices);
                            }
                        }
                    }
                });
            }

            @Override
            public void remoteDeviceDiscoveryFailed(final Registry registry, final RemoteDevice remoteDevice, final Exception ex) {
                DLNAEngine.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final Iterator<RegistryListener> iterator = DLNAEngine.this.mRegistryListenerSet.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().remoteDeviceDiscoveryFailed(registry, remoteDevice, ex);
                        }
                    }
                });
            }

            @Override
            public void remoteDeviceDiscoveryStarted(final Registry registry, final RemoteDevice remoteDevice) {
                try {
                    DLNAEngine.this.mMainHandler.post((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            final Iterator<RegistryListener> iterator = DLNAEngine.this.mRegistryListenerSet.iterator();
                            while (iterator.hasNext()) {
                                iterator.next().remoteDeviceDiscoveryStarted(registry, remoteDevice);
                            }
                        }
                    });
                }
                catch (Exception ex) {}
            }

            @Override
            public void remoteDeviceRemoved(final Registry registry, final RemoteDevice remoteDevice) {
                DLNAEngine.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final Iterator<RegistryListener> iterator = DLNAEngine.this.mRegistryListenerSet.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().remoteDeviceRemoved(registry, remoteDevice);
                        }
                        if (DLNAEngine.this.mRemoteDevices.contains(remoteDevice)) {
                            DLNAEngine.this.mRemoteDevices.remove(remoteDevice);
                            if (DLNAEngine.this.mDlnaController != null) {
                                DLNAEngine.this.mDlnaController.onRemoteDevicesChanged(DLNAEngine.this.mRemoteDevices);
                            }
                        }
                    }
                });
            }

            @Override
            public void remoteDeviceUpdated(final Registry registry, final RemoteDevice remoteDevice) {
                DLNAEngine.this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final Iterator<RegistryListener> iterator = DLNAEngine.this.mRegistryListenerSet.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().remoteDeviceUpdated(registry, remoteDevice);
                        }
                    }
                });
            }
        };
        this.mContext = mContext;
        this.mRegistryListenerSet = new HashSet<RegistryListener>();
        this.mLocalDevices = new ArrayList<Device>();
        this.mRemoteDevices = new ArrayList<Device>();
    }

    static /* synthetic */ void access$0(final DLNAEngine dlnaEngine, final AndroidUpnpService mAndroidUpnpService) {
        dlnaEngine.mAndroidUpnpService = mAndroidUpnpService;
    }

    static /* synthetic */ void access$3(final DLNAEngine dlnaEngine, final DLNAController mDlnaController) {
        dlnaEngine.mDlnaController = mDlnaController;
    }

    public static DLNAEngine getInstance(final Context context) {
        if (DLNAEngine.mInstance == null) {
            DLNAEngine.mInstance = new DLNAEngine(context);
        }
        return DLNAEngine.mInstance;
    }

    private void release() {
        this.mRegistryListenerSet.clear();
        this.mLocalDevices.clear();
        this.mRemoteDevices.clear();
        if (this.mAndroidUpnpService != null) {
            this.mAndroidUpnpService.getRegistry().removeListener(this.mRegistryListenerInner);
            this.mAndroidUpnpService = null;
        }
        if (this.mDlnaController != null) {
            this.mDlnaController.release();
            this.mDlnaController = null;
        }
    }

    public void addRegistryListener(final RegistryListener registryListener) {
        if (registryListener != null) {
            this.mRegistryListenerSet.add(registryListener);
        }
    }

    public DLNAController getController() {
        return this.mDlnaController;
    }

    public boolean isEngineStarted() {
        return this.mEngineStarted;
    }

    public void removeRegistryListener(final RegistryListener registryListener) {
        if (registryListener != null) {
            this.mRegistryListenerSet.remove(registryListener);
        }
    }

    public void search() {
        if (this.isEngineStarted()) {
            this.mAndroidUpnpService.getControlPoint().search();
        }
    }

    public void startEngine() {
        if (!this.isEngineStarted()) {
            mContext.bindService(new Intent(mContext,DLNAUpnpService.class),mServiceConnection,Context.BIND_AUTO_CREATE);
            this.mEngineStarted = true;
        }
    }

    public void stopEngine() {
        if (this.isEngineStarted()) {
            this.mEngineStarted = false;
            mContext.unbindService(mServiceConnection);
        }
    }
}

