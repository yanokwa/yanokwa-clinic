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
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.tables.PatientTable;
import com.odkclinic.client.db.tables.UpdatesTable;

/**
 * Displays a list of updates yet to be synced back to the server
 * 
 * @author Jessica Leung
 *
 */
public class UpdateList extends ListActivity {
	
	private DbAdapter mDb;
	private Cursor mCursor;
	private Long mPatientID;
	private TextView mTitle;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatelist_list);
        
        mDb = new DbAdapter(this);
		mDb.open();
		
        //finds which update is being viewed ()
		mPatientID = savedInstanceState != null ? savedInstanceState.getLong(PatientTable.ID.getName()) 
				: null;
        if (mPatientID == null) {
        	Bundle extras = getIntent().getExtras();            
        	mPatientID = extras != null ? extras.getLong(PatientTable.ID.getName()) 
        			: null;
        }
		
        mTitle = (TextView) findViewById(R.id.updatelist_name);
        
        // Get a cursor with all updates
        if(mPatientID == null){
        	mTitle.setText("All Updates");
        	mCursor = mDb.getUpdateList();
        	
        }else{
        	mTitle.setText("Patient Updates");
        	mCursor = mDb.getUpdateList(mPatientID);
        }
		
        startManagingCursor(mCursor);
        ListAdapter adapter = new SimpleCursorAdapter(this, 
                R.layout.updatelist_group_item, 
                mCursor, 
                new String[] {UpdatesTable.NAME.getName(), UpdatesTable.DATE.getName()} ,
                new int[] {R.id.updatename,R.id.updatedetails}); 
        setListAdapter(adapter);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(v.getContext(),UpdateDetails.class);
		
		// give an update id to the details page
		i.putExtra(UpdatesTable.ID.getName(), id);
		
		// go to details page
		startActivity(i);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDb.close();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mPatientID != null){
			outState.putLong(PatientTable.ID.getName(), mPatientID);
		}
	}
	
}
