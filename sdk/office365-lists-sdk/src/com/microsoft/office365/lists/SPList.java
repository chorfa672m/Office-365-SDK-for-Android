/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information.
 ******************************************************************************/
package com.microsoft.office365.lists;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.microsoft.office365.OfficeEntity;

// TODO: Auto-generated Javadoc
/**
 * The Class SPList.
 */
public class SPList extends OfficeEntity {

	/**
	 * List from json.
	 * 
	 * @param json
	 *            the json
	 * @return the list
	 * @throws JSONException
	 *             the JSON exception
	 */
	public static List<SPList> listFromJson(JSONObject json) throws JSONException {
		return OfficeEntity.listFromJson(json, SPList.class);
	}

	/**
	 * Instantiates a new SP list.
	 * 
	 * @throws JSONException
	 *             the JSON exception
	 */
	public SPList() {
		super();
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return getData("Id").toString();
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return getData("Title").toString();
	}

	/**
	 * Gets the list item entity type full name.
	 * 
	 * @return the list item entity type full name
	 */
	public String getListItemEntityTypeFullName() {
		return getData("ListItemEntityTypeFullName").toString();
	}
}
