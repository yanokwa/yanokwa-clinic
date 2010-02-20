/**
 * 
 */

package com.odkclinic.client.xforms;

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
    private Integer conceptId;
    private Integer classId;
    private Boolean isSet;
    private Integer datatypeId;
    private Boolean isRetired;
    private Integer creator;
    private Date dateCreated;

    public Concept()
    {
    }

    public Integer getConceptId()
    {
        return conceptId;
    }

    public void setConceptId(Integer conceptId)
    {
        this.conceptId = conceptId;
    }

    public Integer getClassId()
    {
        return classId;
    }

    public void setClassId(Integer classId)
    {
        this.classId = classId;
    }

    public Boolean getIsSet()
    {
        return isSet;
    }

    public void setIsSet(Boolean boolean1)
    {
        this.isSet = boolean1;
    }

    public Integer getDatatypeId()
    {
        return datatypeId;
    }

    public void setDatatypeId(Integer datatypeId)
    {
        this.datatypeId = datatypeId;
    }

    public Boolean isRetired()
    {
        return isRetired;
    }

    public void setRetired(Boolean isRetired)
    {
        this.isRetired = isRetired;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
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
