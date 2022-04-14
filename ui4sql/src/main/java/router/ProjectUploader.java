/**
 * UI4SQL V1.0 (https://ui4sql.net)
 * Copyright 2022 PaulsenITSolutions 
 * Licensed under MIT (http://github.com/arnepaulsen/ui4sql/LICENSE) 
 */
package router;

import java.io.IOException;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import java.util.List;

import java.util.Iterator;

import router.SessionMgr;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;

//import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;

import java.io.File;

import org.apache.commons.fileupload.*;

import java.io.PrintWriter;
import mpxj.ProjectTaskLoad;

/**
 * 
 * 
 */

public class ProjectUploader extends HttpServlet implements
		javax.servlet.Servlet {

	private PrintWriter out;

	private HttpServletRequest req;

	private HttpServletResponse resp;

	private ServletConfig servletConfig = null;

	private ServletContext servletContext = null;

	static final long serialVersionUID = 752647111412776147L;

	// ******************************
	// * CONSTRUCTORS
	// ******************************

	public void init(ServletConfig config) throws ServletException {
		this.servletConfig = config;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.req = req;
		this.resp = resp;
		RouteIt();
		return;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.req = req;
		this.resp = resp;
		RouteIt();
		return;
	}

	// ******************************
	// * MAIN ROUTINE
	// ******************************


	private void RouteIt() throws ServletException, IOException {

		SessionMgr sm;

		debug("file uploade starting: ");

		servletContext = servletConfig.getServletContext();

		try {
			sm = new SessionMgr(req, resp, servletContext);

		} catch (Exception e) {
			System.out.println("Router... error getting SessionMgr");
			e.printStackTrace();
			return;
		}

		/*
		 * Get the Session Mgr
		 */

		if (ServletFileUpload.isMultipartContent(req)) {

		}

		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		diskFileItemFactory.setSizeThreshold(9000000); /* the unit is bytes */

		File repositoryPath = new File(sm.getExcelPath() + "/");
		diskFileItemFactory.setRepository(repositoryPath);

		ServletFileUpload servletFileUpload = new ServletFileUpload(
				diskFileItemFactory);

		try {
			
			mpxj.ProjectTaskLoad loader = new mpxj.ProjectTaskLoad(sm.getConnection());
			
			servletFileUpload.setSizeMax(981920);

			List fileItemsList = servletFileUpload.parseRequest(req);

			Iterator it = fileItemsList.iterator();

			FileItem fileItem = null;

	

			while (it.hasNext()) {
				FileItem fileItemTemp = (FileItem) it.next();

				if (fileItemTemp.isFormField()) {
				} else {
					fileItem = fileItemTemp;
					processUploadedFile(fileItem);
					debug("loading file");
					String fileName = fileItem.getName();
					debug ("importing tasks");					
					loader.loadFile(fileName);
				}
			}

			System.out.println("done loop ");

		} catch (FileUploadException e) {
			debug("error : ");
			e.printStackTrace();
		} catch (Exception ex) {
			debug("exception : ");
			ex.printStackTrace();
		}

		resp.setContentType("text/html");
		out = resp.getWriter();
		// debug("Log-Level : " + servletContext.getAttribute("Log-Level"));

		// debug("Log-Level : " + servletContext.getInitParameter("Log-Level"));

		out.println("<HTML><BODY>Uploading plan ");

		out.println("File upladeded, importing tasks.<br>");
		
		
		
		out.println("</BODY></HTML>");
		

		out.flush();

		out.close();

		return;
	}



	private void debug(String s) {
		System.out.println("ProjectUploader: " + s);
	}

	private String processUploadedFile(FileItem item) {

	
		String fileName = item.getName();


		String myFile = "c:/temp/" + fileName;
		

		File uploadedFile = new File(myFile);

		try {
			item.write(uploadedFile);
			debug("upload file okay");
		} catch (Exception e) {
			debug("file upload error");
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

		return myFile;
	}

}
