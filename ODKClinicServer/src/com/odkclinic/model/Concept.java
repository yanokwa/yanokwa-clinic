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
public class Concept implements Persistent
{
    private int conceptId;
    private int classId;
    private boolean isSet;
    private int datatypeId;
    private boolean isRetired;
    private int creator;
    private Date dateCreated;

    public Concept()
    {
    }

    public int getConceptId()
    {
        return conceptId;
    }

    public void setConceptId(int conceptId)
    {
        this.conceptId = conceptId;
    }

    public int getClassId()
    {
        return classId;
    }

    public void setClassId(int classId)
    {
        this.classId = classId;
    }

    public boolean getIsSet()
    {
        return isSet;
    }

    public void setIsSet(Boolean boolean1)
    {
        this.isSet = boolean1;
    }

    public int getDatatypeId()
    {
        return datatypeId;
    }

    public void setDatatypeId(int datatypeId)
    {
        this.datatypeId = datatypeId;
    }

    public boolean isRetired()
    {
        return isRetired;
    }

    public void setRetired(boolean isRetired)
    {
        this.isRetired = isRetired;
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
        setConceptId(SerializationUtils.readInteger(dis));
        setClassId(SerializationUtils.readInteger(dis));
        setIsSet(SerializationUtils.readBoolean(dis));
        setDatatypeId(SerializationUtils.readInteger(dis));
        setRetired(SerializationUtils.readBoolean(dis));
        setCreator(SerializationUtils.readInteger(dis));
        setDateCreated(SerializationUtils.readDate(dis));
    }

    @Override
    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.writeInteger(dos, getConceptId());
        SerializationUtils.writeInteger(dos, getClassId());
        SerializationUtils.writeBoolean(dos, getIsSet());
        SerializationUtils.writeInteger(dos, getDatatypeId());
        SerializationUtils.writeBoolean(dos, isRetired);
        SerializationUtils.writeInteger(dos, getCreator());
        SerializationUtils.writeDate(dos, getDateCreated());
    }

}
