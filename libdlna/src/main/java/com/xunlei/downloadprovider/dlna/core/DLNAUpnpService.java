package com.xunlei.downloadprovider.dlna.core;

/**
 * Created by Stephan on 2016/9/16.
 */

import org.fourthline.cling.*;
import org.fourthline.cling.protocol.*;
import org.fourthline.cling.android.*;
import android.content.*;
import android.os.*;


public class DLNAUpnpService extends AndroidUpnpServiceImpl
{
    @Override
    protected UpnpServiceConfiguration createConfiguration() {
        return new DLNAUpnpServiceConfiguration();
    }

    @Override
    protected AndroidRouter createRouter(final UpnpServiceConfiguration upnpServiceConfiguration, final ProtocolFactory protocolFactory, final Context context) {
        return super.createRouter(upnpServiceConfiguration, protocolFactory, context);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

