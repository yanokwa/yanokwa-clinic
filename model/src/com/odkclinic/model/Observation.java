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

package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

/**
 * 
 * @author Euzel Villanueva
 *
 */
public class Observation implements Persistent {

	private Integer obsId;
	private Integer patientId;
	private Integer conceptId;
	private Integer encounterId;
	private String text;
	private Date date;
	private Double value;
	private Boolean valueBoolean;
	private Integer creator;
	private Date dateCreated;

	public Observation() {
		super();

	}

	public Observation(Integer obsId, Integer patientId, Integer encounterId,
			Integer conceptId, String text, Date date, Double value,
			Integer creator, Date dateCreated, Boolean valueBoolean) {
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

	public void setValueBoolean(Boolean valueBoolean) {
		this.valueBoolean = valueBoolean;
	}

	public Boolean getValueBoolean() {
		return valueBoolean;
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
		setValue(SerializationUtils.readDouble(dis));
		setCreator(SerializationUtils.readInteger(dis));
		setDateCreated(SerializationUtils.readDate(dis));
		setValueBoolean(SerializationUtils.readBoolean(dis));
	}

	public void write(DataOutputStream dos) throws IOException {
		SerializationUtils.writeInteger(dos, getObsId());
		SerializationUtils.writeInteger(dos, getPatientId());
		SerializationUtils.writeInteger(dos, getEncounterId());
		SerializationUtils.writeInteger(dos, getConceptId());
		SerializationUtils.writeUTF(dos, getText());
		SerializationUtils.writeDate(dos, getDate());
		SerializationUtils.writeDouble(dos, getValue());
		SerializationUtils.writeInteger(dos, getCreator());
		SerializationUtils.writeDate(dos, getDateCreated());
		SerializationUtils.writeBoolean(dos, getValueBoolean());
	}

}