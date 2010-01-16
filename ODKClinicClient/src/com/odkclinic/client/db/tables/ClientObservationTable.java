/**
 * 
 */
package com.odkclinic.client.db.tables;

/**
 * @author Euzel Villanueva
 *
 */
public enum ClientObservationTable
{
    ID("obs_id", "INTEGER PRIMARY KEY autoincrement"),
    PATIENT_ID("observation_patient_id", PatientTable.FKEY_RELATIONSHIP),
    CONCEPT_ID("observation_concept_id", ConceptTable.FKEY_RELATIONSHIP),
    ENCOUNTER_ID("observation_encounter_id", "INTEGER"),
    TEXT("value_text", "text NULL"),
    DATETIME("value_datetime", "DATETIME DEFAULT(CURRENT_TIMESTAMP)"),
    NUMERIC("value_numeric", "DOUBLE NULL"),
    BOOLEAN("value_boolean", "bit NULL"),
    CREATOR("creator", "INTEGER NOT NULL"),
    DATE_CREATED("observation_date_created", "DATETIME DEFAULT (CURRENT_TIMESTAMP)"),
    VOIDED("voided", "bit NOT NULL"),
    ISUPDATE("isUpdate", "bit DEFAULT 1");
    
    public static String TABLE_NAME = "client_observation";
    public static String TABLE_ACRONYM = "o"; //for queries
    public static String FKEY_RELATIONSHIP = "INTEGER REFERENCES " + TABLE_NAME + "(" + ID.getName() + ") ON DELETE CASCADE";
    
    private String COLUMN_NAME;
    private String COLUMN_TYPE;
    
    public static String[] COLUMNS = new String[ClientObservationTable.values().length];
    public static String[] COLUMNS_TYPE = new String[ClientObservationTable.values().length];
    
    private ClientObservationTable(String column_name, String column_type) {
        COLUMN_NAME = column_name;
        COLUMN_TYPE = column_type;
    }
    
    static {
        for (int c = 0; c < ClientObservationTable.values().length; c++) {
            COLUMNS[c] =ClientObservationTable.values()[c].COLUMN_NAME;
        }

        for (int c = 0; c < ClientObservationTable.values().length; c++) {
            COLUMNS_TYPE[c] =ClientObservationTable.values()[c].COLUMN_TYPE;
        }
    }

    public String type() {
        return COLUMN_TYPE;
    }

    public String getName() {
        return COLUMN_NAME;
    }
}
