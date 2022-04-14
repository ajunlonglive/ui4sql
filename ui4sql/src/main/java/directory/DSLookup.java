/**
 * UI4SQL V1.0 (https://ui4sql.net)
 * Copyright 2022 PaulsenITSolutions 
 * Licensed under MIT (http://github.com/arnepaulsen/ui4sql/LICENSE) 
 */

/////////////////////////////////////////////////////////////////////
// Author: Enrique M. Meneses
// June 2004
//
//
/////////////////////////////////////////////////////////////////////
package directory;


import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.NamingException;


import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;

import javax.naming.directory.SearchControls;


import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import java.io.FileWriter;
import java.io.PrintWriter;

//import org.kp.kphc.util.ApplicationProperties;


public class DSLookup
{

	private Properties envProperties = new Properties();
	private DirContext dirContext    = null;

	public void connect(String hostName, int port, String mgdn, String pwd) throws Exception
	{

		if (mgdn != null && pwd != null)
		{
			envProperties.put(Context.SECURITY_AUTHENTICATION, "simple");
			envProperties.put(Context.SECURITY_PRINCIPAL, mgdn);
			envProperties.put(Context.SECURITY_CREDENTIALS, pwd);
		}

		envProperties.put("java.naming.ldap.version", "3" );
		envProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		envProperties.put(Context.PROVIDER_URL, "ldap://" + hostName + ":" + port);

		try
		{
			dirContext = new InitialDirContext(envProperties);
		}
		catch (NamingException exception)
		{
			envProperties.list(System.out);

			envProperties.clear();
			throw exception;
		}
	}


	public void close() throws NamingException
	{
		dirContext.close();
	}


	public NamingEnumeration search(String dn, String filter, String [] searchAttributes) throws Exception
	{
		NamingEnumeration namingEnumeration = null;

		try
		{
			int scopeType = SearchControls.SUBTREE_SCOPE;	// SUBTREE_SCOPE, ONELEVEL_SCOPE, OBJECT_SCOPE;  

			String scopeTypeString = ApplicationProperties.getInstance("directory").getProperty("ds.search-scope");

			if (scopeTypeString != null && scopeTypeString.equals("ONELEVEL_SCOPE"))
			{
				scopeType = SearchControls.ONELEVEL_SCOPE;
			}

			int countLimit = 1000;
			int timeout    = 0;	// ms

			SearchControls constraints = new SearchControls();

			constraints.setSearchScope(scopeType);   
			constraints.setCountLimit(countLimit);
			constraints.setTimeLimit(timeout);
			constraints.setReturningAttributes(searchAttributes);

			namingEnumeration = dirContext.search(dn, filter, constraints);   
		}
		catch (NamingException exception)
		{
			System.err.println("Exception Name:    " + exception.getClass().getName());
			System.err.println("Exception Message: " + exception.getMessage());

			throw exception;
		}

		return namingEnumeration;
	}


	public void printSearch(String dn, String filter, String [] searchAttributes) throws Exception
	{
		NamingEnumeration namingEnumeration = search(dn, filter, searchAttributes);

		while (namingEnumeration.hasMore())
		{
			SearchResult searchResult = (SearchResult) namingEnumeration.next();

			printSearchResult(searchResult);
		}
	}


	public static void printSearchResult(SearchResult searchResult) throws Exception
	{
		System.out.println(">>>" + searchResult.getName());

		Attributes attributes = searchResult.getAttributes();


		NamingEnumeration enumeration = attributes.getAll();

		while (enumeration.hasMoreElements())
		{
			Attribute attribute = (Attribute) enumeration.next();

			System.out.print(attribute.getID() + ": ");

			for (int i=0; i<attribute.size(); i++)
			{
				if (attribute.size() > 2)
				{
					System.out.println("");
				}

				System.out.print(attribute.get(i));

				if (i<attribute.size()-1)
				{
					System.out.println("");
				}
			}

			System.out.println("");

		}}

	public static void printFileHeaders(PrintWriter writer, SearchResult searchResult) throws Exception
	{
		Attributes attributes = searchResult.getAttributes();

		NamingEnumeration enumeration = attributes.getAll();

		boolean hasMoreElements = enumeration.hasMoreElements();

		while (hasMoreElements)
		{
			Attribute attribute = (Attribute) enumeration.next();

			writer.print(attribute.getID());

			hasMoreElements = enumeration.hasMoreElements();

			if (hasMoreElements)
			{
				writer.print("\t");
			}
			else
			{
				writer.print("\n");
			}
		}
	}

	public static void printFileRows(PrintWriter writer, SearchResult searchResult) throws Exception
	{
		Attributes attributes = searchResult.getAttributes();

		NamingEnumeration enumeration = attributes.getAll();

		boolean hasMoreElements = enumeration.hasMoreElements();

		while (hasMoreElements)
		{
			Attribute attribute = (Attribute) enumeration.next();

			for (int i=0; i<attribute.size(); i++)
			{
				writer.print(attribute.get(i));

				//break;
				if (i<attribute.size()-1)
				{
					writer.print("::");
				}
			}           

			hasMoreElements = enumeration.hasMoreElements();

			if (hasMoreElements)
			{
				writer.print("\t");
			}
			else
			{
				writer.print("\n");
			}
		}
	}
}

