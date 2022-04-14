/**
 * UI4SQL V1.0 (https://ui4sql.net)
 * Copyright 2022 PaulsenITSolutions 
 * Licensed under MIT (http://github.com/arnepaulsen/ui4sql/LICENSE) 
 */

/////////////////////////////////////////////////////////////////////
// Author: Enrique Meneses
//
// Change History:
// June 2004 - Initial Implementation
//
//  May 2005 - Renamed class to PersonSearch. 
//             Refactored to work with any directory service. 
//             Tested with EDS, AD, TIM and X500 directories.
/////////////////////////////////////////////////////////////////////


package directory;


import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;


import java.util.List;
import java.util.ArrayList;



public class PersonSearch
{
	private DSLookup directoryLookup = null;
	private String   dsType;

	protected static final String ANONYMOUS = "ANONYMOUS";
	protected static String adNUID          = "sAMAccountName";

	private static String [] adGroups = {"Sacramento Valley-Production-Hyperspace", 
		"Fresno Central Valley-Production-Hyperspace",
		"East Bay GSAA-Production-Hyperspace",
		"SF MS SM-Production-Hyperspace",        
		"Diablo Napa Sonoma-Production-Hyperspace",
		"Stanta Clara SanJose-Production-Hyperspace",   
		"Regional ACs",     
		"DevTest-AllApps",                        
		"CS HealthConnect National Help Desk",
		"SCal Central Business Office Users"};              


	/**
	 * See Constants
	 * 
	 *  DIRECTORY_EDS  
	 *  DIRECTORY_TIM  
	 *  DIRECTORY_AD   
	 */
	public PersonSearch(String directoryType)
	{
		dsType = directoryType.toLowerCase();
	}


	public static void main(String[] args) 
	{

		if (!validateParameters(args))
		{
			System.out.println("       ");
			System.out.println("Usage: PersonSearch -ds (AD | EDS | TIM | X500)");
			System.out.println("                    -nuid <USERID> OR -file <UserDataFile.xls>)");
			System.out.println("                    [-group GROUP_NUMBER] ");
			System.out.println("                    [-match PATTERN] ");
			System.out.println("       ");
			System.out.println("       ** Options between square brakets are optional.");
			System.out.println("       ");
			System.out.println("       Available Directory Servers:");
			System.out.println("       ");
			System.out.println("        1.) AD   - CS Active Directory Server");
			System.out.println("        2.) EDS  - Enterprise Directory Server");
			System.out.println("        3.) TIM  - IBM Directory Server (TIM)");
			System.out.println("        4.) X500 - Email Server");
			System.out.println("       ");
			System.out.println("       UserDataFile.xls should contain the following headers");
			System.out.println("       and corresponding data.");
			System.out.println("       ");
			System.out.println("       NUID, LASTNAME, FIRSTNAME");
			System.out.println("       ");
			System.out.println("       GROUP_NUMBER is one of:");
			System.out.println("       ");

			for (int i=0; i<adGroups.length; i++)
			{
				System.out.println("       " + i + ".)   " + adGroups[i]);              
			}

			System.out.println("       ");                 

			System.out.println("       Example:");                 
			System.out.println("       ");                 
			System.out.println("       Search for user with given ID having AD Groups containing");
			System.out.println("       Hyperspace in group name.");                
			System.out.println("");
			System.out.println("       PersonSearch -ds AD -id D748705 -match Hyperspace");                  
			System.out.println("       ");                 
			System.out.println("       Search for users NUIDs contained in specified file matching");
			System.out.println("       group 1 (see above group numbers.)");   
			System.out.println("");
			System.out.println("       PersonSearch -ds AD -file ELGUSERS120804.xls -group 1");                
			return;
		}

		String dsType       = getDSType(args);
		String nuid         = getNUID(args); 
		String userDataFile = getUserDataFile(args);
		String groupNumber  = getGroupNumber(args);
		String pattern      = getPattern(args);

		String    dn               = getDN(dsType);
		System.out.println("dn: " + dn);
		
		String [] searchAttributes = getSearchAttributes(dsType);
		String    filter           = getNUIDFilter(nuid, dsType);


		System.out.println("Searching...");
		System.out.println("-----------------------------------------------");
		System.out.println("Directory Server:       " + dsType);

		System.out.println("DN:                     " + dn);
		System.out.println("Search Filter:          " + filter);

		if (nuid != null)
		{
			System.out.println("NUID:               " + nuid);
		}

		if (userDataFile != null)
		{
			System.out.println("UserDataFile:       " + userDataFile);
		}

		int grpIndex = 0;

		if (groupNumber != null)
		{
			grpIndex = Integer.valueOf(groupNumber).intValue()-1;
		}

		if (groupNumber != null)
		{
			System.out.println("Group Name:         " + adGroups[grpIndex] + " (" + groupNumber + ")");
		}

		if (pattern != null)
		{
			System.out.println("Group Name Pattern: " + pattern);
		}
		System.out.println("-----------------------------------------------");


		if (groupNumber != null)
		{
			pattern = adGroups[grpIndex];
		}

		try
		{
			
			PersonSearch personSearch = new PersonSearch(dsType);

			personSearch.connect();

			String first_name = "Scott";
			String last_name = "Schork";
			
			
			//Person person = personSearch.findPersonByNUID(nuid);
			
			Person person = personSearch.findPersonByName(first_name, last_name);

			if (person.isValid())
			{
				System.out.println(person);
			}
			else
			{
				System.out.println("Unable to find person  " + last_name + "," + first_name + " in " + dsType + " directory.");
			}

			personSearch.close();

		}
		catch (Throwable exception)
		{
			exception.printStackTrace();
		}
	} 

	public void connect() throws Exception
	{           
		String dsHost   = getDSHost(dsType);
		String dsPort   = getDSPort(dsType);
		String userName = getUserName(dsType);
		String password = getPassword(dsType);

		directoryLookup = new DSLookup();
		directoryLookup.connect(dsHost, Integer.parseInt(dsPort), userName, password);
	}


	public void connect(String userName, String password) throws Exception
	{           
		String dsHost   = getDSHost(dsType);
		String dsPort   = getDSPort(dsType);

		directoryLookup = new DSLookup();
		directoryLookup.connect(dsHost, Integer.parseInt(dsPort), userName, password);
	}


	public void close() throws Exception
	{
		if (directoryLookup != null)
		{
			directoryLookup.close();            
		}

		directoryLookup = null;
	}

	public void closeConnection() throws Exception
	{
		close();
	}

	public Person findPersonByNUID(String nuid) throws Exception
	{
		String filter = getNUIDFilter(nuid, dsType);

		return findPerson(filter);
	}


	public Person findPersonByEmployeeID(String employeeID) throws Exception
	{
		String filter = getEmployeeIDFilter(employeeID);

		return findPerson(filter);
	}

	public Person findPersonByName(String firstName, String lastName) throws Exception
	{
		String filter = getNameFilter(firstName, lastName);

		return findPerson(filter);
	}


	protected Person findPerson(String filter) throws Exception
	{
		String    dn               = getDN(dsType);
		String [] searchAttributes = getSearchAttributes(dsType);

		NamingEnumeration namingEnumeration = searchDirectoryServer(dn, filter, searchAttributes);

		List personList = new ArrayList();
		setPersonAttributes(personList, namingEnumeration);

		Person person = null;

		if(personList.size() == 1)
		{
			person = (Person)personList.get(0);
		}
		else
		{
			person = new Person();
		}

		return person;
	}

	public List findPersons(PersonFilter filter) throws Exception
	{
		String    dn               = getDN(dsType);
		String [] searchAttributes = getSearchAttributes(dsType);

		NamingEnumeration namingEnumeration = searchDirectoryServer(dn, filter.getLDAPFilter(), searchAttributes);

		List personList = new ArrayList();
		setPersonAttributes(personList, namingEnumeration);

		return personList;
	}

	private void setPersonAttributes(List personList, NamingEnumeration namingEnumeration) throws Exception
	{

		while (namingEnumeration.hasMore())
		{

			Person person             = new Person();

			SearchResult searchResult = (SearchResult) namingEnumeration.next();

			Attributes attributes = searchResult.getAttributes();

			NamingEnumeration enumeration = attributes.getAll();

			while (enumeration.hasMoreElements())
			{
				Attribute attribute = (Attribute) enumeration.next();

				setPersonAttribute(person, attribute);
			}

			if (person.getNUID().length() == 7)
			{
				person.setValid(true);
			}

			personList.add(person);
		}
	}


	private void setPersonAttribute(Person person, Attribute attribute) throws Exception
	{       
		String attributeName  = attribute.getID().toUpperCase();
		String attributeValue = "";

		int attributeCount = attribute.size();

		for (int i=0; i<attributeCount; i++)
		{
			attributeValue =  attributeValue + (String)attribute.get(i);

			if (i<attributeCount-1)
			{
				attributeValue += "::";
			}
		}       


		// for TIM, Critical Path and X500
		if (attributeName.equals("UID"))				   person.setNUID(attributeValue);

		// AD sAMAccountName is the NUID attribute
		else if (attributeName.equals("SAMACCOUNTNAME"))   person.setNUID(attributeValue);

		else if (attributeName.equals("SN"))		   person.setLastName(attributeValue);
		else if (attributeName.equals("GIVENNAME"))	   person.setFirstName(attributeValue);
		else if (attributeName.equals("EMPLOYEENUMBER"))   person.setEmployeeID(attributeValue);
		else if (attributeName.equals("KPMIDDLENAME"))	   person.setMiddleName(attributeValue);
		else if (attributeName.equals("KPCOSTCENTER"))	   person.setCostCenterCode(attributeValue);
		else if (attributeName.equals("KPCOSTCENTERNAME")) person.setCostCenterName(attributeValue);
		else if (attributeName.equals("KPJOBCODE"))	   person.setJobCode(attributeValue);
		else if (attributeName.equals("KPJOBCODEDESC"))	   person.setJobTitle(attributeValue);
		else if (attributeName.equals("MAIL"))		   person.setEmailAddress(attributeValue);
		else if (attributeName.equals("KPLOCATIONNUMBER")) person.setLocationCode(attributeValue);
		else if (attributeName.equals("KPLOCATIONDESC"))   person.setLocationName(attributeValue);
		else if (attributeName.equals("KPREGION"))	   person.setRegionName(attributeValue);
		else if (attributeName.equals("KPREGIONCODE"))	   person.setRegionCode(attributeValue);
		else if (attributeName.equals("KPEMPLOYEECLASS"))  person.setEmployeeClass(attributeValue);
		else if (attributeName.equals("TELEPHONENUMBER"))  person.setTelephoneNumber(attributeValue);
		else if (attributeName.equals("ERCUSTOMDISPLAY"))  person.setCustomDisplay(attributeValue);
		else person.addMiscAttribute(attributeName, attributeValue);

	}



	protected NamingEnumeration searchDirectoryServer(String dn, String filter, String [] searchAttributes) throws Exception
	{
		NamingEnumeration namingEnumeration = null;

		namingEnumeration = directoryLookup.search(dn, filter, searchAttributes);

		return namingEnumeration;
	}


	protected static String getDN(String dsType)
	{
		System.out.println("getDn : " + dsType);
		return ApplicationProperties.getInstance("directory").getProperty(dsType + ".directory.dn");              
	}


	protected static String getDSHost(String dsType)
	{
		return ApplicationProperties.getInstance("directory").getProperty(dsType + ".directory.hostname");
	}


	protected static String getDSPort(String dsType)
	{
		return ApplicationProperties.getInstance("directory").getProperty(dsType + ".directory.port");        
	}


	protected static String getUserName(String dsType)
	{
	    return ApplicationProperties.getInstance("directory").getProperty(dsType + ".directory.username", null);
	}


	protected static String getPassword(String dsType)
	{
	    return ApplicationProperties.getInstance("directory").getProperty(dsType + ".directory.password", null); 
	}


	protected static String [] getSearchAttributes(String dsType)
	{
		String attributeString = ApplicationProperties.getInstance("directory").getProperty(dsType + ".directory.search-attributes");


		String [] attributes = attributeString.split(",");

		return attributes;
	}


	public static String getNUIDFilter(String nuid, String dsType)
	{
		String nuidFilter = "(uid=" + nuid + ")";

		if (dsType.toUpperCase().equals("AD"))
		{
			nuidFilter = "(" + adNUID + "=" + nuid + ")";
		}

		return nuidFilter;
	}

	public static String getEmployeeIDFilter(String employeeID)
	{

		return "(employeeNumber="+ employeeID + ")";
	}


	public static String getNameFilter(String firstName, String lastName)
	{
		return "(& (sn=" + lastName + ")(givenName=" + firstName + "))";
	}

	public static String getLocationNameFilter(String locationName)
	{
		return "(kpLocation=" + locationName + ")";
	}

	public static String getCostCenterNameFilter(String costCenterName)
	{
		return "(kpCostCenterName=" + costCenterName +")";
	}

	public static String getADGroupFilter(String groupPattern)
	{
		return "(memberOf=*"+ groupPattern +"*)";
	}


	private static boolean validateParameters(String [] args)
	{
		boolean hasDSOption       = false; 
		boolean hasNUIDOption     = false; 
		boolean hasFileNameOption = false; 
		boolean hasGroupOption    = false; 
		boolean hasMatchOption    = false;
		boolean hasADOption       = false;

		for (int i=0; i<args.length; i++)
		{
			if (args[i].toUpperCase().equals("-DS"))
			{
				hasDSOption = true;

				if (args[i+1].toUpperCase().equals("AD"))
				{
					hasADOption = true;
				}
			}
			else if (args[i].toUpperCase().equals("-NUID"))
			{
				hasNUIDOption = true;
			}
			else if (args[i].toUpperCase().equals("-FILE"))
			{
				hasFileNameOption = true;
			}
			else if (args[i].toUpperCase().equals("-GROUP"))
			{
				hasGroupOption = true;
			}
			else if (args[i].toUpperCase().equals("-MATCH"))
			{
				hasMatchOption = true;
			}
		}

		boolean valid = hasDSOption && (hasNUIDOption || hasFileNameOption);

		/*
		// require either an AD Group or AD group pattern option if DS=AD.
		if(hasADOption)
		{
			valid = valid && (hasGroupOption || hasMatchOption);
		}
		*/

		return valid;
	}


	private static String getDSType(String [] args)
	{
		String [] params = getParameter(args, "-ds");

		return params[1].toLowerCase();
	}

	private static String getNUID(String [] args)
	{
		String [] params = getParameter(args, "-nuid");

		return params[1];
	}

	private static String getUserDataFile(String [] args)
	{
		String [] params = getParameter(args, "-file");

		return params[1];
	}

	private static String getGroupNumber(String [] args)
	{
		String [] params = getParameter(args, "-group");

		return params[1];
	}

	private static String getPattern(String [] args)
	{
		String [] params = getParameter(args, "-match");

		return params[1];
	}

	private static String[] getParameter(String [] args, String option)
	{
		String [] params = new String[2];

		params[0] = null;
		params[1] = null;

		for (int i=0; i<args.length; i++)
		{
			if (args[i].toUpperCase().equals(option.toUpperCase()))
			{
				params[0] = args[i].toUpperCase();

				if (i < args.length-1)
				{
					params[1] = args[i+1].toUpperCase();
				}
			}
		}

		return params;
	}


	public static void debugSearchResult(SearchResult searchResult) throws Exception
	{
		System.out.println(searchResult.getName());

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

		}
	}

	public static void writeSearchResult(SearchResult searchResult, String dsType, String groupNumber, String groupPattern) throws Exception
	{
		Attributes attributeSet = searchResult.getAttributes();

		Attribute attribute = null;

		if (dsType.equals("ad"))
		{
			attribute = attributeSet.get("sAMAccountName");
		}
		else
		{
			attribute = attributeSet.get("uid");
		}

		if (attribute != null) System.out.println("NUID: " + attribute.get(0));

		attribute           = attributeSet.get("sn"); 

		if (attribute != null) System.out.println("Last Name:" + attribute.get(0));

		attribute           = attributeSet.get("givenName"); 

		if (attribute != null) System.out.println("First Name:" + attribute.get(0));


		if (dsType.equals("ad"))
		{

			attribute           = attributeSet.get("memberOf"); 
			for (int i=0; i<attribute.size(); i++)
			{
				String memberOfDN = (String)attribute.get(i);
				if (groupPattern != null && memberOfDN.matches(".*" + groupPattern + ".*"))
				{
					memberOfDN = memberOfDN.substring(0, memberOfDN.indexOf(","));
					memberOfDN = memberOfDN.substring(memberOfDN.indexOf("=") + 1);

					System.out.print(memberOfDN);
					System.out.print("\t");
				}


				//if (i<attribute.size()-1)
				//{
				//	System.out.print("");
				//}
			}

			attribute = attributeSet.get("homedirectory");
			if (attribute != null) System.out.print(attribute.get(0));
			System.out.print("\t");

			attribute = attributeSet.get("homedrive");
			if (attribute != null)	System.out.print(attribute.get(0));
			System.out.print("\t");

			attribute = attributeSet.get("scriptPath");
			if (attribute != null) System.out.print(attribute.get(0));
			System.out.print("\t");
		}

		System.out.print(searchResult.getName());
		System.out.println("");     

	}
}

