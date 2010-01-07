package com.odkclinic.client.xforms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

public class Patient implements Persistent {
	Integer patientId;
	String gender;
	String race;
	Date birth;
	Integer dead;
	String birthplace;
	Double height;
	Double weight;
	String name;
	
	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	
	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public Integer getDead() {
		return dead;
	}

	public void setDead(Integer dead) {
		this.dead = dead;
	}

	public String getBirthplace() {
		return birthplace;
	}

	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getName() {
		return name; 
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	@Override
	public void read(DataInputStream dis) throws IOException,
			InstantiationException, IllegalAccessException {
		setPatientId(SerializationUtils.readInteger(dis));
		setGender(SerializationUtils.readUTF(dis));
		setRace(SerializationUtils.readUTF(dis));
		setBirth(SerializationUtils.readDate(dis));
		setDead(SerializationUtils.readInteger(dis));
		setBirthplace(SerializationUtils.readUTF(dis));
		//setHeight(SerializationUtils.readDouble(dis));
		//setWeight(SerializationUtils.readDouble(dis));
		setName(SerializationUtils.readUTF(dis));
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		SerializationUtils.writeInteger(dos, getPatientId());
		SerializationUtils.writeUTF(dos, getGender());
		SerializationUtils.writeUTF(dos, getRace());
		SerializationUtils.writeDate(dos, getBirth());
		SerializationUtils.writeInteger(dos, getDead());
		SerializationUtils.writeUTF(dos, getBirthplace());
		//SerializationUtils.writeDouble(dos, getHeight());
		//SerializationUtils.writeDouble(dos, getWeight());
		SerializationUtils.writeUTF(dos, getName());
	}

}
