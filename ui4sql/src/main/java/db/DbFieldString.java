/**
 * UI4SQL V1.0 (https://ui4sql.net)
 * Copyright 2022 PaulsenITSolutions 
 * Licensed under MIT (http://github.com/arnepaulsen/ui4sql/LICENSE) 
 */
package db;

import db.SqlHelper;

/**
 * @author PAULSEAR
 * 
 *  5/1/05 return an empty string if null
 */
public class DbFieldString extends DbField {

	private static db.SqlHelper sql = new SqlHelper();

	// ****************************
	// CONSTRUCTORS
	// ****************************

	// 1 parm
	public DbFieldString(String parmName) {
		super(parmName, new String(""));
	}

	// 2 parms
	public DbFieldString(String parmName, String parmValue) {
		super(parmName, parmValue);
		//System.out.println("DbFieldString:init : " + parmValue);
	}

	// 3 parms
	public DbFieldString(String parmName, String parmFormName, String parmValue) {
		super(parmName, parmFormName, parmValue);
	}

	// ****************************
	// PUBLIC GET
	// ****************************

	public String getText() {

		try {
			if (fieldValue != null) {
				return (String) fieldValue;
			} else {
				return new String("");
			}
		} catch (Exception e) {
			return new String("");
		}
	}

	public String getSQL() {
		return "'" + sql.doubleQ((String) fieldValue) + "'";
		
	}

	public void setValue(String parmString) {
		fieldValue = parmString;
	}

}
