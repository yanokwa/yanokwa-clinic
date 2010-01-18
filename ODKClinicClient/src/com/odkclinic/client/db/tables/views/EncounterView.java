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

package com.odkclinic.client.db.tables.views;

import com.odkclinic.client.db.tables.ClientEncounterTable;
import com.odkclinic.client.db.tables.EncounterTable;

/**
 * @author Euzel Villanueva
 *
 */
public enum EncounterView
{
    ID("encounter_id"),
    TYPE_NAME("encounter_type_name"),
    TYPE_DESC("encounter_type_desc"),
    PATIENT_ID("encounter_patient_id"),
    PROVIDER_ID("encounter_provider_id"),
    LOCATION_ID("encounter_location_id"), 
    DATETIME("encounter_datetime"),
    CREATOR("creator"),
    DATE_CREATED("date_created"),
    VOIDED("voided");
    
    private String COLUMN_NAME;
    
    public static String TABLE_NAME = "encounter_view";
    
    public static String[] COLUMNS = new String[EncounterView.values().length];
    
    private EncounterView(String column_name) {
        COLUMN_NAME = column_name;
    }
    
    static {
        for (int c = 0; c < EncounterView.values().length; c++) {
            COLUMNS[c] = EncounterView.values()[c].COLUMN_NAME;
        }
    }
    
    public static String getCreateStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE VIEW ");
        sb.append(TABLE_NAME);
        sb.append(" AS SELECT ");
        
        for (int c = 0; c < COLUMNS.length; c++) {
            sb.append(COLUMNS[c]);
            if (c < (COLUMNS.length - 1)) {
                sb.append(", ");
            } else {
                sb.append(" ");
            }
        }
        sb.append(" FROM ");
        sb.append(EncounterTable.TABLE_NAME);
        sb.append(" UNION ALL ");
        
        sb.append(" SELECT ");
        
        for (int c = 0; c < COLUMNS.length; c++) {
            sb.append(COLUMNS[c]);
            if (c < (COLUMNS.length - 1)) {
                sb.append(", ");
            } else {
                sb.append(" ");
            }
        }
        sb.append(" FROM ");
        sb.append(ClientEncounterTable.TABLE_NAME);
        sb.append(";");
        
        return sb.toString();
    }
    
    public String getName() {
        return COLUMN_NAME;
    }
}
