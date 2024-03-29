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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.odkclinic.client.db.tables.ClientEncounterTable;
import com.odkclinic.client.db.tables.ClientObservationTable;
import com.odkclinic.client.db.tables.CohortMemberTable;
import com.odkclinic.client.db.tables.CohortTable;
import com.odkclinic.client.db.tables.ConceptNameTable;
import com.odkclinic.client.db.tables.ConceptTable;
import com.odkclinic.client.db.tables.EncounterTable;
import com.odkclinic.client.db.tables.LocationTable;
import com.odkclinic.client.db.tables.ObservationTable;
import com.odkclinic.client.db.tables.PatientProgramTable;
import com.odkclinic.client.db.tables.PatientTable;
import com.odkclinic.client.db.tables.ProgramTable;
import com.odkclinic.client.db.tables.ProgramWorkflowTable;
import com.odkclinic.client.db.tables.SettingsTable;
import com.odkclinic.client.db.tables.UpdatesTable;
import com.odkclinic.client.db.tables.VisitedTable;
import com.odkclinic.model.Cohort;
import com.odkclinic.model.CohortMember;
import com.odkclinic.model.Concept;
import com.odkclinic.model.ConceptName;
import com.odkclinic.model.Encounter;
import com.odkclinic.model.Location;
import com.odkclinic.model.Observation;
import com.odkclinic.model.Patient;
import com.odkclinic.model.PatientProgram;
import com.odkclinic.model.Program;
import com.odkclinic.model.ProgramWorkflow;
import com.odkclinic.model.bundle.CohortBundle;
import com.odkclinic.model.bundle.CohortMemberBundle;
import com.odkclinic.model.bundle.ConceptBundle;
import com.odkclinic.model.bundle.ConceptNameBundle;
import com.odkclinic.model.bundle.EncounterBundle;
import com.odkclinic.model.bundle.LocationBundle;
import com.odkclinic.model.bundle.ObservationBundle;
import com.odkclinic.model.bundle.PatientBundle;
import com.odkclinic.model.bundle.PatientProgramBundle;
import com.odkclinic.model.bundle.ProgramBundle;
import com.odkclinic.model.bundle.ProgramWorkflowBundle;

/**
 * Main interface for subset OpenMrs database.
 * 
 * @author Euzel Villanueva
 */
public class DbAdapter {

	private static final String LOG_TAG = DbAdapter.class.getName();
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mCtx;

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
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
	 * @throws SQLException
	 *             if the database could be neither opened or created
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
	 * Column 1: Patient Name Column 2: Cohort ID Column 3: Patient ID
	 * 
	 * @return Returns a cursor for list of patients to be viewed
	 */
	public Cursor getPatientList() {
		return mDb.rawQuery(DbConstants.PATIENT_LIST_QUERY, null);
	}

	/**
	 * Column 1: Patient ID Column 2: Patient Name Column 3: BeenVisted Column
	 * 4: Number of Updates for patient
	 * 
	 * @return Returns a cursor for list of patients to be viewed
	 */
	public Cursor getPatientManagerList() {
		return mDb.rawQuery(DbConstants.PATIENT_MANAGER_LIST_QUERY, null);
	}

	/**
	 * Column 1: Patient Gender Column 2: Patient Race Column 3: Patient
	 * Birthdate Column 4: Patient Height //TODO: Still not sure how to get this
	 * 
	 * @param id
	 * @return Returns patient specific demographic information
	 */
	public Cursor getPatientDemographicInfo(long id) {
		return mDb.rawQuery(DbConstants.PATIENT_DEMOGRAPHIC_QUERY,
				new String[] { id + "" });
	}

	/**
	 * Column 1: Patient ID Column 2: Patient Name Column 3: Cohort Id Column 4:
	 * Update Name
	 * 
	 * Sorted by patient id, then update name, then update date
	 * 
	 * @param id
	 *            Patient whose update history we want.
	 * @return Returns a cursor to a list of updates left for the patent id
	 *         specified.
	 */
	public Cursor getUpdateList(long id) {
		return mDb.rawQuery(DbConstants.PATIENT_UPDATE_LIST_QUERY,
				new String[] { id + "" });
	}

	/**
	 * Column 1: Patient ID Column 2: Patient Name Column 3: Cohort Id Column 4:
	 * Update Name
	 * 
	 * Sorted by patient id, then update name, then update date
	 * 
	 * @return Returns a cursor to a list of all updates left.
	 */
	public Cursor getUpdateList() {
		return mDb.rawQuery(DbConstants.ALL_UPDATE_LIST_QUERY, null);
	}

	/**
	 * Column 1 Update Name Column 2 Update Date
	 * 
	 * @return
	 */
	public Cursor getUpdateDetails(long id) {
		return mDb.query(UpdatesTable.TABLE_NAME, new String[] {
				UpdatesTable.NAME.getName(), UpdatesTable.DATE.getName(),
				UpdatesTable.TYPE.getName(), UpdatesTable.TYPE_ID.getName() },
				UpdatesTable.ID.getName() + "=" + id, null, null, null, null);
	}

	/**
	 * 
	 * @param concept_id
	 * @param patient_id
	 * @return Returns a cursor to a list of histoical values availible for the
	 *         given concept and patient id.
	 */
	public Cursor getConceptHistory(long concept_id, long patient_id) {
		return mDb.rawQuery(DbConstants.CONCEPT_HISTORY_QUERY, new String[] {
				concept_id + "", patient_id + "" });
	}

	/**
	 * 
	 * @param patient_id
	 * @return Returns a cursor to a list of histoical encounters availible for
	 *         the given patient id.
	 */
	public Cursor getEncounterHistory(long patient_id) {
		return mDb.rawQuery(DbConstants.ENCOUNTER_HISTORY_QUERY,
				new String[] { patient_id + "" });
	}

	/**
	 * 
	 * @param concept_id
	 * @return Returns a cursor the details of the concept
	 */
	public Cursor getConcept(long concept_id) {
		return mDb.rawQuery(DbConstants.CONCEPT_QUERY,
				new String[] { concept_id + "" });
	}

	/**
	 * 
	 * @param id
	 *            ID of patient to be deleted
	 * 
	 */
	public void deletePatient(long id) {
		mDb.delete(PatientTable.TABLE_NAME, PatientTable.ID.getName() + "="
				+ id, null);
	}

	/**
	 * 
	 * @param id
	 *            ID of patient to be marked visited
	 * @return sucess of marking patient visited
	 */
	public void markPatientVisited(long id) {
		ContentValues values = new ContentValues();
		values.put(VisitedTable.BEEN_VISITED.getName(), "Already Visited.");
		mDb.update(VisitedTable.TABLE_NAME, values, VisitedTable.PATIENT_ID
				.getName()
				+ "=" + id, null);
	}

	/**
	 * 
	 * @param id
	 *            ID of patient to be marked unvisited
	 * @return sucess of marking patient unvisited
	 */
	public void unmarkPatientVisited(long id) {
		ContentValues values = new ContentValues();
		values.put(VisitedTable.BEEN_VISITED.getName(), "Already Visited.");
		mDb.update(VisitedTable.TABLE_NAME, values, VisitedTable.PATIENT_ID
				.getName()
				+ "=" + id, null);
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
		values.put(ClientObservationTable.TEXT.getName(), value);
		values.put(ClientObservationTable.CREATOR.getName(), 1);
		values.put(ClientObservationTable.VOIDED.getName(), 0);
		values.put(ClientObservationTable.CONCEPT_ID.getName(), concept_id);
		values.put(ClientObservationTable.PATIENT_ID.getName(), patient_id);
		mDb.insert(ClientObservationTable.TABLE_NAME, null, values);
	}

	public void newObservation(long concept_id, long patient_id, Double value) {
		ContentValues values = new ContentValues();
		values.put(ClientObservationTable.NUMERIC.getName(), value);
		values.put(ClientObservationTable.CREATOR.getName(), 1);
		values.put(ClientObservationTable.VOIDED.getName(), 0);
		values.put(ClientObservationTable.CONCEPT_ID.getName(), concept_id);
		values.put(ClientObservationTable.PATIENT_ID.getName(), patient_id);
		mDb.insert(ClientObservationTable.TABLE_NAME, null, values);
	}

	public void newObservation(long concept_id, long patient_id, Date value) {
		ContentValues values = new ContentValues();
		Date date = value;
		values.put(ClientObservationTable.DATETIME.getName(),
				date != null ? mDateFormat.format(date) : mZeroDate);
		values.put(ClientObservationTable.CREATOR.getName(), 1);
		values.put(ClientObservationTable.VOIDED.getName(), 0);
		values.put(ClientObservationTable.CONCEPT_ID.getName(), concept_id);
		values.put(ClientObservationTable.PATIENT_ID.getName(), patient_id);
		mDb.insert(ClientObservationTable.TABLE_NAME, null, values);
	}

	public void newObservation(long concept_id, long patient_id, Boolean value) {
		ContentValues values = new ContentValues();
		values.put(ClientObservationTable.BOOLEAN.getName(), value);
		values.put(ClientObservationTable.VOIDED.getName(), 0);
		values.put(ClientObservationTable.CREATOR.getName(), 1);
		values.put(ClientObservationTable.CONCEPT_ID.getName(), concept_id);
		values.put(ClientObservationTable.PATIENT_ID.getName(), patient_id);
		mDb.insert(ClientObservationTable.TABLE_NAME, null, values);
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
		return mDb.rawQuery(DbConstants.GET_PATIENT_CONCEPTS,
				new String[] { patientId + "" });
	}

	/**
	 * Sets checkbox value for patient
	 */
	public void setPatientCheck(int check, int patientId) {
		ContentValues values = new ContentValues();
		values.put(PatientTable.CHECKED.getName(), check);
		mDb.update(PatientTable.TABLE_NAME, values, PatientTable.ID.getName()
				+ "=" + patientId, null);
	}

	/**
	 * Gets all patient ids.
	 */
	public Cursor getPatientIds() {
		return mDb.query(PatientTable.TABLE_NAME,
				new String[] { PatientTable.ID.getName() }, null, null, null,
				null, null);
	}

	/**
	 * Gets all cohort ids
	 */
	public Cursor getCohortIds() {
		return mDb.query(CohortTable.TABLE_NAME, new String[] { CohortTable.ID
				.getName() }, null, null, null, null, null);
	}

	/**
	 * Gets all patientid for a particular cohort id
	 */
	public Cursor getPatientsByCohort(int id) {
		return mDb.rawQuery(DbConstants.PATIENT_MANAGER_COHORT_LIST_QUERY,
				new String[] { id + "" });
	}

	/**
	 * Gets all patient ids who are checked.
	 */
	public Cursor getCheckedPatientIds() {
		return mDb.query(PatientTable.TABLE_NAME,
				new String[] { PatientTable.ID.getName() },
				PatientTable.CHECKED.getName() + "!=0", null, null, null, null);
	}

	/**
	 * Returns whether or not a patient is checked.
	 */
	public boolean isChecked(long patientId) {
		Cursor cursor = mDb.query(PatientTable.TABLE_NAME,
				new String[] { PatientTable.ID.getName() },
				PatientTable.CHECKED.getName() + "!=0 AND "
						+ PatientTable.ID.getName() + "=" + patientId, null,
				null, null, null);
		boolean ret = cursor.moveToFirst();
		cursor.close();
		return ret;
	}

	/**
	 * Server Synchronization methods
	 */

	private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String mZeroDate = "0000-00-00 00:00:00";

	public void insertOrUpdate(String table, String where, ContentValues values) {
		Cursor cursor = mDb.query(table, null, where, null, null, null, null);
		boolean isEmpty = cursor.getCount() != 0;
		cursor.close();
		try {
		if (isEmpty) {
			mDb.update(table, values, where, null);
		} else
			mDb.insert(table, where, values);
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}

	public void insertEncounterBundle(EncounterBundle eb) {
		for (Encounter e : eb.getBundle()) {
			String table = EncounterTable.TABLE_NAME;
			String where = EncounterTable.ID.getName() + "="
					+ e.getEncounterId();
			ContentValues values = getValues(e);
			insertOrUpdate(table, where, values);
		}
	}
	
	public ContentValues getValues(Encounter e) {
		ContentValues values = new ContentValues();
		values.put(EncounterTable.ID.getName(), e.getEncounterId());
		values.put(EncounterTable.LOCATION_ID.getName(), e.getLocationId());
		values.put(EncounterTable.PATIENT_ID.getName(), e.getPatientId());
		values.put(EncounterTable.PROVIDER_ID.getName(), e.getProviderId());
		Date date = e.getDateCreated();
		values.put(EncounterTable.DATE_CREATED.getName(),
				date != null ? mDateFormat.format(date) : mZeroDate);
		date = e.getDateEncountered();
		values.put(EncounterTable.DATETIME.getName(),
				date != null ? mDateFormat.format(date) : mZeroDate);
		values.put(EncounterTable.ID.getName(), e.getEncounterId());
		values.put(EncounterTable.CREATOR.getName(), e.getCreator());
		values.put(EncounterTable.VOIDED.getName(), 0);
		return values;
	}

	public void insertObservationBundle(ObservationBundle ob) {
		for (Observation o : ob.getBundle()) {
			String table = ObservationTable.TABLE_NAME;
			String where = ObservationTable.ID.getName() + "=" + o.getObsId();
			ContentValues values = getValues(o);
			insertOrUpdate(table, where, values);
		}
	}

	public ContentValues getValues(Observation o) {
		ContentValues values = new ContentValues();
		values.put(ObservationTable.ID.getName(), o.getObsId());
		values.put(ObservationTable.ENCOUNTER_ID.getName(), o.getEncounterId());
		values.put(ObservationTable.CONCEPT_ID.getName(), o.getConceptId());
		values.put(ObservationTable.PATIENT_ID.getName(), o.getPatientId());
		values.put(ObservationTable.NUMERIC.getName(), o.getValue());
		values.put(ObservationTable.TEXT.getName(), o.getText());
		values.put(ObservationTable.BOOLEAN.getName(), o.getValueBoolean());
		values.put(ObservationTable.VOIDED.getName(), 0);
		Date date = o.getDateCreated();
		values.put(ObservationTable.DATE_CREATED.getName(),
				date != null ? mDateFormat.format(date) : mZeroDate);
		date = o.getDate();
		values.put(ObservationTable.DATETIME.getName(),
				date != null ? mDateFormat.format(date) : mZeroDate);
		values.put(ObservationTable.CREATOR.getName(), o.getCreator());
		return values;
	}

	public void insertProgramBundle(ProgramBundle prb) {
		for (Program pr : prb.getBundle()) {
			String table = ProgramTable.TABLE_NAME;
			String where = ProgramTable.ID.getName() + "=" + pr.getProgramId();
			ContentValues values = getValues(pr);
			insertOrUpdate(table, where, values);
			;
		}
	}

	public ContentValues getValues(Program pr) {
		ContentValues values = new ContentValues();
		values.put(ProgramTable.ID.getName(), pr.getProgramId());
		values.put(ProgramTable.CONCEPT_ID.getName(), pr.getConceptId());
		return values;
	}

	public void insertPatientProgramBundle(PatientProgramBundle prb) {
		for (PatientProgram pr : prb.getBundle()) {
			String table = PatientProgramTable.TABLE_NAME;
			String where = PatientProgramTable.PROGRAM_ID.getName() + "="
					+ pr.getProgramId() + " AND " + PatientProgramTable.PATIENT_ID.getName() + "="
                    + pr.getPatientId();
			ContentValues values = getValues(pr);
			insertOrUpdate(table, where, values);
		}
	}

	public ContentValues getValues(PatientProgram ppr) {
		ContentValues values = new ContentValues();
		if (ppr.getPatientProgramId() != null)
		    values.put(PatientProgramTable.ID.getName(), ppr.getPatientProgramId());
		
		values
				.put(PatientProgramTable.PATIENT_ID.getName(), ppr
						.getPatientId());
		values
				.put(PatientProgramTable.PROGRAM_ID.getName(), ppr
						.getProgramId());
		return values;
	}
	
	public void insertProgramWorkflowBundle(ProgramWorkflowBundle prb) {
        for (ProgramWorkflow pr : prb.getBundle()) {
            String table = ProgramWorkflowTable.TABLE_NAME;
            String where = ProgramWorkflowTable.ID.getName() + "="
                    + pr.getProgramWorkflowId();
            ContentValues values = getValues(pr);
            insertOrUpdate(table, where, values);
        }
    }

    public ContentValues getValues(ProgramWorkflow pw) {
        ContentValues values = new ContentValues();
        values.put(ProgramWorkflowTable.ID.getName(), pw.getProgramWorkflowId());
        values.put(ProgramWorkflowTable.CONCEPT_ID.getName(), pw.getConceptId());
        values.put(ProgramWorkflowTable.PROGRAM_ID.getName(), pw.getProgramId());
        return values;
    }

	public void insertPatientBundle(PatientBundle pb) {
		for (Patient p : pb.getBundle()) {
			String table = PatientTable.TABLE_NAME;
			String where = PatientTable.ID.getName() + "=" + p.getPatientId();
			ContentValues values = getValues(p);
			insertOrUpdate(table, where, values);
		}
	}

	public ContentValues getValues(Patient p) {
		ContentValues values = new ContentValues();
		values.put(PatientTable.ID.getName(), p.getPatientId());
		values.put(PatientTable.GENDER.getName(), p.getGender());
		values.put(PatientTable.RACE.getName(), p.getRace());
		Date date = p.getBirth();
		values.put(PatientTable.BIRTHDATE.getName(), date != null ? mDateFormat
				.format(date) : mZeroDate);
		values.put(PatientTable.DEAD.getName(), p.getDead());
		values.put(PatientTable.BIRTHPLACE.getName(), p.getBirthplace());
		values.put(PatientTable.HEIGHT.getName(), p.getHeight());
		values.put(PatientTable.WEIGHT.getName(), p.getWeight());
		values.put(PatientTable.NAME.getName(), p.getName());
		return values;
	}

	public void insertConceptBundle(ConceptBundle pb) {
		for (Concept c : pb.getBundle()) {
			String table = ConceptTable.TABLE_NAME;
			String where = ConceptTable.ID.getName() + "=" + c.getConceptId();
			ContentValues values = getValues(c);
			insertOrUpdate(table, where, values);
		}
	}

	public ContentValues getValues(Concept p) {
		ContentValues values = new ContentValues();
		values.put(ConceptTable.ID.getName(), p.getConceptId());
		values.put(ConceptTable.DATATYPE_ID.getName(), p.getDatatypeId());
		values.put(ConceptTable.CLASS_ID.getName(), p.getClassId());
		values.put(ConceptTable.IS_SET.getName(), p.getIsSet());
		values.put(ConceptTable.RETIRED.getName(), p.isRetired());
		values.put(ConceptTable.CREATOR.getName(), p.getCreator());
		Date date = p.getDateCreated();
		values.put(ConceptTable.DATE_CREATED.getName(),
				date != null ? mDateFormat.format(date) : mZeroDate);
		values.put(ConceptTable.VOIDED.getName(), 0);
		return values;
	}

	public void insertLocationBundle(LocationBundle pb) {
		for (Location p : pb.getBundle()) {
			String table = LocationTable.TABLE_NAME;
			String where = LocationTable.ID.getName() + "=" + p.getLocationId();
			ContentValues values = getValues(p);
			insertOrUpdate(table, where, values);
		}
	}

	public ContentValues getValues(Location p) {
		ContentValues values = new ContentValues();
		values.put(LocationTable.ID.getName(), p.getLocationId());
		values.put(LocationTable.NAME.getName(), p.getName());
		values.put(LocationTable.DESC.getName(), p.getDesc());
		values.put(LocationTable.CREATOR.getName(), p.getCreator());
		Date date = p.getDateCreated();
		values.put(LocationTable.DATE_CREATED.getName(),
				date != null ? mDateFormat.format(date) : mZeroDate);
		return values;
	}

	public void insertConceptNameBundle(ConceptNameBundle pb) {
		for (ConceptName p : pb.getBundle()) {
			String table = ConceptNameTable.TABLE_NAME;
			String where = ConceptNameTable.ID.getName() + "="
					+ p.getConceptNameId();
			ContentValues values = getValues(p);
			insertOrUpdate(table, where, values);
		}
	}

	public ContentValues getValues(ConceptName p) {
		ContentValues values = new ContentValues();
		values.put(ConceptNameTable.ID.getName(), p.getConceptNameId());
		values.put(ConceptNameTable.NAME.getName(), p.getName());
		values.put(ConceptNameTable.CONCEPT_ID.getName(), p.getConceptId());
		return values;
	}

	public void insertCohortBundle(CohortBundle pb) {
		for (Cohort p : pb.getBundle()) {
			String table = CohortTable.TABLE_NAME;
			String where = CohortTable.ID.getName() + "=" + p.getCohortId();
			ContentValues values = getValues(p);
			insertOrUpdate(table, where, values);
		}
	}

	public ContentValues getValues(Cohort p) {
		ContentValues values = new ContentValues();
		values.put(CohortTable.ID.getName(), p.getCohortId());
		values.put(CohortTable.DESC.getName(), p.getCohortDesc());
		values.put(CohortTable.VOIDED.getName(), 0);
		values.put(CohortTable.NAME.getName(), p.getCohortName());
		values.put(CohortTable.CREATOR.getName(), p.getCreator());
		Date date = p.getDateCreated();
		values.put(CohortTable.DATE_CREATED.getName(),
				date != null ? mDateFormat.format(date) : mZeroDate);
		return values;
	}

	public void insertCohortMemberBundle(CohortMemberBundle pb) {
		for (CohortMember p : pb.getBundle()) {
			String table = CohortMemberTable.TABLE_NAME;
			String where = CohortMemberTable.COHORT_ID.getName() + "="
					+ p.getCohortId() + " AND "
					+ CohortMemberTable.PATIENT_ID.getName() + "="
					+ p.getPatientId();
			ContentValues values = getValues(p);
			insertOrUpdate(table, where, values);
		}
	}


	public ContentValues getValues(CohortMember p) {
		ContentValues values = new ContentValues();
		values.put(CohortMemberTable.COHORT_ID.getName(), p.getCohortId());
		values.put(CohortMemberTable.PATIENT_ID.getName(), p.getPatientId());
		return values;
	}

	public EncounterBundle getEncounterBundle() {
		EncounterBundle eb = new EncounterBundle();

		// Send failed bundle first (assumes rev token still the same)
		Cursor encountersCursor = mDb.query(ClientEncounterTable.TABLE_NAME,
				null, ClientEncounterTable.ISUPDATE.getName() + "=1", null,
				null, null, null);

		// Send table instead if no failed bundle.
		if (!encountersCursor.moveToFirst())
			encountersCursor = mDb.query(ClientEncounterTable.TABLE_NAME, null,
					ClientEncounterTable.ISUPDATE.getName() + "=0", null, null,
					null, null);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (encountersCursor.moveToFirst()) {
			do {
				Encounter tempEncounter = null;
				try {
					tempEncounter = new Encounter(
							encountersCursor
									.getInt(encountersCursor
											.getColumnIndex(ClientEncounterTable.PATIENT_ID
													.getName())),
							encountersCursor.getInt(encountersCursor
									.getColumnIndex(ClientEncounterTable.ID
											.getName())),
							0,// encountersCursor.getInt(encountersCursor.getColumnIndex(ClientEncounterTable..getName()),);
							encountersCursor
									.getInt(encountersCursor
											.getColumnIndex(ClientEncounterTable.PROVIDER_ID
													.getName())),
							encountersCursor
									.getInt(encountersCursor
											.getColumnIndex(ClientEncounterTable.LOCATION_ID
													.getName())),
							df
									.parse(encountersCursor
											.getString(encountersCursor
													.getColumnIndex(ClientEncounterTable.DATE_CREATED
															.getName()))),
							df
									.parse(encountersCursor
											.getString(encountersCursor
													.getColumnIndex(ClientEncounterTable.DATE_CREATED
															.getName()))), 0);
					eb.add(tempEncounter);
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				encountersCursor.moveToNext();
			} while (!encountersCursor.isAfterLast());
		}
		Log.i(LOG_TAG, String.format(
				"Returning bundle of size %d from cursor of size %d.", eb
						.getBundle().size(), encountersCursor.getCount()));
		return eb;
	}

	public PatientBundle getPatientBundle() {
		PatientBundle pb = new PatientBundle();
		Cursor patientsCursor = mDb.query(PatientTable.TABLE_NAME, null, null,
				null, null, null, null);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (patientsCursor.moveToFirst()) {
			do {
				Patient tempPatient = null;
				try {
					tempPatient = new Patient();
					tempPatient
							.setPatientId(patientsCursor.getInt(patientsCursor
									.getColumnIndex(PatientTable.ID.getName())));
					tempPatient.setGender(patientsCursor
							.getString(patientsCursor
									.getColumnIndex(PatientTable.GENDER
											.getName())));
					tempPatient.setRace(patientsCursor.getString(patientsCursor
							.getColumnIndex(PatientTable.RACE.getName())));
					tempPatient.setBirth(df.parse(patientsCursor
							.getString(patientsCursor
									.getColumnIndex(PatientTable.BIRTHDATE
											.getName()))));
					tempPatient.setDead(patientsCursor.getInt(patientsCursor
							.getColumnIndex(PatientTable.DEAD.getName())));
					tempPatient.setBirthplace(patientsCursor
							.getString(patientsCursor
									.getColumnIndex(PatientTable.BIRTHPLACE
											.getName())));
					tempPatient.setHeight(0.); // TODO
					tempPatient.setWeight(0.); // TODO
					tempPatient.setName(patientsCursor.getString(patientsCursor
							.getColumnIndex(PatientTable.NAME.getName())));
					pb.add(tempPatient);
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				patientsCursor.moveToNext();
			} while (!patientsCursor.isAfterLast());
		}
		Log.i(LOG_TAG, String.format(
				"Returning bundle of size %d from cursor of size %d.", pb
						.getBundle().size(), patientsCursor.getCount()));
		return pb;
	}

	public ObservationBundle getObservationBundle() {
		ObservationBundle ob = new ObservationBundle();
		Cursor observationsCursor = mDb.query(
				ClientObservationTable.TABLE_NAME, null,
				ClientObservationTable.ISUPDATE.getName() + "=1", null, null,
				null, null);
		if (!observationsCursor.moveToFirst())
			observationsCursor = mDb.query(ClientObservationTable.TABLE_NAME,
					null, ClientObservationTable.ISUPDATE.getName() + "=0",
					null, null, null, null);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (observationsCursor.moveToFirst()) {
			do {
				Observation tempObservation = null;
				try {
					tempObservation = new Observation();
					tempObservation.setObsId(observationsCursor
							.getInt(observationsCursor
									.getColumnIndex(ClientObservationTable.ID
											.getName())));
					tempObservation
							.setPatientId(observationsCursor
									.getInt(observationsCursor
											.getColumnIndex(ClientObservationTable.PATIENT_ID
													.getName())));
					tempObservation
							.setConceptId(observationsCursor
									.getInt(observationsCursor
											.getColumnIndex(ClientObservationTable.CONCEPT_ID
													.getName())));
					tempObservation
							.setEncounterId(observationsCursor
									.getInt(observationsCursor
											.getColumnIndex(ClientObservationTable.ENCOUNTER_ID
													.getName())));
					tempObservation
							.setDateCreated(df
									.parse(observationsCursor
											.getString(observationsCursor
													.getColumnIndex(ClientObservationTable.DATE_CREATED
															.getName()))));
					tempObservation
							.setDate(df
									.parse(observationsCursor
											.getString(observationsCursor
													.getColumnIndex(ClientObservationTable.DATETIME
															.getName()))));
					tempObservation.setText(observationsCursor
							.getString(observationsCursor
									.getColumnIndex(ClientObservationTable.TEXT
											.getName())));
					tempObservation
							.setValue(observationsCursor
									.getDouble(observationsCursor
											.getColumnIndex(ClientObservationTable.NUMERIC
													.getName())));
					tempObservation
							.setValueBoolean(observationsCursor
									.getInt(observationsCursor
											.getColumnIndex(ClientObservationTable.BOOLEAN
													.getName())) == 0 ? false
									: true);
					tempObservation.setCreator(0); // TODO
					ob.add(tempObservation);
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				observationsCursor.moveToNext();
			} while (!observationsCursor.isAfterLast());
		}
		Log.i(LOG_TAG, String.format(
				"Returning bundle of size %d from cursor of size %d.", ob
						.getBundle().size(), observationsCursor.getCount()));
		return ob;
	}

	public ProgramBundle getProgramBundle() {
		ProgramBundle pb = new ProgramBundle();
		Cursor programsCursor = mDb.query(ProgramTable.TABLE_NAME, null, null,
				null, null, null, null);
		if (programsCursor.moveToFirst()) {
			do {
				Program tempProgram = null;
				try {
					tempProgram = new Program();
					tempProgram
							.setProgramId(programsCursor.getInt(programsCursor
									.getColumnIndex(ProgramTable.ID.getName())));
					tempProgram.setConceptId(programsCursor
							.getInt(programsCursor
									.getColumnIndex(ProgramTable.CONCEPT_ID
											.getName())));
					pb.add(tempProgram);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				programsCursor.moveToNext();
			} while (!programsCursor.isAfterLast());
		}
		Log.i(LOG_TAG, String.format(
				"Returning bundle of size %d from cursor of size %d.", pb
						.getBundle().size(), programsCursor.getCount()));
		return pb;
	}

	public PatientProgramBundle getPatientProgramBundle() {
		PatientProgramBundle pb = new PatientProgramBundle();
		Cursor patientProgramsCursor = mDb.query(
				PatientProgramTable.TABLE_NAME, null, null, null, null, null,
				null);
		if (patientProgramsCursor.moveToFirst()) {
			do {
				PatientProgram tempPatientProgram = null;
				try {
					tempPatientProgram = new PatientProgram();
					tempPatientProgram
							.setPatientProgramId(patientProgramsCursor
									.getInt(patientProgramsCursor
											.getColumnIndex(PatientProgramTable.ID
													.getName())));
					tempPatientProgram
							.setPatientId(patientProgramsCursor
									.getInt(patientProgramsCursor
											.getColumnIndex(PatientProgramTable.PATIENT_ID
													.getName())));
					tempPatientProgram
							.setProgramId(patientProgramsCursor
									.getInt(patientProgramsCursor
											.getColumnIndex(PatientProgramTable.PROGRAM_ID
													.getName())));
					pb.add(tempPatientProgram);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				patientProgramsCursor.moveToNext();
			} while (!patientProgramsCursor.isAfterLast());
		}
		Log.i(LOG_TAG, String.format(
				"Returning bundle of size %d from cursor of size %d.", pb
						.getBundle().size(), patientProgramsCursor.getCount()));
		return pb;
	}

	/**
	 * Protocol V2 DB Functions
	 */

	/**
	 * Check syncronization lock status.
	 * 
	 * @return Returns true if table is locked, false otherwise.
	 */
	public boolean checkSync() {
		Cursor bool = mDb.query(SettingsTable.TABLE_NAME,
				new String[] { SettingsTable.NUMERIC_VALUE.getName() },
				SettingsTable.NAME.getName() + "='LOCK'", null, null, null,
				null);
		if (bool.moveToFirst()) {
			return bool.getInt(0) == 1 ? true : false;
		} else {
			return false;
		}
	}

	public void markSync(boolean mark) {
		ContentValues values = new ContentValues();
		values.put(SettingsTable.NUMERIC_VALUE.getName(), mark ? 1 : 0);
		mDb.update(SettingsTable.TABLE_NAME, values, SettingsTable.NAME
				.getName()
				+ "='LOCK'", null);
	}

	public long getRevToken() {
		Cursor tok = mDb
				.query(SettingsTable.TABLE_NAME,
						new String[] { SettingsTable.NUMERIC_VALUE.getName() },
						SettingsTable.NAME.getName() + "='REV'", null, null,
						null, null);
		tok.moveToFirst();
		return tok.getLong(0);
	}

	public void setRevToken(long token) {
		ContentValues values = new ContentValues();
		values.put(SettingsTable.NUMERIC_VALUE.getName(), token);
		mDb.update(SettingsTable.TABLE_NAME, values, SettingsTable.NAME
				.getName()
				+ "='REV'", null);
	}

	public void markEncountersFailed(EncounterBundle eb) {
	    if (eb == null || eb.getBundle().size() == 0)
            return;
		ContentValues values = new ContentValues();
		values.put(ClientEncounterTable.ISUPDATE.getName(), 0);
		StringBuilder sb = new StringBuilder();
		sb.append(ClientEncounterTable.ID.getName());
		sb.append("=");
		sb.append(eb.getBundle().get(0).getEncounterId());
		for (int i = 1; i < eb.getBundle().size(); i++) {
			sb.append(" OR ");
			sb.append(ClientEncounterTable.ID.getName());
			sb.append(" = ");
			sb.append(eb.getBundle().get(i).getEncounterId());
			sb.append(" ");
		}
		mDb.update(ClientEncounterTable.TABLE_NAME, values, sb.toString(), null);
	}

	public void markObservationsFailed(ObservationBundle ob) {
	    if (ob == null || ob.getBundle().size() == 0)
	        return;
		ContentValues values = new ContentValues();
		values.put(ClientObservationTable.ISUPDATE.getName(), 0);
		StringBuilder sb = new StringBuilder();
		sb.append(ClientObservationTable.ID.getName());
		sb.append("=");
		sb.append(ob.getBundle().get(0).getObsId());
		for (int i = 1; i < ob.getBundle().size(); i++) {
			sb.append(" OR ");
			sb.append(ClientObservationTable.ID.getName());
			sb.append(" = ");
			sb.append(ob.getBundle().get(i).getObsId());
			sb.append(" ");
		}
		mDb.update(ClientObservationTable.TABLE_NAME, values, sb.toString(), null);
	}

	public void deleteSyncedEncounters(EncounterBundle eb) {
	    if (eb == null || eb.getBundle().size() == 0)
            return;
		StringBuilder sb = new StringBuilder();
		for (Encounter e : eb.getBundle()) {
			sb.append(" OR ");
			sb.append(ClientEncounterTable.ID.getName());
			sb.append(" = ");
			sb.append(e.getEncounterId());
			sb.append(" ");
		}
		mDb.delete(ClientEncounterTable.TABLE_NAME, ClientEncounterTable.ISUPDATE.getName() + "=1" + sb.toString(), null);
	}

	public void deleteSyncedObservations(ObservationBundle ob) {
	    if (ob == null || ob.getBundle().size() == 0)
            return;
		StringBuilder sb = new StringBuilder();
		for (Observation o : ob.getBundle()) {
			sb.append(" OR ");
			sb.append(ClientObservationTable.ID.getName());
			sb.append(" = ");
			sb.append(o.getObsId());
			sb.append(" ");
		}
		mDb.delete(ClientObservationTable.TABLE_NAME, ClientObservationTable.ISUPDATE.getName() + "=1" + sb.toString(), null);
	}

}
