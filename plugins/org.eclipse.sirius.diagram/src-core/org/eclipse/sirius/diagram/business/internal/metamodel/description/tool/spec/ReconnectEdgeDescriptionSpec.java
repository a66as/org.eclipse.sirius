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
import org.eclipse.sirius.diagram.description.EdgeMapping;
import org.eclipse.sirius.diagram.description.tool.impl.ReconnectEdgeDescriptionImpl;

/**
 * Implementation of ReconnectEdgeDescription.
 * 
 * @author nlepine
 */
public class ReconnectEdgeDescriptionSpec extends ReconnectEdgeDescriptionImpl {
    @Override
    public EList<EdgeMapping> getMappings() {
        return DerivedInverseReferenceHelper.getInverseReferences(this, EdgeMapping.class, DescriptionPackage.eINSTANCE.getEdgeMapping_Reconnections());
    }
}
