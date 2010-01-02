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

package com.odkclinic.client.utils;
/**
 *  The different types of information that displayed in 
 *  connection to a patient
 * 
 * @author Jessica Leung
 */
	
public enum PatientDetailType {
	
	ENCOUNTER(1),
	CONCEPT(2),
	/** demographic information that is stored in the definition of the 
	 * patient as opposed to in a concept **/
	PATIENT_DEMOGRAPHIC(3);
	
	private Integer detailType;
	
	PatientDetailType(Integer detailType){
		this.detailType = detailType;
	}
}
