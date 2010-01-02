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
 
/**
 *  Displays details for given:
 *   concept id
 *   patient id
 *   observation number (defaults to first)
 * 
 * @author: Jessica Leung
 */
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.tables.ConceptTable;
import com.odkclinic.client.db.tables.ObservationTable;
import com.odkclinic.client.db.tables.PatientTable;
import com.odkclinic.client.utils.ConceptDatatypeHL7;

public class ObservationDetails extends Activity {
	static String LOG_TAG = ObservationDetails.class.getName();
	static String KEY_OBS_POS = "observation position";
	
	private DbAdapter mDb;
	//private Cursor mCursor;
	private Long mConceptID;
	private Long mPatientID;
	private Integer mObservationPosition;
	private ConceptDatatypeHL7 mConceptType;
	
	
	private TextView mName;
	private TextView mDetails;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.observationdetails);
		  //finds which concept is being viewed
		mConceptID = savedInstanceState != null ? savedInstanceState.getLong(ConceptTable.ID.getName()) 
				: null;
        if (mConceptID == null) {
        	Bundle extras = getIntent().getExtras();            
        	mConceptID = extras != null ? extras.getLong(ConceptTable.ID.getName()) 
        			: null;
        }
        
        if (mConceptID == null) {
        	//there is no if provided, record an error and exit
        	Log.e(LOG_TAG, "No concept id provided.");
        	finish();
        }
        
        //finds which patient is being viewed
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
        
        //finds which position of the cursor is being viewed
        mObservationPosition = savedInstanceState != null ? savedInstanceState.getInt(KEY_OBS_POS,-1) 
				: -1;
        if (mObservationPosition == -1) {
        	Bundle extras = getIntent().getExtras();            
        	mObservationPosition = extras != null ? extras.getInt(KEY_OBS_POS,-1)  
        			: -1;
        }
        
        if (mObservationPosition == -1) {
        	//use default
        	mObservationPosition = 0;
        }
		
		
        //finds which ConceptDatatypeHL7 is being viewed
		String ConceptType = savedInstanceState != null ? savedInstanceState.getString(ConceptDatatypeHL7.class.getName()) 
				: null;
        if (ConceptType == null) {
        	Bundle extras = getIntent().getExtras();            
        	ConceptType = extras != null ? extras.getString(ConceptDatatypeHL7.class.getName()) 
        			: null;
        }
        
        if (ConceptType == null) {
        	//there is no if provided, record an error and exit
        	Log.e(LOG_TAG, "No ConceptDatatypeHL7 provided.");
        	finish();
        }
        
        mConceptType = ConceptType != null? ConceptDatatypeHL7.valueOf(ConceptType)
        		: null;
        
		
		//set fields
		mName = (TextView)this.findViewById(R.id.observationdestails_name);	
		mDetails = (TextView)this.findViewById(R.id.observationdetails_details);
		
		
		fillData();
	}

	private void fillData(){
		mDb = new DbAdapter(this);
		mDb.open();
		
		Cursor c = mDb.getConceptHistory(mConceptID, mPatientID);
    	startManagingCursor(c);
    	if(!c.moveToPosition(mObservationPosition)){
			finish();
			return;
    	}
    	

    	String value = "";
    	if(!c.isAfterLast()){
    		//fill in the value
    		switch(mConceptType){
	    		case NUMERIC:
	    			value = c.getString(c.getColumnIndexOrThrow(ObservationTable.NUMERIC.getName()));
	    			break;
	    		case BOOLEAN:
	    			value = c.getString(c.getColumnIndexOrThrow(ObservationTable.BOOLEAN.getName()));
	    			break;
	    		case TEXT:
	    			value = c.getString(c.getColumnIndexOrThrow(ObservationTable.TEXT.getName()));
	    			break;
    		}   		
    	}

    	mDetails.setText(value);
    	mName.setText(c.getString(c.getColumnIndexOrThrow(ObservationTable.DATE_CREATED.getName())));
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
		outState.putLong(ConceptTable.ID.getName(), mConceptID);
		outState.putInt(KEY_OBS_POS, mObservationPosition);
		outState.putString(ConceptDatatypeHL7.class.getName(), mConceptType.toString());
	}

}
