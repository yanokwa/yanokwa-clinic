/**
 * 
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
    VOIDED("voided"),
    ISUPDATE("isUpdate");
    
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
