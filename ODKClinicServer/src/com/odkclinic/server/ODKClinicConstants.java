package com.odkclinic.server;

public class ODKClinicConstants {
	/** Actions for Android ODK app */
	public static final byte ACTION_ANDROID_DOWNLOAD_ENCOUNTER = 1;
	public static final byte ACTION_ANDROID_UPLOAD_ENCOUNTER = 2;
	public static final byte ACTION_ANDROID_DOWNLOAD_OBS = 3;
	public static final byte ACTION_ANDROID_UPLOAD_OBS = 4;
	public static final byte ACTION_ANDROID_DOWNLOAD_PATIENTS = 5;
	public static final byte ACTION_ANDROID_DOWNLOAD_PROGRAMS = 6;
	public static final byte ACTION_ANDROID_END = 45;
	
	/** Networking responses */
	/** Problems occured during connection of the request. */
	public static final byte STATUS_ERROR = 0;
	
	/** Request communicated successfully. */
	public static final byte STATUS_SUCCESS = 1;
	
	/** Not permitted to carry out the requested operation. */
	public static final byte STATUS_ACCESS_DENIED = 2;
	
}
