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

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.tables.ConceptNameTable;
import com.odkclinic.client.db.tables.ConceptTable;
import com.odkclinic.client.db.tables.PatientTable;
import com.odkclinic.client.utils.PatientDetailType;
/**
 * Lists patient details and choices to transition to new activities for
 * history information, encounter detail, etc.
 * 
 * @author Euzel Villanueva
 *
 */
public class PatientDetails extends ListActivity {
	private static String LOG_TAG = PatientDetails.class.getName();
	private Long mPatientID;
	private TextView mName;
	private TextView mRace;
	private TextView mGender;
	private TextView mBirthDate;
	private DbAdapter mDb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patientdetails);
		mDb = new DbAdapter(this);
		mDb.open();

		mPatientID = savedInstanceState != null ? savedInstanceState.getLong(PatientTable.ID.getName()) 
				: null;
        if (mPatientID == null) {
        	Bundle extras = getIntent().getExtras();            
        	mPatientID = extras != null ? extras.getLong(PatientTable.ID.getName()) 
        			: null;
        }
        
        if (mPatientID == null) {
        	Log.e(LOG_TAG, "Somehow the patient id was not passed in.");
        	finish();
        }

        mRace = (TextView) findViewById(R.id.patientdetails_race);
        mName = (TextView) findViewById(R.id.patientdetails_name);
        mGender = (TextView) findViewById(R.id.patientdetails_gender);
        mBirthDate = (TextView) findViewById(R.id.patientdetails_birthdate);
		fillData();
	}
	 
	private void fillData() {
		Cursor patientInfo = mDb.getPatientDemographicInfo(mPatientID);
		startManagingCursor(patientInfo);
		patientInfo.moveToFirst();
		mName.setText(patientInfo.getString(patientInfo.getColumnIndexOrThrow(PatientTable.NAME.getName())));
		mGender.setText(patientInfo.getString(patientInfo.getColumnIndex(PatientTable.GENDER.getName())));
		mRace.setText(patientInfo.getString(patientInfo.getColumnIndex(PatientTable.RACE.getName())));
		mBirthDate.setText(patientInfo.getString(patientInfo.getColumnIndex(PatientTable.BIRTHDATE.getName())));
		Cursor concepts = mDb.getPatientConcepts(mPatientID);
		startManagingCursor(concepts);
		SimpleCursorAdapter patients = new SimpleCursorAdapter(this,
																R.layout.patientdetails_row, 
																concepts, 
																new String[] { ConceptNameTable.NAME.getName()}, 
																new int[] { R.id.patientdetails_concept });
		setListAdapter(patients);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(PatientTable.ID.getName(), mPatientID);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		conceptIntent(id);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDb.close();
	}
	
	/**
	 * Start ConceptHistory Intent
	 * @param id ID of Concept
	 */
	private void conceptIntent(long id) {
		Log.d(LOG_TAG, String.format("Starting intent for patient %d and concept %d.", mPatientID, id));
		Intent i = new Intent(this, ConceptHistory.class);
		Bundle extras = new Bundle();
		extras.putLong(PatientTable.ID.getName(), mPatientID); 
		extras.putLong(ConceptTable.ID.getName(), id);
		extras.putString(PatientDetailType.class.getName(), PatientDetailType.CONCEPT.toString());
		i.putExtras(extras);
		startActivityForResult(i, R.id.conceptmeasuretitle);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the patientdetails menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.patientdetails, menu);
        
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.patientdetails_encounter:
				encounterIntent();
				return true;
			case R.id.patientdetails_updates:
				updatesIntent();
				return true;
			default:
				break;
		}
		return false;
	}
	
	/**
	 * Starts intent for setting up encounter data.
	 */
	private void encounterIntent() {
		Intent i = new Intent(this, EncounterOptions.class); 
		Bundle extras = new Bundle();
		extras.putLong(PatientTable.ID.getName(), mPatientID);
		i.putExtras(extras);
		startActivityForResult(i, R.id.patientlist_sync);
	}
	
	/**
	 * Starts intent for syncing data.
	 */
	private void updatesIntent() {
		Log.d(LOG_TAG, "Starting intent for update list: Patient " + mPatientID);
		Intent i = new Intent(this, UpdateList.class); 
		Bundle extras = new Bundle();
		extras.putLong(PatientTable.ID.getName(), mPatientID);
		i.putExtras(extras);
		startActivityForResult(i, R.id.patientlist_sync);
	}
}
