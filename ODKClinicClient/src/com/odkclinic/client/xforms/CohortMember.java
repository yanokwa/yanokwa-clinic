
package com.odkclinic.client.xforms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;


/**
 * @author Euzel Villanueva
 * 
 */
public class CohortMember implements Persistent
{
    public int cohortId;
    public int patientId;

    public CohortMember()
    {
    }

    public int getCohortId()
    {
        return cohortId;
    }

    public void setCohortId(int cohortId)
    {
        this.cohortId = cohortId;
    }

    public int getPatientId()
    {
        return patientId;
    }

    public void setPatientId(int patientId)
    {
        this.patientId = patientId;
    }

    @Override
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        setCohortId(SerializationUtils.readInteger(dis));
        setPatientId(SerializationUtils.readInteger(dis));
    }

    @Override
    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.writeInteger(dos, getCohortId());
        SerializationUtils.writeInteger(dos, getPatientId());
    }

}
