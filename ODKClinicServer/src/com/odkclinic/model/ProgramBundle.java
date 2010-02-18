
package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.xforms.serialization.SerializationUtils;

public class ProgramBundle
{
    private List<Program> bundle;

    public ProgramBundle()
    {
        bundle = new ArrayList<Program>();
    }

    public void add(Program element)
    {
        bundle.add(element);
    }

    public ArrayList<Program> getBundle()
    {
        return (ArrayList<Program>) bundle;
    }

    @SuppressWarnings("unchecked")
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        List temp = SerializationUtils.read(dis, Program.class);
        if (temp != null)
            bundle = (ArrayList<Program>) temp;
    }

    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.write(bundle, dos);
    }

}
