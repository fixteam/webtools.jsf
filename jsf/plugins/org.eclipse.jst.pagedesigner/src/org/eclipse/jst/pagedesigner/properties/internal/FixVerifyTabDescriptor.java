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

import java.util.List;
import java.util.Vector;

import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISectionDescriptor;

import com.founder.fix.studio.wpeformdesigner.jst.pagedesigner.properties.ConstantVariable;

/**
 * Quick Edit Tab Descriptor
 */
public class FixVerifyTabDescriptor extends AbstractTabDescriptor {

	/**
	 * Quick Edit tab id
	 */
	public static final String TAB_ID = "jst.pagedesigner.tabbed.properties.fixAttributies"; //$NON-NLS-1$
	
	private Vector<ISectionDescriptor> descriptors;
	/**
	 * Constructor
	 */
	public FixVerifyTabDescriptor(){
		super();
		descriptors = new Vector<ISectionDescriptor>(1);
		descriptors.add(new Fix6SectionDescriptor(ConstantVariable.fixVerify_en));
//		getSectionDescriptors().add(new MDQuickEditTabSectionDescriptor());
	}
	
	@Override
	public List getSectionDescriptors() {		
		return descriptors;
	}
	
	public String getCategory() {
		return ConstantVariable.fixVerify_en;
	}

	public String getId() {
		return TAB_ID;
	}

	public String getLabel() {
		return ConstantVariable.fixVerify_cn;
	}

}
