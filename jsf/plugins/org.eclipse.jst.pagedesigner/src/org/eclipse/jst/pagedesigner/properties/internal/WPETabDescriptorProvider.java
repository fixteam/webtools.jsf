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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptorProvider;

/**
 * Class that provides the default tabs for the Web Page Editor
 */
public class WPETabDescriptorProvider implements ITabDescriptorProvider {

	private AttributesTabDescriptor _attributesTabDescriptor;
//	private QuickEditTabDescriptor _quickTabDescriptor;
	
	
	/*
	 * founderfix
	 */
	private FixAdvanceTabDescriptor _fixAdvanceTabDescriptor;
	private FixNormalTabDescriptor _fixNormalTabDescriptor;
	private FixServerTabDescriptor _fixServerTabDescriptor;
	private FixEventTabDescriptor _fixEventTabDescriptor;
	private FixVerifyTabDescriptor _fixVerifyTabDescriptor;
	private FixActionTabDescriptor _fixActionTabDescriptor;

	/**
	 * Constructor
	 */
	public WPETabDescriptorProvider() {
//		_quickTabDescriptor = new QuickEditTabDescriptor();
		_attributesTabDescriptor = new AttributesTabDescriptor();
		
		/*
		 * founderfix
		 * 属性页改造
		 */
		_fixAdvanceTabDescriptor = new FixAdvanceTabDescriptor(); //高级
		_fixNormalTabDescriptor = new FixNormalTabDescriptor();	//常规
		_fixServerTabDescriptor = new FixServerTabDescriptor();	//后台	
		_fixEventTabDescriptor = new FixEventTabDescriptor();	//事件
		_fixVerifyTabDescriptor = new FixVerifyTabDescriptor();	//验证
		_fixActionTabDescriptor = new FixActionTabDescriptor();	//操作
	}
	
	/**
	 * Provides a QuickTabDescriptor and the AttributesTabDescriptor
	 */
	public ITabDescriptor[] getTabDescriptors(IWorkbenchPart part,
			ISelection selection) {

		return new ITabDescriptor[]{
									/*
									 * founderfix
									 * 属性页改造
									 */
									_fixAdvanceTabDescriptor,
									_fixNormalTabDescriptor,
									_fixServerTabDescriptor,
									_fixEventTabDescriptor,
									_fixVerifyTabDescriptor,
									_fixActionTabDescriptor
									,
									_attributesTabDescriptor
									};
	}

}
