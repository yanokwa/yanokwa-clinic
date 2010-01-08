package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.openmrs.module.xforms.serialization.SerializationUtils;

public class ProgramBundle {
	private Vector<Program> bundle;
	
	public ProgramBundle() {
		bundle = new Vector<Program>();
	}
	
	public void add(Program element) {
		bundle.add(element);
	}
	
	public Vector<Program> getBundle() {
		return bundle;
	}
	
	public void read(DataInputStream dis) throws IOException, 
			InstantiationException, IllegalAccessException {
		bundle = (Vector<Program>) SerializationUtils.read(dis, Program.class);
	}
	
	public void write(DataOutputStream dos) throws IOException {
		SerializationUtils.write(bundle, dos);
	}

}
