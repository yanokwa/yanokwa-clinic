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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

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
import android.widget.LinearLayout;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.tables.CohortMemberTable;
import com.odkclinic.client.db.tables.PatientTable;
import com.odkclinic.client.xforms.CohortBundle;
import com.odkclinic.client.xforms.CohortMemberBundle;
import com.odkclinic.client.xforms.ConceptBundle;
import com.odkclinic.client.xforms.ConceptNameBundle;
import com.odkclinic.client.xforms.EncounterBundle;
import com.odkclinic.client.xforms.LocationBundle;
import com.odkclinic.client.xforms.ObservationBundle;
import com.odkclinic.client.xforms.PatientBundle;
import com.odkclinic.client.xforms.PatientProgramBundle;
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
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patientlist_list);
		
		// instantiate db instance and open
		mDb = new DbAdapter(this);
		mDb.open();
		fillData();
		
		mToast = Toast.makeText(this, "test", 2);
		mContext = this;
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

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor,
                boolean isLastChild)
        {
            String tempGender = cursor.getString(0);
            String tempRace = cursor.getString(1);
            String tempBDay = cursor.getString(2);
            
            if (tempGender != null) {
                ((TextView) view.findViewById(R.id.patientgender)).setText(tempGender);
            }
            if (tempRace != null) {
                ((LinearLayout) view.findViewById(R.id.patientracemodule)).setVisibility(1);
                ((TextView) view.findViewById(R.id.patientrace)).setText(tempRace);
            } else {
                ((LinearLayout) view.findViewById(R.id.patientracemodule)).setVisibility(0);
            }
            if (tempBDay != null) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
                try
                {
                    Date date = df.parse(tempBDay);
                    ((TextView) view.findViewById(R.id.patientbirthdate)).setText(df2.format(date));
                } catch (ParseException e)
                {
                    ((TextView) view.findViewById(R.id.patientbirthdate)).setText(tempBDay);
                    e.printStackTrace();
                }
            }   
        }
	}
	
	public static final String SERVER_URL = "http://10.0.2.2:8080/openmrs/moduleServlet/odkclinic/ODKClinicServer";
	private static final String USER = "admin";
    private static final String PASS = "Emmzelv69";
    private static final String SKEY = "null"; //dummy serializer key
    
    /** Actions for Android ODK app */
    public static final byte ACTION_ANDROID_DOWNLOAD_ENCOUNTER = 1;
    public static final byte ACTION_ANDROID_DOWNLOAD_OBS = 2;
    public static final byte ACTION_ANDROID_DOWNLOAD_PATIENTS = 4;
    public static final byte ACTION_ANDROID_DOWNLOAD_PROGRAMS = 8;
    public static final byte ACTION_ANDROID_DOWNLOAD_CONCEPTS = 16;
    public static final byte ACTION_ANDROID_DOWNLOAD_LOCATIONS = 32;
    public static final byte ACTION_ANDROID_DOWNLOAD_CONCEPTNAMES = 64;
    public static final byte ACTION_ANDROID_DOWNLOAD_COHORTS = 127;
    public static final byte ACTION_ANDROID_DOWNLOAD_COHORTMEMBERS = 126;
    public static final byte ACTION_ANDROID_DOWNLOAD_PATIENTPROGRAMS = 125;
    //public static final byte ACTION_ANDROID_DOWNLOADS = 15;
    public static final byte ACTION_ANDROID_UPLOAD_ENCOUNTER = 16;
    public static final byte ACTION_ANDROID_UPLOAD_OBS = 32;
    public static final byte ACTION_ANDROID_UPLOADS = 48;
    public static final byte ACTION_ANDROID_END = 64;
    
    /** Networking responses */
    /** Problems occured during connection of the request. */
    public static final int STATUS_ERROR = 500;
    
    /** Request communicated successfully. */
    public static final int STATUS_SUCCESS = 200;
    
    /** Not permitted to carry out the requested operation. */
    public static final int STATUS_ACCESS_DENIED = 403;
    
	/**
     * 
     * Class for syncing data from the server.
     *
     */
    
    private class SendDataTask extends AsyncTask<Void, Void, Void> {
        private boolean fail = true;
        
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mToast.setText("Starting synchronization of server");
            mToast.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DbAdapter db = mDb; //just random reference
            if (db == null) {
                db = new DbAdapter(mContext);
                db.open();
            }
            if (db.checkSync()) {
                return null;
            } 
            db.markSync(true);
             
            EncounterBundle eb = db.getEncounterBundle();
            ObservationBundle ob = db.getObservationBundle();
            HttpURLConnection con = null;
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpost = new HttpPost(SERVER_URL);

                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart("USER", new StringBody(USER));
                reqEntity.addPart("PASS", new StringBody(PASS));
                reqEntity.addPart("SKEY", new StringBody(SKEY));
                reqEntity.addPart("REVTOKEN", new StringBody(db.getRevToken()+""));
                byte[] ebBytes = eb.getBytes();
                reqEntity.addPart("UPLOAD_ENCOUNTER", new InputStreamKnownSizeBody(new ByteArrayInputStream(ebBytes), ebBytes.length, "img/jpeg", "ff"));
                byte[] obBytes = ob.getBytes();
                reqEntity.addPart("UPLOAD_OBSERVATION", new InputStreamKnownSizeBody(new ByteArrayInputStream(obBytes), obBytes.length, "img/jpeg", "ff"));
                reqEntity.addPart("DOWNLOAD_ACTIONS", new StringBody("DOWNLOAD_PROGRAM;DOWNLOAD_PATIENT;DOWNLOAD_ENCOUNTER;DOWNLOAD_OBSERVATION;DOWNLOAD_LOCATION;DOWNLOAD_PATIENTPROGRAM;DOWNLOAD_COHORT;DOWNLOAD_COHORTMEMBER;DOWNLOAD_CONCEPT;DOWNLOAD_CONCEPTNAME"));
                
                httpost.setEntity(reqEntity);
                
                HttpResponse response = httpclient.execute(httpost);
                Log.d(LOG_TAG, response.getStatusLine().toString());
                
                if (response.getStatusLine().getStatusCode() == STATUS_SUCCESS) { 
                    Log.d(LOG_TAG + "/SendDataTask", "Sending data successfull");
                    
                    
                    HttpEntity he = response.getEntity();
                    DataInputStream dis = new DataInputStream(he.getContent());
                    ProgramBundle prb = new ProgramBundle();
                    PatientBundle pb = new PatientBundle();
                    ObservationBundle secondOb = new ObservationBundle(); 
                    EncounterBundle secondEb = new EncounterBundle();
                    LocationBundle lb = new LocationBundle();
                    PatientProgramBundle ppb = new PatientProgramBundle();
                    CohortBundle cb = new CohortBundle();
                    CohortMemberBundle cmb = new CohortMemberBundle();
                    ConceptBundle cnb = new ConceptBundle();
                    ConceptNameBundle cnnb = new ConceptNameBundle();
                    
                    boolean success = false;
                    
                    try {
                        
                        int x = dis.read();
                        do {
                            switch(x) {
                                case ACTION_ANDROID_DOWNLOAD_ENCOUNTER:
                                    secondEb.read(dis);
                                    break;
                                case ACTION_ANDROID_DOWNLOAD_OBS:
                                    secondOb.read(dis);
                                    break;
                                case ACTION_ANDROID_DOWNLOAD_PROGRAMS:
                                    prb.read(dis);
                                    break;
                                case ACTION_ANDROID_DOWNLOAD_COHORTMEMBERS:
                                    cmb.read(dis);
                                    break;
                                case ACTION_ANDROID_DOWNLOAD_COHORTS:
                                    cb.read(dis);
                                    break;
                                case ACTION_ANDROID_DOWNLOAD_CONCEPTS:
                                    cnb.read(dis);
                                    break;
                                case ACTION_ANDROID_DOWNLOAD_CONCEPTNAMES:
                                    cnnb.read(dis);
                                    break;
                                case ACTION_ANDROID_DOWNLOAD_LOCATIONS:
                                    lb.read(dis);
                                    break;
                                case ACTION_ANDROID_DOWNLOAD_PATIENTPROGRAMS:
                                    ppb.read(dis);
                                    break;
                                case ACTION_ANDROID_DOWNLOAD_PATIENTS:
                                    pb.read(dis);
                                    break;
                            }
                            x = dis.read();
                        } while (x != -1);
                        success = true;
                    } catch(EOFException e) {
                        Log.d(LOG_TAG + "/SendDataTask", "Error in Stream");
                        e.printStackTrace();
                    } finally {
                        dis.close();
                    }
                    
                    if (!success) {
                        fail = true;
                        return (Void) null;
                    }
                    
                    if (db == null) {
                        db = new DbAdapter(mContext);
                        db.open();
                    }
                    
                    // Delete the encounters/obs that were sent from client table
                    db.deleteSyncedEncounters(eb);
                    db.deleteSyncedObservations(ob);
                    
                    if (cb.getBundle().size() > 0)
                    {
                        db.insertCohortBundle(cb);
                        Log.d(LOG_TAG, "GOT NONEMPTY CB");
                    } else
                    {
                        Log.d(LOG_TAG, "GOT EMPTY CB");
                    }
                    
                    if (cnb.getBundle().size() > 0)
                    {
                        db.insertConceptBundle(cnb);
                        Log.d(LOG_TAG, "GOT NONEMPTY CNB");
                    } else
                    {
                        Log.d(LOG_TAG, "GOT EMPTY CNB");
                    }
                    
                    if (cnnb.getBundle().size() > 0)
                    {
                        db.insertConceptNameBundle(cnnb);
                        Log.d(LOG_TAG, "GOT NONEMPTY CNNB");
                    } else
                    {
                        Log.d(LOG_TAG, "GOT EMPTY CNNB");
                    }
                    
                    if (pb.getBundle().size() > 0)
                    {
                        db.insertPatientBundle(pb);
                        Log.d(LOG_TAG, "GOT NONEMPTY PB");
                    } else
                    {
                        Log.d(LOG_TAG, "GOT EMPTY PB");
                    }
                    
                    if (cmb.getBundle().size() > 0)
                    {
                        db.insertCohortMemberBundle(cmb);
                        Log.d(LOG_TAG, "GOT NONEMPTY CMB");
                    } else
                    {
                        Log.d(LOG_TAG, "GOT EMPTY CMB");
                    }
                    
                    if (lb.getBundle().size() > 0)
                    {
                        db.insertLocationBundle(lb);
                        Log.d(LOG_TAG, "GOT NONEMPTY LB");
                    } else
                    {
                        Log.d(LOG_TAG, "GOT EMPTY LB");
                    }
                    
                    if (prb.getBundle().size() > 0) {
                        db.insertProgramBundle(prb);
                        Log.d(LOG_TAG, "GOT NONEMPTY PRB");
                    } else
                    {
                        Log.d(LOG_TAG, "GOT EMPTY PRB");
                    }
                    
                    if (ppb.getBundle().size() > 0)
                    {
                        db.insertPatientProgramBundle(ppb);
                        Log.d(LOG_TAG, "GOT NONEMPTY PPB");
                    } else
                    {
                        Log.d(LOG_TAG, "GOT EMPTY PPB");
                    }
                    
                    if (secondEb.getBundle().size() > 0)
                    {
                        db.insertEncounterBundle(secondEb);
                        Log.d(LOG_TAG, "GOT NONEMPTY EB");
                    } else
                    {
                        Log.d(LOG_TAG, "GOT EMPTY EB");
                    }
                    if (secondOb.getBundle().size() > 0)
                    {
                        db.insertObservationBundle(secondOb);
                        Log.d(LOG_TAG, "GOT NONEMPTY OB");
                    } else
                    {
                        Log.d(LOG_TAG, "GOT EMPTY OB");
                    }

                    
                    
                    // update rev token to new value
                    //db.setRevToken(dis.readLong()); 
                    fail = false;
                } else { // mark the entries in the respective tables and leave them there.
                    Log.d(LOG_TAG + "/SendDataTask", String.format("Sending data failed. Status: %s.", response.getStatusLine()));
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
                if (con != null)
                    con.disconnect();
                
                db.markSync(false);
            } 
            return (Void) null;
        }
        private class InputStreamKnownSizeBody extends InputStreamBody {
            private int length;

            public InputStreamKnownSizeBody(final InputStream in, final int length,
                    final String mimeType, final String filename) {
                super(in, mimeType, filename);
                this.length = length;
            }

            @Override
            public long getContentLength() {
                return this.length;
            }
        }
        @Override
        protected void onPostExecute(Void result)
        {
            if (mToast == null)
                mToast = Toast.makeText(new PatientList(), "", 2);
            if (fail) {
                mToast.setText("Synchronization with server has failed.");
            } else 
                mToast.setText("Synchronization with server has completed.");
            mToast.show();
            if (mDb != null) {
                fillData();
            }
        }
    }    
}
