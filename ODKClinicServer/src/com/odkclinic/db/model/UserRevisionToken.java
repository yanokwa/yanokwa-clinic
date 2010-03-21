package com.odkclinic.db.model;

import java.io.Serializable;
import java.util.Date;

public class UserRevisionToken implements Serializable
{
    private static final long serialVersionUID = 8768975762286964078L;
    
    private String id;
    private Date revisionToken;
     
    public UserRevisionToken(String id, Date revisionToken) {
        this.id = id;
        this.revisionToken = revisionToken;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Date getRevisionToken()
    {
        return revisionToken;
    }

    public void setRevisionToken(Date revisionToken)
    {
        this.revisionToken = revisionToken;
    }
}
