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
public class PatientProgram implements Persistent {

    private Integer patientProgramId;
	private Integer programId;
	private Integer patientId;
	
	public PatientProgram(Integer patientId, Integer programId, Integer patientProgramId) {
		this.patientId = patientId;
		this.programId = programId;
		this.patientProgramId = patientProgramId;
	}
	
	public PatientProgram() {
		this(null, null, null);
	}
	
	public Integer getPatientProgramId() {
		return patientProgramId;
	}
	
	public void setPatientProgramId(Integer patientProgramId) {
		this.patientProgramId = patientProgramId;
	}
	
	public Integer getProgramId() {
        return programId;
    }
    
    public void setProgramId(Integer programId) {
        this.programId = programId;
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
		setProgramId(SerializationUtils.readInteger(dis));
		setPatientId(SerializationUtils.readInteger(dis));		
	}
	@Override
	public void write(DataOutputStream dos) throws IOException {
		SerializationUtils.writeInteger(dos, programId);
		SerializationUtils.writeInteger(dos, patientId);
		
	}

}
