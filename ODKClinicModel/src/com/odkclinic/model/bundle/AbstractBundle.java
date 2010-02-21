/**
 * 
 */

package com.odkclinic.model.bundle;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

import com.sun.xml.internal.ws.message.ByteArrayAttachment;

/**
 * @author Euzel Villanueva
 * 
 */
public abstract class AbstractBundle implements Bundle<Persistent>
{
    private List<Persistent> bundle;
    private final Class<?> cls;

    public AbstractBundle(Class<?> cls)
    {
        bundle = new ArrayList<Persistent>();
        this.cls = cls;
    }

    public final void add(Persistent element)
    {
        bundle.add(element);
    }

    public final List<Persistent> getBundle()
    {
        return bundle;
    }

    private final void setBundle(List<Persistent> bundle)
    {
        this.bundle = bundle;
    }

    @SuppressWarnings("unchecked")
    public final void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        List<Persistent> bundle = SerializationUtils.read(dis, cls);
        if (bundle != null)
        {
            setBundle(bundle);
        }
    }

    public final void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.write(bundle, dos);
    }
    
    public final byte[] getBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        write(dos);
        return baos.toByteArray();
    }
}
