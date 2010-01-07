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
