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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.tables.PatientTable;

/**
 * Activity that provides options for types of encounter measurements
 * 
 * @author Jessica Leung
 *
 */
public class EncounterOptions extends Activity {

	private static String LOG_TAG = EncounterOptions.class.getName();
	
	private static int ACTIVITY_NOTE = 1;
	private static int ACTIVITY_FULL = 2;
	
	 /** UI Buttons*/
	private Button visitedButton;
	private Button visitedNoteButton;
	private Button visitedFullButton;
	
	private Long mPatientID;
	private DbAdapter mDb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encounteroptions);
		
        mDb = new DbAdapter(this);
        mDb.open();
		
        //finds which patient is being encountered
		mPatientID = savedInstanceState != null ? savedInstanceState.getLong(PatientTable.ID.getName()) 
				: null;
        if (mPatientID == null) {
        	Bundle extras = getIntent().getExtras();            
        	mPatientID = extras != null ? extras.getLong(PatientTable.ID.getName()) 
        			: null;
        }
        
        if (mPatientID == null) {
        	//there is no if provided, record an error and exit
        	Log.e(LOG_TAG, "No patient id provided.");
        	finish();
        }

        
		//
		visitedButton = (Button) this.findViewById(R.id.encounter_visited);
		visitedButton.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v){
	        		// mark the patient visited
	        		mDb.markPatientVisited(mPatientID);
	        		
	        		Log.d(LOG_TAG, "Patient "+mPatientID+" marked visited");
	        		setResult(RESULT_OK);
	        		finish();
	        	}
	     });

		visitedNoteButton = (Button) this.findViewById(R.id.encounter_visitednote);
		visitedNoteButton.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v){
	        		//fire off intent for note
	        		Bundle extras = new Bundle();
	        		extras.putLong(PatientTable.ID.getName(), mPatientID);
	        		Intent i = new Intent(v.getContext(), EncounterNote.class);
	        		i.putExtras(extras);
	        		
	        		startActivityForResult(i, ACTIVITY_NOTE);
	        		Log.d(LOG_TAG, "Fired intent for note encounter");
	        	}
	     });
		
		visitedFullButton = (Button) this.findViewById(R.id.encounter_full);
		visitedFullButton.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v){
	        		//TODO: link in the ODKClinic - forms here
	        		//fire off intent for full encounter
/*	       		Intent i = new Intent();
*	        		startActivityForResult(i, ACTIVITY_FULL);
	        		Log.d(LOG_TAG, "Fired intent for full encounter");
*/	        	}
	     });
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (resultCode){
			case RESULT_CANCELED:
				Log.i(LOG_TAG, "Fired off encounter did not suceed");
				setResult(RESULT_CANCELED);
				break;
			default:
        		// mark the patient visited
        		mDb.markPatientVisited(mPatientID);
        		Log.d(LOG_TAG, "Patient "+mPatientID+" marked visited");
        		setResult(RESULT_OK);
        		break;
		}
		
		finish();
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDb.close();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(PatientTable.ID.getName(), mPatientID);
	}

}
