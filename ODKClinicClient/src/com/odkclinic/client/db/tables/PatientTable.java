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
public enum PatientTable {
	ID("patient_id", "INTEGER PRIMARY KEY"),
	GENDER("gender", "VARCHAR(50)"),
	RACE("race","VARCHAR(50) NULL"),
	BIRTHDATE("birthdate", "DATE NULL"),
	DEAD("dead", "INTEGER"),
	BIRTHPLACE("birthplace", "VARCHAR(50) NULL"),
	HEIGHT("patient_height", "DOUBLE NULL"),
	WEIGHT("patient_weight", "DOUBLE NULL"),
	NAME("patient_name", "VARCHAR(50) NULL"),
	CHECKED("isChecked", "integer default 0");
		
	public static String TABLE_NAME = "patient";
	public static String TABLE_ACRONYM = "p"; //for queries
	public static String FKEY_RELATIONSHIP = "INTEGER REFERENCES " + TABLE_NAME + "(" + ID.getName() + ") ON DELETE CASCADE";
	
	private String COLUMN_NAME;
	private String COLUMN_TYPE;
	
	public static String[] COLUMNS = new String[PatientTable.values().length];
	public static String[] COLUMNS_TYPE = new String[PatientTable.values().length];
	
	private PatientTable(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}
	
	static {
		for (int c = 0; c < PatientTable.values().length; c++) {
			COLUMNS[c] =PatientTable.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < PatientTable.values().length; c++) {
			COLUMNS_TYPE[c] =PatientTable.values()[c].COLUMN_TYPE;
		}
	}

	public String getType() {
		return COLUMN_TYPE;
	}

	public String getName() {
		return COLUMN_NAME;
	}
}
