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
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.odkclinic.client.db.DbAdapter;
import com.odkclinic.client.db.DbConstants;
import com.odkclinic.client.db.tables.CohortTable;
import com.odkclinic.client.db.tables.PatientTable;
import com.odkclinic.client.db.tables.VisitedTable;

/**
 * Activity that displays and allows modification to list of patients.
 * 
 * @author Euzel Villanueva
 * 
 */
public class PatientManagerList extends ListActivity {
	private static String LOG_TAG = PatientManagerList.class.getName();
	
	private DbAdapter mDb;
	private boolean getCohort;
	private int cohortId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patientmanager_list);
		mDb = new DbAdapter(this);
		mDb.open();
		cohortId = -1;
		getCohort = false;
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("getCohort"))
				getCohort = savedInstanceState.getBoolean("getCohort");
			if (savedInstanceState.containsKey("cohortId")) 
				cohortId = savedInstanceState.getInt("cohortId");
		}
		
		fillData();
		final ListView listView = new ListView(this);

		listView.setItemsCanFocus(true);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (mDb.isChecked(id)) {
			l.setItemChecked(position, false);
			((CheckedTextView) v).setChecked(false);
			mDb.setPatientCheck(0, (int) id);
		} else {
			l.setItemChecked(position, true);
			((CheckedTextView) v).setChecked(true);
			mDb.setPatientCheck(1, (int) id);
		}
	}


	private class TextViewBinder implements SimpleCursorAdapter.ViewBinder {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			startManagingCursor(cursor);
			String name = cursor.getString(cursor.getColumnIndex(PatientTable.NAME.getName()));
			String beenVisited = cursor.getString(cursor.getColumnIndex(VisitedTable.BEEN_VISITED.getName()));
			int updates = cursor.getInt(cursor.getColumnIndex(DbConstants.UPDATE_COUNT));
			int patientId = cursor.getInt(cursor.getColumnIndex(PatientTable.ID.getName()));
			int isChecked = cursor.getInt(cursor.getColumnIndex(PatientTable.CHECKED.getName()));
			
			String out = String.format("%s\n\t\tPatientID: %d\n\t\t# of Updates: %d\n\t\t%s",
										name, patientId, updates, beenVisited);
			((CheckedTextView) view).setText(out);
			((CheckedTextView) view).setChecked(isChecked != 0); 
			return true;
		}
	}
	/**
	 * Fills in the list with patient data.
	 * 
	 * Based on the global variable getCohort will use get all patients if set to false or 
	 * just the patients of cohort id which is set by global variable cohortId
	 * 
	 */
	private void fillData() {
		Log.d(LOG_TAG, "Filling up listview with patient data.");
		Cursor patientCursor;
		if (!getCohort)
			patientCursor = mDb.getPatientManagerList();
		else 
			patientCursor = mDb.getPatientsByCohort(cohortId);
		startManagingCursor(patientCursor);

		String[] from = new String[] { PatientTable.NAME.getName() };

		int[] to = new int[] { R.id.patientmanager_name};

		SimpleCursorAdapter patients = new SimpleCursorAdapter(this,
																R.layout.patientmanager_row, 
																patientCursor, 
																from,
																to);
		patients.setViewBinder(new TextViewBinder());
		setListAdapter(patients);
	}
	
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("cohortId", cohortId);
		outState.putBoolean("getCohort", getCohort);
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
        inflater.inflate(R.menu.patientmanagerlist, menu);
        Cursor cohortCursor = mDb.getCohortIds();
        startManagingCursor(cohortCursor);
        if (cohortCursor.moveToFirst()) {
        	Log.d(LOG_TAG, "Adding submenu item.");
			SubMenu cohort = menu.addSubMenu("Filter By Cohort");
			cohort.add("All Cohorts");
			do{
				int id = cohortCursor.getInt(cohortCursor.getColumnIndexOrThrow(CohortTable.ID.getName()));
				cohort.add("Cohort " + id);
				cohortCursor.moveToNext();
			} while(!cohortCursor.isAfterLast());
		} 
       
        return true;
    }
	
	private void deletePatients() {
		Cursor cursor = mDb.getCheckedPatientIds();
		startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do{
				int id = cursor.getInt(0);
				mDb.deletePatient(id);
				cursor.moveToNext();
			} while(!cursor.isAfterLast());
		}
		fillData();
	}
	
	/**
	 * Selects all patients without updates ie. # of Updates = 0
	 */
	private void selectPatientsWoUpdates() {
		Cursor cursor = mDb.getPatientsWoUpdates();
		startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do{
				int id = cursor.getInt(0);
				mDb.setPatientCheck(1, id);
				cursor.moveToNext();
			} while(!cursor.isAfterLast());
		}
		fillData();
	}	
	
	/**
	 * Selects all patients
	 */
	private void selectAllPatients() {
		Cursor cursor = mDb.getPatientIds();
		startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do{
				int id = cursor.getInt(0);
				mDb.setPatientCheck(1, id);
				cursor.moveToNext();
			} while(!cursor.isAfterLast());
		}
		fillData();
	}
	
	/**
	 * Deselects all patients.
	 */
	private void selectNone() {
		Cursor cursor = mDb.getCheckedPatientIds();
		startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do{
				int id = cursor.getInt(0);
				mDb.setPatientCheck(0, id);
				cursor.moveToNext();
			} while(!cursor.isAfterLast());
		}
		fillData();
	}
	
	/**
	 * Sets up filldata such that it will display patients based on cohort id.
	 * @param id Cohort ID that is to be displayed. 
	 */
	private void selectCohortId(int id) {
		getCohort = true;
		cohortId = id;
		fillData();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!item.hasSubMenu()) {
			switch (item.getItemId()) {
				case R.id.patientmanagerlist_delete:
					deletePatients();
					return true;
				case R.id.patientmanagerlist_woupdate:
					selectPatientsWoUpdates();
					return true;
				case R.id.patientmanagerlist_none:
					selectNone();
					fillData();
					return true;
				case R.id.patientmanagerlist_all:
					selectAllPatients();
					return true;
				default:
					Log.d(LOG_TAG, item.getTitle().toString());
					String[] cohort = item.getTitle().toString().split(" ");
					if (cohort.length == 2) {
						if (!cohort[1].equals("Cohorts"))
							selectCohortId(Integer.parseInt(cohort[1]));
						else {
							getCohort = false;
							cohortId = -1;
							fillData();
						}
					}
					return true;
			}
		}
		return true;
	}
	
}
