/*******************************************************************************
 * Copyright (c) 2016 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.business.internal.migration;

import org.eclipse.sirius.business.api.migration.AbstractRepresentationsFileMigrationParticipant;
import org.eclipse.sirius.business.internal.query.DRepresentationDescriptorInternalHelper;
import org.eclipse.sirius.viewpoint.DAnalysis;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.DRepresentationDescriptor;
import org.eclipse.sirius.viewpoint.DView;
import org.osgi.framework.Version;

/**
 * Migration to handle the creation of {@link DRepresentationDescriptor}.
 * 
 * @author mporhel
 */
public class DRepresentationDescriptorCreationMigrationParticipant extends AbstractRepresentationsFileMigrationParticipant {
    /**
     * The version for which this migration is added.
     */
    public static final Version MIGRATION_VERSION = new Version("11.1.0.201606300900"); //$NON-NLS-1$

    @Override
    public Version getMigrationVersion() {
        return MIGRATION_VERSION;
    }

    @Override
    protected void postLoad(DAnalysis dAnalysis, Version loadedVersion) {

        super.postLoad(dAnalysis, loadedVersion);

        if (loadedVersion.compareTo(MIGRATION_VERSION) < 0) {
            for (DView view : dAnalysis.getOwnedViews()) {
                for (DRepresentation rep : view.getOwnedRepresentations()) {
                    DRepresentationDescriptor descriptor = DRepresentationDescriptorInternalHelper.createDescriptor(rep);
                    view.getOwnedRepresentationDescriptors().add(descriptor);
                }
            }
        }
    }
}
