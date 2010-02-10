package com.odkclinic.server;

import java.util.Date;

import com.odkclinic.db.RevisionTokenDAO;

public interface ODKClinicService {
	
	public RevisionTokenDAO getRevisionTokenDAO();
	public void setRevisionTokenDAO(RevisionTokenDAO tokenDAO);
	
	public Date getRevisionToken(String table, int id);
	
}
