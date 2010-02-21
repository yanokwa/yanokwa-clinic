package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

public class Encounter implements Persistent {
	

	Integer patientId;
	Integer encounterId;
	Integer encounterType;
	Integer providerId;
	Integer locationId;
	Date dateCreated;
	Date dateEncountered;
	Integer creator;
	
	public Encounter() {
		super();
	}
	
	public Encounter(Integer patientId, Integer encounterId, Integer encounterType, 
						Integer providerId, Integer locationId, Date dateCreated, Date dateEncountered, Integer creator) {
		this();
		setPatientId(patientId);
		setEncounterId(encounterId);
		setEncounterType(encounterType);
		setProviderId(providerId);
		setLocationId(locationId);
		setDateCreated(dateCreated);
		setDateEncountered(dateEncountered);
		setCreator(creator);
	}
	
	public Integer getCreator() {
		return creator;
	}
	
	public void setCreator(Integer creator) {
		this.creator = creator;
	}
	
	public void setDateCreated(Date date) {
		this.dateCreated = date;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	
	public void setDateEncountered(Date date) {
		this.dateEncountered = date;
	}
	
	public Date getDateEncountered() {
		return dateEncountered;
	}
	
	public Integer getLocationId() {
		return locationId;
	}
	
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	
	public Integer getProviderId() {
		return providerId;
	}
	
	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}
	
	public Integer getEncounterType() {
		return encounterType;
	}
	
	public void setEncounterType(Integer encounterType) {
		this.encounterType = encounterType;
	}
	
	public Integer getEncounterId() {
		return encounterId;
	}
	
	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}
	
	public Integer getPatientId() {
		return patientId;
	}
	
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	@Override
	public void read(DataInputStream dis) throws IOException,
			InstantiationException, IllegalAccessException {
		setPatientId(SerializationUtils.readInteger(dis));
		setEncounterId(SerializationUtils.readInteger(dis));
		setEncounterType(SerializationUtils.readInteger(dis));
		setProviderId(SerializationUtils.readInteger(dis));
		setLocationId(SerializationUtils.readInteger(dis));
		setDateCreated(SerializationUtils.readDate(dis));
		setDateEncountered(SerializationUtils.readDate(dis));
		setCreator(SerializationUtils.readInteger(dis));
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		SerializationUtils.writeInteger(dos, getPatientId());
		SerializationUtils.writeInteger(dos, getEncounterId());
		SerializationUtils.writeInteger(dos, getEncounterType());
		SerializationUtils.writeInteger(dos, getProviderId());
		SerializationUtils.writeInteger(dos, getLocationId());
		SerializationUtils.writeDate(dos, getDateCreated());
		SerializationUtils.writeDate(dos, getDateEncountered());		
		SerializationUtils.writeInteger(dos, getCreator());
	}
	
}
