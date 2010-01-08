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
		
		ZOutputStream gzip = new ZOutputStream(dosParam,JZlib.Z_BEST_COMPRESSION);
		DataOutputStream dos = new DataOutputStream(gzip);
		

		byte responseStatus = ODKClinicConstants.STATUS_ERROR;

		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			String name = dis.readUTF();
			String pw = dis.readUTF();
			String serializerKey = dis.readUTF();
			//String locale = dis.readUTF();
				
			Context.openSession();
			
			try{
				Context.authenticate(name, pw);
			}
			catch(ContextAuthenticationException ex){
				responseStatus = ODKClinicConstants.STATUS_ACCESS_DENIED;
			}

			if(responseStatus != ODKClinicConstants.STATUS_ACCESS_DENIED){
				DataOutputStream dosTemp = new DataOutputStream(baos);
				byte action = ODKClinicConstants.ACTION_ANDROID_END;
				
				// keep reading actions and data until end byte is seen
				do {
        			action = dis.readByte();
        			
        			if(action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_ENCOUNTER)
        				downloadEncounters(dis.readInt(), dosTemp, serializerKey);
        			else if(action == ODKClinicConstants.ACTION_ANDROID_UPLOAD_ENCOUNTER)
        				uploadEncounters(dis, dosTemp, serializerKey);
        			else if(action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_OBS)
        				downloadObservations(dis.readInt(), dosTemp, serializerKey);
        			else if(action == ODKClinicConstants.ACTION_ANDROID_UPLOAD_OBS)
        				uploadObservations(dis, dosTemp, serializerKey);
        			else if (action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PATIENTS) 
        				downloadPatients(dis, dosTemp, serializerKey);
        			else if (action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PROGRAMS)
        				downloadPrograms(dis, dosTemp, serializerKey);
        			
				} while (action != ODKClinicConstants.ACTION_ANDROID_END);

				responseStatus = ODKClinicConstants.STATUS_SUCCESS;
			}

			dos.writeByte(responseStatus);

			if(responseStatus == ODKClinicConstants.STATUS_SUCCESS)
				dos.write(baos.toByteArray());

			dos.flush();
			gzip.finish();
		}
		catch(Exception ex){
			log.error(ex.getMessage(),ex);
			try{
				dos.writeByte(responseStatus);
				dos.flush();
				gzip.finish();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		finally{
			Context.closeSession();
		}
	}
	
	private void downloadEncounters(Integer patientId, OutputStream os, String serializerKey) throws Exception{
		
		AndroidDownloadManager.downloadEncounters(patientId, os, serializerKey);

	}
	
	private void uploadEncounters(DataInputStream is, DataOutputStream dos, String serializerKey) throws IOException {

		boolean result = AndroidDownloadManager.uploadEncounters(is, serializerKey);
		if (result) {
			dos.writeBoolean(true);
		} else {
			dos.writeBoolean(false);
		}
		
	}
	
	private void downloadObservations(Integer patientId, OutputStream os, String serializerKey) throws Exception{
		
		AndroidDownloadManager.downloadObservations(patientId, os, serializerKey);

	}
	
	private void uploadObservations(DataInputStream is, DataOutputStream dos, String serializerKey) throws IOException {

		boolean result = AndroidDownloadManager.uploadObservations(is, serializerKey);
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
