/*******************************************************************************
 * Copyright (c) 2008 Oracle Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Oracle - initial API and implementation
 *    
 ********************************************************************************/
package org.eclipse.jst.pagedesigner.editors.palette;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.jsf.common.metadata.Entity;
import org.eclipse.jst.jsf.common.metadata.Model;
import org.eclipse.jst.jsf.common.metadata.Trait;
import org.eclipse.jst.jsf.common.metadata.internal.IImageDescriptorProvider;
import org.eclipse.jst.jsf.common.metadata.internal.IMetaDataSourceModelProvider;
import org.eclipse.jst.jsf.common.metadata.internal.TraitValueHelper;
import org.eclipse.jst.jsf.common.metadata.query.ITaglibDomainMetaDataModelContext;
import org.eclipse.jst.jsf.common.metadata.query.TaglibDomainMetaDataQueryHelper;
import org.eclipse.jst.pagedesigner.PDPlugin;
import org.eclipse.jst.pagedesigner.editors.palette.paletteinfos.PaletteInfo;
import org.eclipse.jst.pagedesigner.editors.palette.paletteinfos.PaletteInfos;
import org.eclipse.swt.graphics.Image;

/**
 * Locates and creates Images for tags using the common metadata framework.
 * 
 * No caching is of images is occurring at this time.  
 * 
 * Some code is being duplicated in palette helper.   PaletteHelper should be re-factored to use this code
 * 
 */
public class TagImageManager {
	private static TagImageManager INSTANCE = null;
	
	private final static ImageDescriptor DEFAULT_SMALL_ICON = PDPlugin
	.getDefault().getImageDescriptor(
		"palette/GENERIC/small/PD_Palette_Default.gif");

	private final static ImageDescriptor DEFAULT_LARGE_ICON = PDPlugin
		.getDefault().getImageDescriptor(
				"palette/GENERIC/large/PD_Palette_Default.gif");

	private static final String TRAIT_ICON_SMALL = "small-icon";

	private static final String TRAIT_ICON_LARGE = "large-icon";
	
	/**
	 * @return singleton instance 
	 */
	public synchronized static TagImageManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TagImageManager();
		}
		return INSTANCE;
	}	
	
	/**
	 * @param project
	 * @param nsUri
	 * @param tagName
	 * @return small image using metadata.  May be null.
	 */
	public Image getSmallIconImage(IProject project, String nsUri, String tagName) {
		Image image = null;
		Model model = getModel(project, nsUri);
		if (model != null){
			ImageDescriptor imgDesc = getSmallIconImageDescriptor(model, tagName);
			if (imgDesc != null)
				image = imgDesc.createImage();
		}
		
		return image;
	}
	
	/**
	 * @param project
	 * @param nsUri
	 * @param tagName
	 * @return large image using metadata.  May be null.
	 */
	public Image getLargeIconImage(IProject project, String nsUri, String tagName) {
		Image image = null;
		Model model = getModel(project, nsUri);
		if (model != null){
			ImageDescriptor imgDesc = getLargeIconImageDescriptor(model, nsUri);
			if (imgDesc != null)
				image = imgDesc.createImage();
		}
		
		return image;
	}
	
	private Model getModel(IProject project, String nsUri) {
		ITaglibDomainMetaDataModelContext modelContext = TaglibDomainMetaDataQueryHelper.createMetaDataModelContext(project, nsUri);
		Model model =TaglibDomainMetaDataQueryHelper.getModel(modelContext);
		// no caching at this time so there is no need to listen to model notifications
//		if (model != null && !hasAdapter(model))
//			addAdapter(model);
		return model;
	}


//	private void addAdapter(Model model) {
//		if (model != null){			
////			model.eAdapters().add(INSTANCE);  
//		}		
//	}
//
//	private boolean hasAdapter(Model model) {
//		for(Adapter a : model.eAdapters()){
//			if (a == INSTANCE)
//				return true;
//		}
//		return false;
//	}

	private ImageDescriptor getSmallIconImageDescriptor(Model model, String tagName) {
		return getIconImageDescriptor(model, tagName, true);
	}
	
	private ImageDescriptor getLargeIconImageDescriptor(Model model, String tagName) {
		return getIconImageDescriptor(model, tagName, false);
	}
	
	private ImageDescriptor getIconImageDescriptor(Model model, String tagName, boolean small) {		
		ImageDescriptor icon = null;
		
		//use palette infos if available
		Trait trait = TaglibDomainMetaDataQueryHelper.getTrait(model, "paletteInfos");
		if (trait != null){
			PaletteInfos tags = (PaletteInfos)trait.getValue();
			for (Iterator it=tags.getInfos().iterator();it.hasNext();){
				PaletteInfo tag = (PaletteInfo)it.next();
				if (tag.getId().equalsIgnoreCase(tagName)){					
					IMetaDataSourceModelProvider sourceProvider = ((Trait)tag.eContainer().eContainer()).getSourceModelProvider();
					if (small)
						icon = getImageDescriptorFromString(sourceProvider, tag.getSmallIcon(), DEFAULT_SMALL_ICON);
					else
						icon = getImageDescriptorFromString(sourceProvider, tag.getLargeIcon(), DEFAULT_LARGE_ICON);
					
					break;
				}
			}	
		}
		else {
			for (Iterator it=model.getChildEntities().iterator();it.hasNext();){
				Entity tagAsEntity = (Entity)it.next();
				if (tagAsEntity.getId().equalsIgnoreCase(tagName)){										
					if (small)
						icon = getImageDescriptorFromTagTraitValueAsString(tagAsEntity, TRAIT_ICON_SMALL, DEFAULT_SMALL_ICON);
					else
						icon = getImageDescriptorFromTagTraitValueAsString(tagAsEntity, TRAIT_ICON_LARGE, DEFAULT_LARGE_ICON);	
					
					break;
				}				
			}
			
		}

		return icon;
	}
		
	private ImageDescriptor getImageDescriptorFromString(IMetaDataSourceModelProvider sourceModelProvider,  String imgDesc, ImageDescriptor defaultValue){
		ImageDescriptor image = defaultValue;
		IImageDescriptorProvider imageProvider = (IImageDescriptorProvider)sourceModelProvider.getAdapter(IImageDescriptorProvider.class);			
		if (imageProvider != null){
			image = imageProvider.getImageDescriptor(imgDesc);
		}
		return image;
	}
	
	private ImageDescriptor getImageDescriptorFromTagTraitValueAsString(Entity entity, String key, ImageDescriptor defaultValue){
		Trait t = TaglibDomainMetaDataQueryHelper.getTrait(entity, key);
		if (t != null){
			String imgDesc = TraitValueHelper.getValueAsString(t);
			return getImageDescriptorFromString(t.getSourceModelProvider(), imgDesc, defaultValue);
		}
		return defaultValue;
	}

}