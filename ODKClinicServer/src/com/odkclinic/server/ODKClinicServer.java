package com.odkclinic.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;


import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;
import com.odkclinic.download.AndroidDownloadManager;
import com.odkclinic.server.ODKClinicConstants;

public class ODKClinicServer {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Called to handle data input and output streams, reading requests and writing response
	 * Modeled after Xforms modules
	 * 
	 * @param dis - the stream to read from.
	 * @param dos - the stream to write to.
	 */
	public void handleStreams(DataInputStream dis, DataOutputStream dosParam)
	throws IOException, Exception {
		
		//ZOutputStream gzip = new ZOutputStream(dosParam,JZlib.Z_BEST_COMPRESSION);
		//DataOutputStream dos = new DataOutputStream(gzip);
		DataOutputStream dos = dosParam;

		byte responseStatus = ODKClinicConstants.STATUS_SUCCESS;

		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			String name = dis.readUTF();
			String pw = dis.readUTF();
			String serializerKey = dis.readUTF();
			long revToken = dis.readLong();
			
			Context.openSession();
			log.debug("Session opened");
			try{
				Context.authenticate(name, pw);
			}
			catch(ContextAuthenticationException ex){
				responseStatus = ODKClinicConstants.STATUS_ACCESS_DENIED;
			}
			
			dos.writeByte(responseStatus);

			if(responseStatus != ODKClinicConstants.STATUS_ACCESS_DENIED){
				DataOutputStream dosTemp = new DataOutputStream(baos);
				byte action = ODKClinicConstants.ACTION_ANDROID_END;
				
				// keep reading actions and data until end byte is seen
				do {
        			action = dis.readByte();
        			log.debug("Action:" + action);
        			
        			if(action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_ENCOUNTER)
        				downloadEncounters(dis.readInt(), dosTemp, serializerKey, revToken);
        			else if(action == ODKClinicConstants.ACTION_ANDROID_UPLOAD_ENCOUNTER) {
        				uploadEncounters(dis, dosTemp, serializerKey, revToken);
        			} else if(action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_OBS)
        				downloadObservations(dis.readInt(), dosTemp, serializerKey,revToken);
        			else if(action == ODKClinicConstants.ACTION_ANDROID_UPLOAD_OBS) {
        				uploadObservations(dis, dosTemp, serializerKey, revToken);
        			} else if (action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PATIENTS) 
        				downloadPatients(dis, dosTemp, serializerKey);
        			else if (action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PROGRAMS)
        				downloadPrograms(dis, dosTemp, serializerKey);
        			
				} while (action != ODKClinicConstants.ACTION_ANDROID_END);

				responseStatus = ODKClinicConstants.STATUS_SUCCESS;
			}

			

			if(responseStatus == ODKClinicConstants.STATUS_SUCCESS)
				dos.write(baos.toByteArray());

			dos.flush();
			//gzip.finish();
		}
		catch(Exception ex){
			log.error(ex.getMessage(),ex);
			try{
				dos.writeByte(ODKClinicConstants.STATUS_ERROR);
				dos.flush();
				//gzip.finish();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		finally{
			Context.closeSession();
		}
	}
	
	private void downloadEncounters(Integer patientId, OutputStream os, String serializerKey, long revToken) throws Exception{
		
		AndroidDownloadManager.downloadEncounters(patientId, os, serializerKey, revToken);

	}
	
	private void uploadEncounters(DataInputStream is, DataOutputStream dos, String serializerKey, long revToken) throws IOException {

		boolean result = AndroidDownloadManager.uploadEncounters(is, serializerKey, revToken);
		if (result) {
			dos.writeBoolean(true);
		} else {
			dos.writeBoolean(false);
		}
		
	}
	
	private void downloadObservations(Integer patientId, OutputStream os, String serializerKey, long revToken) throws Exception{
		
		AndroidDownloadManager.downloadObservations(patientId, os, serializerKey, revToken);

	}
	
	private void uploadObservations(DataInputStream is, DataOutputStream dos, String serializerKey, long revToken) throws IOException {

		boolean result = AndroidDownloadManager.uploadObservations(is, serializerKey, revToken);
		if (result) {
			dos.writeBoolean(true);
		} else {
			dos.writeBoolean(false);
		}
		
	}
	
	private void downloadPatients(DataInputStream is, DataOutputStream dos, String serializerKey) throws Exception {
		
		AndroidDownloadManager.downloadPatients(dos, serializerKey);
		
	}
	
	private void downloadPrograms(DataInputStream is, DataOutputStream dos, String serializerKey) throws Exception {
		
		AndroidDownloadManager.downloadPrograms(dos, serializerKey);
		
	}
}
