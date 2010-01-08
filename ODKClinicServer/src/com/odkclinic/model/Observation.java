package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

public class Observation implements Persistent {
	Integer obsId;
	Integer patientId;
	Integer conceptId;
	Integer encounterId;
	String text;
	Date date;
	Double value;
	Integer creator;
	Date dateCreated;

	public Observation() {
		super();
			
	}
	
	public Observation(Integer obsId, Integer patientId, Integer encounterId,
						Integer conceptId, String text, Date date,
						Double value, Integer creator, Date dateCreated) {
		this();
		setObsId(obsId);
		setPatientId(patientId);
		setEncounterId(encounterId);
		setConceptId(conceptId);
		setText(text);
		setDate(date);
		setValue(value);
		setCreator(creator);
		setDateCreated(dateCreated);
	}
	
	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}
	
	public Integer getConceptId() {
		return conceptId;
	}
	
	public void setObsId(Integer obsId) {
		this.obsId = obsId;
	}
	
	public Integer getObsId() {
		return obsId;
	}
	
	public void setDateCreated(Date date) {
		this.dateCreated = date;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
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
	
	public Integer getCreator() {
		return creator;
	}
	
	public void setCreator(Integer creator) {
		this.creator = creator;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Date getDate() {
		return date;
	}
	
	
	public void read(DataInputStream dis) throws IOException,
			InstantiationException, IllegalAccessException {
		setObsId(SerializationUtils.readInteger(dis));
		setPatientId(SerializationUtils.readInteger(dis));
		setEncounterId(SerializationUtils.readInteger(dis));
		setConceptId(SerializationUtils.readInteger(dis));
		setText(SerializationUtils.readUTF(dis));
		setDate(SerializationUtils.readDate(dis));
		setValue(Double.valueOf(SerializationUtils.readUTF(dis)));
		setCreator(SerializationUtils.readInteger(dis));
		setDateCreated(SerializationUtils.readDate(dis));
	}

	public void write(DataOutputStream dos) throws IOException {
		SerializationUtils.writeInteger(dos, getObsId());
		SerializationUtils.writeInteger(dos, getPatientId());
		SerializationUtils.writeInteger(dos, getEncounterId());
		SerializationUtils.writeInteger(dos, getConceptId());
		SerializationUtils.writeUTF(dos, getText());
		SerializationUtils.writeDate(dos, getDate());
		SerializationUtils.writeUTF(dos, getValue().toString());
		SerializationUtils.writeInteger(dos, getCreator());
		SerializationUtils.writeDate(dos, getDateCreated());
		
	}
}
