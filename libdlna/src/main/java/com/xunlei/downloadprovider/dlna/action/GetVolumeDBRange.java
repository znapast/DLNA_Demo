package com.xunlei.downloadprovider.dlna.action;

/**
 * Created by Stephan on 2016/9/16.
 */

import org.fourthline.cling.controlpoint.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.support.model.*;
import org.fourthline.cling.model.message.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.model.action.*;


public abstract class GetVolumeDBRange extends ActionCallback
{
    public GetVolumeDBRange(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }

    public GetVolumeDBRange(final UnsignedIntegerFourBytes unsignedIntegerFourBytes, final Service service) {
        super(new ActionInvocation(service.getAction("GetVolumeDBRange")));
        this.getActionInvocation().setInput("InstanceID", unsignedIntegerFourBytes);
        this.getActionInvocation().setInput("Channel", Channel.Master.toString());
    }

    @Override
    public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
    }

    public abstract void received(final ActionInvocation p0, final int p1, final int p2);

    @Override
    public void success(final ActionInvocation actionInvocation) {
        int n = 1;
        int n2 = 0;
        int n3 = 0;
        while (true) {
            try {
                final int n4 = n2 = Integer.valueOf(actionInvocation.getOutput("MinValue").getValue().toString());
                n2 = Integer.valueOf(actionInvocation.getOutput("MaxValue").getValue().toString());
                n3 = n4;
                final int n5 = n2;
                if (n != 0) {
                    this.received(actionInvocation, n3, n5);
                }
            }
            catch (Exception ex) {
                actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse ProtocolInfo response: " + ex, ex));
                this.failure(actionInvocation, null);
                n = 0;
                final int n5 = n3;
                n3 = n2;
                continue;
            }
            break;
        }
    }
}

