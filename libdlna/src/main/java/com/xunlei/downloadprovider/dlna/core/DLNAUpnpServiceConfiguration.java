package com.xunlei.downloadprovider.dlna.core;

/**
 * Created by Stephan on 2016/9/16.
 */

import org.fourthline.cling.android.*;
import org.fourthline.cling.model.types.*;


public class DLNAUpnpServiceConfiguration extends AndroidUpnpServiceConfiguration
{
    @Override
    public ServiceType[] getExclusiveServiceTypes() {
        return super.getExclusiveServiceTypes();
    }

    @Override
    public int getRegistryMaintenanceIntervalMillis() {
        return super.getRegistryMaintenanceIntervalMillis();
    }
}

