/*******************************************************************************
 * Copyright (c) 2007, 2008 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.diagram.business.internal.metamodel.description.tool.spec;

import org.eclipse.emf.common.util.EList;
import org.eclipse.sirius.business.internal.metamodel.helper.DerivedInverseReferenceHelper;
import org.eclipse.sirius.diagram.description.tool.impl.DiagramCreationDescriptionImpl;
import org.eclipse.sirius.viewpoint.description.DescriptionPackage;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.eclipse.sirius.viewpoint.description.RepresentationElementMapping;

/**
 * Implementation of
 * {@link org.eclipse.sirius.viewpoint.description.DiagramDescription}.
 * 
 * @author cbrun
 */
public class DiagramCreationDescriptionSpec extends DiagramCreationDescriptionImpl {
    @Override
    public RepresentationDescription basicGetRepresentationDescription() {
        return getDiagramDescription();
    }

    @Override
    public RepresentationDescription getRepresentationDescription() {
        return getDiagramDescription();
    }

    @Override
    public EList<RepresentationElementMapping> getMappings() {
        return DerivedInverseReferenceHelper.getInverseReferences(this, RepresentationElementMapping.class, DescriptionPackage.eINSTANCE.getRepresentationElementMapping_DetailDescriptions());
    }
}
