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
public enum ObservationTable {
	ID("obs_id", "INTEGER PRIMARY KEY autoincrement"),
	PATIENT_ID("observation_patient_id", PatientTable.FKEY_RELATIONSHIP),
	CONCEPT_ID("observation_concept_id", ConceptTable.FKEY_RELATIONSHIP),
	ENCOUNTER_ID("observation_encounter_id", "INTEGER"),
	TEXT("value_text", "text NULL"),
	DATETIME("value_datetime", "DATETIME DEFAULT(CURRENT_TIMESTAMP)"),
	NUMERIC("value_numeric", "DOUBLE NULL"),
	BOOLEAN("value_boolean", "bit NULL"),
	CREATOR("creator", "INTEGER NOT NULL"),
	DATE_CREATED("observation_date_created", "DATETIME DEFAULT (CURRENT_TIMESTAMP)"),
	VOIDED("voided", "bit NOT NULL"),
	ISUPDATE("isUpdate", "bit DEFAULT 1");
	
	public static String TABLE_NAME = "observation";
	public static String TABLE_ACRONYM = "o"; //for queries
	public static String FKEY_RELATIONSHIP = "INTEGER REFERENCES " + TABLE_NAME + "(" + ID.getName() + ") ON DELETE CASCADE";
	
	private String COLUMN_NAME;
	private String COLUMN_TYPE;
	
	public static String[] COLUMNS = new String[ObservationTable.values().length];
	public static String[] COLUMNS_TYPE = new String[ObservationTable.values().length];
	
	private ObservationTable(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}
	
	static {
		for (int c = 0; c < ObservationTable.values().length; c++) {
			COLUMNS[c] =ObservationTable.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < ObservationTable.values().length; c++) {
			COLUMNS_TYPE[c] =ObservationTable.values()[c].COLUMN_TYPE;
		}
	}

	public String type() {
		return COLUMN_TYPE;
	}

	public String getName() {
		return COLUMN_NAME;
	}
}
