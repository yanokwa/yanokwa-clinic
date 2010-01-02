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

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.tables.CohortMemberTable;
import com.odkclinic.client.db.tables.PatientTable;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patientlist_list);

		// instantiate db instance and open
		mDb = new DbAdapter(this);
		mDb.open();
		fillData();
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
		case R.id.patientlist_sync:
			
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
		// TODO Auto-generated method stub
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
			//TODO: Networking stuff
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
}
