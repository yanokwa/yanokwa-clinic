package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.xforms.serialization.SerializationUtils;

public class CohortMemberBundle
{
    private List<CohortMember> bundle;

    public CohortMemberBundle()
    {
        bundle = new ArrayList<CohortMember>();
    }

    public void add(CohortMember element)
    {
        bundle.add(element);
    }

    public ArrayList<CohortMember> getBundle()
    {
        return (ArrayList<CohortMember>) bundle;
    }

    @SuppressWarnings("unchecked")
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        List temp = SerializationUtils.read(dis, CohortMember.class);
        if (temp != null)
            bundle = (ArrayList<CohortMember>) temp;
    }

    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.write(bundle, dos);
    }

}
