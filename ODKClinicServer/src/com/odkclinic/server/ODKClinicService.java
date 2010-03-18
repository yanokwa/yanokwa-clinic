
package com.odkclinic.server;

import java.util.Date;

import com.odkclinic.db.RevisionTokenDAO;

public interface ODKClinicService
{

    public RevisionTokenDAO getRevisionTokenDAO();

    public void setRevisionTokenDAO(RevisionTokenDAO tokenDAO);

    public Date getRevisionToken(String table, int id);

    public long getLargestRevisionToken(String table);
    
    public Long getUserRevisionToken(String user);
    
    public void updateUserRevisionToken(String user);
}
