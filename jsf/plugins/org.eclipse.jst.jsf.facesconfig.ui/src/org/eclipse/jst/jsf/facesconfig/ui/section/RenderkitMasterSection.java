/*******************************************************************************
 * Copyright (c) 2004, 2005 Sybase, Inc. and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sybase, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsf.facesconfig.ui.section;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jst.jsf.facesconfig.emf.FacesConfigFactory;
import org.eclipse.jst.jsf.facesconfig.emf.FacesConfigPackage;
import org.eclipse.jst.jsf.facesconfig.emf.RenderKitType;
import org.eclipse.jst.jsf.facesconfig.ui.EditorMessages;
import org.eclipse.jst.jsf.facesconfig.ui.page.FacesConfigMasterDetailPage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Bryan Yang
 * 
 */
public class RenderkitMasterSection extends FacesConfigMasterSection {

	/**
	 * 
	 * @param parent
	 * @param managedForm
	 * @param toolkit
	 * @param page
	 */
	public RenderkitMasterSection(Composite parent, IManagedForm managedForm,
			FormToolkit toolkit, FacesConfigMasterDetailPage page) {
		super(parent, managedForm, toolkit, page, null, null);
		getSection().setText(EditorMessages.RenderKitMasterSection_Name);
		getSection().setDescription(
				EditorMessages.RenderKitMasterSection_Description);
	}

	/**
	 * Config the table viwer, set a filter for it, only the object of
	 * RenderKitType will be selected.
	 */
	protected void configViewer(StructuredViewer structuredViewer) {
		super.configViewer(structuredViewer);
		structuredViewer.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				return FacesConfigPackage.eINSTANCE.getRenderKitType()
						.isInstance(element);
			}
		});

	}

	/**
	 * Create a new render kit.
	 */
	protected void addButtonSelected(SelectionEvent e) {
		RenderKitType Renderkit = FacesConfigFactory.eINSTANCE
				.createRenderKitType();

		Command command = AddCommand.create(getEditingDomain(),
				this.getInput(), FacesConfigPackage.eINSTANCE
						.getFacesConfigType_RenderKit(), Renderkit);

		if (command.canExecute()) {
			getEditingDomain().getCommandStack().execute(command);
			IStructuredSelection selection = new StructuredSelection(Renderkit);
			getStructuredViewer().refresh();
			getStructuredViewer().setSelection(selection);
		}
	}
}
