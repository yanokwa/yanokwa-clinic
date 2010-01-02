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
 * H17 abbreviation for concept types as defined in 
 * ConceptDatatype of 1.6.0 openMRS build
 * 
 * @author Jessica Leung
 */
public enum ConceptDatatypeHL7 {
	/* commented out types are nor currently supported for entry */
	
	BOOLEAN("BIT"),
//	CODED("CWE"),
//	DATE("DT"),
//	DATETIME("TS"),
//	DOCUMENT("RP"),
	NUMERIC("NM"),
	TEXT("ST"),
//	TIME("TM")
	;
	
	private String h17;
	
	ConceptDatatypeHL7(String h17){
		this.h17 = h17;
	}

	public String getH17() {
		return h17;
	}
}
