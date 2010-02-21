
package com.odkclinic.client.db.defaultvalues;

import com.odkclinic.client.db.tables.ConceptDataTypeTable;

public enum ConceptDataTypeDefault
{
    BOOLEAN(10, "BOOLEAN", "BIT", "BOOL"), 
    NUMERIC(1, "NUMERIC", "NM", "NUM"), 
    TEXT(3, "TEXT", "ST", "TEXT");

    public static final String TABLE = ConceptDataTypeTable.TABLE_NAME;

    private String name;
    private int id;
    private String hl7;
    private String desc;

    private ConceptDataTypeDefault(int id, String name, String hl7, String desc)
    {
        this.name = name;
        this.id = id;
        this.hl7 = hl7;
        this.desc = desc;
    }

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    public String getHl7()
    {
        return hl7;
    }

    public String getDesc()
    {
        return desc;
    }

    public static String[] getInsertStatements()
    {
        String[] ret = new String[ConceptDataTypeDefault.values().length];
        int i = 0;
        for (ConceptDataTypeDefault sd : ConceptDataTypeDefault.values())
        {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ");
            sb.append(TABLE);
            sb.append("(");
            for (int j = 0; j < ConceptDataTypeTable.COLUMNS.length; j++)
            {
                sb.append(ConceptDataTypeTable.COLUMNS[j]);
                if (j < ConceptDataTypeTable.COLUMNS.length - 1)
                {
                    sb.append(",");
                }
            }
            sb.append(") VALUES(");
            sb.append(sd.id);
            sb.append(",");
            sb.append(sd.name);
            sb.append(",");
            sb.append(sd.hl7);
            sb.append(",");
            sb.append(sd.desc);
            sb.append(");");
            ret[i++] = sb.toString();
        }
        return ret;
    }
}
