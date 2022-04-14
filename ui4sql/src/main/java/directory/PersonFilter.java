/**
 * UI4SQL V1.0 (https://ui4sql.net)
 * Copyright 2022 PaulsenITSolutions 
 * Licensed under MIT (http://github.com/arnepaulsen/ui4sql/LICENSE) 
 */

/////////////////////////////////////////////////////////////////////
// Author: Enrique Meneses
// August 2005
//
//
/////////////////////////////////////////////////////////////////////
package directory;


public class PersonFilter
{   
    private String firstName    = null;
    private String lastName     = null;
    private String jobTitle     = null;
    private String costCenter   = null;
    private String locationCode = null;


    public void setFirstName(String firstName)
    {
	if(firstName != null && !firstName.equals(""))
	{
	    this.firstName = "(givenName=" + firstName + "*)";	    
	}
    }


    public void setLastName(String lastName)
    {
	if(lastName != null && !lastName.equals(""))
	{
	    this.lastName = "(sn=" + lastName + "*)";
	}
    }


    public void setJobTitle(String jobTitle)
    {
	if(jobTitle != null && !jobTitle.equals(""))
	{
	    this.jobTitle = "(kpJobCodeDesc=" + jobTitle + "*)";
	}
    }

    
    public void setCostCenter(String costCenter)
    {
	if(costCenter != null && !costCenter.equals(""))
	{
	    this.costCenter = "(kpCostCenterName=" + costCenter + "*)";
	}
    }

    
    public void setLocationCode(String locationCode)
    {
	if(locationCode != null && !locationCode.equals(""))
	{
	    this.locationCode = "(kpLocationNumber=" + locationCode + ")";
	}
    }

    public boolean isSet()
    {

       return (firstName!=null || lastName != null || jobTitle != null || costCenter != null || locationCode != null);

    }


    public String toString()
    {
	return getLDAPFilter();
    }


    public String getLDAPFilter()
    {
	String ldapFilter = "";
	int filterCount   = 0;

	if(firstName != null && !firstName.equals(""))
	{
	    ldapFilter += firstName;
	    filterCount++;
	}
	
	if (lastName != null && !lastName.equals(""))
	{
	    ldapFilter += lastName;
	    filterCount++;
	}
	
	if (jobTitle != null && !jobTitle.equals(""))
	{
	    ldapFilter += jobTitle;
	    filterCount++;
	}
	
	if (costCenter != null && !costCenter.equals(""))
	{
	    ldapFilter += costCenter; 
	    filterCount++;
	}
	
	if (locationCode != null && !locationCode.equals(""))
	{
	    ldapFilter += locationCode;
	    filterCount++;
	}

	
	if(ldapFilter.equals(""))
	{
	    throw new RuntimeException("Invalid LDAP Filter. No Filters Specified.");
	}

	if(filterCount > 1)
	{
	    ldapFilter = "(&" + ldapFilter + ")";
	}

	return ldapFilter;
    }

    public static void main(String [] arguments)
    {
	PersonFilter filter = new PersonFilter();


	filter.setFirstName("FN");
	filter.setLastName("LN");
	filter.setJobTitle("JOB");
	filter.setCostCenter("CC");
	filter.setLocationCode("LOC");

	System.out.println("Filter: " + filter);
    }
}
