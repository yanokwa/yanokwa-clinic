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

import java.sql.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.odkclinic.client.db.tables.CohortTable;
import com.odkclinic.client.db.tables.EncounterTable;
import com.odkclinic.client.db.tables.ObservationTable;
import com.odkclinic.client.db.tables.PatientProgramTable;
import com.odkclinic.client.db.tables.PatientTable;
import com.odkclinic.client.db.tables.ProgramTable;
import com.odkclinic.client.db.tables.UpdatesTable;
import com.odkclinic.client.db.tables.VisitedTable;
import com.odkclinic.client.xforms.Encounter;
import com.odkclinic.client.xforms.Observation;
import com.odkclinic.client.xforms.Patient;
import com.odkclinic.client.xforms.PatientProgram;
import com.odkclinic.client.xforms.Program;

/**
 * Main interface for subset OpenMrs database.
 * 
 * @author Euzel Villanueva
 */
public class DbAdapter {
    
	private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private final Context mCtx;
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the patient database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Column 1: Patient Name
     * Column 2: Cohort ID
     * Column 3: Patient ID
     * 
     * @return Returns a cursor for list of patients to be viewed
     */
    public Cursor getPatientList() {
    	return mDb.rawQuery(DbConstants.PATIENT_LIST_QUERY, null);
    }
    
    /**
     * Column 1: Patient ID
     * Column 2: Patient Name
     * Column 3: BeenVisted
     * Column 4: Number of Updates for patient
     * 
     * @return Returns a cursor for list of patients to be viewed
     */
    public Cursor getPatientManagerList() {
    	return mDb.rawQuery(DbConstants.PATIENT_MANAGER_LIST_QUERY, null);
    }
    
    /**
     * Column 1: Patient Gender
     * Column 2: Patient Race
     * Column 3: Patient Birthdate
     * Column 4: Patient Height //TODO: Still not sure how to get this
     * @param id
     * @return Returns patient specific demographic information
     */
    public Cursor getPatientDemographicInfo(long id) {
    	return mDb.rawQuery(DbConstants.PATIENT_DEMOGRAPHIC_QUERY, new String[] {id + ""});
    }
    
    /**
     *  Column 1: Patient ID
     *  Column 2: Patient Name
     *  Column 3: Cohort Id
     *  Column 4: Update Name
     *  
     *  Sorted by patient id, then update name, then update date
     *  
     * @param id Patient whose update history we want.
     * @return Returns a cursor to a list of updates left for the patent id specified.
     */
    public Cursor getUpdateList(long id) {
    	return mDb.rawQuery(DbConstants.PATIENT_UPDATE_LIST_QUERY, new String[] {id + ""});
    }
    
    /**
     *  Column 1: Patient ID
     *  Column 2: Patient Name
     *  Column 3: Cohort Id
     *  Column 4: Update Name
     *  
     *  Sorted by patient id, then update name, then update date
     *  
     * @return Returns a cursor to a list of all updates left.
     */
    public Cursor getUpdateList() {
    	return mDb.rawQuery(DbConstants.ALL_UPDATE_LIST_QUERY, null);
    }
    
    /**
     * Column 1 Update Name
     * Column 2 Update Date
     * @return
     */
    public Cursor getUpdateDetails(long id) {
    	return mDb.query(UpdatesTable.TABLE_NAME, 
    				     new String[] {UpdatesTable.NAME.getName(), UpdatesTable.DATE.getName()},
    				     UpdatesTable.ID.getName() +"=" +id, null, null, null, null);
    	//return mDb.rawQuery(DbConstants.UPDATE_DETAILS, new String[] {id + ""});
    }
    
 
    /**
     * 
     * @param concept_id
     * @param patient_id
     * @return Returns a cursor to a list of histoical values availible for the given 
     * concept and patient id.
     */
    public Cursor getConceptHistory(long concept_id, long patient_id) {
    	return mDb.rawQuery(DbConstants.CONCEPT_HISTORY_QUERY, new String[] {concept_id + "", patient_id + ""});
    }
    
    /**
     * 
     * @param patient_id
     * @return Returns a cursor to a list of histoical encounters availible for the given 
     * patient id.
     */
    public Cursor getEncounterHistory(long patient_id) {
    	return mDb.rawQuery(DbConstants.ENCOUNTER_HISTORY_QUERY, new String[] {patient_id + ""});
    }
    
    /**
     * 
     * @param concept_id
     * @return Returns a cursor the details of the concept
     */
    public Cursor getConcept(long concept_id) {
    	return mDb.rawQuery(DbConstants.CONCEPT_QUERY, new String[] {concept_id + ""});
    }
    
    /**
     *  
     * @param id ID of patient to be deleted
     * 
     */
    public void deletePatient(long id) {
    	mDb.delete(PatientTable.TABLE_NAME, PatientTable.ID.getName() + "=" + id, null);
    }

    /**
     *  
     * @param id ID of patient to be marked visited
     * @return sucess of marking patient visited
     */
    public void markPatientVisited(long id) {
    	ContentValues values = new ContentValues();
    	values.put(VisitedTable.BEEN_VISITED.getName(), "Already Visited.");
    	mDb.update(VisitedTable.TABLE_NAME, values, VisitedTable.PATIENT_ID.getName() + "=" + id, null);
    }
    
    /**
     *  
     * @param id ID of patient to be marked unvisited
     * @return sucess of marking patient unvisited
     */
    public void unmarkPatientVisited(long id) {
    	ContentValues values = new ContentValues();
    	values.put(VisitedTable.BEEN_VISITED.getName(), "Already Visited.");
    	mDb.update(VisitedTable.TABLE_NAME, values, VisitedTable.PATIENT_ID.getName() + "=" + id, null);
    }
    
    /**
     * 
     * records a new observation
     * 
     * @param concept_id
     * @param patient_id
	 *
     */
    public void newObservation(long concept_id, long patient_id, String value) {
    	ContentValues values = new ContentValues();
    	values.put(ObservationTable.TEXT.getName(), value);
    	values.put(ObservationTable.CREATOR.getName(), 1);
    	values.put(ObservationTable.VOIDED.getName(), 0);
    	values.put(ObservationTable.CONCEPT_ID.getName(), concept_id);
    	values.put(ObservationTable.PATIENT_ID.getName(), patient_id);
    	mDb.insert(ObservationTable.TABLE_NAME, null, values);
    }
    public void newObservation(long concept_id, long patient_id, Double value) {
    	ContentValues values = new ContentValues();
    	values.put(ObservationTable.NUMERIC.getName(), value);
    	values.put(ObservationTable.CREATOR.getName(), 1);
    	values.put(ObservationTable.VOIDED.getName(), 0);
    	values.put(ObservationTable.CONCEPT_ID.getName(), concept_id);
    	values.put(ObservationTable.PATIENT_ID.getName(), patient_id);
    	mDb.insert(ObservationTable.TABLE_NAME, null, values); 
    }
    public void newObservation(long concept_id, long patient_id, Date value) {
    	ContentValues values = new ContentValues();
    	values.put(ObservationTable.DATETIME.getName(), value.toString());
    	values.put(ObservationTable.CREATOR.getName(), 1);
    	values.put(ObservationTable.VOIDED.getName(), 0);
    	values.put(ObservationTable.CONCEPT_ID.getName(), concept_id);
    	values.put(ObservationTable.PATIENT_ID.getName(), patient_id);
    	mDb.insert(ObservationTable.TABLE_NAME, null, values);
    }
    public void newObservation(long concept_id, long patient_id, Boolean value) {
    	ContentValues values = new ContentValues();
    	values.put(ObservationTable.BOOLEAN.getName(), value);
    	values.put(ObservationTable.VOIDED.getName(), 0);
    	values.put(ObservationTable.CREATOR.getName(), 1);
    	values.put(ObservationTable.CONCEPT_ID.getName(), concept_id);
    	values.put(ObservationTable.PATIENT_ID.getName(), patient_id);
    	mDb.insert(ObservationTable.TABLE_NAME, null, values);
    }
    
    /**
     * returns cursor of all patients without updates
     */
    public Cursor getPatientsWoUpdates() {
    	return mDb.rawQuery(DbConstants.GET_PATIENT_WOUPDATES, null);
    }
    
    /**
     * returns cursor of all conceptss associated with patient
     */
    public Cursor getPatientConcepts(long patientId) {
    	return mDb.rawQuery(DbConstants.GET_PATIENT_CONCEPTS, new String[] {patientId + ""});
    }
    
    /**
     * Sets checkbox value for patient
     */
    public void setPatientCheck(int check, int patientId) {
    	ContentValues values = new ContentValues();
    	values.put(PatientTable.CHECKED.getName(), check);
    	mDb.update(PatientTable.TABLE_NAME, values, PatientTable.ID.getName() + "=" + patientId, null);
    }
    
    /**
     * Gets all patient ids.
     */
    public Cursor getPatientIds() {
    	return mDb.query(PatientTable.TABLE_NAME,new String[] {PatientTable.ID.getName()}, null, null, null, null, null);
    }
    
    /**
     * Gets all cohort ids
     */
    public Cursor getCohortIds() {
    	return mDb.query(CohortTable.TABLE_NAME, new String[] {CohortTable.ID.getName()}, null, null, null, null, null);
    }
    
    /**
     * Gets all patientid for a particular cohort id
     */
    public Cursor getPatientsByCohort(int id){
    	return mDb.rawQuery(DbConstants.PATIENT_MANAGER_COHORT_LIST_QUERY, new String[] {id +""});
    }

    /**
     * Gets all patient ids who are checked.
     */
    public Cursor getCheckedPatientIds() {
    	return mDb.query(PatientTable.TABLE_NAME,new String[] {PatientTable.ID.getName()}, PatientTable.CHECKED.getName() +"!=0"  , null, null, null, null);
    }
    
    /**
     * Returns whether or not a patient is checked.
     */
    public boolean isChecked(long patientId) {
    	Cursor cursor = mDb.query(PatientTable.TABLE_NAME,
    							  new String[] {PatientTable.ID.getName()}, 
    							  PatientTable.CHECKED.getName() +"!=0 AND " + PatientTable.ID.getName() + "=" + patientId, 
    							  null, null, null, null);
    	boolean ret = cursor.moveToFirst();
    	cursor.close();
    	return ret;
    }
    
    /**
     *  Server Synchronization methods
     */
    
    /**
     * Inserts given Encounter into database
     */
    public void insertEncounter(Encounter e) {
    	ContentValues values = new ContentValues();
    	values.put(EncounterTable.ID.getName(), e.getEncounterId());
    	values.put(EncounterTable.LOCATION_ID.getName(), e.getLocationId());
    	values.put(EncounterTable.PATIENT_ID.getName(), e.getPatientId());
    	values.put(EncounterTable.PROVIDER_ID.getName(), e.getProviderId());
    	values.put(EncounterTable.DATE_CREATED.getName(), e.getDateCreated().toString()); //TODO may not work
    	values.put(EncounterTable.DATETIME.getName(), e.getDateEncountered().toString());
    	//values.put(EncounterTable, e.getEncounterId()); //TODO need to fix datamodel
    	values.put(EncounterTable.CREATOR.getName(), e.getCreator());
    	values.put(EncounterTable.VOIDED.getName(), 0);
    	mDb.insert(EncounterTable.TABLE_NAME, null, values);
    }
    
    /** 
     * Inserts given Observation into database
     */
    public void insertObservation(Observation o) {
    	ContentValues values = new ContentValues();
    	values.put(ObservationTable.ID.getName(), o.getEncounterId());
    	values.put(ObservationTable.CONCEPT_ID.getName(), o.getConceptId());
    	values.put(ObservationTable.PATIENT_ID.getName(), o.getPatientId());
    	values.put(ObservationTable.NUMERIC.getName(), o.getValue());
    	values.put(ObservationTable.TEXT.getName(), o.getText());
    	values.put(ObservationTable.BOOLEAN.getName(), o.getValueBoolean());
    	values.put(ObservationTable.VOIDED.getName(), 0); //TODO Assuming observation not voided
    	values.put(ObservationTable.DATE_CREATED.getName(), o.getDateCreated().toString()); //TODO may not work
    	values.put(ObservationTable.DATETIME.getName(), o.getDate().toString());
    	values.put(ObservationTable.CREATOR.getName(), o.getCreator());
    	mDb.insert(ObservationTable.TABLE_NAME, null, values);
    }
    
    /**
     * Inserts given Program into database
     */
    public void insertProgram(Program pr) {
    	ContentValues values = new ContentValues();
    	values.put(ProgramTable.ID.getName(), pr.getProgramId());
    	values.put(ProgramTable.CONCEPT_ID.getName(), pr.getConceptId());
    	mDb.insert(ProgramTable.TABLE_NAME, null, values);
    }
    
    /**
     * Inserts given PatientProgram into database.
     */
    public void insertPatientProgram(PatientProgram ppr) {
    	ContentValues values = new ContentValues();
    	values.put(PatientProgramTable.PATIENT_ID.getName(), ppr.getPatientId());
    	values.put(PatientProgramTable.PROGRAM_ID.getName(), ppr.getProgramId());
    	mDb.insert(PatientProgramTable.TABLE_NAME, null, values);
    }
    
    /**
     * Inserts given Patient into database
     */
    public void insertPatient(Patient p) {
    	ContentValues values = new ContentValues();
    	values.put(PatientTable.ID.getName(), p.getPatientId());
    	values.put(PatientTable.GENDER.getName(), p.getGender());
		values.put(PatientTable.RACE.getName(), p.getRace());
		values.put(PatientTable.BIRTHDATE.getName(), p.getBirth().toString());
		values.put(PatientTable.DEAD.getName(), p.getDead());
		values.put(PatientTable.BIRTHPLACE.getName(), p.getBirthplace());
		values.put(PatientTable.HEIGHT.getName(), p.getHeight());
		values.put(PatientTable.WEIGHT.getName(), p.getWeight());
		values.put(PatientTable.NAME.getName(), p.getName());
    	mDb.insert(PatientTable.TABLE_NAME, null, values);
    }
} 
