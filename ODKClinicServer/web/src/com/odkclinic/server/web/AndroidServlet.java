package com.odkclinic.server.web;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.odkclinic.server.ODKClinicServer;

public class AndroidServlet extends HttpServlet {

	public static final long serialVersionUID = 123427878377111L;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * This just delegates to the doGet()
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try{
			new ODKClinicServer().handleStreams(new DataInputStream(request.getInputStream()), new DataOutputStream(response.getOutputStream()));
		}
		catch(Exception e){
			log.error(e.getMessage(),e);
		}
	}
}
