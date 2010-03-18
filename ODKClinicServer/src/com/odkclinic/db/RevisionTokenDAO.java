
package com.odkclinic.db;

import java.util.Date;

public interface RevisionTokenDAO
{

    /**
     * Retrieve the revision token from given table and id
     * 
     * @param type
     * @param id
     * @return revision token
     */
    public Date getRevisionToken(String table, int id);

    public long getLargestRevisionToken(String table);
    
    public Long getUserRevisionToken(String user);
    
    public void updateUserRevisionToken(String user);
}
