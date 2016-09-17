package com.xunlei.downloadprovider.dlna.util;

/**
 * Created by Stephan on 2016/9/16.
 */

//import com.xunlei.downloadprovider.bp.*;
import java.util.*;
import android.os.*;
import org.json.*;
//import com.xunlei.downloadprovider.bp.url.*;

public class DlnaClient //extends BpBox
{
    public static final int DLNA_PARSE_RESULT_CALLBACK = 453674045;
    private static final String POST_EP_LIST_FOR_REAL_URL = "http://quan.m.xunlei.com/cgi-bin/m3u8?";
    private static final String TAG;

    static {
        TAG = DlnaClient.class.getSimpleName();
    }

    public DlnaClient(final Handler handler, final Object o) {
//        super(handler, o);
    }

    public int query(final String s) {
//        final BpDataLoader bpFuture = new BpDataLoader("http://quan.m.xunlei.com/cgi-bin/m3u8?", "POST", s, (String)null, (HashMap)null, (IBpDataLoaderParser)new DlnaEpParser(), 10000, 10000, 1);
//        bpFuture.setBpOnDataLoaderCompleteListener((BpDataLoader$IBpOnDataLoaderCompleteListener)new BpDataLoader$IBpOnDataLoaderCompleteListener() {
//            public void onComplete(int status, final Object o, final Map<String, List<String>> map, final BpDataLoader bpDataLoader) {
//                Label_0069: {
//                    if (status != 0) {
//                        break Label_0069;
//                    }
//                    status = 3;
//                    while (true) {
//                        DlnaClient.this.setStatus(status);
//                        try {
//                            final Message message = new Message();
//                            message.what = 453674045;
//                            final byte[] array = (byte[])o;
//                            if (array != null) {
//                                message.obj = new JSONObject(new String(array));
//                            }
//                            DlnaClient.this.mListener.sendMessage(message);
//                            return;
//                            status = 4;
//                        }
//                        catch (JSONException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
//        this.setBpFuture((BpFuture)bpFuture);
//        return runBox((BpBox)this);
        return 0;
    }
}

