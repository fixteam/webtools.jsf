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
package org.eclipse.jst.jsf.core.internal.tld;

import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDDocument;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDElementDeclaration;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLElementDeclaration;
import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMNodeWrapper;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.w3c.dom.Element;

/**
 * Utility class to content model related information.
 * 
 * @author mengbo
 */
public final class CMUtil {
	/**
	 * If the element is a custom tag, get the URI of it. If the element is a
	 * standard JSP tag, return null. If is not jsp tag, then return null
	 * @param decl
     *  
	 * @return the tag uri as a string
	 */
	public static String getTagURI(CMElementDeclaration decl) {
		if (decl instanceof CMNodeWrapper) {
			decl = (CMElementDeclaration) ((CMNodeWrapper) decl)
					.getOriginNode();
		}
		if (decl instanceof TLDElementDeclaration) {
			CMDocument doc = ((TLDElementDeclaration) decl).getOwnerDocument();
			if (doc instanceof TLDDocument) {
				return ((TLDDocument) doc).getUri();
			}
		}
		return null;
	}

	/**
	 * Test whether this is the JSP core tag.
	 * 
	 * @param decl
	 * @return true if decl is a jsp element declaration
	 */
	public static boolean isJSP(CMElementDeclaration decl) {
		if (!decl.supports(HTMLCMProperties.IS_JSP)) {
			return false;
		}
		Boolean b = (Boolean) decl.getProperty(HTMLCMProperties.IS_JSP);
		return b.booleanValue();
	}

	/**
	 * @param decl
	 * @return true if the element declartion is a non-JSP html element
	 */
	public static boolean isHTML(CMElementDeclaration decl) {
		if (!isJSP(decl) && (decl instanceof HTMLElementDeclaration)) {
			return true;
		}
		return false;
	}

	/**
	 * get element declaration of specified element
	 * 
	 * @param element
	 * @return null if can't get it.
	 */
	public static CMElementDeclaration getElementDeclaration(Element element) {
		if (element == null) {
			return null;
		}
		INodeNotifier notifier = (INodeNotifier) element.getOwnerDocument();
		if (notifier == null) {
			return null;
		}
		ModelQueryAdapter mqa = (ModelQueryAdapter) notifier
				.getAdapterFor(ModelQueryAdapter.class);
		if (mqa == null) {
			return null;
		}
		return mqa.getModelQuery().getCMElementDeclaration(element);
	}

	/**
	 * @param element
	 * @return the TLDElementDeclaration for element or null if not found
	 */
	public static TLDElementDeclaration getTLDElementDeclaration(Element element) {
		CMNode decl = getElementDeclaration(element);
		if (decl instanceof CMNodeWrapper) {
			decl = ((CMNodeWrapper) decl).getOriginNode();
		}
		if (decl instanceof TLDElementDeclaration) {
			return (TLDElementDeclaration) decl;
		}
        return null;
	}

	/**
	 * give an element, get its namespace URI.
	 * 
	 * @param element
	 * @return the namespace URI
	 */
	public static String getElementNamespaceURI(Element element) {
		CMElementDeclaration decl = getElementDeclaration(element);
		if (decl == null) {
			return null;
		}

		if (isJSP(decl)) {
			return ITLDConstants.URI_JSP;
		} else if (isHTML(decl)) {
			return ITLDConstants.URI_HTML;
		}

		return getTagURI(decl);
	}

	/**
	 * @param element
	 * @return true if the element can have children
	 */
	public static boolean canHaveDirectTextChild(Element element) {
		CMElementDeclaration decl = getElementDeclaration(element);
		if (decl == null) {
			return true;
		}
		int contentType = decl.getContentType();
		return contentType != CMElementDeclaration.ELEMENT
				&& contentType != CMElementDeclaration.EMPTY;

	}
    
    private CMUtil()
    {
        // util class, no external instantiation
    }
}
