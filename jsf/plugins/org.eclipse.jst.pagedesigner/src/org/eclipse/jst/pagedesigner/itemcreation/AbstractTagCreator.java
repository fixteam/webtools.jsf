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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jst.jsf.core.internal.tld.ITLDConstants;
import org.eclipse.jst.pagedesigner.dom.IDOMPosition;
import org.eclipse.jst.pagedesigner.editors.palette.ITagDropSourceData;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.founder.fix.studio.wpeformdesigner.FormPageUtil;
import com.founder.fix.studio.wpeformdesigner.TempStatic;
import com.founder.fix.studio.wpeformdesigner.jst.pagedesigner.itemcreation.AbstractTagCreatorProvider;
import com.founder.fix.studio.wpeformdesigner.jst.pagedesigner.itemcreation.XmlPropBufferProvider;
import com.founder.fix.studio.wpeformdesigner.dialog.DetailTable;


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

        final ITagCreationAdvisor  advisor = selectCreationAdvisor(creationData);
        
        // adjust the creation position to accommodate required containers
        final IDOMPosition position = 
            advisor.checkAndApplyNecessaryContainers(creationData.getModel(), creationData.getDomPosition());
        
        if (position == null) return null;//throw exception?

        creationData.setAdjustedPosition(position);
        
        // create the element
        final Element ele =  createElement(creationData);
        if (ele == null) return null;//throw exception?

        // apply tag customization
        advisor.applyCustomization(creationData.getModel(), ele);

        // ensure that any attributes required by the tag's definition
        // is initialized.
        // TODO: a drawback of this approach is that it leaves the tag in
        // a state where there are no error flags to tell the user something is
        // missing, but may initialize the tag with an (empty) invalid value
        //ensureRequiredAttrs(ele, creationData);
        
        
        
        /*
    	 *	@author Fifteenth
    	 *		provider：来判断是否是自定义组件
    	 *		
    	 */
        IDOMDocument domDocument = creationData.getModel().getDocument();
    	ITagDropSourceData  provider = creationData.getTagCreationProvider();
    	String componentType = provider.getId();
    	
    	
    	Node htmlNode = AbstractTagCreatorProvider.
    			getPointParentNode((IDOMNode)position.getContainerNode(),
    					AbstractTagCreatorProvider.nodeName_HTML);
    	
    	
    	if(htmlNode!=null){
    		
    		/*
    		 *	@author Fifteenth
    		 *		处理fix基础组件
    		 *			目前只有明细表2013.05.07
    		 */
        	if(provider.getNamespace().equals("founderfix1")){ //$NON-NLS-1$
        		//static
        		DetailTable dialog = new DetailTable(null);
        		if (dialog.open() == Dialog.OK) {
        			int colCount = dialog.getColCount();
        			int rowCount = dialog.getRowCount();
        			String bizObjName = dialog.getBizObjName();
        			
        			// 把对象ele传过去修改
        			AbstractTagCreatorProvider.createDetailTalbe(colCount,rowCount,bizObjName,
        					domDocument,ele,htmlNode);
    			}else{
    				return null;
    			}
        		addTagToContainer(position, ele);
                return ele;
        	}
        	
    		
        	/*
        	 *	@author Fifteenth
        	 *		设属性写注释
        	 *		对象包括:
        	 *		1.fix(不包括基础组件)
        	 *		2.html的input.text
        	 */
        	if(TempStatic.getCategoriesList().contains(provider.getNamespace())
        			||componentType.equals(AbstractTagCreatorProvider.tagAttr_INPUT)){ 
        		// globleXmlMap
        		XmlPropBufferProvider.initProperty(FormPageUtil.currentFormPagePath);
        		
        		
        		// 设id属性(propIdValue)：自动生成组件编号
        		String nodeId = AbstractTagCreatorProvider.
            			getAutoAttrValue(htmlNode, componentType);
            	ele.setAttribute(AbstractTagCreatorProvider.tagAttr_ID, 
            			nodeId);
        		
        		// 得到componentType
        		if(componentType.equals(AbstractTagCreatorProvider.tagAttr_INPUT)){
        			componentType = AbstractTagCreatorProvider.tagAttrVlue_INPUT; 
        		}
        		
        		// 设componentType属性
        		ele.setAttribute(AbstractTagCreatorProvider.tagAttr_ComponentType, componentType);
        		
        		
        		// 写注释
        		IDOMNode coment = AbstractTagCreatorProvider.getComentNode(componentType, domDocument,nodeId);
        		if(coment!=null){
        			ele.appendChild(coment);
        		}
        	}
        	
        	
			Node headNode = AbstractTagCreatorProvider.getPointChildNode(htmlNode, AbstractTagCreatorProvider.nodeName_HEAD);
			if(headNode!=null){
				// existJSRefList:js引用节点列表
    			ArrayList <Node>existJSRefList = new ArrayList<Node>();
    			// existJSRefTextList:js引用节点内容列表
    			ArrayList <String> existJSRefTextList =  new ArrayList<String>();
    			
    			NodeList childNodeList = headNode.getChildNodes();
    			if(childNodeList!=null){
    				for(int childNum=0;childNum<childNodeList.getLength();childNum++){
    					if(childNodeList.item(childNum).getNodeName().
    							equals(AbstractTagCreatorProvider.nodeName_SCRIPT)){
    						existJSRefList.add(childNodeList.item(childNum));
    						existJSRefTextList.add(
    								childNodeList.item(childNum).
    								getAttributes().getNamedItem(
    										AbstractTagCreatorProvider.tagAttr_TYPE).
    										getNodeValue());
    					}
    				}
    			}
    			
    			// 组件属性列表，用于得到js引用
    			String key = AbstractTagCreatorProvider.getGlobleMapKey(componentType);
    			List <HashMap<String,Object>>propertylist = XmlPropBufferProvider.
    					globleXmlMap.get(key);
    			
    			if(propertylist!=null){
    				/*
    				 *	@author Fifteenth
    				 *		得到组件的引用
    				 *
    				 *		propertylist.get(index):index=0
    				 */
    				Object object = propertylist.get(0).get("jsref"); //$NON-NLS-1$
    				if(object instanceof ArrayList){
    					
    					// jsRefList组件的引用列表
    					ArrayList jsRefList = (ArrayList)object;
    					int jsLength = jsRefList.size();
    					for(int jsRefNum=0;jsRefNum<jsRefList.size();jsRefNum++){
    						/*
    						 *	@author Fifteenth
    						 *		1.组件已用过，refnum++
    						 *		2.组件未引用过，生成
    						 */
    						if(existJSRefTextList.contains(jsRefList.get(jsRefNum))){
    							for(int compareNum=0;compareNum<existJSRefTextList.size();compareNum++){
    								if(jsRefList.get(jsRefNum).equals(existJSRefTextList.get(compareNum))){
    									Node modifyRefNode = existJSRefList.get(compareNum);
    									int refNum = Integer.valueOf(modifyRefNode.getAttributes().getNamedItem("refNum").getNodeValue()); //$NON-NLS-1$
    									refNum++;
    									modifyRefNode.getAttributes().
    									getNamedItem(AbstractTagCreatorProvider.tagAttr_REFNUM).
    									setNodeValue(refNum+""); //$NON-NLS-1$ 
    								}
    							}
    						}else{
    							Element javascript = creationData.getModel().getDocument().
        								createElement(
        										AbstractTagCreatorProvider.nodeName_SCRIPT);
    							
        	        			javascript.setAttribute(AbstractTagCreatorProvider.tagAttr_TYPE, 
        	        					jsRefList.get(jsRefNum).toString());
        	        			javascript.setAttribute(
        	        					AbstractTagCreatorProvider.tagAttr_REFNUM, "1"); //$NON-NLS-1$ 
        	        			headNode.appendChild(javascript);
        	        			// 加入引用后需要自己设置换行
        	        			if(jsRefNum==1||jsRefNum < jsLength-1){
        	        				headNode.appendChild(creationData.getModel().
        	        						getDocument().createTextNode("\r")); //$NON-NLS-1$
        	        			}
    						}
    					}
    				}
    			}
			}
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
        	 * 代码注释
        	 * 
        	 * position.getContainerNode()：父节点
        	 */
            position.getContainerNode().appendChild(tagElement);
        } else {
            position.getContainerNode().insertBefore(tagElement,
                    position.getNextSiblingNode());
        }
    }
}
