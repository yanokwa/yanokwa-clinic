
package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.xforms.serialization.SerializationUtils;

public class ObservationBundle implements Bundle<Observation>
{
    private List<Observation> bundle;

    public ObservationBundle()
    {
        bundle = new ArrayList<Observation>();
    }

    public void add(Observation element)
    {
        bundle.add(element);
    }

    public ArrayList<Observation> getBundle()
    {
        return (ArrayList<Observation>) bundle;
    }

    @SuppressWarnings("unchecked")
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        List tempBundle = SerializationUtils.read(dis, Observation.class);
        if (tempBundle != null)
            bundle.addAll((List<Observation>) tempBundle);
    }

    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.write(bundle, dos);
    }
}
