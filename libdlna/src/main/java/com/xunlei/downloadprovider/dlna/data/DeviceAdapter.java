package com.xunlei.downloadprovider.dlna.data;

/**
 * Created by Stephan on 2016/9/16.
 */

import android.widget.*;
import android.content.*;
import org.fourthline.cling.model.meta.*;
import com.xunlei.downloadprovider.dlna.util.*;
import android.text.*;
import android.view.*;
//import com.xunlei.downloadprovider.web.*;
import java.util.*;

public class DeviceAdapter extends BaseAdapter
{
    private Context mContext;
    private List<Device> mDevices;

    public DeviceAdapter(final Context mContext) {
        this.mDevices = new ArrayList<Device>();
        this.mContext = mContext;
    }

    public void addDevice(final Device device) {
        if (device != null && !this.mDevices.contains(device) && DlnaUtil.isSupportAVTransportService(device)) {
            this.mDevices.add(device);
            this.notifyDataSetChanged();
        }
    }

    public void clearDevice() {
        if (this.mDevices != null && !this.mDevices.isEmpty()) {
            this.mDevices.clear();
            this.notifyDataSetChanged();
        }
    }

    public int getCount() {
        return this.mDevices.size();
    }

    public Device getDeviceByIndex(final int n) {
        if (n < 0 || n >= this.mDevices.size()) {
            return null;
        }
        return this.mDevices.get(n);
    }

    public int getDeviceIndex(final String s) {
        if (s != null) {
            for (int i = 0; i < this.mDevices.size(); ++i) {
                final String deviceUDN = DlnaUtil.getDeviceUDN(this.mDevices.get(i));
                if (!TextUtils.isEmpty((CharSequence)deviceUDN) && deviceUDN.equals(s)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getDeviceIndex(final Device device) {
        if (device != null) {
            for (int i = 0; i < this.mDevices.size(); ++i) {
                if (device.equals(this.mDevices.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public Object getItem(final int n) {
        return this.mDevices.get(n);
    }

    public long getItemId(final int n) {
        return n;
    }

    public View getView(final int n, final View view, final ViewGroup viewGroup) {
        View inflate = view;
        if (view == null) {
            inflate = LayoutInflater.from(viewGroup.getContext()).inflate(this.mContext.getResources().getIdentifier("dlna_search_result_list_view_item", "layout", this.mContext.getPackageName()), (ViewGroup)null);
        }
//        final EllipsizingTextView ellipsizingTextView = (EllipsizingTextView)inflate.findViewById(this.mContext.getResources().getIdentifier("tv_dlna_device_item", "id", this.mContext.getPackageName()));
//        ellipsizingTextView.setMaxLines(1);
//        ellipsizingTextView.setText((CharSequence)DlnaUtil.getDeviceDisplayName(this.mDevices.get(n)));
        return inflate;
    }

    public void removeDevice(final Device device) {
        if (device != null && this.mDevices.contains(device)) {
            this.mDevices.remove(device);
            this.notifyDataSetChanged();
        }
    }

    public void updateDevices(final List<Device> list) {
        this.mDevices.clear();
        if (list != null) {
            this.mDevices.addAll(list);
            this.notifyDataSetChanged();
        }
    }
}

