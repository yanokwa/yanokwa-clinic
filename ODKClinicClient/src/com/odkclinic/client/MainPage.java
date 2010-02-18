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

import com.odkclinic.client.db.DbAdapter;

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
		DbAdapter db = new DbAdapter(this);
		db.open();
		db.markSync(false);
		db.close();
		Intent i = new Intent(this, PatientList.class);
		startActivityForResult(i,0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}
}
