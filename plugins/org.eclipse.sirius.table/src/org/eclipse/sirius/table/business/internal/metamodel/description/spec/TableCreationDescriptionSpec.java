/*******************************************************************************
 * Copyright (c) 2008, 2008 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.table.business.internal.metamodel.description.spec;

import org.eclipse.emf.common.util.EList;
import org.eclipse.sirius.business.internal.metamodel.helper.DerivedInverseReferenceHelper;
import org.eclipse.sirius.table.metamodel.table.description.impl.TableCreationDescriptionImpl;
import org.eclipse.sirius.viewpoint.description.DescriptionPackage;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.eclipse.sirius.viewpoint.description.RepresentationElementMapping;

/**
 * Specific implementation for model instances.
 * 
 * @author <a href="mailto:laurent.redor@obeo.fr">Laurent Redor</a>
 */
public class TableCreationDescriptionSpec extends TableCreationDescriptionImpl {
    @Override
    public RepresentationDescription basicGetRepresentationDescription() {
        return getTableDescription();
    }

    @Override
    public RepresentationDescription getRepresentationDescription() {
        return getTableDescription();
    }

    @Override
    public EList<RepresentationElementMapping> getMappings() {
        return DerivedInverseReferenceHelper.getInverseReferences(this,  RepresentationElementMapping.class, DescriptionPackage.eINSTANCE.getRepresentationElementMapping_DetailDescriptions());
    }
}
