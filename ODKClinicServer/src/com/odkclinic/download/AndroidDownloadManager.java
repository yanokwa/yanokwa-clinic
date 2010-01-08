package com.odkclinic.download;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openmrs.Location;
import org.openmrs.Patient;

import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.CohortService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;

import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;

import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;

import com.odkclinic.model.Encounter;
import com.odkclinic.model.EncounterBundle;
import com.odkclinic.model.Observation;
import com.odkclinic.model.ObservationBundle;
import com.odkclinic.model.PatientBundle;
import com.odkclinic.model.ProgramBundle;

import org.openmrs.EncounterType;

//import org.openmrs.module.xforms.model.Observation;

//import ca.uhn.hl7v2.model.v24.segment.ORG;

/**
 * Manages downloads and uploads for Android application
 * 
 * @author Owen Kim
 *
 */
public class AndroidDownloadManager {

	private static Log log = LogFactory.getLog(AndroidDownloadManager.class);
	
		/**
		 * Retrieves all programs, bundles them, and sends them through the OutputStream
		 * @param os
		 * @param serializerKey (no use)
		 * @return true if successful, false otherwise
		 */
	public static boolean downloadPrograms(OutputStream os, String serializerKey) {
		ProgramBundle bundle = getPrograms();
		
		try {
			bundle.write((DataOutputStream)os);			
		} catch (IOException e) {
			log.error(bundle, e);
			return false;
		}
		
		return true;
	}
	/**
	 * Retrieves all patients, bundles them, and sends them through the OutputStream
	 * @param os
	 * @param serializerKey (no use)
	 * @return true if successful, false otherwise
	 */
	public static boolean downloadPatients(OutputStream os, String serializerKey) {
		PatientBundle bundle = getPatients();
		
		try {
			bundle.write((DataOutputStream) os);
		} catch  (IOException e) {
			log.error(bundle, e);
			return false;
		}
		
		return true;
	}
	/**
	 * Retrieves all encounters, bundles them, and sends them through the OutputStream
	 * @param os
	 * @param serializerKey (no use)
	 * @return true if successful, false otherwise
	 */
	public static boolean downloadEncounters(Integer patientId, OutputStream os, String serializerKey) {
		EncounterBundle bundle = getEncounters(patientId);
		
		try {
			bundle.write((DataOutputStream)os);
		} catch (IOException e) {
			log.error(bundle, e);
			return false;
		}
		
		return true;
	}
	/**
	 * Retrieves all observations, bundles them, and sends them through the OutputStream
	 * @param os
	 * @param serializerKey (no use)
	 * @return true if successful, false otherwise
	 */
	public static boolean downloadObservations(Integer patientId, OutputStream os, String serializerKey) {
		ObservationBundle bundle = getObservations(patientId);
		
		try {
			bundle.write((DataOutputStream)os);
		} catch (IOException e) {
			log.error(bundle, e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Retrieves encounters from InputStream and saves them to server
	 * @param is
	 * @param serializerKey
	 * @return true if successful, false otherwise
	 */
	public static boolean uploadEncounters(InputStream is, String serializerKey) {
		EncounterBundle bundle = new EncounterBundle();
		try {
			bundle.read((DataInputStream)is);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
		EncounterService encounterService = Context.getEncounterService();
		UserService userService = Context.getUserService();
		
		Vector<Encounter> encounters = bundle.getBundle();
		for (Encounter enc : encounters) {
			User prov = userService.getUser(enc.getProviderId()); //provider and creator from phone are the same
			org.openmrs.Encounter inEnc = new org.openmrs.Encounter();
			inEnc.setEncounterId(enc.getEncounterId());
			inEnc.setEncounterType(new EncounterType(enc.getEncounterType()));
			inEnc.setProvider(prov);
			inEnc.setLocation(new Location(enc.getLocationId()));
			inEnc.setDateCreated(enc.getDateCreated());
			inEnc.setEncounterDatetime(enc.getDateEncountered());
			inEnc.setCreator(prov);
			try {
				encounterService.saveEncounter(inEnc);
			} catch (APIException e) {
				log.error(inEnc, e);
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Retrieves all encounters associated with the given patientId and returns their bundle
	 * @param patientId
	 * @return EncounterBundle with all encounters related to patient
	 */
	public static EncounterBundle getEncounters(Integer patientId) {
		EncounterBundle bundle = new EncounterBundle();
		EncounterService encounterService = Context.getEncounterService();
		//if (patientIds != null && patientIds.size() > 0) {
		
			//for (Integer patientId : patientIds) {
				List<org.openmrs.Encounter> encounters = encounterService.getEncountersByPatientId(patientId);
				for (org.openmrs.Encounter inEncounter : encounters) {

					//Integer encounterId = inEncounter.getEncounterId();
					Integer encounterId = null;
					Integer encounterType = inEncounter.getEncounterType().getEncounterTypeId();
					Integer providerId = inEncounter.getProvider().getUserId();
					Integer locationId = inEncounter.getLocation().getLocationId();
					Date dateCreated = inEncounter.getDateCreated();
					Date dateEncountered = inEncounter.getEncounterDatetime();
					Integer creator = inEncounter.getCreator().getUserId();
				
				
					com.odkclinic.model.Encounter outEncounter = new Encounter(patientId, encounterId,
																				encounterType, providerId, locationId,
																				dateCreated, dateEncountered, creator);
					bundle.add(outEncounter);
				
				}
			//}
		//}
		return bundle;
	}
	
	/**
	 * Retrieves observations from InputStream and saves them to server
	 * @param is
	 * @param serializerKey
	 * @return true if successful, false otherwise
	 */
	public static boolean uploadObservations(InputStream is, String serializerKey) {
		ObservationBundle bundle = new ObservationBundle();
		try {
			bundle.read((DataInputStream)is);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
		ObsService obsService = Context.getObsService();
		EncounterService encounterService = Context.getEncounterService();
		UserService userService = Context.getUserService();
		ConceptService conceptService = Context.getConceptService();
		
		Vector<Observation> observations = bundle.getBundle();
		for (Observation obs : observations) {
			
			org.openmrs.Obs inObs = new org.openmrs.Obs();
			//inObs.setObsId(obs.getObsId());
			inObs.setEncounter(encounterService.getEncounter(obs.getEncounterId()));
			inObs.setConcept(conceptService.getConcept(obs.getConceptId()));
			inObs.setValueText(obs.getText());
			inObs.setValueDatetime(obs.getDate());
			inObs.setValueNumeric(obs.getValue());
			inObs.setDateCreated(obs.getDateCreated());

			try {
				obsService.saveObs(inObs, "Changed/added by android phone");
			} catch (APIException e) {
				log.error(inObs, e);
			}
		}
		
		return true;
	}
	/**
	 * Retrieves all observations associated with the given patientId and returns their bundle
	 * @param patientId
	 * @return ObservationBundle with all observations related to patient
	 */
	public static ObservationBundle getObservations(Integer patientId) {
		ObservationBundle bundle = new ObservationBundle();
		ObsService obsService = Context.getObsService();
		PatientService pService = Context.getPatientService();
		//if (patientIds != null && patientIds.size() > 0) {			
				//for (Integer patientId : patientIds) {
					Patient patient = pService.getPatient(patientId);
					List<org.openmrs.Obs> obs = obsService.getObservationsByPerson(patient);
					for (org.openmrs.Obs inObs : obs) {

						Integer obsId = inObs.getObsId();
						Integer encounterId = inObs.getEncounter().getEncounterId();
						Integer conceptId = inObs.getConcept().getConceptId();
						String text = inObs.getValueText();
						Date date = inObs.getValueDatetime();
						Double value = inObs.getValueNumeric();
						Integer creator = inObs.getCreator().getUserId();
						Date dateCreated = inObs.getDateCreated();
					
						Observation outObs = new Observation(obsId, patientId, encounterId,
																					conceptId, text, date,
																					value, creator, dateCreated);
						bundle.add(outObs);
					
					}
				//}
			//}
			return bundle;
	}
	
	/**
	 * Retrieves all patients associated with the hard-coded bundle and returns their bundle
	 * @return PatientBundle with all patients in hard-coded bundle
	 */
	public static PatientBundle getPatients() {
		PatientBundle bundle = new PatientBundle();
		PatientService pService = Context.getPatientService();
		CohortService cService = Context.getCohortService();
		Set<Integer> patients = cService.getCohort(1).getPatientIds(); //TODO unhard-code cohort number
		for (Integer patientId : patients) {
			org.openmrs.Patient patient = pService.getPatient(patientId);
			com.odkclinic.model.Patient outPat = new com.odkclinic.model.Patient();
			
			outPat.setBirth(patient.getBirthdate());
			//birthplace
			if (patient.getDead())
				outPat.setDead(1);
			else
				outPat.setDead(0);
			outPat.setGender(patient.getGender());
			//outPat.setHeight( );
			//outPat.setWeight( );
			outPat.setName(patient.getGivenName() + " " + patient.getFamilyName());
			outPat.setPatientId(patientId);
			//outPat.setRace( );
			
			bundle.add(outPat);
		}
		
		return bundle;
	}
	
	/**
	 * Retrieves all programs
	 * @param patientId
	 * @return ProgramBundle with all programs
	 */
	public static ProgramBundle getPrograms() {
		ProgramBundle bundle = new ProgramBundle();
		ProgramWorkflowService pService = Context.getProgramWorkflowService();
		List<org.openmrs.Program> progs = pService.getAllPrograms();
		
		for (org.openmrs.Program prog : progs) {
			com.odkclinic.model.Program outProg = new com.odkclinic.model.Program();
			
			outProg.setProgramId(prog.getProgramId());
			outProg.setConceptId(prog.getConcept().getConceptId());
			
			bundle.add(outProg);			
		}
		
		return bundle;		
	}
	
}
