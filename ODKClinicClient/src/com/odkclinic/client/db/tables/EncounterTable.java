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
public enum EncounterTable {
	ID("encounter_id", "INTEGER PRIMARY KEY"),
	TYPE_NAME("encounter_type_name", "VARCHAR(50) NULL"),
	TYPE_DESC("encounter_type_desc", "VARCHAR(50) NULL"),
	PATIENT_ID("encounter_patient_id", PatientTable.FKEY_RELATIONSHIP),
	PROVIDER_ID("encounter_provider_id", "INTEGER NOT NULL"),
	LOCATION_ID("encounter_location_id", LocationTable.FKEY_RELATIONSHIP), 
	DATETIME("encounter_datetime", "DATETIME NOT NULL"),
	CREATOR("creator", "INTEGER NOT NULL"),
	DATE_CREATED("date_created", "DATETIME DEFAULT (CURRENT_TIMESTAMP)"),
	VOIDED("voided", "bit NOT NULL"),
	ISUPDATE("isUpdate", "bit DEFAULT 1");
	
	public static String TABLE_NAME = "encounter";
	public static String TABLE_ACRONYM = "e"; //for queries
	public static String FKEY_RELATIONSHIP = "INTEGER REFERENCES " + TABLE_NAME + "(" + ID.getName() + ") ON DELETE CASCADE";
	
	private String COLUMN_NAME;
	private String COLUMN_TYPE;
	
	public static String[] COLUMNS = new String[EncounterTable.values().length];
	public static String[] COLUMNS_TYPE = new String[EncounterTable.values().length];
	
	private EncounterTable(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}
	
	static {
		for (int c = 0; c < EncounterTable.values().length; c++) {
			COLUMNS[c] =EncounterTable.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < EncounterTable.values().length; c++) {
			COLUMNS_TYPE[c] =EncounterTable.values()[c].COLUMN_TYPE;
		}
	}

	public String type() {
		return COLUMN_TYPE;
	}

	public String getName() {
		return COLUMN_NAME;
	}
}
