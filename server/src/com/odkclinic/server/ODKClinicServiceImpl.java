package com.odkclinic.server;

import java.util.Date;

import com.odkclinic.db.RevisionTokenDAO;

public class ODKClinicServiceImpl implements ODKClinicService {
	
	public RevisionTokenDAO tokenDAO;
	
	public ODKClinicServiceImpl() {
		
	}

	public RevisionTokenDAO getRevisionTokenDAO() {
		return tokenDAO;
	}

	public void setRevisionTokenDAO(RevisionTokenDAO tokenDAO) {
		this.tokenDAO = tokenDAO;
	}
	
	public Date getRevisionToken(String table, int id) {
		return tokenDAO.getRevisionToken(table, id);
	}
	
	public long getLargestRevisionToken(String table) {
	    return tokenDAO.getLargestRevisionToken(table);
	}

    @Override
    public Long getUserRevisionToken(String user)
    {
        return tokenDAO.getUserRevisionToken(user);
    }

    @Override
    public void updateUserRevisionToken(String user)
    {
       tokenDAO.updateUserRevisionToken(user);
    }

}
