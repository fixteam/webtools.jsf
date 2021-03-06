/*******************************************************************************
 * Copyright (c) 2006 Sybase, Inc. and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sybase, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsf.common.ui.internal.dialogfield;

/**
 * DialogField can choose to also implement this interface. The purpose of this
 * interface is to make DialogFields to be more easily used to edit element
 * attributes, since attributes are all of string type.
 * 
 * @author mengbo
 * @version 1.5
 */
public interface ISupportTextValue {
	/**
	 * @param value
	 */
	public void setTextWithoutUpdate(String value);

	/**
	 * @return the text
	 */
	public String getText();

	/**
	 * @param value
	 */
	public void setText(String value);
}
