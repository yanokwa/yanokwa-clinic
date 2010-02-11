package com.odkclinic.server;

public class ODKClinicConstants {
	/** table suffixes for openmrs database */
	public static final String ENCOUNTER_TABLE = "encounter";
	public static final String OBS_TABLE = "obs";
	
	/** Actions for Android ODK app */
	public static final byte ACTION_ANDROID_DOWNLOAD_ENCOUNTER = 1;
	public static final byte ACTION_ANDROID_DOWNLOAD_OBS = 2;
	public static final byte ACTION_ANDROID_DOWNLOAD_PATIENTS = 4;
	public static final byte ACTION_ANDROID_DOWNLOAD_PROGRAMS = 8;
	public static final byte ACTION_ANDROID_DOWNLOADS = 15;
	public static final byte ACTION_ANDROID_UPLOAD_ENCOUNTER = 16;
	public static final byte ACTION_ANDROID_UPLOAD_OBS = 32;
	public static final byte ACTION_ANDROID_UPLOADS = 48;
	public static final byte ACTION_ANDROID_END = 64;
	
	/** Networking responses */
	/** Problems occured during connection of the request. */
	public static final byte STATUS_ERROR = 0;
	
	/** Request communicated successfully. */
	public static final byte STATUS_SUCCESS = 1;
	
	/** Not permitted to carry out the requested operation. */
	public static final byte STATUS_ACCESS_DENIED = 2;
	
}
