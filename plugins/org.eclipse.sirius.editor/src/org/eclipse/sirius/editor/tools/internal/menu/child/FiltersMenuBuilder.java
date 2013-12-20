/*******************************************************************************
 * Copyright (c) 2009 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.editor.tools.internal.menu.child;

import org.eclipse.sirius.diagram.description.filter.FilterPackage;
import org.eclipse.sirius.editor.tools.api.menu.AbstractTypeRestrictingMenuBuilder;
import org.eclipse.sirius.viewpoint.description.concern.ConcernPackage;

/**
 * The filters menu.
 * 
 * @author cbrun
 * 
 */
public class FiltersMenuBuilder extends AbstractTypeRestrictingMenuBuilder {
    /**
     * build the menu.
     */
    public FiltersMenuBuilder() {
        super();
        addValidType(FilterPackage.eINSTANCE.getFilterDescription());
        addValidType(ConcernPackage.eINSTANCE.getConcernSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLabel() {
        return "New Filter...";
    }

}
