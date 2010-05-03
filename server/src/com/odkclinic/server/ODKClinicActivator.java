/**
 * 
 */
package com.odkclinic.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Activator;

/**
 * @author Euzel Villanueva
 *
 */
public class ODKClinicActivator implements Activator
{
    private Log log = LogFactory.getLog(this.getClass());
    @Override
    public void shutdown()
    {
        log.info("Shutting down ODKClinicServer module.");

    }

    @Override
    public void startup()
    {
        log.info("Starting ODKClinicServer module.");
    }

}
