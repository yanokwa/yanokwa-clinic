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
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.tables.ConceptNameTable;
import com.odkclinic.client.db.tables.ConceptTable;
import com.odkclinic.client.db.tables.PatientTable;
import com.odkclinic.client.utils.ConceptDatatypeHL7;

/**
 * Activity to enter a new measurement for a given concept id
 * 
 * @author Jessica Leung
 *
 */
public class ConceptMeasurement extends Activity{
	
	static String LOG_TAG = ConceptMeasurement.class.getName();
	private DbAdapter mDb;
	
	/*Activity parameters*/
	private Long mConceptID;
	private Long mPatientID;
	private ConceptDatatypeHL7 mConceptType;
	
	/* UI views */
	private TextView mTitleView;
	private Button mSaveButton;
	
	/*Input views*/
	private RadioGroup mRadioGroup;
	private DatePicker mDatePicker;
	private TimePicker mTimePicker;
	private EditText mEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conceptmeasurement);
		mDb = new DbAdapter(this);
      	mDb.open();
		recoverActivityParameters(savedInstanceState);
		associateViews();
		recoverActivityInputState(savedInstanceState);
        setupUIInput(mConceptType);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(PatientTable.ID.getName(), mPatientID);
		outState.putLong(ConceptTable.ID.getName(), mConceptID);
		outState.putString(ConceptDatatypeHL7.class.getName(), mConceptType.toString());
		//save appropriate input
		switch(mConceptType){
			case NUMERIC:
				outState.putString(ConceptDatatypeHL7.NUMERIC.toString(), mEditText.getText().toString());
				break;
			case BOOLEAN:
				outState.putInt(ConceptDatatypeHL7.BOOLEAN.toString(), mRadioGroup.getCheckedRadioButtonId());
				break;
			case TEXT:
				outState.putString(ConceptDatatypeHL7.TEXT.toString(), mEditText.getText().toString());
				break;
		}
		outState.putString(ConceptNameTable.NAME.getName(), 
							mTitleView.getText().toString().substring(getString(R.string.conceptmeasuretitle).length()));
	}
	/**
	 * Adjusts the UI to show appropriate controls according to the ConceptType
	 * 
	 * @param ConceptType
	 */
	protected void setupUIInput(ConceptDatatypeHL7 ConceptType){
		//Improvement: Dynamically generate inputs
		
		// Clear out existing inputs
        mEditText.setVisibility(View.GONE);
        mRadioGroup.setVisibility(View.GONE); 
        mDatePicker.setVisibility(View.GONE);
        mTimePicker .setVisibility(View.GONE);
        
		//add in the appropriate input
		switch(ConceptType){
			case NUMERIC:
				mEditText.setVisibility(View.VISIBLE);
				break;
			case BOOLEAN:
				mRadioGroup.setVisibility(View.VISIBLE);
				break;
			case TEXT:
				mEditText.setVisibility(View.VISIBLE);
				break;
		}


		
	}
	
	/**
	 * Checks weather the object is acceptable to place into database
	 * 
	 * @param input
	 * @param ConceptType
	 * @return
	 */
	protected boolean validateInput(Object input, ConceptDatatypeHL7 ConceptType){
		if(input == null){
			return false;
		}
		switch(mConceptType){
		case NUMERIC:
			return input.getClass() == Double.class;
		case BOOLEAN:
			return input.getClass() == Boolean.class;
		case TEXT:
			return input.getClass() == String.class;
		}	
		return false;
	}
	
	/**
	 * Saves the input into the database
	 * 
	 * @param input
	 * @param ConceptType
	 * @return
	 */
	protected boolean saveInput(Object input, ConceptDatatypeHL7 ConceptType){
		if(input == null){
			return false;
		}
		switch(mConceptType){
		case NUMERIC:
			mDb.newObservation(mConceptID, mPatientID, (Double) input);
			break;
		case BOOLEAN:
			mDb.newObservation(mConceptID, mPatientID, (Boolean) input);
			break;
		case TEXT:
			mDb.newObservation(mConceptID, mPatientID, (String) input);
			break;
		}	
		return false;
	}
	/**
	 * Get the input from the UI of the given type
	 * 
	 * @param ConceptType
	 * @return
	 */
	protected Object getInput(ConceptDatatypeHL7 ConceptType){
		Object input = null;
		switch(mConceptType){
		case NUMERIC:
			try{
				String value_num = mEditText.getText().toString();
				input = Double.valueOf(value_num);
			}catch(NumberFormatException e){
				// invalid input don't return value
			}
			break;
		case BOOLEAN:
			int value_bool = mRadioGroup.getCheckedRadioButtonId();
			if(value_bool == R.id.value_radio_true){
				input =  (Boolean)true;
			}
			if(value_bool == R.id.value_radio_false){
				input =  (Boolean)false;
			}
			break;
		case TEXT:
			String value_txt = mEditText.getText().toString();
			if(! value_txt.trim().equals("")){
				input = value_txt;
			}
			break;
		}	
		return input;
	}
	/**
	 * Recovers the state of input views
	 * 
	 * @param savedInstanceState
	 */
	protected void recoverActivityInputState(Bundle savedInstanceState) {
		//recover values of the input the user has submitted
		switch(mConceptType){
			case NUMERIC:
				String value_num = savedInstanceState != null ? savedInstanceState.getString(ConceptDatatypeHL7.NUMERIC.toString()) 
						: null;
				if(value_num != null){
					mEditText.setText(value_num);
				}
				break;
			case BOOLEAN:
				int value_bool = savedInstanceState != null ? savedInstanceState.getInt(ConceptDatatypeHL7.BOOLEAN.toString()) 
						: 0;
				if(value_bool != 0){
					mRadioGroup.check(value_bool);
				}else{
					//default otherwise
					mRadioGroup.check(R.id.value_radio_true);
				}
				
				break;
			case TEXT:
				String value_txt = savedInstanceState != null ? savedInstanceState.getString(ConceptDatatypeHL7.TEXT.toString()) 
						: null;
				if(value_txt != null){
					mEditText.setText(value_txt);
				}
				break;
		}
		
		//recover the name of the concept
		String title = savedInstanceState != null ? savedInstanceState.getString(ConceptNameTable.NAME.getName()) 
				: null;
		
		if(title == null){
			Cursor cursor = mDb.getConcept(mConceptID);
			startManagingCursor(cursor);
			cursor.moveToFirst();
			if(cursor.getCount()<1){
				Log.e(LOG_TAG, "Concept Details not found for concept "+String.valueOf(mConceptID));
				title = String.valueOf(null);
			}else{
				title = cursor.getString(cursor.getColumnIndex(ConceptNameTable.NAME.getName()));
			}
			
			if(title.equals(String.valueOf((Object)null))){
				// default for when the database fails
				title = "measurement";
			}
		}
		mTitleView.setText(getString(R.string.conceptmeasuretitle)+" "+title);
		
	}
	/**
	 * Recovers the values of Activity Parameters
	 * 
	 * @param savedInstanceState
	 */
	protected void recoverActivityParameters(Bundle savedInstanceState) {
		 //finds which concept is being measured
		mConceptID = savedInstanceState != null ? savedInstanceState.getLong(ConceptTable.ID.getName()) 
				: null;
        if (mConceptID == null) {
        	Bundle extras = getIntent().getExtras();            
        	mConceptID = extras != null ? extras.getLong(ConceptTable.ID.getName()) 
        			: null;
        }
        
        if (mConceptID == null) {
        	Log.e(LOG_TAG, "No concept id provided.");
        	setResult(RESULT_CANCELED);
        	finish();
        }
        
        //finds which patient is being measured
		mPatientID = savedInstanceState != null ? savedInstanceState.getLong(PatientTable.ID.getName()) 
				: null;
        if (mPatientID == null) {
        	Bundle extras = getIntent().getExtras();            
        	mPatientID = extras != null ? extras.getLong(PatientTable.ID.getName()) 
        			: null;
        }
        
        if (mPatientID == null) {
        	Log.e(LOG_TAG, "No patient id provided.");
        	setResult(RESULT_CANCELED);
        	finish();
        }
        
        //finds which ConceptDatatypeH17 is being measured
		String conceptType = savedInstanceState != null ? savedInstanceState.getString(ConceptDatatypeHL7.class.getName()) 
				: null;
        if (conceptType == null) {
        	Bundle extras = getIntent().getExtras();            
        	conceptType = extras != null ? extras.getString(ConceptDatatypeHL7.class.getName()) 
        			: null;
        }
        
        if (conceptType == null) {
        	Log.e(LOG_TAG, "No detail type provided.");
        	setResult(RESULT_CANCELED);
        	finish();
        }
        
        mConceptType = conceptType != null? ConceptDatatypeHL7.valueOf(conceptType)
        		: null;
	}
	/**
	 * Associates local view variables with their UI counterparts	
	 */
	protected void associateViews(){
	     //associate views
        mTitleView = (TextView) this.findViewById(R.id.conceptmeasuretitle);
        mSaveButton = (Button) this.findViewById(R.id.savebutton);
        mSaveButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		//save observation
        		Object input = getInput(mConceptType);        		
        		if(validateInput(input, mConceptType)){
        			//everything is good, send away
        			saveInput(input,mConceptType);
	        		setResult(RESULT_OK);
	        		Log.d(LOG_TAG, "saved observation "+input.toString());
	        		finish();
        		}else{
        			// let the user know the value is invalid
        			Log.w(LOG_TAG, "input invalid");
        			Toast.makeText(getApplicationContext(), 
        							getString(R.string.invalid_input), 
        							Toast.LENGTH_LONG).show();
        		}
        	}
        });
        mEditText = (EditText)this.findViewById(R.id.value_text);
        mRadioGroup = (RadioGroup) this.findViewById(R.id.value_radio);
        mDatePicker = (DatePicker) this.findViewById(R.id.value_date);
        mTimePicker = (TimePicker) this.findViewById(R.id.value_time);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDb.close();
	}
}
