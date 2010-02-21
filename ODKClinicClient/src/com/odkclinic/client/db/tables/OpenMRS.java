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
public enum OpenMRS {
	Patients(PatientTable.TABLE_NAME,
				 PatientTable.COLUMNS,
				 PatientTable.COLUMNS_TYPE),
	Cohorts(CohortTable.TABLE_NAME,
			CohortTable.COLUMNS,
			CohortTable.COLUMNS_TYPE),
	Concepts(ConceptTable.TABLE_NAME,
			 ConceptTable.COLUMNS,
			 ConceptTable.COLUMNS_TYPE),
	CohortMember(CohortMemberTable.TABLE_NAME,
				 CohortMemberTable.COLUMNS,
				 CohortMemberTable.COLUMNS_TYPE),
	ConceptName(ConceptNameTable.TABLE_NAME,
				ConceptNameTable.COLUMNS,
				ConceptNameTable.COLUMNS_TYPE),
	ConceptDesc(ConceptDescTable.TABLE_NAME,
			 	ConceptDescTable.COLUMNS,
				ConceptDescTable.COLUMNS_TYPE),
	ConceptDataType(ConceptDataTypeTable.TABLE_NAME,
			 		ConceptDataTypeTable.COLUMNS,
			 		ConceptDataTypeTable.COLUMNS_TYPE),
	Location(LocationTable.TABLE_NAME,
		     LocationTable.COLUMNS,
		     LocationTable.COLUMNS_TYPE),
	Encounter(EncounterTable.TABLE_NAME,
			  EncounterTable.COLUMNS,
			  EncounterTable.COLUMNS_TYPE),
    ClientEncounter(ClientEncounterTable.TABLE_NAME,
                    ClientEncounterTable.COLUMNS,
                    ClientEncounterTable.COLUMNS_TYPE),
    Observation(ObservationTable.TABLE_NAME,
			    ObservationTable.COLUMNS,
			    ObservationTable.COLUMNS_TYPE),
    ClientObservation(ClientObservationTable.TABLE_NAME,
                      ClientObservationTable.COLUMNS,
                      ClientObservationTable.COLUMNS_TYPE),		 
    Program(ProgramTable.TABLE_NAME,
		 	ProgramTable.COLUMNS,
		 	ProgramTable.COLUMNS_TYPE),
 	PatientProgram(PatientProgramTable.TABLE_NAME,
			 	   PatientProgramTable.COLUMNS,
			       PatientProgramTable.COLUMNS_TYPE),
    Visited(VisitedTable.TABLE_NAME,
			VisitedTable.COLUMNS,
			VisitedTable.COLUMNS_TYPE),
	Updates(UpdatesTable.TABLE_NAME,
		    UpdatesTable.COLUMNS,
		    UpdatesTable.COLUMNS_TYPE),
    Settings(SettingsTable.TABLE_NAME,
             SettingsTable.COLUMNS,
             SettingsTable.COLUMNS_TYPE);
	
	public static final String DATABASE_NAME = "openmrsdata";
	public static final int DATABASE_VERSION = 4;
	public static final String TAG = "DbAdapter";
	
	public final String TABLE_NAME;
	public final String[] TABLE_COLUM;
	public final String[] TABLE_COLUM_TYPE;
	
	private OpenMRS(String name, String[] colum, String[] columType) {
		this.TABLE_NAME = name; 
		this.TABLE_COLUM = colum;
		this.TABLE_COLUM_TYPE = columType;
	}
	
	public static String[]  createStatements(){
		String[] ret = new String[OpenMRS.values().length];
		int size = 0;
		for (OpenMRS t : OpenMRS.values()) {
			StringBuilder b = new StringBuilder();
			b.append("create table ");
			b.append(t.TABLE_NAME + " (");
			for (int c = 0; c < t.TABLE_COLUM.length; c++) {
				b.append(t.TABLE_COLUM[c] + " ");
				b.append(t.TABLE_COLUM_TYPE[c] + " ");
				if (c < (t.TABLE_COLUM.length - 1)) {
					b.append(", ");
				}
			}
			b.append(") ");
			ret[size++] = b.toString();
		}
		return ret;
	}
	
	public static String[] dropStatements(){ 
		String[] ret = new String[OpenMRS.values().length];
		int size = ret.length - 1;
		for (OpenMRS t : OpenMRS.values()) {
			StringBuilder b = new StringBuilder();
			b.append("DROP TABLE IF EXISTS ");
			b.append(t.TABLE_NAME);
			ret[size--] = b.toString(); 
		}
		return ret;
	}
}
