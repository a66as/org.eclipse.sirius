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
package org.eclipse.sirius.diagram.business.internal.metamodel.description.tool.spec;

import org.eclipse.emf.common.util.EList;
import org.eclipse.sirius.business.internal.metamodel.helper.DerivedInverseReferenceHelper;
import org.eclipse.sirius.diagram.description.DescriptionPackage;
import org.eclipse.sirius.diagram.description.DiagramElementMapping;
import org.eclipse.sirius.diagram.description.tool.impl.DirectEditLabelImpl;

/**
 * Implementation of DirectEditLabel.
 * 
 * @author nlepine
 * 
 */
public class DirectEditLabelSpec extends DirectEditLabelImpl {
    @Override
    public EList<DiagramElementMapping> getMapping() {
        return DerivedInverseReferenceHelper.getInverseReferences(this, DiagramElementMapping.class, DescriptionPackage.eINSTANCE.getDiagramElementMapping_LabelDirectEdit());
    }
}
