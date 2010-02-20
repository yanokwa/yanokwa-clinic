package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.xforms.serialization.SerializationUtils;

public class ConceptNameBundle
{
    private List<ConceptName> bundle;

    public ConceptNameBundle()
    {
        bundle = new ArrayList<ConceptName>();
    }

    public void add(ConceptName element)
    {
        bundle.add(element);
    }

    public ArrayList<ConceptName> getBundle()
    {
        return (ArrayList<ConceptName>) bundle;
    }

    @SuppressWarnings("unchecked")
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        List temp = SerializationUtils.read(dis, ConceptName.class);
        if (temp != null)
            bundle = (ArrayList<ConceptName>) temp;
    }

    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.write(bundle, dos);
    }

}
