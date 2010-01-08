package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.openmrs.module.xforms.serialization.SerializationUtils;

public class ObservationBundle {
	private Vector<Observation> bundle;
	
	public ObservationBundle() {
		bundle = new Vector<Observation>();
	}
	
	public void add(Observation element) {
		bundle.add(element);
	}
	
	public Vector<Observation> getBundle() {
		return bundle;
	}
	
	public void read(DataInputStream dis) throws IOException, 
			InstantiationException, IllegalAccessException {
		bundle = (Vector<Observation>) SerializationUtils.read(dis, ObservationBundle.class);
	}
	
	public void write(DataOutputStream dos) throws IOException {
		SerializationUtils.write(bundle, dos);
	}
}
