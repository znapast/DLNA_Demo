package com.xunlei.downloadprovider.dlna.util;

/**
 * Created by Stephan on 2016/9/16.
 */

import java.util.*;
import org.fourthline.cling.model.meta.*;
import android.content.*;
import android.text.*;
import org.fourthline.cling.model.types.*;
import java.io.*;


public class DlnaUtil
{
    private static final String LOCAL_HTTP_SERVER_ADDRESS_PREFIX = "http://127.0.0.1";
    private static final String LOCAL_LOOPBACK_ADDRESS = "127.0.0.1";
    private static StringBuilder mFormatBuilder;
    private static Formatter mFormatter;

    static {
        DlnaUtil.mFormatBuilder = new StringBuilder();
        DlnaUtil.mFormatter = new Formatter(DlnaUtil.mFormatBuilder, Locale.getDefault());
    }

    public static String convertRelativeTimeTarget(int n) {
        n /= 1000;
        return String.format(Locale.US, "%02d:%02d:%02d", n / 60 / 60, n / 60 % 60, n % 60);
    }

    public static String getDeviceDisplayName(final Device device) {
        String s;
        if (device == null) {
            s = null;
        }
        else {
            String friendlyName = null;
            final DeviceDetails details = device.getDetails();
            if (details != null) {
                friendlyName = details.getFriendlyName();
            }
            if ((s = friendlyName) == null) {
                return device.getDisplayString();
            }
        }
        return s;
    }

    public static String getDeviceUDN(final Device device) {
        if (device == null) {
            return null;
        }
        return device.getIdentity().getUdn().getIdentifierString();
    }

    public static String getDlnaUri(final Context context, final String s) {
        if (TextUtils.isEmpty((CharSequence)s)) {
            return null;
        }
        if (!s.startsWith("http://127.0.0.1")) {
            return s;
        }
        final String wifiIpAddress = NetworkUtil.getWifiIpAddress(context);
        if (TextUtils.isEmpty((CharSequence)wifiIpAddress)) {
            return null;
        }
        return s.replace("127.0.0.1", wifiIpAddress);
    }

    public static String getVideoDisplayTime(int n) {
        final int n2 = n / 1000;
        n = n2 % 60;
        final int n3 = n2 / 60 % 60;
        final int n4 = n2 / 3600;
        DlnaUtil.mFormatBuilder.setLength(0);
        if (n4 > 0) {
            return DlnaUtil.mFormatter.format("%02d:%02d:%02d", n4, n3, n).toString();
        }
        return DlnaUtil.mFormatter.format("%02d:%02d:%02d", n4, n3, n).toString();
    }

    public static boolean isSupportAVTransportService(final Device device) {
        return device != null && device.findService(new UDAServiceType("AVTransport")) != null;
    }

    public static boolean isSupportRenderingControlService(final Device device) {
        return device != null && device.findService(new UDAServiceType("RenderingControl")) != null;
    }
}
