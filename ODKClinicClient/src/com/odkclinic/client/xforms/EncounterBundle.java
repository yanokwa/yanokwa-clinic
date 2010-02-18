package com.odkclinic.client.xforms;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.spec.ECFieldF2m;
import java.util.List;
import java.util.ArrayList;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

public class EncounterBundle implements Bundle<Encounter>
{
    private List<Encounter> bundle;

    public EncounterBundle()
    {
        bundle = new ArrayList<Encounter>();
    }

    public void add(Encounter element)
    {
        bundle.add(element);
    }

    public ArrayList<Encounter> getBundle()
    {
        return (ArrayList<Encounter>) bundle;
    }

    @SuppressWarnings("unchecked")
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        List temp = SerializationUtils.read(dis, Encounter.class);
        if (temp != null)
            bundle.addAll(temp);
    }

    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.write(bundle, dos);
    }

    public byte[] getBytes() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        write(dos);
        return baos.toByteArray();
    }
}
