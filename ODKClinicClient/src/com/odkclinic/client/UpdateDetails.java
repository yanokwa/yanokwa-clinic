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
import android.widget.TextView;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.tables.UpdatesTable;

/**
 * Displays details of an update left to be synced 
 * 
 * @author Jessica Leung
 *
 */
public class UpdateDetails extends Activity {
	
	static String LOG_TAG = UpdateDetails.class.getName();
	
	private DbAdapter mDb;
	private Cursor mCursor;
	private Long mUpdateID;
	
	private TextView mName;
	private TextView mDetails;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upatedetails);
        //finds which update is being viewed
		mUpdateID = savedInstanceState != null ? savedInstanceState.getLong(UpdatesTable.ID.getName()) 
				: null;
        if (mUpdateID == null) {
        	Bundle extras = getIntent().getExtras();            
        	mUpdateID = extras != null ? extras.getLong(UpdatesTable.ID.getName()) 
        			: null;
        }
        
        if (mUpdateID == null) {
        	//there is no if provided, record an error and exit
        	Log.e(LOG_TAG, "No update id provided.");
        	finish();
        }
		
		mDb = new DbAdapter(this);
		mDb.open();
		
        // Get a cursor with the desired update
		mCursor = mDb.getUpdateDetails(mUpdateID);
		
		//BUGFIX
		startManagingCursor(mCursor);
		mCursor.moveToFirst();
		
		//set fields
		//TODO: add more details when they are available
		mName = (TextView) findViewById(R.id.updatename);
		mName.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(UpdatesTable.NAME.getName())));
		
		mDetails = (TextView) findViewById(R.id.updatedetails);
		mDetails.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(UpdatesTable.DATE.getName())));
	}

	@Override
	protected void onDestroy() {	
		super.onDestroy();
		mDb.close();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(UpdatesTable.ID.getName(), mUpdateID);
	}


}
