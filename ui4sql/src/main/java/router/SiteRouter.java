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

import javax.servlet.RequestDispatcher;
import java.io.PrintWriter;


/**
 * This Sevlet is used to map different domains to subdomains.

 * 1. if the url is 
 * 					http://www.cmmFrameworks.com
 * 					http://www.arne-paulsen.com
 * 					http://www.wilhelmtree.com
 * 
 * 2. the dns entry all point to the same ip address
 * 		which is the root directory of cmmframeworks.com
 *
 * 3. 	which goes to the default page - index.html
 * 
 * 4. 	index.html redirect to this servlet  /servlet/SiteRouter
 * 
 * 5. 	this serlvet examines the server name in the url (cmmFrameworks, arne-pauslen or wilhelmTree)
 * 
 * 6. 	then uses dispatch to include the either:
 * 
 * 
 * 		a. CMMRedirect.html - which then redirects to 
 * 
 * 				http://www.cmmFrameworks.com/cmmFrameworks/cmmFrameworksWelcome.html
 * 
 * 			or 
 * 
 * 		b. FamilyRedirect.html - which redirects to the subdomain 
 * 				http://family.arne-paulsen.com
 * 	
 * 
  */

public class SiteRouter extends HttpServlet {

	static final long serialVersionUID = 256437672776147L;
	
	private PrintWriter out;
	private RequestDispatcher rd;

	/**
	* @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		RouteIt(req, resp);

	}

	/**
	* @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	*/
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		RouteIt(req, resp);
	}

	private void RouteIt(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		String cmmSite = new String("../html/RedirectToCMM.html");
		String familySite =
			new String("../html/RedirectToFamily.html");
		String blogSite = new String("../html/RedirectToBlog.html");

		String newUrl = new String("");

		resp.setContentType("text/html");

		String serverName = new String(req.getServerName());

		if (serverName.indexOf("arne-paulsen") > 0) {
			newUrl = blogSite;
		} else {
			if ((serverName.indexOf("cmm") > 0) || (serverName.indexOf("ui4sql") > 0) ) {
				newUrl = cmmSite;
			} else {
				if (serverName.indexOf("wilhelm") > 0) {
					newUrl = familySite;
				} else {
					newUrl = cmmSite; // default
				}
			}

		}
		
		out = resp.getWriter();
		rd = req.getRequestDispatcher(newUrl);
		rd.include(req, resp);
	}
}
