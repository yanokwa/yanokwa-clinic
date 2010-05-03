package com.odkclinic.db.model;

import java.io.Serializable;
import java.util.Date;

public class ObsRevisionToken extends AbstractRevToken implements Serializable
{
    private static final long serialVersionUID = 34007632365411497L;

    public ObsRevisionToken(Integer id, Date date)
    {
        super(id, date);
    }
    
}
