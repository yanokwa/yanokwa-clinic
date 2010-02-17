package com.odkclinic.server.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.odkclinic.server.ODKClinicServer;

public class AndroidServlet extends HttpServlet {

    private static final long serialVersionUID = -3165879845083174090L;
    private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * This just delegates to the doGet()
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			new ODKClinicServer().handleStreams(request, response);
		}
		catch(Exception e){
			log.error(e.getMessage(),e);
		}
	}
}
