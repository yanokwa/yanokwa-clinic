/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.odkclinic.client.xforms;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fcitmuk.openmrs.PatientData;
import org.fcitmuk.openmrs.PatientList;

import android.util.Log;

public class XformsUtil {
	private static final String U1 = "admin";
	private static final String P1 = "anKasemar77";
	private static final String TEST_OPEN = "http://lab7.openmrs.org:8080/openmrs/module/xforms/patientDownload.form";
	private static final String OPEN_MRS = "http://10.0.2.2:8080/openmrs/module/xforms/patientDownload.form";
	private static final String URL_PARAM = "?downloadPatients=true";
	
	/**
	 * Retrieves a patient list with given cohort id from openmrs
	 * @param id long cohort id
	 * @return PatientList of cohort
	 */
	public static PatientList getCohort(long id) throws IOException {
		PatientList pl = null;
		
		String uri = OPEN_MRS + URL_PARAM;
		URL url = new URL(uri);
		Log.d("Connection", "Starting");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		Log.d("Connection", "Starting");
	    //con.setRequestMethod( "POST" );
	    //con.setDoInput( true );
	    //con.setDoOutput( true );
	    //con.connect();
		//URLConnection con = url.openConnection();
		
		//HttpConnection con = (HttpConnection)url.openConnection();
		//con.connect();
		//con.setRequestProperty("Content-Type","application/octet-stream");
		//con.setRequestProperty("User-Agent","Profile/MIDP-2.0 Configuration/CLDC-1.0");
		//con.setRequestProperty("Content-Language", "en-US");
		OutputStream os = con.getOutputStream();
		DataOutputStream daos = new DataOutputStream(os);
		//daos.writeByte(6);
		//daos.flush();
		
		//HttpConnection httpClient = new DefaultHttpClientConnection();
		/*HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost request = new HttpPost(uri);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		DataOutputStream daos = new DataOutputStream(os);*/
		
		daos.writeUTF(U1);
		daos.writeUTF(P1);
		daos.writeUTF("xforms.patientSerializer"); 
		daos.writeUTF("en");
		daos.writeByte(6);
		daos.writeInt(1); 
		daos.flush();
		daos.close();
		
		/*ByteArrayEntity bae = new ByteArrayEntity(os.toByteArray());
		request.setEntity(bae);
		
		HttpResponse response = httpClient.execute(request);
		int status = response.getStatusLine().getStatusCode();
		
		Log.d("GETCOHORT", uri);

		if (status == HttpStatus.SC_OK) {
			
			HttpEntity ent = response.getEntity();
			
			if (ent != null) {
				Log.d("Entity", "Not Null");
				Log.d("Entity", "Length: " + ent.getContentLength());
				InputStream is = ent.getContent();
				ZInputStream gzip = new ZInputStream()
				DataInputStream dis = new DataInputStream(is);*/
				//DataInputStream dis = new DataInputStream((InputStream)con.getContent());
				
				DataInputStream dis = (DataInputStream) con.getInputStream();
				
				PatientData pd = new PatientData();
				try {
					Log.e("Getting Cohort Stream", String.valueOf(dis
							.available()));
					Log.e("Getting something?", String.valueOf(dis.read()));
					pd.read(dis); 
					pl = pd.getPatients();
					for (int i = 0; i < pl.size(); i++) {
						Log.d("Patient", pl.getPatient(i).getName());
					}
				} catch (InstantiationException e) {   
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace(); 
				} catch (Exception e) {
					e.printStackTrace();
				}
			/*} else {
				Log.d("Entity","Ent is null");
			}
		} else {
			Log.e("STATUS", "is " + status);
		} */
		
		return pl;
	}
}
