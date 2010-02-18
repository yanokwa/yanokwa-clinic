package com.odkclinic.client.xforms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.openmrs.module.xforms.serialization.Persistent;

/**
 * @author Euzel Villanueva
 *
 */
public interface Bundle<E> 
{
    public void add(E element);
    public List<E> getBundle();
    public void read(DataInputStream dis)throws IOException, InstantiationException, IllegalAccessException;
    public void write(DataOutputStream dos) throws IOException;
}
