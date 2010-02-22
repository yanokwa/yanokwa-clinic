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

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

/**
 * @author Euzel Villanueva
 * 
 */
public class CohortMember implements Persistent {
	private Integer cohortId;
	private Integer patientId;

	public CohortMember() {
	}

	public Integer getCohortId() {
		return cohortId;
	}

	public void setCohortId(Integer cohortId) {
		this.cohortId = cohortId;
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
		setCohortId(SerializationUtils.readInteger(dis));
		setPatientId(SerializationUtils.readInteger(dis));
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		SerializationUtils.writeInteger(dos, getCohortId());
		SerializationUtils.writeInteger(dos, getPatientId());
	}

}
