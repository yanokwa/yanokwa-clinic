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

package com.odkclinic.client.db.tables;

public enum CohortMemberTable {
	COHORT_ID("cohortmember_cohort_id", CohortTable.FKEY_RELATIONSHIP),
	PATIENT_ID("cohortmember_patient_id", PatientTable.FKEY_RELATIONSHIP),
	PKEY("PRIMARY KEY(cohortmember_cohort_id, cohortmember_patient_id)", "");
	
	public static String TABLE_NAME = "cohort_member";
	public static String TABLE_ACRONYM = "cm"; //for queries
	
	private String COLUMN_NAME;
	private String COLUMN_TYPE;
	
	public static String[] COLUMNS = new String[CohortMemberTable.values().length];
	public static String[] COLUMNS_TYPE = new String[CohortMemberTable.values().length];
	
	private CohortMemberTable(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}
	
	static {
		for (int c = 0; c < CohortMemberTable.values().length; c++) {
			COLUMNS[c] =CohortMemberTable.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < CohortMemberTable.values().length; c++) {
			COLUMNS_TYPE[c] =CohortMemberTable.values()[c].COLUMN_TYPE;
		}
	}

	public String getType() {
		return COLUMN_TYPE;
	}

	public String getName() {
		return COLUMN_NAME;
	}
}
