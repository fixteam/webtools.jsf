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

package org.eclipse.jst.jsf.common.metadata.query;

import org.eclipse.jst.jsf.common.metadata.Entity;

/**
 * Abstract implementation that concrete subclasses should ovveride
 * <p><b>Provisional API - subject to change</b></p>
 */
public abstract class AbstractEntityVisitor extends AbstractMetaDataVisitor
		implements IEntityVisitor {

	public abstract void visit(Entity entity);

	public void visitCompleted(Entity entity){
		//subclasses should override if needed
	}

}
