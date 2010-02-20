package com.odkclinic.client.xforms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.xforms.serialization.SerializationUtils;

public class CohortBundle
{
    private List<Cohort> bundle;

    public CohortBundle()
    {
        bundle = new ArrayList<Cohort>();
    }

    public void add(Cohort element)
    {
        bundle.add(element);
    }

    public ArrayList<Cohort> getBundle()
    {
        return (ArrayList<Cohort>) bundle;
    }

    @SuppressWarnings("unchecked")
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        List temp = SerializationUtils.read(dis, Cohort.class);
        if (temp != null)
            bundle = (ArrayList<Cohort>) temp;
    }

    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.write(bundle, dos);
    }

}
