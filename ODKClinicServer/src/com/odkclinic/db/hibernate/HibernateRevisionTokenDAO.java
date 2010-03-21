
package com.odkclinic.db.hibernate;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.odkclinic.db.RevisionTokenDAO;
import com.odkclinic.db.model.EncounterRevisionToken;
import com.odkclinic.db.model.ObsRevisionToken;
import com.odkclinic.db.model.UserRevisionToken;
import com.odkclinic.server.ODKClinicConstants;

public class HibernateRevisionTokenDAO implements RevisionTokenDAO
{
    protected final Log log = LogFactory.getLog(getClass());

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Date getRevisionToken(String table, int id)
    {
        String sql = "select revision_token from odkclinic_" + table
                + " where id = " + id;
        log.debug("querying for revision token");
        Date date = null;
        try
        {
            date = (Date) sessionFactory.getCurrentSession()
                    .createSQLQuery(sql).uniqueResult();
            if (date == null) {
                if (table.equals(ODKClinicConstants.OBS_TABLE))
                    sql = "select date_created from obs where obs_id = " + id;
                else if (table.equals(ODKClinicConstants.ENCOUNTER_TABLE))
                    sql = "select date_created from encounter where encounter_id = " + id;
                date = (Date) sessionFactory.getCurrentSession().createSQLQuery(sql).uniqueResult();
                if (date != null) {
                    putRevisionToken(table, id, date);
                    return date;
                } else 
                    log.error("Date is null. For id: " + id);
            }
        } catch (Exception e)
        {
            log.error("query for rev token fail", e);
        }
        if (date != null)
            log.debug("Getting token: " + date.toString());
        return date;
    }
    
    private void putRevisionToken(String table, int id, Date date) {
       // String sql = "INSERT INTO "+ table +"(id, revision_token) VALUES ("+ id +", " + date.getTime() + ");";
        //sessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
       // sessionFactory.getCurrentSession().flush();
        Session s = sessionFactory.getCurrentSession();
        if (table.equals(ODKClinicConstants.ENCOUNTER_TABLE)) {
            s.saveOrUpdate(new EncounterRevisionToken(id, date));
        } else if (table.equals(ODKClinicConstants.OBS_TABLE)) {
            s.saveOrUpdate(new ObsRevisionToken(id, date));
        }
        s.flush();
    }
    
    public Long getUserRevisionToken(String user) {
        String sql = "select revision_token from odkclinic_user where id = '" + user + "'";
        log.debug("querying for revision token");
        Long date = null;
        try
        {
            date = (Long) sessionFactory.getCurrentSession().createSQLQuery(sql).uniqueResult();
        } catch (Exception e)
        {
            log.error("query for rev token fail", e);
        }
        if (date != null)
            log.debug("Getting token: " + date.toString());
        return date != null ? date : null;
    }
    
    public void updateUserRevisionToken(String user) {
        /*try
        {
            log.error("Trying to insert value in database.");
            String sql = "INSERT INTO odkclinic_user(id, revision_token) VALUES ('"+ user +"' , " + System.currentTimeMillis() + ");";
            Session s = sessionFactory.getCurrentSession();
            s.createSQLQuery(sql).executeUpdate();
            s.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
            log.error("Trying to update value in database.");
            String sql = "UPDATE odkclinic_user SET revision_token = " + System.currentTimeMillis() + " WHERE id='"+ user +"';";
            sessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
            sessionFactory.getCurrentSession().flush();
        }*/
        Date newDate = new Date();
        Session s = sessionFactory.getCurrentSession();
        log.info(String.format("Updating revision token of user %s to %d", user, newDate.getTime()));
        s.saveOrUpdate(new UserRevisionToken(user, newDate));
        s.flush();
    }
    
    @Override
    public long getLargestRevisionToken(String table)
    {
        String sql = "select revision_token from odkclinic_" + table
                + " order by revision_token DESC LIMIT 1";
        log.debug("querying for revision token");
        Date date = null;
        try
        {
            date = (Date) sessionFactory.getCurrentSession()
                    .createSQLQuery(sql).uniqueResult();
        } catch (Exception e)
        {
            log.error("query for rev token fail", e);
        }
        if (date != null)
            return date.getTime();
        else
            return -1;
    }

}
