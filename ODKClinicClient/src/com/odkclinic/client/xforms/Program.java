package com.odkclinic.client.xforms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

public class Program implements Persistent{
	Integer programId;
	Integer conceptId;
	
	public Program() {
		
	}
	
	public Integer getProgramId() {
		return programId;
	}
	
	public void setProgramId(Integer programId) {
		this.programId = programId;
	}
	
	public Integer getConceptId() {
		return conceptId;
	}
	
	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}
	
	@Override
	public void read(DataInputStream dis) throws IOException,
			InstantiationException, IllegalAccessException {
		setProgramId(SerializationUtils.readInteger(dis));
		setConceptId(SerializationUtils.readInteger(dis));		
	}
	@Override
	public void write(DataOutputStream dos) throws IOException {
		SerializationUtils.writeInteger(dos, programId);
		SerializationUtils.writeInteger(dos, conceptId);
		
	}
	
	
}
