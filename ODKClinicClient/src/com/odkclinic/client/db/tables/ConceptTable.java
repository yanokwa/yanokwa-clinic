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
public enum ConceptTable {
	ID("concept_id", "INTEGER PRIMARY KEY"),
	CLASS_ID("class_id", "INTEGER NOT NULL"),
	IS_SET("is_set", "bit NOT NULL"),
	DATATYPE_ID("concept_datatype_id", "INTEGER NOT NULL"),
	RETIRED("retired", "bit NOT NULL"),
	CREATOR("creator", "INTEGER NOT NULL"),
	DATE_CREATED("date_created", "DATETIME NOT NULL"),
	VOIDED("voided", "bit NOT NULL");
	
	public static String TABLE_NAME = "concept";
	public static String TABLE_ACRONYM = "co"; //for queries
	public static String FKEY_RELATIONSHIP = "INTEGER REFERENCES " + TABLE_NAME + "(" + ID.getName() + ") ON DELETE CASCADE";
	
	private String COLUMN_NAME;
	private String COLUMN_TYPE;
	
	public static String[] COLUMNS = new String[ConceptTable.values().length];
	public static String[] COLUMNS_TYPE = new String[ConceptTable.values().length];
	
	private ConceptTable(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}
	
	static {
		for (int c = 0; c < ConceptTable.values().length; c++) {
			COLUMNS[c] =ConceptTable.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < ConceptTable.values().length; c++) {
			COLUMNS_TYPE[c] =ConceptTable.values()[c].COLUMN_TYPE;
		}
	}

	public String type() {
		return COLUMN_TYPE;
	}

	public String getName() {
		return COLUMN_NAME;
	}
}
