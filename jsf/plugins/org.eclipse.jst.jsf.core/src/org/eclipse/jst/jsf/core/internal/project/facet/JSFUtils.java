/*******************************************************************************
 * Copyright (c) 2005 Oracle Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gerry Kessler - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.jst.jsf.core.internal.project.facet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.ContextParam;
import org.eclipse.jst.j2ee.webapplication.JSPType;
import org.eclipse.jst.j2ee.webapplication.Servlet;
import org.eclipse.jst.j2ee.webapplication.ServletMapping;
import org.eclipse.jst.j2ee.webapplication.ServletType;
import org.eclipse.jst.j2ee.webapplication.WebApp;
import org.eclipse.jst.j2ee.webapplication.WebapplicationFactory;
import org.eclipse.jst.jsf.core.internal.JSFCorePlugin;
import org.eclipse.jst.jsf.core.internal.Messages;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * Utility file for JSF model
 * 
 * @author Gerry Kessler - Oracle
 * @since M1
 */
public class JSFUtils {
	public static final String JSF_SERVLET_CLASS = "javax.faces.webapp.FacesServlet"; //$NON-NLS-1$
	public static final String JSF_CONFIG_CONTEXT_PARAM = "javax.faces.CONFIG_FILES"; //$NON-NLS-1$
	public static final String JSF_DEFAULT_CONFIG_PATH = "/WEB-INF/faces-config.xml"; //$NON-NLS-1$

	/**
	 * Convenience method for getting writeable WebApp model
	 * @param IProject
	 * @return WebArtifactEdit
	 */
	public static WebArtifactEdit getWebArtifactEditForWrite(IProject project) {
		return WebArtifactEdit.getWebArtifactEditForWrite(project);
	}

	/**
	 * Convenience method for getting read-only WebApp model
	 * @param IProject
	 * @return WebArtifactEdit
	 */
	public static WebArtifactEdit getWebArtifactEditForRead(IProject project) {
		return WebArtifactEdit.getWebArtifactEditForRead(project);
	}

	/**
	 * @param webApp
	 * @return Servlet - the JSF Servlet for the specified WebApp or null if not present
	 */
	public static Servlet findJSFServlet(WebApp webApp) {
		Servlet servlet = null;
		Iterator it = webApp.getServlets().iterator();
		while (it.hasNext()) {
			servlet = (Servlet) it.next();
			if (servlet.getWebType().isServletType()) {
				if (((ServletType) servlet.getWebType()).getClassName().equals(
						JSF_SERVLET_CLASS)) {
					break;
				}
			} else if (servlet.getWebType().isJspType()) {
				if (((JSPType) servlet.getWebType()).getJspFile().equals(
						JSF_SERVLET_CLASS)) {
					break;
				}
			}
		}
		return servlet;
	}

	/**
	 * Creates a stubbed JSF configuration file for specified JSF version and path
	 * @param String jsfVersion
	 * @param String configPath
	 */
	public static void createConfigFile(String jsfVersion, IPath configPath) {
		FileOutputStream os = null;
		PrintWriter pw = null;
		final String QUOTE = new String(new char[] { '"' });
		try {
			IPath dirPath = configPath.removeLastSegments(1);
			dirPath.toFile().mkdirs();
			File file = configPath.toFile();
			file.createNewFile();
			os = new FileOutputStream(file);
			pw = new PrintWriter(os);
			pw.write("<?xml version=" + QUOTE + "1.0" + QUOTE + " encoding=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ QUOTE + "UTF-8" + QUOTE + "?>\n\n"); //$NON-NLS-1$ //$NON-NLS-2$

			if (jsfVersion.equals("1.1")) { //$NON-NLS-1$
				pw.write("<!DOCTYPE faces-config PUBLIC\n"); //$NON-NLS-1$
				pw
						.write("    " //$NON-NLS-1$
								+ QUOTE
								+ "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN" //$NON-NLS-1$
								+ QUOTE + "\n"); //$NON-NLS-1$
				pw.write("    " + QUOTE //$NON-NLS-1$
						+ "http://java.sun.com/dtd/web-facesconfig_1_1.dtd" //$NON-NLS-1$
						+ QUOTE + ">\n\n"); //$NON-NLS-1$

				pw.write("<faces-config>\n\n"); //$NON-NLS-1$
				pw.write("</faces-config>\n"); //$NON-NLS-1$
			} else if (jsfVersion.equals("1.2")) { //$NON-NLS-1$
				pw.write("<faces-config\n"); //$NON-NLS-1$
				pw.write("    " + "xmlns=" + QUOTE //$NON-NLS-1$ //$NON-NLS-2$
						+ "http://java.sun.com/xml/ns/j2ee" + QUOTE + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
				pw.write("    " + "xmlns:xsi=" + QUOTE //$NON-NLS-1$ //$NON-NLS-2$
						+ "http://www.w3.org/2001/XMLSchema-instance" + QUOTE //$NON-NLS-1$
						+ "\n"); //$NON-NLS-1$
				pw
						.write("    " //$NON-NLS-1$
								+ "xsi:schemaLocation=" //$NON-NLS-1$
								+ QUOTE
								+ "http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-facesconfig_1_2.xsd" //$NON-NLS-1$
								+ QUOTE + "\n"); //$NON-NLS-1$
				pw.write("    " + "version=" + QUOTE + "1.2" + QUOTE + ">\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				pw.write("</faces-config>\n"); //$NON-NLS-1$
			}

			pw.close();
			pw = null;
		} catch (FileNotFoundException e) {
			JSFCorePlugin.log(IStatus.ERROR, Messages.JSFUtils_ErrorCreatingConfigFile, e);
		} catch (IOException e) {
			JSFCorePlugin.log(IStatus.ERROR, Messages.JSFUtils_ErrorCreatingConfigFile, e);
		} finally {
			if (pw != null)
				pw.close();
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					JSFCorePlugin.log(IStatus.ERROR, Messages.JSFUtils_ErrorClosingConfigFile, e);
				}
			}
		}
	}

	/**
	 * Creates servlet reference in WebApp if not present or updates servlet name if found
	 * using the passed configuration.
	 * 
	 * @param WebApp webApp
	 * @param IDataModel config
	 * @param Servlet servlet
	 * @return Servlet servlet - if passed servlet was null, will return created servlet
	 */
	public static Servlet createOrUpdateServletRef(WebApp webApp,
			IDataModel config, Servlet servlet) {
		String displayName = config.getStringProperty(IJSFFacetInstallDataModelProperties.SERVLET_NAME);

		if (servlet == null) {
			// Create the servlet instance and set up the parameters from data
			// model
			servlet = WebapplicationFactory.eINSTANCE.createServlet();
			servlet.setServletName(displayName);

			ServletType servletType = WebapplicationFactory.eINSTANCE
					.createServletType();
			servletType.setClassName(JSF_SERVLET_CLASS);
			servlet.setWebType(servletType);
			servlet.setLoadOnStartup(new Integer(1));
			// Add the servlet to the web application model
			webApp.getServlets().add(servlet);
		} else {
			// update
			servlet.setServletName(displayName);
			servlet.setLoadOnStartup(new Integer(1));
		}
		return servlet;
	}

	/**
	 * Creates servlet-mappings for the servlet
	 * 
	 * @param WebApp webApp
	 * @param List urlMappingList - list of string values to  be used in url-pattern for servlet-mapping
	 * @param Servlet servlet
	 */
	public static void setUpURLMappings(WebApp webApp, List urlMappingList,
			Servlet servlet) {
		// Add mappings
		Iterator it = urlMappingList.iterator();
		while (it.hasNext()) {
			String pattern = (String) it.next();
			ServletMapping mapping = WebapplicationFactory.eINSTANCE
					.createServletMapping();
			mapping.setServlet(servlet);
			mapping.setName(servlet.getServletName());
			mapping.setUrlPattern(pattern);
			webApp.getServletMappings().add(mapping);
		}
	}
	
	/**
	 * Removes servlet-mappings for servlet using servlet-name.
	 * @param WebApp webApp
	 * @param Servlet servlet
	 */
	public static void removeURLMappings(WebApp webApp, Servlet servlet) {
		String servletName = servlet.getServletName();
		if (servletName != null) {
			Iterator oldMappings = webApp.getServletMappings().iterator();
			while (oldMappings.hasNext()) {
				ServletMapping mapping = (ServletMapping) oldMappings.next();
				if (mapping.getServlet().getServletName()
						.equals(servletName)) {
					webApp.getServletMappings().remove(mapping);
				}
			}
		}
	}

	/**
	 * Creates or updates config file context-param in v 2.3 WebApp if non default configuration file is specified.
	 * @param WebApp webApp
	 * @param IDataModel config
	 */
	public static void setupConfigFileContextParamForV2_3(WebApp webApp,
			IDataModel config) {
		// if not default name and location, then add context param
		ContextParam cp = null;
		ContextParam foundCP = null;
		boolean found = false;
		if (!config.getStringProperty(IJSFFacetInstallDataModelProperties.CONFIG_PATH).equals(JSF_DEFAULT_CONFIG_PATH)) {
			// check to see if present
			Iterator it = webApp.getContexts().iterator();
			while (it.hasNext()) {
				cp = (ContextParam) it.next();
				if (cp.getParamName().equals(JSF_CONFIG_CONTEXT_PARAM)) {
					foundCP = cp;
					found = true;
				}
			}
			if (!found) {
				cp = WebapplicationFactory.eINSTANCE.createContextParam();
				cp.setParamName(JSF_CONFIG_CONTEXT_PARAM);
				cp.setParamValue(config.getStringProperty(IJSFFacetInstallDataModelProperties.CONFIG_PATH));
				webApp.getContexts().add(cp);
			} else {
				cp = foundCP;
				if (cp.getParamValue().indexOf(config.getStringProperty(IJSFFacetInstallDataModelProperties.CONFIG_PATH)) < 0) {
					String curVal = cp.getParamValue();
					String val = config.getStringProperty(IJSFFacetInstallDataModelProperties.CONFIG_PATH);
					if (curVal != null || !curVal.trim().equals("")) { //$NON-NLS-1$
						val = curVal + ",\n" + val; //$NON-NLS-1$
					}
					cp.setParamValue(val);
				}
			}
		}
	}
	/**
	 * Creates or updates config file context-param in v2.4 WebApp  if non default configuration file is specified.
	 * @param WebApp webApp
	 * @param IDataModel config
	 */
	public static void setupConfigFileContextParamForV2_4(WebApp webApp,
			IDataModel config) {
		// if not default name and location, then add context param
		ParamValue foundCP = null;
		ParamValue cp = null;
		boolean found = false;
		if (!config.getStringProperty(IJSFFacetInstallDataModelProperties.CONFIG_PATH).equals(JSF_DEFAULT_CONFIG_PATH)) {
			// check to see if present
			Iterator it = webApp.getContextParams().iterator();
			while (it.hasNext()) {
				cp = (ParamValue) it.next();
				if (cp.getName().equals(JSF_CONFIG_CONTEXT_PARAM)) {
					foundCP = cp;
					found = true;
				}
			}
			if (!found) {
				ParamValue pv = CommonFactory.eINSTANCE.createParamValue();
				pv.setName(JSF_CONFIG_CONTEXT_PARAM);
				pv.setValue(config.getStringProperty(IJSFFacetInstallDataModelProperties.CONFIG_PATH));
				webApp.getContextParams().add(pv);
			} else {
				cp = foundCP;
				if (cp.getValue().indexOf(config.getStringProperty(IJSFFacetInstallDataModelProperties.CONFIG_PATH)) < 0) {
					String curVal = cp.getValue();
					String val = config.getStringProperty(IJSFFacetInstallDataModelProperties.CONFIG_PATH);
					if (curVal != null || !curVal.trim().equals("")) { //$NON-NLS-1$
						val = curVal + ",\n" + val; //$NON-NLS-1$
					}
					cp.setValue(val);
				}
			}
		}
	}

}