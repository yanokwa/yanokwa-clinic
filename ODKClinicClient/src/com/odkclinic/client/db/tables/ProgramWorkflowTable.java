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
public enum ProgramWorkflowTable {
	ID("programworkflow_id", "INTEGER PRIMARY KEY"),
	CONCEPT_ID("programworkflow_concept_id", ConceptTable.FKEY_RELATIONSHIP),
	PROGRAM_ID("ProgramWorkflow_program_id", ProgramTable.FKEY_RELATIONSHIP);
	
	public static String TABLE_NAME = "program_workflow";
	public static String TABLE_ACRONYM = "ppr"; //for queries
	
	private String COLUMN_NAME;
	private String COLUMN_TYPE;
	
	public static String[] COLUMNS = new String[ProgramWorkflowTable.values().length];
	public static String[] COLUMNS_TYPE = new String[ProgramWorkflowTable.values().length];
	
	private ProgramWorkflowTable(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}
	
	static {
		for (int c = 0; c < ProgramWorkflowTable.values().length; c++) {
			COLUMNS[c] =ProgramWorkflowTable.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < ProgramWorkflowTable.values().length; c++) {
			COLUMNS_TYPE[c] =ProgramWorkflowTable.values()[c].COLUMN_TYPE;
		}
	}

	public String getType() {
		return COLUMN_TYPE;
	}

	public String getName() {
		return COLUMN_NAME;
	}
}
