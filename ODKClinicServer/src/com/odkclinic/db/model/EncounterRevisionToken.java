package com.odkclinic.db.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zellv
 *
 */
public class EncounterRevisionToken extends AbstractRevToken implements Serializable
{
    private static final long serialVersionUID = 4032703691410322276L;

    public EncounterRevisionToken(Integer id, Date date)
    {
        super(id, date);
    }
}
