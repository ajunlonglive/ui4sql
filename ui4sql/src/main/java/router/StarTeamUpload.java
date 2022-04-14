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
import services.StarTeam;

/**
 * 
 * Uploads a file from user and saves into STar Team!
 * 
 * the request comes from /UploadAttahment.html pop-up window .. which is
 * popped-up from the Attachment plugin, a detail from of SBAR
 * 
 * 1. read the file and save to the /reports directory 2. Use the
 * /services/StarTeam class login and connect to StarTeam, then add the file 3.
 * gets the tattachment table key from the session, and then update that row ...
 * to indciate the file name in column file_nm.
 * 
 */

public class StarTeamUpload extends HttpServlet implements
		javax.servlet.Servlet {

	String strStarTeamUrl = "starteam.arnepaulsenjr.com:8088";
	int nStarTeamPort = 49201;
	String strStarTeamUser = "D576781";
	String strStarTeamPassword = "Morgan!3";
	String strProjectName = "HC_N00000_AC-NC";

	String strFolder = "Inpatient";
	String strSubFolder = "IssueTriage";

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

	@SuppressWarnings("unchecked")
	private void RouteIt() throws ServletException, IOException {

		SessionMgr sessionMgr;

		StringBuffer html = new StringBuffer();

		servletContext = servletConfig.getServletContext();

		try {
			sessionMgr = new SessionMgr(req, resp, servletContext);
			if (sessionMgr == null) {
				System.out.println("RouteIt: session mgr is null");
				resp.setContentType("text/html");
				out = resp.getWriter();
				out
						.println("<html><Body>Error getting session... problem with server.</body></html>");
				return;
			}
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

		resp.setContentType("text/html");
		out = resp.getWriter();

		html.append("<HTML><BODY>Uploading files..<br> ");

		try {
			StarTeam starTeam = new StarTeam(strStarTeamUrl, nStarTeamPort,
					strStarTeamUser, strStarTeamPassword, strProjectName);

			processFiles(sessionMgr, starTeam, html);

		} catch (services.ServicesException e) {
			debug("Exception " + e.toString());

		}

		html
				.append("<br><br>Congratulations - file(s) successfully uploaded. <br><br><br><br> Please close this window and refresh the attachment page.");

		html.append("</BODY></HTML>");

		out.println(html.toString());

		out.flush();

		out.close();

		return;
	}

	private void debug(String s) {
		System.out.println("StarTeam Attachment Uploader: " + s);
	}

	@SuppressWarnings("unchecked")
	private void processFiles(SessionMgr sessionMgr, StarTeam starTeam,
			StringBuffer html) {
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		diskFileItemFactory.setSizeThreshold(9000000); /* the unit is bytes */

		File repositoryPath = new File(sessionMgr.getExcelPath() + "/");
		diskFileItemFactory.setRepository(repositoryPath);

		ServletFileUpload servletFileUpload = new ServletFileUpload(
				diskFileItemFactory);

		try {

			servletFileUpload.setSizeMax(981920);

			List fileItemsList = servletFileUpload.parseRequest(req);

			Iterator it = fileItemsList.iterator();

			FileItem fileItem = null;

			while (it.hasNext()) {
				FileItem fileItemTemp = (FileItem) it.next();

				if (fileItemTemp.isFormField()) {
				} else {
					fileItem = fileItemTemp;
					uploadFile(fileItem, starTeam, sessionMgr);
					html
							.append("<br><br> File: " + fileItem.getName()
									+ "<br>");
				}
			}

		} catch (FileUploadException e) {
			debug("error : ");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {
			debug("exception : ");
			ex.printStackTrace();
		}

	}

	private void uploadFile(FileItem item, StarTeam starTeam,
			SessionMgr sessionMgr) {

		// String fieldName = item.getFieldName();
		String fileName = item.getName();
		// String contentType = item.getContentType();
		// boolean isInMemory = item.isInMemory();
		// long sizeInBytes = item.getSize();

		File uploadedFile = new File(fileName);

		try {
			item.write(uploadedFile);
			starTeam.addFiles(strFolder, strSubFolder, "SBAR Upload", fileName);

			// post the file name to the tattachment record
			updateFileName(sessionMgr, fileName);

		} catch (Exception e) {
			debug("file upload error");
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return;
	}

	private void updateFileName(SessionMgr sessionMgr, String fileName) {
		Integer rowId = sessionMgr.getAttachmentId();

		String storeName = fileName;
		if (fileName.lastIndexOf("\\")> 0) {
			storeName = fileName.substring(fileName.lastIndexOf("\\") + 1);
		}
		
		String query = " update tattachment set file_nm = '" + storeName 
				+ "' where attachment_id = " + rowId.toString() + " LIMIT 1;";
		
		//debug("query  : " + query );

		try {
			sessionMgr.getDbInterface().runQuery(query);
		} catch (services.ServicesException e) {
			debug("Error updating tattachment with file name");
		}
	}

}
