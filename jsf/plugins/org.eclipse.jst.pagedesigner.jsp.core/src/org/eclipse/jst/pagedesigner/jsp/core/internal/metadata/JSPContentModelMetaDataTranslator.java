/*******************************************************************************
 * Copyright (c) 2007 Oracle Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Oracle - initial API and implementation
 *    
 ********************************************************************************/
package org.eclipse.jst.pagedesigner.jsp.core.internal.metadata;

import org.eclipse.jst.jsf.common.metadata.internal.AbstractTagLibDomainContentModelMetaDataTranslator;
import org.eclipse.jst.jsf.common.metadata.internal.IMetaDataModelMergeAssistant;
import org.eclipse.jst.jsf.common.metadata.internal.IMetaDataTranslator;
import org.eclipse.wst.html.core.internal.contentmodel.JSPCMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;

public class JSPContentModelMetaDataTranslator extends AbstractTagLibDomainContentModelMetaDataTranslator implements IMetaDataTranslator {

	public void translate(final IMetaDataModelMergeAssistant assistant) {
		setAssistant(assistant);
		CMDocument doc = getSourceModel();
		if (doc!= null && doc instanceof JSPCMDocument){
			doTranslate(doc);			
		}
	}


	protected String getURIDescription() {		
		return "JSP Tags";
	}

	protected String getURIDisplayLabel() {		
		return "JSP";
	}

}