package com.odkclinic.client.xforms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.xforms.serialization.SerializationUtils;

public class ConceptBundle
{
    private List<Concept> bundle;

    public ConceptBundle()
    {
        bundle = new ArrayList<Concept>();
    }

    public void add(Concept element)
    {
        bundle.add(element);
    }

    public ArrayList<Concept> getBundle()
    {
        return (ArrayList<Concept>) bundle;
    }

    @SuppressWarnings("unchecked")
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        List temp = SerializationUtils.read(dis, Concept.class);
        if (temp != null)
            bundle = (ArrayList<Concept>) temp;
    }

    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.write(bundle, dos);
    }

}
