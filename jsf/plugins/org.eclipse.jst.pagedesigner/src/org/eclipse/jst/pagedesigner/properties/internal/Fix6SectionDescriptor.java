/*******************************************************************************
 * Copyright (c) 2007 Oracle Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Oracle Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.pagedesigner.properties.internal;

import org.eclipse.jst.pagedesigner.properties.Fix6PropertySection;
import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;

/**
 * SectionDescriptor for the WPE Attributes tab
 */
public class Fix6SectionDescriptor extends AbstractSectionDescriptor {
	/**
	 * Id for the WPE AllPropertySectionDescriptor
	 */
	public static final String ID = "FixAdvanceSectionDescriptor"; //$NON-NLS-1$

	private ISection section;

	
	
	String _propertyType;
	
	public Fix6SectionDescriptor(String propertyType){
		_propertyType = propertyType;
	}
	
	
	
	public String getId() {
		return ID;
	}

	public ISection getSectionClass() {
//		if (section == null){
			section = new Fix6PropertySection(_propertyType);
//		}
		return section;
	}

	public String getTargetTab() {
		return FixAdvanceTabDescriptor.TAB_ID;
	}

}
