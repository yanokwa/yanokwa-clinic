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

package com.odkclinic.client.db;

import com.odkclinic.client.db.tables.CohortMemberTable;
import com.odkclinic.client.db.tables.CohortTable;
import com.odkclinic.client.db.tables.ConceptDataTypeTable;
import com.odkclinic.client.db.tables.ConceptDescTable;
import com.odkclinic.client.db.tables.ConceptNameTable;
import com.odkclinic.client.db.tables.ConceptTable;
import com.odkclinic.client.db.tables.ObservationTable;
import com.odkclinic.client.db.tables.PatientProgramTable;
import com.odkclinic.client.db.tables.PatientTable;
import com.odkclinic.client.db.tables.ProgramTable;
import com.odkclinic.client.db.tables.UpdatesTable;
import com.odkclinic.client.db.tables.VisitedTable;
import com.odkclinic.client.db.tables.views.EncounterView;
import com.odkclinic.client.db.tables.views.ObservationView;

/**
 * Constants used by DB backend and accessing DB functionality.
 * 
 * @author Euzel Villanueva
 */
public class DbConstants {
	
	public static String UPDATE_COUNT = "update_count";
	public static int NOTES_CONCEPT_ID = 2;
    /**
     * Triggers
     */
    public static final String[] TRIGGERS = 
    {
    	" CREATE TRIGGER insert_patient AFTER INSERT ON patient " +
    	" BEGIN " +
    	" INSERT INTO visited(visited_patient_id) VALUES(new.patient_id);" +
    	" END; ",
    	
    	" CREATE TRIGGER insert_encounter AFTER INSERT ON encounter WHEN new.isUpdate = 1" +
    	" BEGIN " +
    	" INSERT INTO updates(updates_patient_id,update_name,update_type, update_type_id) VALUES(new.encounter_patient_id, 'New Encounter', 'ENC', new.encounter_id); " +
    	" END; ",
    	
    	" CREATE TRIGGER delete_encounter AFTER DELETE ON encounter" +
    	" BEGIN " +
    	" DELETE FROM updates WHERE updates_encounter_id = old.encounter_id AND update_type = 'ENC'; " +
    	" END; ",
    	
    	" CREATE TRIGGER delete_observation AFTER DELETE ON observation" +
    	" BEGIN " +
    	" DELETE FROM updates WHERE updates_obs_id = old.obs_id AND update_type = 'OBS'; " +
    	" END; ", 
    	
    	" CREATE TRIGGER insert_observation AFTER INSERT ON observation WHEN new.isUpdate = 1" +
    	" BEGIN " +
    	" INSERT INTO updates(updates_patient_id,update_name, update_type, update_type_id) VALUES(new.observation_patient_id, 'New Note', 'OBS', new.obs_id); " +
    	" END; "};
    
    public static final String[] INDEXES = 
    {
    	"CREATE INDEX visited_q ON " + VisitedTable.TABLE_NAME + "(" + 
    									VisitedTable.PATIENT_ID.getName() + "," + 
    									VisitedTable.BEEN_VISITED.getName() + ");",
    	"CREATE INDEX patient_q ON " + PatientTable.TABLE_NAME + "(" + 
    									PatientTable.ID.getName() + "," + 
    									PatientTable.NAME.getName() + ");",
    	"CREATE INDEX update_q ON " + UpdatesTable.TABLE_NAME + "(" + 
    									UpdatesTable.PATIENT_ID.getName()+ "," + 
    									UpdatesTable.NAME.getName() + "," + 
    									UpdatesTable.DATE.getName() + ");",
    	"CREATE INDEX update_q2 ON " + UpdatesTable.TABLE_NAME + "(" + 
    									UpdatesTable.PATIENT_ID.getName() + "," +
    									UpdatesTable.ID.getName() + ");",
    	"CREATE INDEX concept_data_q ON " + ConceptDataTypeTable.TABLE_NAME + "(" + 
    										ConceptDataTypeTable.ID.getName() + "," + 
    										ConceptDataTypeTable.NAME.getName() + "," + 
    										ConceptDataTypeTable.HL7.getName()  + ");",
    	"CREATE INDEX concept_name_q ON " + ConceptNameTable.TABLE_NAME + "(" + ConceptNameTable.CONCEPT_ID.getName() + "," + ConceptNameTable.NAME.getName() + ");",
    	"CREATE INDEX concept_desc_q ON " + ConceptDescTable.TABLE_NAME + "(" + 
    										ConceptDescTable.CONCEPT_ID.getName() + "," + 
    										ConceptDescTable.DESC.getName() + ");",
    	"CREATE INDEX obs_q ON " + ObservationTable.TABLE_NAME + 
    				"(" + ObservationTable.CONCEPT_ID.getName() + "," + 
    				      ObservationTable.PATIENT_ID.getName() + "," + 
    				      ObservationTable.NUMERIC.getName() + "," + 
    				      ObservationTable.TEXT.getName() + "," + 
    				      ObservationTable.BOOLEAN.getName() + ");"
    };
    
    /**
     * Database selection SQL statements
     *     
     */
    public static final String PATIENT_MANAGER_LIST_QUERY = 
		String.format(
				" SELECT %s, %s, %s, COUNT(DISTINCT updates.%s) %s, %s, %s _id" +
				" FROM %s, %s" +
				" LEFT OUTER JOIN %s on %s = %s " +
				" WHERE  %s = %s " +
				" GROUP BY %s " +
 				" ORDER BY %s ",
				PatientTable.NAME.getName(), PatientTable.ID.getName(), VisitedTable.BEEN_VISITED.getName(), UpdatesTable.ID.getName(), UPDATE_COUNT, PatientTable.CHECKED.getName(),PatientTable.ID.getName(),
				PatientTable.TABLE_NAME, VisitedTable.TABLE_NAME, 
				UpdatesTable.TABLE_NAME, UpdatesTable.PATIENT_ID.getName(), PatientTable.ID.getName(),
				VisitedTable.PATIENT_ID.getName(), PatientTable.ID.getName(),
				PatientTable.ID.getName(),
				PatientTable.ID.getName());
    
    public static final String PATIENT_MANAGER_COHORT_LIST_QUERY = 
    	String.format(
				" SELECT %s, %s, %s, COUNT(DISTINCT updates.%s) %s, %s, %s _id" +
				" FROM %s, %s, %s" +
				" LEFT OUTER JOIN %s on %s = %s " +
				" WHERE %s = %s AND %s = %s  AND %s = ? " +
				" GROUP BY %s " +
 				" ORDER BY %s ",
				PatientTable.NAME.getName(), PatientTable.ID.getName(), VisitedTable.BEEN_VISITED.getName(), UpdatesTable.ID.getName(), UPDATE_COUNT, PatientTable.CHECKED.getName(), PatientTable.ID.getName(),
				PatientTable.TABLE_NAME,  VisitedTable.TABLE_NAME, CohortMemberTable.TABLE_NAME,
				UpdatesTable.TABLE_NAME, UpdatesTable.PATIENT_ID.getName(), PatientTable.ID.getName(),
				PatientTable.ID.getName(), CohortMemberTable.PATIENT_ID.getName(), VisitedTable.PATIENT_ID.getName(), PatientTable.ID.getName(), CohortMemberTable.COHORT_ID.getName(),
				PatientTable.ID.getName(),
				PatientTable.ID.getName());


    public static final String PATIENT_LIST_QUERY = 		
	String.format(
			" SELECT  %s,  %s, %s, %s _id" +
			" FROM %s, %s " +
			" WHERE %s = %s " +
			" ORDER BY %s",
			PatientTable.NAME.getName(),  CohortMemberTable.COHORT_ID.getName(), PatientTable.ID.getName(), PatientTable.ID.getName(),
			PatientTable.TABLE_NAME, CohortMemberTable.TABLE_NAME, 
			PatientTable.ID.getName(), CohortMemberTable.PATIENT_ID.getName(),
			PatientTable.ID.getName());
    
    
    public static final String PATIENT_DEMOGRAPHIC_QUERY = 
    	String.format(
				" SELECT %s, %s, %s, %s, %s _id" +
				" FROM %s" +
				" WHERE %s = ?" +
				" ORDER BY %s",
				PatientTable.GENDER.getName(), PatientTable.RACE.getName(), PatientTable.BIRTHDATE.getName(), PatientTable.NAME.getName(), PatientTable.ID.getName(),
				PatientTable.TABLE_NAME,
				PatientTable.ID.getName(),
				PatientTable.ID.getName());
    
    public static final String ALL_UPDATE_LIST_QUERY = 
    	String.format(
				" SELECT DISTINCT %s, %s, %s, %s,  %s, %s _id" +
				" FROM %s, %s, %s, %s " +
				" WHERE %s = %s AND %s = %s AND %s = %s" +
				" ORDER BY %s, %s",
				PatientTable.ID.getName(), PatientTable.NAME.getName(), CohortTable.ID.getName(), UpdatesTable.NAME.getName(), UpdatesTable.DATE.getName(), UpdatesTable.ID.getName(),
				PatientTable.TABLE_NAME, CohortTable.TABLE_NAME, CohortMemberTable.TABLE_NAME, UpdatesTable.TABLE_NAME,
				PatientTable.ID.getName(), CohortMemberTable.PATIENT_ID.getName(), CohortTable.ID.getName(), CohortMemberTable.COHORT_ID.getName(), PatientTable.ID.getName(), UpdatesTable.PATIENT_ID.getName(),
				PatientTable.ID.getName(), UpdatesTable.DATE.getName());
    
    public static final String PATIENT_UPDATE_LIST_QUERY = 
    	String.format(
				" SELECT DISTINCT %s, %s, %s, %s,  %s, %s _id" +
				" FROM %s, %s, %s, %s " +
				" WHERE %s = %s AND %s = %s AND %s = %s AND %s = ?" +
				" ORDER BY %s, %s",
				PatientTable.ID.getName(), PatientTable.NAME.getName(), CohortTable.ID.getName(), UpdatesTable.NAME.getName(), UpdatesTable.DATE.getName(), UpdatesTable.ID.getName(),
				PatientTable.TABLE_NAME, CohortTable.TABLE_NAME, CohortMemberTable.TABLE_NAME, UpdatesTable.TABLE_NAME,
				PatientTable.ID.getName(), CohortMemberTable.PATIENT_ID.getName(), CohortTable.ID.getName(), CohortMemberTable.COHORT_ID.getName(), PatientTable.ID.getName(), UpdatesTable.PATIENT_ID.getName(), PatientTable.ID.getName(),
				PatientTable.ID.getName(), UpdatesTable.DATE.getName());

    
    public static final String CONCEPT_HISTORY_QUERY = 
    	String.format(
				" SELECT %s, %s, %s, %s, %s, %s " +
				" FROM %s, %s, %s, %s, %s, %s " +
				" WHERE %s = %s AND %s = %s AND %s = %s AND %s = %s AND %s = %s AND %s = ? AND %s = ? AND %s = %s " +
				" ORDER BY %s",
				ConceptNameTable.NAME.getName(), ObservationView.DATE_CREATED.getName(), ObservationView.NUMERIC.getName(), ObservationView.TEXT.getName(), 
				ObservationView.DATETIME.getName(), ObservationView.BOOLEAN.getName(),
				
				PatientTable.TABLE_NAME, ConceptTable.TABLE_NAME, ConceptNameTable.TABLE_NAME, ProgramTable.TABLE_NAME, PatientProgramTable.TABLE_NAME, ObservationView.TABLE_NAME,
				
				PatientTable.ID.getName(), PatientProgramTable.PATIENT_ID.getName(), ProgramTable.ID.getName(), PatientProgramTable.PROGRAM_ID.getName(), ProgramTable.CONCEPT_ID.getName(), 
				ConceptTable.ID.getName(), ConceptTable.ID.getName(), ConceptNameTable.CONCEPT_ID.getName(), ObservationView.CONCEPT_ID.getName(), ConceptTable.ID.getName(), 
				ConceptTable.ID.getName(), PatientTable.ID.getName(), ObservationView.PATIENT_ID.getName(), PatientTable.ID.getName(),
				
				ObservationView.DATE_CREATED.getName());
    
    //TODO add more details
    /* 
     * 
     * a query returning historical encounters with a patient
     * information needed: date, encounter details */
    public static final String  ENCOUNTER_HISTORY_QUERY = 
    	String.format(
				" SELECT  %s,  %s,  %s,  %s" +
				" FROM %s, %s " +
				" WHERE %s = %s AND %s = ?" +
				" ORDER BY %s",
				EncounterView.TYPE_NAME.getName(), EncounterView.TYPE_DESC.getName(), EncounterView.LOCATION_ID.getName(), 
				EncounterView.DATETIME.getName(),
				
				EncounterView.TABLE_NAME, PatientTable.TABLE_NAME,
				
				PatientTable.ID.getName(), EncounterView.PATIENT_ID.getName(), PatientTable.ID.getName(),
				EncounterView.ID.getName());
    
        public static final String CONCEPT_QUERY = 
        	String.format(
    				" SELECT %s, %s, %s, %s" +
    				" FROM %s, %s, %s, %s " +
    				" WHERE %s = %s AND %s = %s AND %s = %s AND %s = ? " +
    				" ORDER BY %s",
    				ConceptNameTable.NAME.getName(), ConceptDataTypeTable.NAME.getName(), ConceptDataTypeTable.HL7.getName(), ConceptDescTable.DESC.getName(),
    				
    				ConceptTable.TABLE_NAME, ConceptDescTable.TABLE_NAME, ConceptNameTable.TABLE_NAME, ConceptDataTypeTable.TABLE_NAME,
    				
    				ConceptTable.ID.getName(), ConceptDescTable.CONCEPT_ID.getName(), ConceptDataTypeTable.ID.getName(), 
    				ConceptTable.DATATYPE_ID.getName(), ConceptTable.ID.getName(), ConceptNameTable.CONCEPT_ID.getName(), ConceptTable.ID.getName(),
    				
    				ConceptTable.ID.getName());
        
        
        public static final String GET_PATIENT_WOUPDATES = 
        	String.format(
        	" SELECT %s" +
			" FROM %s" +
			" LEFT OUTER JOIN %s on %s = %s " +
			" GROUP BY %s " +
			" HAVING COUNT(DISTINCT %s) = 0 " +
			" ORDER BY %s ",
			PatientTable.ID.getName(),
			PatientTable.TABLE_NAME,
			UpdatesTable.TABLE_NAME, PatientTable.ID.getName(), UpdatesTable.PATIENT_ID.getName(),
			PatientTable.ID.getName(),
			UpdatesTable.ID.getName(),
			PatientTable.ID.getName()); 
        
        public static final String GET_PATIENT_CONCEPTS = 
        	String.format(
    				" SELECT  %s, %s _id" +
    				" FROM %s, %s, %s, %s, %s " +
    				" WHERE %s = %s AND %s = %s AND %s = %s AND %s = %s AND %s = ? " +
    				" ORDER BY %s ",
    				ConceptNameTable.NAME.getName(), ConceptTable.ID.getName(),
    				PatientTable.TABLE_NAME, ConceptTable.TABLE_NAME, ConceptNameTable.TABLE_NAME, ProgramTable.TABLE_NAME, PatientProgramTable.TABLE_NAME,
    				PatientTable.ID.getName(), PatientProgramTable.PATIENT_ID.getName(), PatientProgramTable.PROGRAM_ID.getName(), ProgramTable.ID.getName(), 
    				ProgramTable.CONCEPT_ID.getName(), ConceptTable.ID.getName(), ConceptTable.ID.getName(), ConceptNameTable.CONCEPT_ID.getName(), PatientTable.ID.getName(), 
    				ConceptTable.ID.getName()); 
        
        public static final String[] DEFAULT_VALUES = 
        {
        	"INSERT INTO patient(patient_id,gender,race,birthdate,patient_name) VALUES(1,'MALE','Asian','1966-10-28','Mike'); ",
        	"INSERT INTO patient(patient_id,gender,race,birthdate,patient_name) VALUES(2,'FEMALE','Asian','1977-1-2','Michelle'); ",
        	"INSERT INTO cohort(cohort_id,name,creator,voided,date_created) VALUES(1,'PRIME',1,0,'2009/6/2'); ",
        	"INSERT INTO cohort_member(cohortmember_cohort_id,cohortmember_patient_id) VALUES(1,1); ",
        	"INSERT INTO cohort_member(cohortmember_cohort_id,cohortmember_patient_id) VALUES(1,2); ",
        	"INSERT INTO concept(concept_id,class_id,concept_datatype_id,is_set,retired,creator,date_created,voided) VALUES(1,1,1,1,0,1,'1/1/2009',0); ",
        	"INSERT INTO concept(concept_id,class_id,concept_datatype_id,is_set,retired,creator,date_created,voided) VALUES(2,1,2,1,0,1,'1/1/2009',0); ",
        	"INSERT INTO concept(concept_id,class_id,concept_datatype_id,is_set,retired,creator,date_created,voided) VALUES(3,1,3,1,0,1,'1/1/2009',0); ",
        	"INSERT INTO concept(concept_id,class_id,concept_datatype_id,is_set,retired,creator,date_created,voided) VALUES(4,1,4,1,0,1,'1/1/2009',0); ",
        	"INSERT INTO concept_datatype(datatype_id,concept_datatypename,hl7_abbreviation,concept_datatypedesc) VALUES(1,'BOOLEAN','BIT',''); ",
        	"INSERT INTO concept_datatype(datatype_id,concept_datatypename,hl7_abbreviation,concept_datatypedesc) VALUES(2,'TEXT','ST',''); ",
        	"INSERT INTO concept_datatype(datatype_id,concept_datatypename,hl7_abbreviation,concept_datatypedesc) VALUES(3,'NUMERIC','NM',''); ",
        	"INSERT INTO concept_datatype(datatype_id,concept_datatypename,hl7_abbreviation,concept_datatypedesc) VALUES(4,'NUMERIC','NM',''); ",
        	"INSERT INTO concept_description(concept_description_id,conceptdescription_concept_id,concept_desc) VALUES(1,1,'DESC'); ",
        	"INSERT INTO concept_description(concept_description_id,conceptdescription_concept_id,concept_desc) VALUES(2,2,'DESC'); ",
        	"INSERT INTO concept_description(concept_description_id,conceptdescription_concept_id,concept_desc) VALUES(3,3,'DESC'); ",
        	"INSERT INTO concept_description(concept_description_id,conceptdescription_concept_id,concept_desc) VALUES(4,4,'DESC'); ",
        	"INSERT INTO concept_name(concept_name_id,conceptname_concept_id,concept_name) VALUES(1,1,'Visited Hospital'); ",
        	"INSERT INTO concept_name(concept_name_id,conceptname_concept_id,concept_name) VALUES(2,2,'Notes'); ",
        	"INSERT INTO concept_name(concept_name_id,conceptname_concept_id,concept_name) VALUES(3,3,'CD4'); ",
        	"INSERT INTO concept_name(concept_name_id,conceptname_concept_id,concept_name) VALUES(4,4,'Weight'); ",
        	"INSERT INTO program(program_id,program_concept_id) VALUES(1,1); ",
        	"INSERT INTO program(program_id,program_concept_id) VALUES(2,2); ",
        	"INSERT INTO program(program_id,program_concept_id) VALUES(3,3); ",
        	"INSERT INTO program(program_id,program_concept_id) VALUES(4,4); ",
        	"INSERT INTO patient_program(patient_program_id,patientprogram_patient_id,patientprogram_program_id) VALUES(1,1,1); ", 
        	"INSERT INTO patient_program(patient_program_id,patientprogram_patient_id,patientprogram_program_id) VALUES(2,1,2); ",
        	"INSERT INTO patient_program(patient_program_id,patientprogram_patient_id,patientprogram_program_id) VALUES(3,1,3); ",
        	"INSERT INTO patient_program(patient_program_id,patientprogram_patient_id,patientprogram_program_id) VALUES(4,1,4); ",
        	"INSERT INTO patient_program(patient_program_id,patientprogram_patient_id,patientprogram_program_id) VALUES(5,2,1); ",
        	"INSERT INTO patient_program(patient_program_id,patientprogram_patient_id,patientprogram_program_id) VALUES(6,2,2); ",
        	"INSERT INTO patient_program(patient_program_id,patientprogram_patient_id,patientprogram_program_id) VALUES(7,2,3); ",
        	"INSERT INTO patient_program(patient_program_id,patientprogram_patient_id,patientprogram_program_id) VALUES(8,2,4); ",
        	"INSERT INTO location(location_id, name, creator) VALUES(1, 'GUAM', 1);",
        	"INSERT INTO encounter(encounter_id,encounter_patient_id,encounter_provider_id,encounter_location_id,encounter_datetime,creator,date_created,voided,isUpdate) VALUES(1,1,1,1,'2009-10-28',1,'2009-10-28',0, 1); ",
        	"INSERT INTO encounter(encounter_id,encounter_patient_id,encounter_provider_id,encounter_location_id,encounter_datetime,creator,date_created,voided,isUpdate) VALUES(2,2,1,1,'2009-10-28',1,'2009-10-28',0, 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(1,1,4,1,100,1,0,'2009-07-23 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(2,1,4,1,110,1,0,'2009-04-23 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(3,1,4,1,122,1,0,'2009-03-25 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(4,1,4,1,130,1,0,'2009-01-22 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(5,2,4,2,120,1,0,'2009-01-23 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(6,2,4,2,125,1,0,'2009-03-03 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(7,2,4,2,130,1,0,'2009-05-23 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(8,1,3,1,500,1,0,'2009-01-23 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(9,1,3,1,600,1,0,'2009-03-24 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(10,1,3,1,520,1,0,'2009-05-23 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(11,1,3,1,550,1,0,'2009-07-24 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(12,2,3,2,500,1,0,'2009-01-25 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(13,2,3,2,700,1,0,'2009-03-02 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_numeric,creator,voided,observation_date_created,isUpdate) VALUES(14,2,3,2,900,1,0,'2009-06-05 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_text,creator,voided,observation_date_created,isUpdate) VALUES(15,1,2,1,'This Patient is WOW',1,0,'2009-07-23 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_text,creator,voided,observation_date_created,isUpdate) VALUES(16,1,2,1,'WOW a note?',1,0,'2009-03-23 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_text,creator,voided,observation_date_created,isUpdate) VALUES(17,1,2,1,'Hmm___',1,0,'2009-01-21 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_text,creator,voided,observation_date_created,isUpdate) VALUES(18,1,2,1,'Hootie Hoo!',1,0,'2009-05-23 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_text,creator,voided,observation_date_created,isUpdate) VALUES(19,2,2,2,'Dude_',1,0,'2009-01-25 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_text,creator,voided,observation_date_created,isUpdate) VALUES(20,2,2,2,'How ___',1,0,'2009-03-26 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_text,creator,voided,observation_date_created,isUpdate) VALUES(21,2,2,2,'Oh god why??!',1,0,'2009-06-32 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_boolean,creator,voided,observation_date_created,isUpdate) VALUES(22,1,1,2,0,1,0,'2009-04-32 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_boolean,creator,voided,observation_date_created,isUpdate) VALUES(23,1,1,2,1,1,0,'2009-06-32 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_boolean,creator,voided,observation_date_created,isUpdate) VALUES(24,1,1,2,0,1,0,'2009-07-32 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_boolean,creator,voided,observation_date_created,isUpdate) VALUES(25,2,1,2,0,1,0,'2009-04-32 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_boolean,creator,voided,observation_date_created,isUpdate) VALUES(26,2,1,2,1,1,0,'2009-06-32 00:00:00', 1); ",
        	"INSERT INTO observation(obs_id,observation_patient_id,observation_concept_id,observation_encounter_id,value_boolean,creator,voided,observation_date_created,isUpdate) VALUES(27,2,1,2,0,1,0,'2009-07-32 00:00:00', 1); ",
        	"INSERT INTO patient(patient_id,gender,race,birthdate,patient_name) VALUES(3,'MALE','Asian','1988-10-28','Zell'); ",
        	"INSERT INTO patient(patient_id,gender,race,birthdate,patient_name) VALUES(4,'FEMALE','Asian','1987-1-2','Michiko'); ",
        	"INSERT INTO cohort(cohort_id,name,creator,voided,date_created) VALUES(2,'PRIME',1,0,'2009/6/2'); ",
        	"INSERT INTO cohort_member(cohortmember_cohort_id,cohortmember_patient_id) VALUES(2,4); ",
        	"INSERT INTO cohort_member(cohortmember_cohort_id,cohortmember_patient_id) VALUES(2,3); ",
        	"INSERT INTO patient_program(patient_program_id,patientprogram_patient_id,patientprogram_program_id) VALUES(9,3,2); ",
        	"INSERT INTO patient_program(patient_program_id,patientprogram_patient_id,patientprogram_program_id) VALUES(10,4,2); "};
}

