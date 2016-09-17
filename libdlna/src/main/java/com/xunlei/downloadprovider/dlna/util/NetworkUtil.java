package com.xunlei.downloadprovider.dlna.util;

/**
 * Created by Stephan on 2016/9/16.
 */

import android.content.*;
import java.util.*;
import android.net.wifi.*;
import android.net.*;


public class NetworkUtil
{
    public static String getWifiIpAddress(final Context context) {
        final String s = null;
        final WifiManager wifiManager = (WifiManager)context.getSystemService("wifi");
        String format = s;
        if (wifiManager.isWifiEnabled()) {
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            format = s;
            if (connectionInfo != null) {
                final int ipAddress = connectionInfo.getIpAddress();
                format = String.format(Locale.US, "%d.%d.%d.%d", ipAddress & 0xFF, ipAddress >> 8 & 0xFF, ipAddress >> 16 & 0xFF, ipAddress >> 24 & 0xFF);
            }
        }
        return format;
    }

    public static boolean isWifiEnable(final Context context) {
        return ((ConnectivityManager)context.getSystemService("connectivity")).getNetworkInfo(1).isConnectedOrConnecting();
    }
}

