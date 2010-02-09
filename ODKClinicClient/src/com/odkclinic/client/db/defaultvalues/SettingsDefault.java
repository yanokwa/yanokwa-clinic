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

package com.odkclinic.client.db.defaultvalues;

import com.odkclinic.client.db.tables.SettingsTable;

/**
 * @author Euzel Villanueva
 *
 */
public enum SettingsDefault
{
    REV("REV", 0),
    LOCK("LOCK", 0);
    
    public static final String TABLE = SettingsTable.TABLE_NAME;
    
    private String name;
    private int value;
    
    private SettingsDefault(String name, int value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public static String[] getInsertStatements() {
        String[] ret = new String[SettingsDefault.values().length];
        int i = 0;
        for (SettingsDefault sd: SettingsDefault.values()) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ");
            sb.append(TABLE);
            sb.append("(");
            for (int j = 0; j < SettingsTable.COLUMNS.length; j++) {
                sb.append(SettingsTable.COLUMNS[j]);
                if (j < SettingsTable.COLUMNS.length - 1) {
                    sb.append(",");
                }
                sb.append(")VALUES (");
                sb.append(sd.name);
                sb.append(",");
                sb.append(sd.value);
                sb.append(");");
            }   
            ret[i++] = sb.toString();
        }
        return ret;
    }
}
