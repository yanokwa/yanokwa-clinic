/**
 * 
 */

package com.odkclinic.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

/**
 * @author zellv
 * 
 */
public class ProgramWorkflow implements Persistent
{
    private Integer programWorkflowId;
    private Integer programId;
    private Integer conceptId;
    
    public Integer getProgramWorkflowId()
    {
        return programWorkflowId;
    }

    public void setProgramWorkflowId(Integer programWorkflowId)
    {
        this.programWorkflowId = programWorkflowId;
    }

    public Integer getProgramId()
    {
        return programId;
    }

    public void setProgramId(Integer programId)
    {
        this.programId = programId;
    }

    public Integer getConceptId()
    {
        return conceptId;
    }

    public void setConceptId(Integer conceptId)
    {
        this.conceptId = conceptId;
    }

    @Override
    public void read(DataInputStream dis) throws IOException,
            InstantiationException, IllegalAccessException
    {
        setProgramWorkflowId(SerializationUtils.readInteger(dis));
        setProgramId(SerializationUtils.readInteger(dis));
        setConceptId(SerializationUtils.readInteger(dis));
    }

    @Override
    public void write(DataOutputStream dos) throws IOException
    {
        SerializationUtils.writeInteger(dos, getProgramWorkflowId());
        SerializationUtils.writeInteger(dos, getProgramId());
        SerializationUtils.writeInteger(dos, getConceptId());
    }

}
