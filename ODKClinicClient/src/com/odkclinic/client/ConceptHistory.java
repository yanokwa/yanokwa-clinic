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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;

import android.R.color;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.tables.ConceptDataTypeTable;
import com.odkclinic.client.db.tables.ConceptNameTable;
import com.odkclinic.client.db.tables.ConceptTable;
import com.odkclinic.client.db.tables.ObservationTable;
import com.odkclinic.client.db.tables.PatientTable;
import com.odkclinic.client.graph.FlotGraphHandler;
import com.odkclinic.client.graph.OnGraphRenderedListener;
import com.odkclinic.client.utils.ConceptDatatypeHL7;
import com.odkclinic.client.utils.PatientDetailType;

public class ConceptHistory extends Activity implements com.odkclinic.client.graph.OnClickListener, OnGraphRenderedListener{
	
	static String LOG_TAG = ConceptHistory.class.getName();
	
	private DbAdapter mDb;
	
	private Long mConceptID;
	private Long mPatientID;
	private PatientDetailType mDetailType;
	private ConceptDatatypeHL7 mConceptType;
	
	/*Intent Constants*/
	private static int ACTIVITY_NEW_ENCOUNTER = 1;
	private static int ACTIVITY_NEW_CONCEPT = 2;
	
	/*Dialogs*/
	private static int DIALOG_IN_PROGRESS = 1;

	/*UI views*/
	Button mNewButton;
	WebView mWebView;
	TextView mTitleView;
	TextView mEmptyView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.concepthistory);
		mDb = new DbAdapter(this);
		mDb.open();
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
		
        //finds which PatientDetailType is being viewed
		String detailType = savedInstanceState != null ? savedInstanceState.getString(PatientDetailType.class.getName()) 
				: null;
        if (detailType == null) {
        	Bundle extras = getIntent().getExtras();            
        	detailType = extras != null ? extras.getString(PatientDetailType.class.getName()) 
        			: null;
        }
        
        if (detailType == null) {
        	//there is no if provided, record an error and exit
        	Log.e(LOG_TAG, "No detail type provided.");
        	finish();
        }
        
        mDetailType = detailType != null? PatientDetailType.valueOf(detailType)
        		: null;
        
        if(mDetailType != PatientDetailType.CONCEPT){
        	//Currently no support for history displays of anything other then concepts
        	Log.e(LOG_TAG, "No history support for detailtype" + String.valueOf(mDetailType));
        	finish();      	
        }
        
        //finds which ConceptDatatypeH17 is being measured if saved
       	Bundle extras = getIntent().getExtras();            
       	String conceptType = extras != null ? extras.getString(ConceptDatatypeHL7.class.getName()) 
        			: null;

        mConceptType = conceptType != null? ConceptDatatypeHL7.valueOf(conceptType)
        		: null;
        
        if(mConceptType == null){
        	Cursor c = mDb.getConcept(mConceptID);
        	startManagingCursor(c);
        	if(c.moveToFirst()){
        		String dbConceptType = c.getString(c.getColumnIndexOrThrow(ConceptDataTypeTable.HL7.getName()));
        		if( ! dbConceptType.equals(String.valueOf((Object)null))){
        			//TODO: find the native function that does this
        			for(ConceptDatatypeHL7 hl7 :ConceptDatatypeHL7.values()){
        				if(hl7.getH17().equals(dbConceptType)){
        					mConceptType = hl7;
        				}
        			}
        			
        		}
        	}
        	
        	
        }
        
        // if not type is to be found, pretend it is text
        if(mConceptType == null){
        	mConceptType = ConceptDatatypeHL7.TEXT;
        }
        
        
        //grab the concept name
        String concept_name = "";
		Cursor c = mDb.getConcept(mConceptID);
    	startManagingCursor(c);
    	if(c.moveToFirst()){
    		concept_name = c.getString(c.getColumnIndexOrThrow(ConceptNameTable.NAME.getName()));
    		if(concept_name.equals(String.valueOf((Object)null))){
    			concept_name="";
    		}
    	}
        
        // initiate UI views
        mNewButton = (Button) this.findViewById(R.id.newmeasurementbutton);
        //TODO: put this in a resource
        mNewButton.setText("New "+concept_name);
        mNewButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		//create bundle for intent
        		Bundle extras = new Bundle();
        		extras.putLong(PatientTable.ID.getName(), mPatientID);
        		extras.putLong(ConceptTable.ID.getName(), mConceptID);
        		extras.putString(ConceptDatatypeHL7.class.getName(), mConceptType.toString());
        		
        		Intent i;
        		switch(mDetailType){
        			case CONCEPT:
                		i = new Intent(v.getContext(), ConceptMeasurement.class);
                		i.putExtras(extras);
                		startActivityForResult(i, ACTIVITY_NEW_CONCEPT);
        				break;
        			case ENCOUNTER:
                		i = new Intent(v.getContext(), EncounterOptions.class);
                		i.putExtras(extras);
                		startActivityForResult(i, ACTIVITY_NEW_ENCOUNTER);
        				break;
        			default:
        				break;
        		}

  
        	}});
        

        mTitleView= (TextView) this.findViewById(R.id.concepthistory_name);
        mTitleView.setText(concept_name);
        
        mEmptyView = (TextView) this.findViewById(R.id.concepthistory_empty);
        
        //set up graph
        mWebView = (WebView) this.findViewById(R.id.graphview);
       // mWebView.setBackgroundColor(R.color.White);
        fillData();
        Log.i(LOG_TAG,"Displaying historical observations for patient "+mPatientID+", concept "+mConceptID +" of type "+mConceptType);

	}
	
	protected void fillData(){
		//reset views
		mWebView.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.GONE);
		//TODO: better check on wether there is data
		Cursor c = mDb.getConceptHistory(mConceptID, mPatientID);
    	startManagingCursor(c);
        if(c.moveToFirst()){
        	mWebView.setVisibility(View.VISIBLE);
        	renderGraph();
        }else{
        	mEmptyView.setVisibility(View.VISIBLE);
        }
	}
	

	protected void renderGraph(){
		//graphing is intensive
		// let the user know things are in progress
		showDialog(DIALOG_IN_PROGRESS);
		// determine type of graph needed
		FlotGraphHandler.MODE graphMode = null;
		switch(mConceptType){
			case NUMERIC:
				graphMode = FlotGraphHandler.MODE.LINE_GRAPH;
				break;
			case BOOLEAN:
				graphMode = FlotGraphHandler.MODE.BOOLEAN_GRAPH;
				break;
			default:
				graphMode = FlotGraphHandler.MODE.TIMELINE;
				break;
		}
		
		//initialize handler with data
		FlotGraphHandler mGraphHandler = new FlotGraphHandler (mWebView, graphMode, getGraphData());
		
		//set up interactions with handler
        mGraphHandler.setOnClickListener(this);
        mGraphHandler.setOnGraphRenderedListener(this);
        
        //initialize web view
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(mGraphHandler, "mGraphHandler");
//        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.loadUrl("file:///android_asset/flot/html/linegraph.html");	
        
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDb.close();
	}
	
	/**
	 * Gathers and formats a JSONArray of datapoints for flot graph
	 * @return
	 */
	private JSONArray getGraphData(){
		JSONArray graphData = null;
		Cursor c = mDb.getConceptHistory(mConceptID, mPatientID);
    	startManagingCursor(c);
    	if(!c.moveToFirst()){
			try{
				graphData = new JSONArray("[]");
				
			} catch (Exception ex) {
				Log.e(LOG_TAG, ex.getMessage());
			}
			return graphData;
    	}
    	
    	String dataString = "[";
    	while(!c.isAfterLast()){
    		dataString += "[";
    		//fill in the date
    		long date;
    		String dateString = c.getString(c.getColumnIndexOrThrow(ObservationTable.DATE_CREATED.getName()));
    		try{
    		// parse in special format for new dates in system
    		date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString).getTime();
    		}catch(ParseException e){
    			Log.e(LOG_TAG, e.getMessage(), e);
    			finish();
    			return null;
    		}
    		
    		dataString += date+",";
    		
    		//fill in the value
    		switch(mConceptType){
	    		case NUMERIC:
	    			dataString += c.getString(c.getColumnIndexOrThrow(ObservationTable.NUMERIC.getName()));
	    			break;
	    		case BOOLEAN:
	    			//TODO: figure where the booleans are stored and in what format
	    			dataString += c.getString(c.getColumnIndexOrThrow(ObservationTable.BOOLEAN.getName()));
	    			break;
	    		case TEXT:
	    			// a text value was recorded
	    			dataString += "1";
	    			break;
    		}   		
    		dataString +="]";
    		c.moveToNext();
    		
    		if(!c.isAfterLast()){
    			dataString +=",";
    		}
    	}
    	dataString +="]";
    	
		try{
			Log.i(LOG_TAG,"graph data:"+String.valueOf((Object)dataString));
			graphData = new JSONArray(dataString); //[[date,value],[date,value],[date,value]]
		} catch (Exception ex) {
			Log.e(LOG_TAG, ex.getMessage());
		}

		
		return graphData;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(PatientTable.ID.getName(), mPatientID);
		outState.putLong(ConceptTable.ID.getName(), mConceptID);
		outState.putString(PatientDetailType.class.getName(), mDetailType.toString());
		outState.putString(ConceptDatatypeHL7.class.getName(), mConceptType.toString());
	}
	
	/**
	 * onclick for a click on flot graph
	 * 
	 * @param position
	 */
	public void onClick(int position) {
		
		Log.i(LOG_TAG, "click registered on graph for position "+position);
		
		if(mConceptType != ConceptDatatypeHL7.NUMERIC){
			//fire intent off for a details page
			Bundle extras = new Bundle();
			extras.putLong(PatientTable.ID.getName(), mPatientID);
			extras.putLong(ConceptTable.ID.getName(), mConceptID);
			extras.putString(ConceptDatatypeHL7.class.getName(), mConceptType.toString());
			extras.putInt(ObservationDetails.KEY_OBS_POS, position);
			Intent i = new Intent(this.getApplicationContext(), ObservationDetails.class);
			i.putExtras(extras);
			startActivityForResult(i, ACTIVITY_NEW_CONCEPT);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// make sure graph data is fresh with re-rendered data
		if (resultCode !=  RESULT_CANCELED)
			fillData();
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		if(id != DIALOG_IN_PROGRESS){
			return null;
		}
		
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setMessage("Graph Rendering. Please wait...");
		return dialog;
	}

	public void onGraphRendered(){
		dismissDialog(DIALOG_IN_PROGRESS);
	}
}
