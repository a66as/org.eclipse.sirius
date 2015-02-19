/*******************************************************************************
 * Copyright (c) 2010 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.tree.business.internal.metamodel.spec;

import org.eclipse.emf.common.util.EList;
import org.eclipse.sirius.business.internal.metamodel.helper.DerivedInverseReferenceHelper;
import org.eclipse.sirius.tree.description.impl.TreeNavigationDescriptionImpl;
import org.eclipse.sirius.viewpoint.description.DescriptionPackage;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.eclipse.sirius.viewpoint.description.RepresentationElementMapping;

/**
 * Implementation od TreeNavigationDescription.
 * 
 * @author nlepine
 */
public class TreeNavigationDescriptionSpec extends TreeNavigationDescriptionImpl {
    @Override
    public EList<RepresentationElementMapping> getMappings() {
        return DerivedInverseReferenceHelper.getInverseReferences(this, RepresentationElementMapping.class, DescriptionPackage.eINSTANCE.getRepresentationElementMapping_NavigationDescriptions());
    }

    @Override
    public RepresentationDescription basicGetRepresentationDescription() {
        return getTreeDescription();
    }

    @Override
    public RepresentationDescription getRepresentationDescription() {
        return getTreeDescription();
    }
}
