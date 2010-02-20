/**
 * 
 */

package com.odkclinic.client.xforms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

/**
 * @author zellv
 * 
 */
public class ConceptName implements Persistent
{
    private Integer conceptNameId;
    private Integer conceptId;
    private String Name;

    public Integer getConceptNameId()
    {
        return conceptNameId;
    }

    public void setConceptNameId(Integer conceptNameId)
    {
        this.conceptNameId = conceptNameId;
    }

    public Integer getConceptId()
    {
        return conceptId;
    }

    public void setConceptId(Integer conceptId)
    {
        this.conceptId = conceptId;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    @Override
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        setConceptNameId(SerializationUtils.readInteger(dis));
        setConceptId(SerializationUtils.readInteger(dis));
        setName(SerializationUtils.readUTF(dis));
    }

    @Override
    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.writeInteger(dos, getConceptNameId());
        SerializationUtils.writeInteger(dos, getConceptId());
        SerializationUtils.writeUTF(dos, getName());
    }

}
