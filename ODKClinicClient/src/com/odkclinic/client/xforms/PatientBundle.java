package com.odkclinic.client.xforms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.openmrs.module.xforms.serialization.SerializationUtils;

public class PatientBundle {
	private Vector<Patient> bundle;
	
	public PatientBundle() {
		bundle = new Vector<Patient>();
	}
	
	public void add(Patient element) {
		bundle.add(element);
	}
	
	public Vector<Patient> getBundle() {
		return bundle;
	}
	
	public void read(DataInputStream dis) throws IOException, 
			InstantiationException, IllegalAccessException {
		bundle = (Vector<Patient>) SerializationUtils.read(dis, Patient.class);
	}
	
	public void write(DataOutputStream dos) throws IOException {
		SerializationUtils.write(bundle, dos);
	}
}
