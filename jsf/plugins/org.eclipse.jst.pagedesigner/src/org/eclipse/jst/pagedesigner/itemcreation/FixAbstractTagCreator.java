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
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.founder.fix.base.wpe.CurrentRemember;
import com.founder.fix.base.wpe.TempStatic;
import com.founder.fix.fixwpe.wpeformdesigner.XmlPropBufferProvider;
import com.founder.fix.fixwpe.wpeformdesigner.dialog.DetailTable;
import com.founder.fix.fixwpe.wpeformdesigner.jst.pagedesigner.itemcreation.AbstractTagCreatorProvider;
import com.founder.fix.fixwpe.wpeformdesigner.jst.pagedesigner.properties.ConstantProperty;


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
public abstract class FixAbstractTagCreator implements ITagCreator 
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
        
//        IDOMDocument domDocument = creationData.getModel().getDocument();
       
        /*
         * 得到当前主题
         * 	用于得到组件引用指定的JS和CSS
         */
		if (CurrentRemember.webThemeName == null) {
			AbstractTagCreatorProvider.getTheme();
		}

		/*
		 * 得到componentType
		 */
		ITagDropSourceData provider = creationData.getTagCreationProvider();

		/*
		 * componentType为组件类型 componentType可能为：HTMLSelect、Ztree、My97DatePicker等
		 */
		String componentType = provider.getId();

		/*
		 * 初始化所有组件
		 */
		XmlPropBufferProvider.initProperty(CurrentRemember.currentFormPagePath);

		/*
		 * 得到htmlNode节点
		 */
		Node htmlNode = AbstractTagCreatorProvider.getPointParentNode((IDOMNode) position.getContainerNode(),
				AbstractTagCreatorProvider.nodeName_HTML);
		String detailBizObjName = null;
		String detailTableId = null;
		if (htmlNode != null) {

			if (provider.getNamespace().equals(TempStatic.staticCategory)	//basicTag
					&& !componentType
							.equals(AbstractTagCreatorProvider.tagAttr_INPUT)	//INPUT.TEXT
					&& !componentType
							.equals(AbstractTagCreatorProvider.nodeName_TEXTAREA)	//TEXTAREA
					&& !componentType
							.equals(AbstractTagCreatorProvider.nodeName_KBD)	//KBD
					&& !componentType
							.equals(AbstractTagCreatorProvider.nodeName_LABEL)) {	//LABEL
				// String nodeId = AbstractTagCreatorProvider.
				// getAutoAttrValue(htmlNode, componentType);
				// static
				/*
				 * 能进该判断的目前（2013.07.24）基础组件中的表格组件
				 */
				DetailTable dialog = new DetailTable(null);
				if (dialog.open() == Dialog.OK) {
					int colCount = dialog.getColCount();
					int rowCount = dialog.getRowCount();
					detailBizObjName = dialog.getBizObjName();

					String nodeId = "table_" + detailBizObjName; //$NON-NLS-1$

					if (dialog.isDeatailTable == true
							&& !detailBizObjName.equals("")) { //$NON-NLS-1$
						// 选中了明细表同时选择了明细表
						ele.setAttribute(
								AbstractTagCreatorProvider.tagAttr_CLASS,
								AbstractTagCreatorProvider.tagAttrValue_CLASS_DETAIL);
						ele.setAttribute(AbstractTagCreatorProvider.tagAttr_ID,
								nodeId);
						ele.setAttribute(
								AbstractTagCreatorProvider.tagAttrValue_ISDETAIL,
								AbstractTagCreatorProvider.tagAttrValue_TRUE);
						ele.setAttribute(
								AbstractTagCreatorProvider.tagAttr_BIZOBJ,
								detailBizObjName);
						AbstractTagCreatorProvider.createDetailTalbe(colCount,
								rowCount, nodeId, detailBizObjName,
								// domDocument,
								ele, htmlNode, dialog.aISelectionState);
					} else {
						AbstractTagCreatorProvider.createTalbe(colCount,
								rowCount, ele);
					}
				} else {
					return null;
				}
				addTagToContainer(position, ele);
				return ele;
			}

			/*
			 * 基础组件中的：text、textarea、lable、kbd（用于绑定文字的组件）
			 */
			if (TempStatic.categoriesList.contains(provider.getNamespace())	//[DataEntity组件, Feedbacker组件, Presenter组件, 平台组件, 弹出下拉]
					|| componentType
							.equals(AbstractTagCreatorProvider.tagAttr_INPUT)
					|| componentType
							.equals(AbstractTagCreatorProvider.nodeName_TEXTAREA)
					|| componentType
							.equals(AbstractTagCreatorProvider.nodeName_KBD)
					|| componentType
							.equals(AbstractTagCreatorProvider.nodeName_LABEL)) {

				// 生成组件编号
				String nodeId = AbstractTagCreatorProvider.getAutoAttrValue(
						htmlNode, componentType);
				// 编号作为组件的属性
				ele.setAttribute(AbstractTagCreatorProvider.tagAttr_ID, nodeId);

				// set组件的一些其他属性，不同的组件属性可能不同
				if (componentType
						.equals(AbstractTagCreatorProvider.tagAttr_INPUT)) {
					componentType = AbstractTagCreatorProvider.tagAttrValue_INPUT;
					ele.setAttribute("type", "text"); //$NON-NLS-1$ //$NON-NLS-2$
				} 
				if (componentType
						.equals(AbstractTagCreatorProvider.nodeName_LABEL)) {
					ele.setAttribute(
							AbstractTagCreatorProvider.tagAttr_ComponentType,
							componentType);
					IDOMNode node = (IDOMNode) ele.getOwnerDocument()
							.createTextNode("label"); //$NON-NLS-1$
					ele.appendChild(node);
				} else if (componentType
						.equals(AbstractTagCreatorProvider.nodeName_KBD)) {
					ele.setAttribute(
							AbstractTagCreatorProvider.tagAttr_ComponentType,
							componentType);
					IDOMNode node = (IDOMNode) ele.getOwnerDocument()
							.createTextNode("\u5B57\u6BB5\u540D"); //$NON-NLS-1$
					ele.appendChild(node);
				} else {
					ele.setAttribute(
							AbstractTagCreatorProvider.tagAttr_ComponentType,
							componentType);
				}

				// 向外层遍历，遍历到第一个tr节点，节点可以为null
				Node templateTrNode = AbstractTagCreatorProvider
						.getPointParentNode(
								(IDOMNode) position.getContainerNode(),
								AbstractTagCreatorProvider.nodeName_TR);

				// 是否是明细表组件标示
				Boolean isDetailTag = false;
				// 组件的一些关于是否是明细表的信息，会写到注释中去
				String bizObjTypes = ConstantProperty.bizObjTypes[0]
						+ "-" + ConstantProperty.typeMainValue; //$NON-NLS-1$

				/*
				 * 该if用来判断组件是否在明细表中
				 * 不断往外层遍历，遍历到明细表的tr节点（是明细表组件）或者遍历到body节点（非明细表节点）
				 * 
				 * 明细表的tr节点中，会有明细表的信息： 例如：bizobj="AU_ORGINFO"
				 * detailTableId="table_AU_USERINFO"
				 */
				if (templateTrNode != null) {

					NamedNodeMap nodeAttributes = templateTrNode
							.getAttributes();
					Node attrNode = nodeAttributes.getNamedItem("repeat"); //$NON-NLS-1$
					//
					// if(attrNode != null){
					if (attrNode == null
							|| !attrNode.getNodeValue().equals("template")) { //$NON-NLS-1$
						/*
						 * 不断往外层遍历，结束两种情况： 1.遍历到明细表 2.遍历到body节点
						 */
						while (templateTrNode.getParentNode() != null 
								&& !templateTrNode.getParentNode().getNodeName()
								.equals(AbstractTagCreatorProvider.nodeName_BODY)) {
							templateTrNode = templateTrNode.getParentNode();

							if (templateTrNode.getNodeName().equals(
									AbstractTagCreatorProvider.nodeName_TR)) {
								if (templateTrNode.getAttributes().getNamedItem("repeat") != null 	//$NON-NLS-1$
										&& templateTrNode.getAttributes().getNamedItem("repeat"). //$NON-NLS-1$
										getNodeValue().equals("template")) {//$NON-NLS-1$

									isDetailTag = true;
									detailBizObjName = templateTrNode
											.getAttributes()
											.getNamedItem(
													AbstractTagCreatorProvider.tagAttr_BIZOBJ)
											.getNodeValue();
									detailTableId = templateTrNode
											.getAttributes()
											.getNamedItem("detailTableId").getNodeValue(); //$NON-NLS-1$

									bizObjTypes = ConstantProperty.bizObjTypes[1]
											+ "-" //$NON-NLS-1$
											+ detailTableId
											+ "-" + detailBizObjName; //$NON-NLS-1$ 
									break;
								}
							}
						}
					} else {
						isDetailTag = true;
						detailBizObjName = templateTrNode
								.getAttributes()
								.getNamedItem(
										AbstractTagCreatorProvider.tagAttr_BIZOBJ)
								.getNodeValue();
						detailTableId = templateTrNode.getAttributes()
								.getNamedItem("detailTableId").getNodeValue(); //$NON-NLS-1$
					}
					// }
				}
				if (isDetailTag) {
					bizObjTypes = ConstantProperty.bizObjTypes[1]
							+ "-" + detailTableId + "-" + detailBizObjName; //$NON-NLS-1$ //$NON-NLS-2$
				}

				/*
				 * 关于bizObjTypes 明细表的形式类似于：
				 * detail-table_AU_USERINFO-AU_USERINFO--明细表表示-明细表ID-明细表业务对象名称
				 * 主表的形式类似于： main-fixbody 是注释的一部分
				 */

				/*
				 * 组件的属性以注释形式存在，注释作为dom树的一个节点，
				 * 且作为组件的一个子节点（删除组件的时候自动删掉子节点，不需考虑如何删除，但要考虑删除组件的引用）
				 */
				IDOMNode coment = AbstractTagCreatorProvider.getComentNode(
						componentType, ele.getOwnerDocument(), nodeId,
						isDetailTag, bizObjTypes);
				if (coment != null && !coment.getTextContent().equals("")) { //$NON-NLS-1$
					ele.appendChild(coment);
				}
			}

			/*
			 * 找到head节点 所有的引用都是作为head节点的子节点
			 */
			Node headNode = AbstractTagCreatorProvider.getPointChildNode(
					htmlNode, AbstractTagCreatorProvider.nodeName_HEAD);

			/*
			 * 添加组件引用 引用作为head节点的子节点
			 */
			if (headNode != null) {
				AbstractTagCreatorProvider.addRef(headNode, componentType,
						AbstractTagCreatorProvider.jsRef);
				AbstractTagCreatorProvider.addRef(headNode, componentType,
						AbstractTagCreatorProvider.cssRef);
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
            position.getContainerNode().appendChild(tagElement);
        } else {
            position.getContainerNode().insertBefore(tagElement,
                    position.getNextSiblingNode());
        }
    }
}
