/**
 * 
 */
package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * @author Euzel Villanueva
 *
 */
public interface Bundle<E>
{
    public void add(E element);
    public Vector<E> getBundle();
    public void read(DataInputStream dis)throws IOException, InstantiationException, IllegalAccessException;
    public void write(DataOutputStream dos) throws IOException;
}
