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
    private Integer locationId;
    private String name;
    private String desc;
    private Integer creator;
    private Date dateCreated;

    public Integer getLocationId()
    {
        return locationId;
    }

    public void setLocationId(Integer locationId)
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
