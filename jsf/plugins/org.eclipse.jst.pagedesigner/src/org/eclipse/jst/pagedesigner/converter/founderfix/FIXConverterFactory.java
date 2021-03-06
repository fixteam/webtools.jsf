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
package org.eclipse.jst.pagedesigner.converter.founderfix;

//import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jst.jsf.context.resolver.structureddocument.IStructuredDocumentContextResolverFactory;
import org.eclipse.jst.jsf.context.resolver.structureddocument.IWorkspaceContextResolver;
import org.eclipse.jst.jsf.context.structureddocument.IStructuredDocumentContext;
import org.eclipse.jst.jsf.context.structureddocument.IStructuredDocumentContextFactory2;
import org.eclipse.jst.pagedesigner.converter.HiddenTagConverter;
import org.eclipse.jst.pagedesigner.converter.IConverterFactory;
import org.eclipse.jst.pagedesigner.converter.ITagConverter;
//import org.eclipse.jst.pagedesigner.converter.TagConverterToDumBlock;
//import org.eclipse.jst.pagedesigner.editors.palette.TagImageManager;
import org.eclipse.swt.graphics.Image;
import org.w3c.dom.Element;

import com.founder.fix.base.wpe.CurrentRemember;


/**
 * @author mengbo
 * @version 1.5
 */
public class FIXConverterFactory implements IConverterFactory {
    private final ILabelProvider  _labelProvider;
    
	/**
	 * 
	 */
	public FIXConverterFactory() {
        _labelProvider = new MyLabelProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jst.pagedesigner.converter.IConverterFactory#createConverter(org.w3c.dom.Element)
	 */
	public ITagConverter createConverter(Element element, int mode) {
//		String tagName = element.getLocalName();

//		if (mode == IConverterFactory.MODE_PREVIEW) {
			// we want to generate the included page in preview, so
			// handle differently
//			if (IFOUNDERFIXCoreConstants.TAG_INCLUDE.equalsIgnoreCase(tagName)) {
//				IncludeTagConverterPreview c = new IncludeTagConverterPreview(
//						element, "page"); //$NON-NLS-1$
//				c.setMode(mode);
//				return c;
//			} else if (IFOUNDERFIXCoreConstants.TAG_DIRECTIVE_INCLUDE
//					.equalsIgnoreCase(tagName)) {
//				IncludeTagConverterPreview c = new IncludeTagConverterPreview(
//						element, "file"); //$NON-NLS-1$
//				c.setMode(mode);
//				return c;
//			} else if (IFOUNDERFIXCoreConstants.TAG_ROOT.equalsIgnoreCase(tagName)) {
//				TagConverterToDumBlock c = new TagConverterToDumBlock(element);
//				c.setNeedBorderDecorator(true);
//				c.setMode(mode);
//				return c;
//			} else {
//				return new HiddenTagConverter(element, _labelProvider);
//			}
//		}
//        if (IFOUNDERFIXCoreConstants.TAG_ROOT.equalsIgnoreCase(tagName)) {
//        	TagConverterToDumBlock c = new TagConverterToDumBlock(element);
//        	c.setNeedBorderDecorator(true);
//        	c.setMode(mode);
//        	return c;
//        }
        return new HiddenTagConverter(element, _labelProvider);
	}
    
    private static class MyLabelProvider extends org.eclipse.jface.viewers.LabelProvider
    {

        public Image getImage(Object element) 
        {
            if (element instanceof ITagConverter)
            {
                final Element hostElement = ((ITagConverter)element).getHostElement();
                IStructuredDocumentContext context = IStructuredDocumentContextFactory2.INSTANCE.getContext(hostElement);
                if (context != null){                	
                	IWorkspaceContextResolver wsResolver  = IStructuredDocumentContextResolverFactory.INSTANCE.getWorkspaceContextResolver(context);
                	if (wsResolver != null){
                		
//                		return TagImageManager.getInstance().getSmallIconImage(
//                				(IFile)wsResolver.getResource(),"FOUNDERFIX",  //$NON-NLS-1$
////                				ITLDConstants.URI_FOUNDERFIX + ":"+
//                				hostElement.getAttribute("ComponentType").trim()); //$NON-NLS-1$
////                						hostElement.getLocalName()); 
////                				"JQRate");  //$NON-NLS-1$
                		
                		if(CurrentRemember.currentFormPagePath==null){
                			return null;
                		}
                		
//                		String pagePage = CurrentRemember.currentFormPagePath;	
                		String webProjectName = CurrentRemember.webProjectName;
                		
                		
                		if(hostElement.getAttribute("ComponentType")==null //$NON-NLS-1$
                				||hostElement.getAttribute("ComponentType").equals("")){ //$NON-NLS-1$ //$NON-NLS-2$
                			return null;
                		}
						String Path = getWorkspaceRealPath() + webProjectName
								+ "/WebRoot/components/" + hostElement.getAttribute("ComponentType").trim() + "/design.jpg";   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                		return new Image(null, Path); 
                	}
                }
            }
            
            return null;
        }
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jst.pagedesigner.converter.IConverterFactory#getSupportedURI()
	 */
	public String getSupportedURI() {
		return "founderfix"; //$NON-NLS-1$
	}

	/**
	 * 获取eclipse的workspace真实目录
	 */
	public static String getWorkspaceRealPath() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		// root.getFullPath();
		return root.getLocation().toString() + "/";	//$NON-NLS-1$
		// root.getLocationURI();
	}
}
