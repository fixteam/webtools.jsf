/*******************************************************************************
 * Copyright (c) 2001, 2007 Oracle Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Oracle Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.pagedesigner.itemcreation;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jst.jsf.core.internal.tld.ITLDConstants;
import org.eclipse.jst.pagedesigner.dom.IDOMPosition;
import org.eclipse.jst.pagedesigner.editors.palette.ITagDropSourceData;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.founder.fix.base.wpe.FormPageUtil;
import com.founder.fix.base.wpe.TempStatic;
import com.founder.fix.fixwpe.wpeformdesigner.XmlPropBufferProvider;
import com.founder.fix.fixwpe.wpeformdesigner.dialog.DetailTable;
import com.founder.fix.fixwpe.wpeformdesigner.jst.pagedesigner.itemcreation.AbstractTagCreatorProvider;
import com.founder.fix.fixwpe.wpeformdesigner.jst.pagedesigner.properties.ConstantProperty;
import com.founder.fix.ocx.platformdesigner.interfaces.FixLoger;


/**
 * The abstract class from which all client ITagCreator instances
 * should be derived.
 * 
 * The createTag method enforces a set of steps required by the framework
 * to create a new tag.  However, it allows you to configure some of the steps
 * by providing an ITagCreationAdvisor through the doSelectCreationAdvisor.
 *
 * <p><b>Provisional API - subject to change</b></p>
 *
 * @author cbateman
 *
 */
public abstract class AbstractTagCreator implements ITagCreator 
{
    /* (non-Javadoc)
     * @see org.eclipse.jst.pagedesigner.itemcreation.ITagCreator#createTag(org.eclipse.jst.pagedesigner.editors.palette.TagToolPaletteEntry, org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel, org.eclipse.jst.pagedesigner.dom.IDOMPosition)
     */
    public final Element createTag(final CreationData creationData) 
    {

    	FixLoger.info("wpeFormDesignMessage----"); //$NON-NLS-1$
        final ITagCreationAdvisor  advisor = selectCreationAdvisor(creationData);
        FixLoger.info("wpeFormDesignMessage----56"); //$NON-NLS-1$
        // adjust the creation position to accommodate required containers
        final IDOMPosition position = 
            advisor.checkAndApplyNecessaryContainers(creationData.getModel(), creationData.getDomPosition());
        
        if (position == null) return null;//throw exception?
        FixLoger.info("wpeFormDesignMessage----62"); //$NON-NLS-1$
        creationData.setAdjustedPosition(position);
        
        // create the element
        final Element ele =  createElement(creationData);
        if (ele == null) return null;//throw exception?
        FixLoger.info("wpeFormDesignMessage----68"); //$NON-NLS-1$
        // apply tag customization
        advisor.applyCustomization(creationData.getModel(), ele);

        // ensure that any attributes required by the tag's definition
        // is initialized.
        // TODO: a drawback of this approach is that it leaves the tag in
        // a state where there are no error flags to tell the user something is
        // missing, but may initialize the tag with an (empty) invalid value
        //ensureRequiredAttrs(ele, creationData);
        
        
        
        FixLoger.info("wpeFormDesignMessage----...."); //$NON-NLS-1$
        /*
    	 *	@author Fifteenth
    	 *		provider：
    	 *		
    	 */
        IDOMDocument domDocument = creationData.getModel().getDocument();
    	ITagDropSourceData  provider = creationData.getTagCreationProvider();
    	FixLoger.info("wpeFormDesignMessage----89"); //$NON-NLS-1$
    	String componentType = provider.getId();
    	
    	FixLoger.info("wpeFormDesignMessage----92"); //$NON-NLS-1$
    	XmlPropBufferProvider.initProperty(FormPageUtil.currentFormPagePath);
    	
    	
    	FixLoger.info("wpeFormDesignMessage----htmlNode"); //$NON-NLS-1$
    	// htmlNode
    	Node htmlNode = AbstractTagCreatorProvider.
    			getPointParentNode((IDOMNode)position.getContainerNode(),
    					AbstractTagCreatorProvider.nodeName_HTML);
    	FixLoger.info("wpeFormDesignMessage----101"); //$NON-NLS-1$
    	String detailBizObjName = null;
    	String detailTableId = null;
    	if(htmlNode!=null){
    		FixLoger.info("wpeFormDesignMessage----"); //$NON-NLS-1$
    		/*
    		 *	@author Fifteenth
    		 *		2013.05.07
    		 */
        	if(provider.getNamespace().equals("founderfix1")){ //$NON-NLS-1$
        		FixLoger.info("wpeFormDesignMessage----"); //$NON-NLS-1$
//        		String nodeId = AbstractTagCreatorProvider.
//            			getAutoAttrValue(htmlNode, componentType);
        		String nodeAutoId = "node123456"; //$NON-NLS-1$
            	ele.setAttribute(AbstractTagCreatorProvider.tagAttr_ID, 
            			nodeAutoId);
            	FixLoger.info("wpeFormDesignMessage----117"); //$NON-NLS-1$
        		//static
        		DetailTable dialog = new DetailTable(null);
        		if (dialog.open() == Dialog.OK) {
        			int colCount = dialog.getColCount();
        			int rowCount = dialog.getRowCount();
        			detailBizObjName = dialog.getBizObjName();
        			
        			ele.setAttribute(AbstractTagCreatorProvider.tagAttr_CLASS,
        					AbstractTagCreatorProvider.tagAttrValue_CLASS_DETAIL);
        			if(dialog.isDeatailTable==true){
        				ele.setAttribute(AbstractTagCreatorProvider.tagAttrValue_ISDETAIL
        						,AbstractTagCreatorProvider.tagAttrValue_TRUE); 
        				ele.setAttribute(AbstractTagCreatorProvider.tagAttr_BIZOBJ,
        						detailBizObjName);
        			}else{
        				ele.setAttribute(AbstractTagCreatorProvider.tagAttrValue_ISDETAIL
        						,AbstractTagCreatorProvider.tagAttrValue_FALSE); 
        			}
        			FixLoger.info("wpeFormDesignMessage----136"); //$NON-NLS-1$
        			// 
        			AbstractTagCreatorProvider.createDetailTalbe(colCount,rowCount,nodeAutoId,detailBizObjName,
        					domDocument,ele,htmlNode,dialog.aISelectionState);
    			}else{
    				return null;
    			}
        		addTagToContainer(position, ele);
                return ele;
        	}
        	
        	/*
        	 *	@author Fifteenth
        	 *		
        	 *		:
        	 *		1.fix
        	 *		2.html的input.text|TEXTAREA
        	 */
        	if(TempStatic.getCategoriesList().contains(provider.getNamespace())
        			||componentType.equals(AbstractTagCreatorProvider.tagAttr_INPUT)
        			||componentType.equals(AbstractTagCreatorProvider.nodeName_TEXTAREA)
        			||componentType.equals(AbstractTagCreatorProvider.nodeName_LABEL)
        			||componentType.equals(AbstractTagCreatorProvider.nodeName_CAPTION)){ 
        		FixLoger.info("wpeFormDesignMessage----Start....:"+componentType); //$NON-NLS-1$
        		
        		// id(propIdValue)：
//        		String nodeId = AbstractTagCreatorProvider.
//            			getAutoAttrValue(htmlNode, componentType);
        		String nodeAutoId = "node5555"; //$NON-NLS-1$
            	ele.setAttribute(AbstractTagCreatorProvider.tagAttr_ID, 
            			nodeAutoId);
        		
        		// componentType
        		if(componentType.equals(AbstractTagCreatorProvider.tagAttr_INPUT)){
        			componentType = AbstractTagCreatorProvider.tagAttrValue_INPUT; 
        		}
        		
        	
        		//
        		if(componentType.equals(AbstractTagCreatorProvider.tagAttr_INPUT)){
        			// type
            		ele.setAttribute("type", "text"); //$NON-NLS-1$ //$NON-NLS-2$
        		}
//        		else if(componentType.equals(AbstractTagCreatorProvider.tagAttr_TEXTAREA)){
//        			
//        		}
        		else if(componentType.equals(AbstractTagCreatorProvider.nodeName_LABEL)){
        			IDOMNode node = (IDOMNode) domDocument.createTextNode("label"); //$NON-NLS-1$
        			ele.appendChild(node);
        		}else if(componentType.equals(AbstractTagCreatorProvider.nodeName_CAPTION)){
        			IDOMNode node = (IDOMNode) domDocument.createTextNode("field"); //$NON-NLS-1$
        			ele.appendChild(node);
        		}else{
        			// componentType
            		ele.setAttribute(AbstractTagCreatorProvider.tagAttr_ComponentType, componentType);
        		}
        		
        		FixLoger.info("wpeFormDesignMessage----"); //$NON-NLS-1$
        		
        		// 
            	Node tableNode = AbstractTagCreatorProvider.
            			getPointParentNode((IDOMNode)position.getContainerNode(),
            					AbstractTagCreatorProvider.nodeName_TABLE);
            	
            	Boolean isDetailTag = false;
            	String bizObjTypes = ConstantProperty.bizObjTypes[0]
            			+"-"+ConstantProperty.typeMainValue; //$NON-NLS-1$
            	
            	if(tableNode!=null){
            		// tableNode
            		NamedNodeMap nodeAttributes = tableNode.getAttributes();
            		Node attrNode = nodeAttributes.getNamedItem(AbstractTagCreatorProvider.tagAttrValue_ISDETAIL);
            		if(attrNode != null){
            			if(!tableNode.getAttributes().
                				getNamedItem(AbstractTagCreatorProvider.tagAttrValue_ISDETAIL).
                				getNodeValue().equals(AbstractTagCreatorProvider.tagAttrValue_TRUE)){
                    		while(!tableNode.getParentNode().getNodeName().equals(
                        			AbstractTagCreatorProvider.nodeName_BODY)){
                    			tableNode = tableNode.getParentNode();
                    			
                    			if(tableNode.getNodeName().equals(
                    					AbstractTagCreatorProvider.nodeName_TABLE)){
                    				// 
                            		if(tableNode.getAttributes().
                            				getNamedItem(AbstractTagCreatorProvider.tagAttrValue_ISDETAIL
                            						).getNodeValue().equals(
                            						AbstractTagCreatorProvider.tagAttrValue_TRUE)){
                            			
                            			isDetailTag = true;
                            			detailBizObjName = tableNode.getAttributes().
                                				getNamedItem(AbstractTagCreatorProvider.tagAttr_BIZOBJ
                                						).getNodeValue();
                            			detailTableId = tableNode.getAttributes().
                                				getNamedItem(AbstractTagCreatorProvider.tagAttr_ID
                                						).getNodeValue();
                            			
                            			bizObjTypes = ConstantProperty.bizObjTypes[1]+"-" //$NON-NLS-1$
                            					+tableNode.getAttributes().getNamedItem(
                            							AbstractTagCreatorProvider.tagAttr_ID).getNodeValue();
                            			break;
                            		}
                    			}
                        	}
                    	}else{
                    		isDetailTag = true;
                    		detailBizObjName = tableNode.getAttributes().
                    				getNamedItem(AbstractTagCreatorProvider.tagAttr_BIZOBJ
                    						).getNodeValue();
                    		detailTableId = tableNode.getAttributes().
                    				getNamedItem(AbstractTagCreatorProvider.tagAttr_ID
                    						).getNodeValue();
                    	}
            		}
            	}
            	FixLoger.info("wpeFormDesignMessage----"); //$NON-NLS-1$
            	
            	if(isDetailTag){
            		bizObjTypes = ConstantProperty.bizObjTypes[1]+"-"+detailTableId+"-"+detailBizObjName; //$NON-NLS-1$ //$NON-NLS-2$
            	}
        		
        		// 
        		IDOMNode coment = AbstractTagCreatorProvider.getComentNode(
        				componentType, domDocument,nodeAutoId,isDetailTag,bizObjTypes);
        		if(coment!=null
        				&&!coment.getTextContent().equals("")){ //$NON-NLS-1$
        			ele.appendChild(coment);
        		}
        		FixLoger.info("wpeFormDesignMessage----"); //$NON-NLS-1$
        	}
        	
        	// headNode
			Node headNode = AbstractTagCreatorProvider.getPointChildNode(htmlNode, 
					AbstractTagCreatorProvider.nodeName_HEAD);
			
			// 
			if(headNode!=null){
				AbstractTagCreatorProvider.setRef(headNode, domDocument, 
						componentType, AbstractTagCreatorProvider.jsRef);
				AbstractTagCreatorProvider.setRef(headNode, domDocument, 
						componentType, AbstractTagCreatorProvider.cssRef);
			}
			FixLoger.info("wpeFormDesignMessage----"); //$NON-NLS-1$
    	}

        addTagToContainer(position, ele);
        return ele;
    }

    private ITagCreationAdvisor selectCreationAdvisor(CreationData creationData)
    {
        ITagCreationAdvisor advisor  = doSelectCreationAdvisor(creationData);

        // enforce that the advisor must be an AbstractTagCreationAdvisor to 
        // avoid using the default (not this case also covers advisor == null)
        if (! (advisor instanceof AbstractTagCreationAdvisor))
        {
            advisor = new DefaultTagCreationAdvisor(creationData);
        }

        return advisor;
    }
    
    /**
     * @param creationData
     * @return a tag creation advisor or null to indicate the use of the system default
     */
    protected abstract ITagCreationAdvisor doSelectCreationAdvisor(CreationData creationData);

    /**
     * @param creationData 
     * @return {@link Element}
     */
    protected final Element createElement(final CreationData creationData)
    {
        Element ele =  creationData.getModel().getDocument().createElement(creationData.getTagName());
        if (ele == null) return null;

        //ugly... fix me
        
        // TODO: move this into an ensure method?
        // XXX: we are using "startsWith("directive.")" to test whether
        // should setJSPTag, this
        // maybe is not the best way. Need check whether SSE have special
        // API for it.
        if (ITLDConstants.URI_JSP.equals(creationData.getUri())
                && (ele.getLocalName().startsWith("directive.") //$NON-NLS-1$
                        || "declaration".equals(ele.getLocalName()) //$NON-NLS-1$
                        || "expression".equals(ele.getLocalName()) || "scriptlet" //$NON-NLS-1$ //$NON-NLS-2$
                        .equals(ele.getLocalName()))) {
            // it is a jsp tag
            ((IDOMElement) ele).setJSPTag(true);
        }

        if (creationData.getPrefix() != null)
        {
            ele.setPrefix(creationData.getPrefix());
        }

        return ele;
    }

    /**
     * @param position
     * @param tagElement
     */
    private void addTagToContainer(final IDOMPosition position, final Element tagElement) {
        if (position == null || position.getContainerNode() == null) {
            return;
        }

        if (position.getNextSiblingNode() == null) {
        	/*
        	 * founderfix
        	 * 
        	 * 
        	 * position.getContainerNode()：
        	 */
            position.getContainerNode().appendChild(tagElement);
        } else {
            position.getContainerNode().insertBefore(tagElement,
                    position.getNextSiblingNode());
        }
    }
}
