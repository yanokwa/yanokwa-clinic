
package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.xforms.serialization.SerializationUtils;

public class PatientBundle
{
    private List<Patient> bundle;

    public PatientBundle()
    {
        bundle = new ArrayList<Patient>();
    }

    public void add(Patient element)
    {
        bundle.add(element);
    }

    public ArrayList<Patient> getBundle()
    {
        return (ArrayList<Patient>) bundle;
    }

    @SuppressWarnings("unchecked")
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        List temp = SerializationUtils.read(dis, Patient.class);
        if (temp != null)
            bundle = (ArrayList<Patient>) SerializationUtils
                    .read(dis, Patient.class);
    }

    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.write(bundle, dos);
    }
}
