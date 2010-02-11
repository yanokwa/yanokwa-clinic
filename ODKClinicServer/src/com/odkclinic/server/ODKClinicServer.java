package com.odkclinic.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;

import com.odkclinic.download.AndroidDownloadManager;
import com.odkclinic.model.Bundle;
import com.odkclinic.model.EncounterBundle;
import com.odkclinic.model.ObservationBundle;

public class ODKClinicServer {
	
	private Log log = LogFactory.getLog(this.getClass());
	private enum UploadType { Observations, Encounters };
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
				byte action = dis.readByte();
				Map<UploadType, Bundle<?>> map = new HashMap<UploadType, Bundle<?>>();
				
				do {
				    Bundle<?> bundle = null;
				    
                    if(action == ODKClinicConstants.ACTION_ANDROID_UPLOAD_ENCOUNTER) {
                        bundle = uploadEncounters(dis, serializerKey);
                        if (bundle != null) {
                            map.put(UploadType.Encounters, bundle);
                        } 
                    } else if(action == ODKClinicConstants.ACTION_ANDROID_UPLOAD_OBS) {
                        bundle = uploadObservations(dis, serializerKey);
                        if (bundle != null) {
                            map.put(UploadType.Observations, bundle);
                        } 
                    }
                    if (bundle == null) {
                        responseStatus = ODKClinicConstants.STATUS_ERROR;
                        break;
                    }
                    action = dis.readByte();
                    log.debug("Upload Action:" + action);
                    
                    //read until actionbyte changes from upload type
                } while ((action | ODKClinicConstants.ACTION_ANDROID_UPLOADS) > 0);
				
				if(responseStatus == ODKClinicConstants.STATUS_SUCCESS) {
				    
				    // try to commit changes to server database first
				    if (commitChanges(map, revToken)) {
				        while (action != ODKClinicConstants.ACTION_ANDROID_END) {
    	                    if(action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_ENCOUNTER)
    	                        downloadEncounters(dis.readInt(), dos, serializerKey, revToken);
    	                    else if(action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_OBS)
    	                        downloadObservations(dis.readInt(), dos, serializerKey,revToken);
    	                    else if (action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PATIENTS) 
    	                        downloadPatients(dos, serializerKey);
    	                    else if (action == ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PROGRAMS)
    	                        downloadPrograms(dos, serializerKey);
    	                    
    	                    action = dis.readByte();
    	                    log.debug("Action:" + action);
    	                } 
				    }
				}
				
				dos.flush();
				
				responseStatus = ODKClinicConstants.STATUS_SUCCESS;
			}
						
			
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
	
	private boolean commitChanges(Map<UploadType, Bundle<?>> map, long revToken) {
	    for (Map.Entry<UploadType, Bundle<?>> entry: map.entrySet()) {
	        switch(entry.getKey()) {
	            case Encounters:
	                if (!AndroidDownloadManager.commitEncounters((EncounterBundle) entry.getValue(), revToken)) 
	                    return false;
	                break;
	            case Observations:
	                if (!AndroidDownloadManager.commitObservations((ObservationBundle) entry.getValue(), revToken))
	                    return false;
	                break;
	            default:
	                return false;
	        }
	    }
	    return true;
	}
	
	private void downloadEncounters(Integer patientId, OutputStream os, String serializerKey, long revToken) throws Exception{
		
		AndroidDownloadManager.downloadEncounters(patientId, os, serializerKey, revToken);

	}
	
	private EncounterBundle uploadEncounters(DataInputStream is, String serializerKey) throws IOException {
	    return AndroidDownloadManager.uploadEncounters(is, serializerKey);
	}
	
	private void downloadObservations(Integer patientId, OutputStream os, String serializerKey, long revToken) throws Exception{
		
		AndroidDownloadManager.downloadObservations(patientId, os, serializerKey, revToken);

	}
	
	private ObservationBundle uploadObservations(DataInputStream is, String serializerKey) throws IOException {

		return AndroidDownloadManager.uploadObservations(is, serializerKey);
		
	}
	
	private void downloadPatients(DataOutputStream dos, String serializerKey) throws Exception {
		
		AndroidDownloadManager.downloadPatients(dos, serializerKey);
		
	}
	
	private void downloadPrograms(DataOutputStream dos, String serializerKey) throws Exception {
		
		AndroidDownloadManager.downloadPrograms(dos, serializerKey);
		
	}
}
