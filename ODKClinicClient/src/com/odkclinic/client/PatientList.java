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

package com.odkclinic.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.tables.CohortMemberTable;
import com.odkclinic.client.db.tables.PatientTable;
import com.odkclinic.client.xforms.EncounterBundle;
import com.odkclinic.client.xforms.ObservationBundle;
import com.odkclinic.client.xforms.PatientBundle;
import com.odkclinic.client.xforms.ProgramBundle;

/**
 * 
 * Activity that lists a set of patients to be viewed.
 * 
 * @author Euzel Villanueva
 * 
 */
public class PatientList extends ExpandableListActivity {
	private static String LOG_TAG = PatientList.class.getName();
	
	private DbAdapter mDb;
	private int mGroupIdColumnIndex;
	private ExpandableListAdapter mAdapter;
	private Toast mToast;
	private SendDataTask mSData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patientlist_list);
		
		// instantiate db instance and open
		mDb = new DbAdapter(this);
		mDb.open();
		fillData();
		
		mToast = Toast.makeText(this, "test", 2);
	}

	private void fillData() {
		// get cursor to list of patients
		Cursor patientCursor = mDb.getPatientList();
		startManagingCursor(patientCursor);

		// cache group id
		mGroupIdColumnIndex = patientCursor
				.getColumnIndexOrThrow(PatientTable.ID.getName());
		
		Log.d(LOG_TAG, "There are " + patientCursor.getCount() + " patients.");

		// create listadapter for cursor
		mAdapter = new ExpandablePatientListAdapter(patientCursor, this,
				R.layout.patientlist_group_item,
				R.layout.patientlist_child_item, 
				new String[] { PatientTable.NAME.getName(), 
							   PatientTable.ID.getName(),
							   CohortMemberTable.COHORT_ID.getName() },
			    new int[] { R.id.patient_name,
						 	R.id.patient_id, R.id.cohort_id }, 
			 	new String[] { PatientTable.GENDER.getName(), 
							   PatientTable.RACE.getName(),
							   PatientTable.BIRTHDATE.getName() }, 
				new int[] {R.id.patientgender, 
						   R.id.patientrace, 
						   R.id.patientbirthdate });
		
		setListAdapter(mAdapter);
	}

	// TODO check change through result code
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case R.id.patientlist_manage:
			
			break;
		case R.id.patientlist_update:
			
			break;
		default:
			break;
		}
		fillData();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDb.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the patientlist menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.patientlist, menu);

		return true;
	}

	/**
	 * Starts intent for managing patients.
	 */
	private void managePatientsIntent() {
		Intent i = new Intent(this, PatientManagerList.class);
		startActivityForResult(i, R.id.patientlist_manage);
	}

	/**
	 * Starts intent for viewing updates.
	 */
	private void updatesIntent() {
		Intent i = new Intent(this, UpdateList.class);
		startActivityForResult(i, R.id.patientlist_sync);
	}
	
	/**
	 * Starts intent for viewing patientdata
	 */
	private void patientDetailsIntent(long id) {
		Intent i = new Intent(this, PatientDetails.class);
		Bundle extras = new Bundle();
		extras.putLong(PatientTable.ID.getName(), id);
		i.putExtras(extras);
		startActivityForResult(i, R.layout.patientdetails);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.patientlist_manage:
			managePatientsIntent();
			return true;
		case R.id.patientlist_sync:
		    if (mSData == null || mSData.getStatus().equals(AsyncTask.Status.FINISHED)) { 
		        Log.d(LOG_TAG, "Starting synchronization with server.");
		        mSData = new SendDataTask();
		        mSData.execute((Void[]) null);
		        mToast.setText("Starting synchronization with server.");
		        mToast.show();
		    } else {
		        mToast.setText("Already started synchronization with server.");
                mToast.show();
		    }
			return true;
		case R.id.patientlist_update:
			updatesIntent();
			return true;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		patientDetailsIntent(id);
		return true;
	}

	private class ExpandablePatientListAdapter extends SimpleCursorTreeAdapter {

		public ExpandablePatientListAdapter(Cursor cursor, Context context,
				int groupLayout, int childLayout, String[] groupFrom,
				int[] groupTo, String[] childrenFrom, int[] childrenTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo,
					childLayout, childrenFrom, childrenTo);
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			long id = groupCursor.getLong(mGroupIdColumnIndex); 
			Cursor patientInfo = mDb.getPatientDemographicInfo(id);
			startManagingCursor(patientInfo);
			return patientInfo;
		}
	}
	
    //TODO change to proper URL
	public static final String SERVER_URL = "http://10.0.2.2:8080/openmrs/module/xforms/patientDownload.form";
	private static final String USER = "admin";
    private static final String PASS = "anKasemar77";
    private static final String SKEY = "null"; //dummy serializer key
    
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
    
	/**
     * 
     * Class for syncing data from the server.
     *
     */

    private class SendDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            DbAdapter db = new DbAdapter(new MainPage()); //just random reference
            if (db.checkSync()) {
                return null;
            } 
            EncounterBundle eb = db.getEncounterBundle();
            ObservationBundle ob = db.getObservationBundle();
            long newRevToken = System.currentTimeMillis();
            DataInputStream dis = null;
            DataOutputStream dos = null;
            HttpURLConnection con = null;
            try
            {
                URL url = new URL(SERVER_URL);
                Log.d(LOG_TAG, "Starting Connection.");
                con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod( "POST" );
                con.setDoInput( true );
                con.setDoOutput( true );
                con.connect();
                dis = new DataInputStream(con.getInputStream());
                dos = new DataOutputStream(con.getOutputStream());
                
                // Try to authenticate
                Log.d(LOG_TAG + "/SendDataTask", String.format("Sending user %s, password %s.", USER, PASS));
                dos.writeUTF(USER);
                dos.writeUTF(PASS);
                dos.writeUTF(SKEY);
                dos.writeLong(db.getRevToken());
                
                // Check if authentication failed
                byte success = dis.readByte();
                if (success == STATUS_ACCESS_DENIED){ 
                    Log.d(LOG_TAG + "/SendDataTask", "Authentication failed.");
                    return null;
                }
                
                // Do actions
                dos.writeByte(ACTION_ANDROID_UPLOAD_ENCOUNTER);
                eb.write(dos);
                
                dos.writeByte(ACTION_ANDROID_UPLOAD_OBS);
                ob.write(dos);
                
                dos.flush();
                
                dos.writeByte(ACTION_ANDROID_DOWNLOAD_ENCOUNTER);
                dos.flush();
                eb = new EncounterBundle();
                eb.read(dis);
                
                dos.writeByte(ACTION_ANDROID_DOWNLOAD_OBS);
                dos.flush();
                ob = new ObservationBundle();
                ob.read(dis);
                
                dos.writeByte(ACTION_ANDROID_DOWNLOAD_PATIENTS);
                dos.flush();
                PatientBundle pb = new PatientBundle();
                pb.read(dis);
                
                dos.writeByte(ACTION_ANDROID_DOWNLOAD_PROGRAMS);
                dos.flush();
                ProgramBundle prb = new ProgramBundle();
                prb.read(dis);
                
                dos.writeByte(ACTION_ANDROID_END);
                
                dos.flush();
                
                success = dis.readByte();
                
                if (success == STATUS_SUCCESS) { 
                    Log.d(LOG_TAG + "/SendDataTask", "Sending data successfull");
                    
                    // Delete the encounters/obs that were sent from client table
                    db.deleteSyncedEncounters(eb);
                    db.deleteSyncedObservations(ob);
                    
                    // Insert bundles into database
                    db.insertProgramBundle(prb);
                    db.insertPatientBundle(pb);
                    db.insertObservationBundle(ob);
                    db.insertEncounterBundle(eb);
                    
                    // update rev token to new value
                    db.setRevToken(newRevToken); //TODO check if we get new one from server or not
                    
                } else { // mark the entries in the respective tables and leave them there.
                    Log.d(LOG_TAG + "/SendDataTask", String.format("Sending data failed. Status Code: %d.", success));
                    db.markEncountersFailed(eb);
                    db.markObservationsFailed(ob);
                }
                         
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (InstantiationException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            } finally {
                try
                { 
                    if (dis != null)
                        dis.close();
                    if (dos != null)
                        dos.close();
                    if (con != null)
                        con.disconnect();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            mToast.setText("Synchronization with server has completed.");
            mToast.show();
        }
        
        
    }
    
}
