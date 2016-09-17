package com.xunlei.downloadprovider.dlna.player;

/**
 * Created by Stephan on 2016/9/16.
 */

import com.xunlei.downloadprovider.dlna.core.*;

public interface IDLNAPlayer
{
    public static final int DLNA_ERROR_EXTRA_UNKNOWN = -1;
    public static final int DLNA_ERROR_FLAG_DISCONNECTED = 0;
    public static final int DLNA_ERROR_FLAG_PREPARE_FAILED = 1;
    public static final int DLNA_ERROR_FLAG_SERVER_FAILED = 2;
    public static final int DLNA_ERROR_FLAG_UNKNOWN = -1;
    public static final int DLNA_INFO_EXTRA_UNKNOWN = -1;
    public static final int DLNA_INFO_FLAG_PAUSED = 1;
    public static final int DLNA_INFO_FLAG_PLAYING = 0;
    public static final int DLNA_INFO_FLAG_STOPPED = 2;
    public static final int DLNA_INFO_FLAG_UNKNOWN = -1;

    int getCurrentPosition();

    int getDuration();

    boolean isPlaying();

    boolean pause();

    boolean play();

    void release();

    boolean seekTo(final int p0);

    void setDataSource(final String p0) throws IllegalStateException, IllegalArgumentException;

    boolean stop();

    public interface Callback
    {
        void onCompletion(final DLNAController p0);

        void onError(final DLNAController p0, final int p1, final int p2);

        void onInfo(final DLNAController p0, final int p1, final int p2);

        void onPrepared(final DLNAController p0);

        void onPreparing(final DLNAController p0);

        void onSeekResult(final boolean p0);
    }
}

