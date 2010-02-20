package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.xforms.serialization.SerializationUtils;

public class LocationBundle
{
    private List<Location> bundle;

    public LocationBundle()
    {
        bundle = new ArrayList<Location>();
    }

    public void add(Location element)
    {
        bundle.add(element);
    }

    public ArrayList<Location> getBundle()
    {
        return (ArrayList<Location>) bundle;
    }

    @SuppressWarnings("unchecked")
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        List temp = SerializationUtils.read(dis, Location.class);
        if (temp != null)
            bundle = (ArrayList<Location>) temp;
    }

    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.write(bundle, dos);
    }

}
