package com.xunlei.downloadprovider.dlna;

public interface OnDLNADialogListener {
	public void onDialogDismiss(boolean isDismiss, MediaPlayerPlayCMD playCMD);
	/**	当全局设备list发生变动时调用，isAdd为真时为设备增加，为假时为设备被移除 */
	public void onListChange(boolean isAdd);

	public enum MediaPlayerPlayCMD {
		Play_None, // 什么也不做, surfaceCreated时不去prepare，onResume时也不播放
		Play_Prepare, // surfaceCreated时，让mediaplayer去prepare，prepare完后不播放
		Play_Prepare_Start, // surfaceCreated时，让mediaplayer去prepare，prepare完后start播放
		Play_OnResume_Start // 在onResume时，直接调用mediaplayer的start播放
	}
}