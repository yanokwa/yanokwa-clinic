package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

public class EncounterBundle implements Bundle<Encounter>{
	private Vector<Encounter> bundle;
	
	public EncounterBundle() {
		bundle = new Vector<Encounter>();
	}
	
	public void add(Encounter element) {
		bundle.add(element);
	}
	
	public Vector<Encounter> getBundle() {
		return bundle;
	}
	
	@SuppressWarnings("unchecked")
	public void read(DataInputStream dis) throws IOException, 
			InstantiationException, IllegalAccessException {
		bundle = (Vector<Encounter>)SerializationUtils.read(dis, EncounterBundle.class);
	}
	
	public void write(DataOutputStream dos) throws IOException {
		SerializationUtils.write(bundle, dos);
	}
}
