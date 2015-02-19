/*******************************************************************************
 * Copyright (c) 2011 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.business.internal.metamodel.description.tool.spec;

import org.eclipse.emf.common.util.EList;
import org.eclipse.sirius.business.internal.metamodel.helper.DerivedInverseReferenceHelper;
import org.eclipse.sirius.viewpoint.description.DescriptionPackage;
import org.eclipse.sirius.viewpoint.description.PasteTargetDescription;
import org.eclipse.sirius.viewpoint.description.tool.impl.PasteDescriptionImpl;

/**
 * Implementation of PasteDescription.
 * 
 * @author mporhel
 */
public class PasteDescriptionSpec extends PasteDescriptionImpl {
    @Override
    public EList<PasteTargetDescription> getContainers() {
        return DerivedInverseReferenceHelper.getInverseReferences(this, PasteTargetDescription.class, DescriptionPackage.eINSTANCE.getPasteTargetDescription_PasteDescriptions());
    }
}
