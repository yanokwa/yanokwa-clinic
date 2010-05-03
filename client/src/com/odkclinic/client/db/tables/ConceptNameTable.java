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
public enum ConceptNameTable {
	ID("concept_name_id", "integer PRIMARY KEY"),
	CONCEPT_ID("conceptname_concept_id", ConceptTable.FKEY_RELATIONSHIP),
	NAME("concept_name", "TEXT NOT NULL");
	 
	public static String TABLE_NAME = "concept_name";
	public static String TABLE_ACRONYM = "cn"; //for queries
	
	private String COLUMN_NAME;
	private String COLUMN_TYPE;
	
	public static String[] COLUMNS = new String[ConceptNameTable.values().length];
	public static String[] COLUMNS_TYPE = new String[ConceptNameTable.values().length];
	
	private ConceptNameTable(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}
	
	static {
		for (int c = 0; c < ConceptNameTable.values().length; c++) {
			COLUMNS[c] =ConceptNameTable.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < ConceptNameTable.values().length; c++) {
			COLUMNS_TYPE[c] =ConceptNameTable.values()[c].COLUMN_TYPE;
		}
	}

	public String getType() {
		return COLUMN_TYPE;
	}

	public String getName() {
		return COLUMN_NAME;
	}
}