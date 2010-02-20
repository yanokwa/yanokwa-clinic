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
public class Location implements Persistent
{   
    private int locationId;
    private String name;
    private String desc;
    private int creator;
    private Date dateCreated;

    public int getLocationId()
    {
        return locationId;
    }

    public void setLocationId(int locationId)
    {
        this.locationId = locationId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
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
        setLocationId(SerializationUtils.readInteger(dis));
        setName(SerializationUtils.readUTF(dis));
        setDesc(SerializationUtils.readUTF(dis));
        setCreator(SerializationUtils.readInteger(dis));
        setDateCreated(SerializationUtils.readDate(dis));
    }

    @Override
    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.writeInteger(dos, getLocationId());
        SerializationUtils.writeUTF(dos, getName());
        SerializationUtils.writeUTF(dos, getDesc());
        SerializationUtils.writeInteger(dos, getCreator());
        SerializationUtils.writeDate(dos, getDateCreated());
    }

}
