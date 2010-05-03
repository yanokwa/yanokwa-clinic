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
 * @author zellv
 *
 */
public enum ProgramTable {
	ID("program_id", "INTEGER PRIMARY KEY"),
	CONCEPT_ID("program_concept_id", ConceptTable.FKEY_RELATIONSHIP);
	
	public static String TABLE_NAME = "program";
	public static String TABLE_ACRONYM = "pr"; //for queries
	public static String FKEY_RELATIONSHIP = "INTEGER REFERENCES " + TABLE_NAME + "(" + ID.getName() + ") ON DELETE CASCADE";
	
	private String COLUMN_NAME;
	private String COLUMN_TYPE;
	
	public static String[] COLUMNS = new String[ProgramTable.values().length];
	public static String[] COLUMNS_TYPE = new String[ProgramTable.values().length];
	
	private ProgramTable(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}
	
	static {
		for (int c = 0; c < ProgramTable.values().length; c++) {
			COLUMNS[c] =ProgramTable.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < ProgramTable.values().length; c++) {
			COLUMNS_TYPE[c] =ProgramTable.values()[c].COLUMN_TYPE;
		}
	}

	public String type() {
		return COLUMN_TYPE;
	}

	public String getName() {
		return COLUMN_NAME;
	}
}
