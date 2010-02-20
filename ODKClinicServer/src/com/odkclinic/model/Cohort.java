/**
 * 
 */

package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

/**
 * @author Euzel Villanueva
 * 
 */
public class Cohort implements Persistent
{
    private int cohortId;
    private String cohortName;
    private String cohortDesc;
    private int creator;
    private Date dateCreated;

    public Cohort()
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

    public String getCohortName()
    {
        return cohortName;
    }

    public void setCohortName(String cohortName)
    {
        this.cohortName = cohortName;
    }

    public String getCohortDesc()
    {
        return cohortDesc;
    }

    public void setCohortDesc(String cohortDesc)
    {
        this.cohortDesc = cohortDesc;
    }

    public int getCreator()
    {
        return creator;
    }

    public void setCreator(int creator)
    {
        this.creator = creator;
    }

    public Date getDateCreated()
    {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }
    
    @Override
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        setCohortId(SerializationUtils.readInteger(dis));
        setCohortName(SerializationUtils.readUTF(dis));
        setCohortDesc(SerializationUtils.readUTF(dis));
        setCreator(SerializationUtils.readInteger(dis));
        setDateCreated(SerializationUtils.readDate(dis));
    }

    @Override
    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.writeInteger(dos, getCohortId());
        SerializationUtils.writeUTF(dos, getCohortName());
        SerializationUtils.writeUTF(dos, getCohortDesc());
        SerializationUtils.writeInteger(dos, getCreator());
        SerializationUtils.writeDate(dos, getDateCreated());
    }

}
