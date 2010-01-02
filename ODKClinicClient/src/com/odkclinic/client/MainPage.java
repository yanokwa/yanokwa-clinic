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
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * Activity for displaying choice between managing cohorts and patients or viewing
 * list of patients.
 *  
 * @author Euzel Villanueva
 *
 */
public class MainPage extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		Bundle extras = new Bundle();
//		extras.putLong(DbConstants.CONCEPT_ID, (long)11110);
//		extras.putLong(DbConstants.PATIENT_ID, (long)11011);
//		extras.putString(PatientDetailType.class.getName(), PatientDetailType.CONCEPT.toString());
//		Intent i = new Intent(this, ConceptHistory.class);
//		i.putExtras(extras);
//		startActivity(i);
		
		Intent i = new Intent(this, PatientList.class);
		startActivityForResult(i,0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}

	
	/**
	 * 
	 * Class for recieving data from the server.
	 *
	 */
	private class GetDataTask extends AsyncTask<Long, String, Long> {

		@Override
		protected Long doInBackground(Long... params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
		}
	}
	
	/**
	 * Class for sending data back to server.
	 *
	 */
	private class SendDataTask extends AsyncTask<Long, String, Long> {

		@Override
		protected Long doInBackground(Long... params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
		}
	}
}
