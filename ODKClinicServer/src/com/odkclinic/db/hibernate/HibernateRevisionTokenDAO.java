package com.odkclinic.db.hibernate;

import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.odkclinic.db.RevisionTokenDAO;

public class HibernateRevisionTokenDAO implements RevisionTokenDAO {
	protected final Log log = LogFactory.getLog(getClass());
	
	private SessionFactory sessionFactory;
	
	
	public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }
	
	@Override
	public Date getRevisionToken(String table, int id) {
		String sql = "select revision_token from odkclinic_" + table + " where id = "+ id;
		log.debug("querying for revision token");
		Date date = null;
		try {date = (Date)sessionFactory.getCurrentSession().createSQLQuery(sql).uniqueResult();}
		catch(Exception e) {
			log.error("query for rev token fail", e);
		}
		
		return date;
	}
	
	
}
