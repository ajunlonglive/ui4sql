/**
 * UI4SQL V1.0 (https://ui4sql.net)
 * Copyright 2022 PaulsenITSolutions 
 * Licensed under MIT (http://github.com/arnepaulsen/ui4sql/LICENSE) 
 */
package plugins;

import java.util.Hashtable;
import forms.*;

/**
 * 
 * 2/15 added mySql 3/10 use tcodes
 */

public class RequestPlugin extends AbsDivisionPlugin {

	/***************************************************************************
	 * 
	 * Constructor
	 * 
	 **************************************************************************/

	public RequestPlugin() throws services.ServicesException {

		super();
		this.setTableName("trequest");
		this.setKeyName("request_id");
		this.setTargetTitle("IT Work Requests");

		this.setListHeaders( new String[] { "Title", "Reference", "Status",
				"Reason", "Requestor", "Owner" });

		this.setMoreListColumns(new  String[] {
				"trequest.title_nm",
				"trequest.reference_nm",
				"stat.code_desc as Stat",
				"reason.code_desc as ReasonDesc",
				"concat(requestor.last_name, ',', requestor.first_name) as theRequestor ",
				"concat(owner.last_name, ',', owner.first_name) as theOwner " });

		this.setMoreListJoins(new  String[] {
				" left join tcodes reason on trequest.reason_cd = reason.code_value and reason.code_type_id = 13 ",
				" left join tcodes stat on trequest.status_cd = stat.code_value and stat.code_type_id = 5 ",
				" left join tuser owner on trequest.owner_uid = owner.user_id ",
				" left join tuser requestor on trequest.requestor_uid = requestor.user_id " });

	}

	public boolean listColumnHasSelector(int columnNumber) {
		// the status column (#2) has a selector, other fields do not
		if (columnNumber > 1)
			// true causes getListSelector to be called for this column.
			return true;
		else
			return false;
	}

	/*
	 * 8/22 : only the status column will be called, so no need to check the
	 * column #
	 */
public WebField getListSelector(int columnNumber) {

		switch (columnNumber) {

		case 2: {
			// default the status to open when starting new list
			WebFieldSelect wf = new WebFieldSelect("FilterStatus", sm
					.Parm("FilterStatus"), sm.getCodes("STATUS"),
					"All Status");
			wf.setDisplayClass("listform");
			return wf;
		}
		case 3:
		{
			WebFieldSelect wf = new WebFieldSelect("FilterReason", sm
					.Parm("FilterReason"), sm.getCodes("OBJECTIVE"),
					"All Reasons");
				wf.setDisplayClass("listform");
			return wf;
		}

		case 4: {
			WebFieldSelect wf = new WebFieldSelect("FilterRequestor", (sm.Parm(
					"FilterRequestor").length() == 0 ? new Integer("0")
					: new Integer(sm.Parm("FilterRequestor"))), sm.getUserHT(),
					"All Requestors");
				wf.setDisplayClass("listform");
			return wf;

		}
		
		default:
		{

			WebFieldSelect wf = new WebFieldSelect("FilterOwner", (sm.Parm(
					"FilterOwner").length() == 0 ? new Integer("0")
					: new Integer(sm.Parm("FilterOwner"))), sm.getUserHT(),
					"All Owners");
				wf.setDisplayClass("listform");
			return wf;

		}
		}

	}
	public String getListAnd() {
		/*
		 * todo: cheating... need to map the code value to the description
		 */

		StringBuffer sb = new StringBuffer();

		// default status to open if no filter present

		if ((!sm.Parm("FilterStatus").equalsIgnoreCase("0"))
				&& (sm.Parm("FilterStatus").length() > 0)) {
			sb.append(" AND stat.code_value = '" + sm.Parm("FilterStatus") + "'");
		}

		if ((!sm.Parm("FilterReason").equalsIgnoreCase("0"))
				&& (sm.Parm("FilterReason").length() > 0)) {
			sb
					.append(" AND reason.code_value = '" + sm.Parm("FilterPriority")
							+ "'");
		}

		if ((!sm.Parm("FilterRequestor").equalsIgnoreCase("0"))
				&& (sm.Parm("FilterRequestor").length() > 0)) {
			sb.append(" AND trequest.requestor_uid = "
					+ sm.Parm("FilterRequestor"));
		}
		
		if ((!sm.Parm("FilterOwner").equalsIgnoreCase("0"))
				&& (sm.Parm("FilterOwner").length() > 0)) {
			sb.append(" AND trequest.owner_uid = "
					+ sm.Parm("FilterOwner"));
		}
		

		return sb.toString();

	}

	/***************************************************************************
	 * 
	 * HTML Field Mapping
	 * 
	 **************************************************************************/

	public Hashtable getWebFields(String parmMode)
			throws services.ServicesException {

		boolean addMode = parmMode.equalsIgnoreCase("add") ? true : false;

		Hashtable ht = new Hashtable();

		/*
		 * Ids
		 */

		ht.put("owner_uid", new WebFieldSelect("owner_uid",
				addMode ? new Integer("0") : (Integer) db
						.getObject("owner_uid"), sm.getUserHT(), true));

		ht.put("requestor_uid", new WebFieldSelect("requestor_uid",
				addMode ? new Integer("0") : (Integer) db
						.getObject("requestor_uid"), sm.getContactHT(), true));

		/*
		 * Codes
		 */
		ht.put("reason_cd", new WebFieldSelect("reason_cd", addMode ? "" : db
				.getText("reason_cd"), sm.getCodes("OBJECTIVE")));

		ht.put("status_cd", new WebFieldSelect("status_cd", addMode ? "O" : db
				.getText("status_cd"), sm.getCodes("STATUS")));

		ht.put("size_cd", new WebFieldSelect("size_cd", addMode ? "S" : db
				.getText("size_cd"), sm.getCodes("PROCESSTYPE")));

		ht.put("priority_cd", new WebFieldSelect("priority_cd", addMode ? "S"
				: db.getText("priority_cd"), sm.getCodes("PRIORITY")));

		ht.put("budgeted_cd", new WebFieldSelect("budgeted_cd", addMode ? "Y"
				: db.getText("budgeted_cd"), sm.getCodes("YESNO")));

		/*
		 * Strings
		 */

		ht.put("reference_nm", new WebFieldString("reference_nm", (addMode ? ""
				: db.getText("reference_nm")), 32, 32));

		ht.put("title_nm", new WebFieldString("title_nm", (addMode ? "" : db
				.getText("title_nm")), 64, 64));

		ht.put("status_tx", new WebFieldString("status_tx", (addMode ? "" : db
				.getText("status_tx")), 64, 100));

		ht.put("version_nm", new WebFieldString("version_nm", (addMode ? ""
				: db.getText("version_nm")), 8, 8));

		/*
		 * Flags
		 */
		ht.put("accepted_flag", new WebFieldCheckbox("accepted_flag",
				addMode ? "N" : db.getText("accepted_flag"), ""));

		/*
		 * Dates
		 */
		ht.put("request_dt", new WebFieldDate("request_dt", addMode ? "" : db
				.getText("request_dt")));

		ht.put("required_dt", new WebFieldDate("required_dt", addMode ? "" : db
				.getText("required_dt")));

		ht.put("accept_dt", new WebFieldDate("accept_dt", addMode ? "" : db
				.getText("accept_dt")));

		/*
		 * Blobs
		 */
		ht.put("desc_blob", new WebFieldText("desc_blob", addMode ? "" : db
				.getText("desc_blob"), 5, 100));

		ht.put("reason_blob", new WebFieldText("reason_blob", addMode ? "" : db
				.getText("reason_blob"), 5, 100));

		ht.put("alternative_blob", new WebFieldText("alternative_blob",
				addMode ? "" : db.getText("alternative_blob"), 5, 100));

		ht.put("qualify_blob", new WebFieldText("qualify_blob", addMode ? ""
				: db.getText("qualify_blob"), 5, 100));

		ht.put("rejection_blob", new WebFieldText("rejection_blob",
				addMode ? "" : db.getText("rejection_blob"), 5, 100));

		/*
		 * Return
		 */
		return ht;

	}
}
