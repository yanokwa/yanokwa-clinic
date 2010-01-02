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

/**
 * @author Euzel Villanueva
 *
 */
public enum UpdatesTable {
	ID("_id", "INTEGER PRIMARY KEY autoincrement"), //change
	PATIENT_ID("updates_patient_id", PatientTable.FKEY_RELATIONSHIP),
	NAME("update_name", "VARCHAR(255)"),
	DESC("update_description", "VARCHAR(255)"),
	DATE("pdate_date", "DATETIME DEFAULT(CURRENT_TIMESTAMP)"),
	ENCOUNTER_ID("updates_encounter_id", "integer"),
	OBSERVATION_ID("updates_obs_id", "integer");
	
	public static String TABLE_NAME = "updates";
	public static String TABLE_ACRONYM = "u"; //for queries
	
	private String COLUMN_NAME;
	private String COLUMN_TYPE;
	
	public static String[] COLUMNS = new String[UpdatesTable.values().length];
	public static String[] COLUMNS_TYPE = new String[UpdatesTable.values().length];
	
	private UpdatesTable(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}
	
	static {
		for (int c = 0; c < UpdatesTable.values().length; c++) {
			COLUMNS[c] =UpdatesTable.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < UpdatesTable.values().length; c++) {
			COLUMNS_TYPE[c] =UpdatesTable.values()[c].COLUMN_TYPE;
		}
	}

	public String getType() {
		return COLUMN_TYPE;
	}

	public String getName() {
		return COLUMN_NAME;
	}
}
