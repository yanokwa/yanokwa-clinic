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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.DbConstants;
import com.odkclinic.client.db.tables.PatientTable;

/**
 * Record encounter with a note
 * 
 * @author Jessica Leung
 */

public class EncounterNote extends Activity {
	private final static String NOTE_LABEL = "note_label";
	private static String LOG_TAG = EncounterNote.class.getName();
	
	private Long mPatientID;
	 /** UI views*/
	Button mSaveButton;
	EditText mNoteText;
	TextView mTitle;
	private DbAdapter mDb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encounternote);
		
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

        
        mNoteText = (EditText) this.findViewById(R.id.encounter_note);
        
        //recovers saved value of note
        String noteText = savedInstanceState != null ? savedInstanceState.getString(NOTE_LABEL) 
				: "";
        mNoteText.setText(noteText);
        
        
        mTitle = (TextView) this.findViewById(R.id.encounternotetitle);
        
        
        mSaveButton = (Button) this.findViewById(R.id.savebutton);
        mSaveButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		//save note
        		mDb.newObservation(DbConstants.NOTES_CONCEPT_ID, mPatientID, mNoteText.getText().toString());
        		
        		// assume everything is good (later feed back database 
        		// results into status)
        		setResult(RESULT_OK);
        		Log.d(LOG_TAG, "saved note");
        		finish();
        	}
     });

        

        
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
		outState.putString(NOTE_LABEL, mNoteText.getText().toString());
	}
	
}
