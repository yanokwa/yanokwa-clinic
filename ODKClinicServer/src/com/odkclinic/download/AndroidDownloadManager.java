package com.odkclinic.download;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.Program;
import org.openmrs.User;
import org.openmrs.api.CohortService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.xforms.serialization.Persistent;

import com.odkclinic.model.CohortMember;
import com.odkclinic.model.Concept;
import com.odkclinic.model.ConceptName;
import com.odkclinic.model.Encounter;
import com.odkclinic.model.Observation;
import com.odkclinic.model.PatientProgram;
import com.odkclinic.model.bundle.Bundle;
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
import com.odkclinic.server.ODKClinicConstants;
import com.odkclinic.server.ODKClinicService;
import com.odkclinic.server.ODKClinicServiceImpl;

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
	
	public static boolean downloadBundle(DataOutputStream dos, int action, long revToken) {
        Bundle<?> bundle = null;
        switch (action) {
            case ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PROGRAMS:
                bundle = getPrograms();
                break;
            case ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_COHORTMEMBERS:
                bundle = getCohortMembers();
                break;
            case ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_COHORTS:
                bundle = getCohorts();
                break;
            case ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_CONCEPTNAMES:
                bundle = getConceptNames();
                break;
            case ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_CONCEPTS:
                bundle = getConcepts();
                break;
            case ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_LOCATIONS:
                bundle = getCohortMembers();
                break;
            case ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_OBS:
                bundle = getObservations(revToken);
                break;
            case ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_ENCOUNTER:
                bundle = getEncounters(revToken);
                break;
            case ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PATIENTS:
                bundle = getPatients();
                break;
            case ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PATIENTPROGRAMS:
                bundle = getPatientPrograms();
                break;
            
        }
       
        try {
            dos.writeByte(action);
            bundle.write(dos);           
        } catch (IOException e) {
            log.error("odkclinic download failed", e);
            return false;
        }
        return true;
    }
	
	
	/**
	 * Retrieves bundle from InputStream
	 * @param is
	 * @param action
	 * @return true if successful, false otherwise
	 */
	public static Bundle<?> uploadBundle(InputStream is, int action) {
		Bundle<?> bundle = null;
	    switch (action) {
		    case ODKClinicConstants.ACTION_ANDROID_UPLOAD_ENCOUNTER:
		        bundle = new EncounterBundle();
		        break;
		    case ODKClinicConstants.ACTION_ANDROID_UPLOAD_OBS:
		        bundle = new ObservationBundle();
		        break;
		        
		}
		
		if (bundle == null)
		    return null;
		
		boolean success = false;
		
		try {
			bundle.read((DataInputStream)is);
			success = true;
		} catch (IOException e) {
			log.error("odkclinic upload failed", e);
		} catch (InstantiationException e) {
			log.error("odkclinic upload failed", e);
		} catch (IllegalAccessException e) {
			log.error("odkclinic upload failed", e);
		} 
		if (success)
		    return bundle;
		else 
		    return null;
	}
	
	/**
	 * Commits given encounters to server database.
	 * @param eb
	 * @param revToken
	 * @return true if successful, false otherwise
	 */
	public static boolean commitEncounters(EncounterBundle eb, long revToken) {
	    ODKClinicService revService = (ODKClinicService)Context.getService(ODKClinicService.class);
	    EncounterService encounterService = Context.getEncounterService();
        UserService userService = Context.getUserService();
        boolean success = true;
        List<Encounter> encounters = eb.getBundle();
        System.out.println(encounters.size());
        for (Encounter enc: encounters) {
            Date stateToken = revService.getRevisionToken(ODKClinicConstants.ENCOUNTER_TABLE, enc.getEncounterId());
            if (stateToken == null || revToken > stateToken.getTime()) {
                User prov = userService.getUser(enc.getProviderId()); //provider and creator from phone are the same
                org.openmrs.Encounter inEnc = new org.openmrs.Encounter();
                inEnc.setEncounterId(0);
                inEnc.setEncounterType(new EncounterType(enc.getEncounterType()));
                inEnc.setProvider(prov);
                inEnc.setLocation(new Location(enc.getLocationId()));
                inEnc.setDateCreated(enc.getDateCreated());
                inEnc.setEncounterDatetime(enc.getDateEncountered() == null ? new Date() : enc.getDateEncountered());
                inEnc.setCreator(prov);
                inEnc.setPatientId(enc.getPatientId());
                try {
                    encounterService.saveEncounter(inEnc);
                } catch (Exception e) {
                    log.error("Failed committing encounter.");
                    success = false;
                    break;
                }
            }
        }
	    return success;
	}
	
	/**
	 * Retrieves all encounters associated with the given patientId and returns their bundle
	 * @param patientId
	 * @return EncounterBundle with all encounters related to patient
	 */
	public static EncounterBundle getEncounters(long revToken) {
		EncounterBundle bundle = new EncounterBundle();
		CohortService cService = Context.getCohortService();
		Cohort cohort = cService.getCohort(1);
		if (cohort != null) {
            Set<Integer> patients = cohort.getPatientIds();
            System.out.println("NUMBER OF PATIENTS (ENC): " + patients.size());
    		EncounterService encounterService = Context.getEncounterService();
    		if (patients != null && patients.size() > 0) {
    		    PatientService pService = Context.getPatientService();
    			for (Integer patientId : patients) {
    				List<org.openmrs.Encounter> encounters = encounterService.getEncounters(pService.getPatient(patientId),
    													null, /*new Date(revToken)*/null, null, null, null, false);
    				System.out.println("NUMBER OF ENCOUNTERS: " + encounters.size());
    				//List<org.openmrs.Encounter> encounters = encounterService.getEncountersByPatientId(patientId);
    				for (org.openmrs.Encounter inEncounter : encounters) {
    					Integer encounterId = inEncounter.getEncounterId();
    					EncounterType type = inEncounter.getEncounterType();
    					Integer encounterType = null;
    					if (type != null)
    						encounterType = type.getEncounterTypeId();
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
    			}
    		}
		}
		return bundle;
	}
	
	/**
	 * Saves given observations to server database.
	 * @param ob
	 * @param revToken
	 * @return true if successful, false otherwise
	 */
	public static boolean commitObservations(ObservationBundle ob, long revToken) {
	    ODKClinicService revService = (ODKClinicService)Context.getService(ODKClinicService.class);
        ObsService obsService = Context.getObsService();
        EncounterService encounterService = Context.getEncounterService();
        ConceptService conceptService = Context.getConceptService();
        PatientService patientService = Context.getPatientService();
        boolean success = true;
        
        Location loc = Context.getLocationService().getLocation(1); //hard-coded unknown location
        
        List<Observation> observations = ob.getBundle();
        for (Observation obs : observations) {
            Date stateToken = revService.getRevisionToken(ODKClinicConstants.OBS_TABLE, obs.getObsId());
                if (stateToken == null || revToken > stateToken.getTime()) {
                    org.openmrs.Obs inObs = new org.openmrs.Obs();
                    //inObs.setObsId(0);
                    inObs.setEncounter(encounterService.getEncounter(obs.getEncounterId()));
                    inObs.setConcept(conceptService.getConcept(obs.getConceptId()));
                    inObs.setObsDatetime(obs.getDateCreated() == null ? new Date() : obs.getDateCreated());
                    inObs.setValueText(obs.getText());
                    inObs.setValueDatetime(obs.getDate());
                    inObs.setValueNumeric(obs.getValue());
                    inObs.setDateCreated(obs.getDateCreated() == null ? new Date() : obs.getDateCreated());
                    inObs.setPerson(patientService.getPatient(obs.getPatientId()));
                    inObs.setLocation(loc);
                try {
                    obsService.createObs(inObs);
                    //obsService.saveObs(inObs, "Changed/added by android phone");
                } catch (Exception e) {
                    log.error("odkclinic upload failed", e);
                    success = false;
                    break;
                }
            }
        }
	    return success;
	}
	
	/**
	 * Retrieves all observations associated with the given patientId and returns their bundle
	 * @param patientId
	 * @return ObservationBundle with all observations related to patient
	 */
    public static ObservationBundle getObservations(long revToken)
    {
        ObservationBundle bundle = new ObservationBundle();
        ObsService obsService = Context.getObsService();
        PatientService pService = Context.getPatientService();
        CohortService cService = Context.getCohortService();
        Cohort cohort = cService.getCohort(1);
        if (cohort != null)
        {
            Set<Integer> patients = cohort.getPatientIds();
            System.out.println("NUMBER OF PATIENTS (OBS) " + patients.size());
            List<Person> person = new ArrayList<Person>();
            for (Integer patientId : patients)
            {
                Patient patient = pService.getPatient(patientId);
                person.add(patient);
                /*
                 * List<org.openmrs.Obs> obs = obsService
                 * .getObservations(person, null, null, null, null, null, null,
                 * null, null, new Date( revToken), null, false);
                 */
            }
            List<org.openmrs.Obs> obs = obsService
                    .getObservations(person, null, null, null, null, null, null, null, null, null, null, false);
            System.out.println("NUMBER OF OBS " + obs.size());

            for (org.openmrs.Obs inObs : obs)
            {
                Observation outObs = new Observation();
                outObs.setObsId(inObs.getObsId());
                outObs.setPatientId(inObs.getPatient().getPatientId());
                outObs.setCreator(inObs.getCreator().getUserId());
                outObs.setConceptId(inObs.getConcept().getConceptId());
                outObs.setEncounterId(inObs.getEncounter().getEncounterId());
                outObs.setConceptId(inObs.getConcept().getConceptId());
                outObs.setText(inObs.getValueText());
                outObs.setDate(inObs.getObsDatetime());
                outObs.setValue(inObs.getValueNumeric());
                // outObs.setUinObs.getCreator().getUserId();
                outObs.setDateCreated(inObs.getDateCreated());

                bundle.add(outObs);

            }
        }
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
		log.debug("NUMBER OF PATIENTS " + patients.size());
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
			PersonAttribute race = patient.getAttribute("race");
			if (race != null)
			    outPat.setRace(race.getValue());
			
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
		List<org.openmrs.Program> progs = pService.getAllPrograms(false);
		
		for (org.openmrs.Program prog : progs) {
			com.odkclinic.model.Program outProg = new com.odkclinic.model.Program();
			
			outProg.setProgramId(prog.getProgramId());
			outProg.setConceptId(prog.getConcept().getConceptId());
			
			bundle.add(outProg);			
		}
		
		return bundle;		
	}
	private static Set<org.openmrs.ConceptDatatype> mConceptDTs;
	private static Set<org.openmrs.ConceptDatatype> getConceptDTs() {
	    if (mConceptDTs == null) {
    	    ConceptService cService = Context.getConceptService();
    	    mConceptDTs = new HashSet<org.openmrs.ConceptDatatype>();
    	    mConceptDTs.add(cService.getConceptDatatype(1));
    	    mConceptDTs.add(cService.getConceptDatatype(3));
    	    mConceptDTs.add(cService.getConceptDatatype(10));
	    }
	    System.out.println("Size of DTs: " + mConceptDTs.size());
	    return mConceptDTs;
	}
	public static ConceptBundle getConcepts() {
	    ConceptBundle bundle = new ConceptBundle();
	    ProgramWorkflowService pService = Context.getProgramWorkflowService();
        Cohort cohort = Context.getCohortService().getCohort(1);
        List<Program> programs = pService.getAllPrograms(false);
        Set<org.openmrs.Concept> concepts = new HashSet<org.openmrs.Concept>();
        for (org.openmrs.PatientProgram patientProgram: pService.getPatientPrograms(cohort, programs)) {
           org.openmrs.Concept concept = patientProgram.getProgram().getConcept();
           if (getConceptDTs().contains(concept.getDatatype())) 
               concepts.add(concept);
        }
	    for (org.openmrs.Concept concept: concepts) {
	        com.odkclinic.model.Concept outConcept = new Concept();
	        outConcept.setConceptId(concept.getConceptId());
	        outConcept.setClassId(concept.getConceptClass().getConceptClassId());
	        outConcept.setCreator(concept.getCreator().getUserId());
	        outConcept.setDatatypeId(concept.getDatatype().getConceptDatatypeId());
	        outConcept.setDateCreated(concept.getDateCreated());
	        outConcept.setIsSet(concept.isSet());
	        outConcept.setRetired(concept.getRetired());
	        bundle.add(outConcept);
	    }
	    return bundle;
	}
	
	public static ConceptNameBundle getConceptNames() {
	    ConceptNameBundle bundle = new ConceptNameBundle();
	    ProgramWorkflowService pService = Context.getProgramWorkflowService();
        Cohort cohort = Context.getCohortService().getCohort(1);
        List<Program> programs = pService.getAllPrograms(false);
        Set<org.openmrs.Concept> concepts = new HashSet<org.openmrs.Concept>();
        for (org.openmrs.PatientProgram patientProgram: pService.getPatientPrograms(cohort, programs)) {
            org.openmrs.Concept concept = patientProgram.getProgram().getConcept();
            if (getConceptDTs().contains(concept.getDatatype())) 
                concepts.add(concept);
        }
        for (org.openmrs.Concept concept: concepts) {
            org.openmrs.ConceptName conceptName = concept.getName();
            com.odkclinic.model.ConceptName outCN = new ConceptName();
            outCN.setConceptId(concept.getConceptId());
            outCN.setConceptNameId(conceptName.getConceptNameId());
            outCN.setName(conceptName.getName());
            bundle.add(outCN);
        }
	    return bundle;
	}
	
	public static CohortBundle getCohorts() {
	    //for now just return bundle of size 1 containing cohort 1
	    CohortBundle cohortBundle = new CohortBundle();
	    
	    org.openmrs.Cohort cohort = Context.getCohortService().getCohort(1);
	    com.odkclinic.model.Cohort outCohort = new com.odkclinic.model.Cohort();
	    
	    outCohort.setCohortDesc(cohort.getDescription());
	    outCohort.setCohortId(cohort.getCohortId());
	    outCohort.setCohortName(cohort.getName());
	    outCohort.setCreator(cohort.getCreator().getUserId());
	    outCohort.setDateCreated(cohort.getDateCreated());
	    
	    cohortBundle.add(outCohort);
	    
	    return cohortBundle;
	}
	
	public static CohortMemberBundle getCohortMembers() {
	    CohortMemberBundle bundle = new CohortMemberBundle();
	    org.openmrs.Cohort cohort = Context.getCohortService().getCohort(1);
	    for (Integer id: cohort.getMemberIds()) {
	        CohortMember cohortMember = new CohortMember();
	        cohortMember.setCohortId(cohort.getCohortId());
	        cohortMember.setPatientId(id);
	        bundle.add(cohortMember);
	    }
	    return bundle;
	}
	
	public static LocationBundle getLocations() {
	    LocationBundle bundle = new LocationBundle();
	    LocationService lService = Context.getLocationService();
	    for (org.openmrs.Location location: lService.getAllLocations()) {
	        com.odkclinic.model.Location outLocation = new com.odkclinic.model.Location();
	        outLocation.setCreator(location.getCreator().getUserId());
	        outLocation.setDateCreated(location.getDateCreated());
	        outLocation.setDesc(location.getDescription());
	        outLocation.setLocationId(location.getLocationId());
	        outLocation.setName(location.getName());
	        bundle.add(outLocation);
	    }
	    return bundle;
	}
	
	public static PatientProgramBundle getPatientPrograms() {
	    PatientProgramBundle bundle = new PatientProgramBundle();
	    ProgramWorkflowService pService = Context.getProgramWorkflowService();
	    Cohort cohort = Context.getCohortService().getCohort(1);
	    List<Program> programs = pService.getAllPrograms(false);
	    for (org.openmrs.PatientProgram patientProgram: pService.getPatientPrograms(cohort, programs)) {
	        com.odkclinic.model.PatientProgram outPPR = new PatientProgram();
	        outPPR.setPatientId(patientProgram.getPatient().getPatientId());
	        outPPR.setPatientProgramId(patientProgram.getPatientProgramId());
	        outPPR.setProgramId(patientProgram.getProgram().getProgramId());
	        bundle.add(outPPR);
	    }
	    return bundle;
	}
	
	public static long getLargestRevisionToken() {
	    ODKClinicServiceImpl revService = (ODKClinicServiceImpl) Context.getService(ODKClinicService.class);
	    long encToken = revService.getLargestRevisionToken(ODKClinicConstants.ENCOUNTER_TABLE);
	    long obsToken = revService.getLargestRevisionToken(ODKClinicConstants.OBS_TABLE);
	    return encToken >= obsToken ? encToken : obsToken;
	}
}
