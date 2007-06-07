/*******************************************************************************
 * Copyright (c) 2001, 2006 Oracle Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Oracle Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsf.validation.internal.appconfig;

/**
 * Used to expose internal message data to unit tests.  Should not be used by clients
 * @author cbateman
 *
 */
public interface ILocalizedMessage {
    /**
     * @return the error code
     */
    public int getErrorCode();
}