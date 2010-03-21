package com.odkclinic.db.model;

import java.util.Date;


public abstract class AbstractRevToken
{

    private Integer id;
    private Date revisionToken;
    
    public AbstractRevToken(Integer id, Date revisionToken) {
        this.id = id;
        this.setRevisionToken(revisionToken);
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setRevisionToken(Date revisionToken)
    {
        this.revisionToken = revisionToken;
    }

    public Date getRevisionToken()
    {
        return revisionToken;
    }

 
    
}
