/*******************************************************************************
 * Copyright (c) 2008, 2015 THALES GLOBAL SERVICES and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.business.internal.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.sirius.business.api.migration.ResourceVersionMismatchDiagnostic;
import org.eclipse.sirius.business.api.session.resource.AirdResource;
import org.eclipse.sirius.business.api.session.resource.DResource;
import org.eclipse.sirius.business.internal.migration.AbstractSiriusMigrationService;
import org.eclipse.sirius.business.internal.migration.RepresentationsFileExtendedMetaData;
import org.eclipse.sirius.business.internal.migration.RepresentationsFileMigrationService;
import org.eclipse.sirius.business.internal.migration.RepresentationsFileResourceHandler;
import org.eclipse.sirius.business.internal.migration.RepresentationsFileVersionSAXParser;
import org.eclipse.sirius.business.internal.migration.resource.MigrationUtil;
import org.eclipse.sirius.business.internal.resource.parser.RepresentationsFileXMIHelper;
import org.eclipse.sirius.common.tools.DslCommonPlugin;
import org.eclipse.sirius.ext.base.Option;
import org.eclipse.sirius.tools.api.profiler.SiriusTasksKey;
import org.osgi.framework.Version;

/**
 * Aird resource to provide custom factory.
 * 
 * @author mchauvin
 */
public class AirDResourceImpl extends XMIResourceImpl implements DResource, AirdResource {

    /**
     * Use this option to abort loading a resource immediately when an error
     * occurs. The default is <code>Boolean.FALSE</code> unless set to
     * <code>Boolean.TRUE</code> explicitly.
     */
    public static final String OPTION_ABORT_ON_ERROR = "ABORT_ON_ERROR"; //$NON-NLS-1$

    /**
     * Use this option to only load the first DAnalysis element of the resource,
     * and skip other roots. The resulting model will be incomplete and not
     * suitable to use for a real session, but should provide access to most of
     * the interesting "session metadata" for a much lower cost.
     */
    public static final String OPTION_LOAD_DANALYSIS_ONLY = "LOAD_DANALYSIS_ONLY"; //$NON-NLS-1$

    /**
     * The number of current load in progress. Usefull for determine if the
     * current load is the first one or is a load triggered by a resolve (for
     * fragmented files for examples).
     */
    private static ThreadLocal<Integer> nbLoadInProgress = new ThreadLocal<Integer>() {
        @Override
        protected synchronized Integer initialValue() {
            return Integer.valueOf(0);
        }
    };

    /**
     * This constructor should be used only if the version is up to date. There
     * is no automatic migration during the resolution of an object.
     * 
     * @param uri
     *            the URI
     */
    public AirDResourceImpl(final URI uri) {
        super(uri);
    }

    @Override
    protected boolean useUUIDs() {
        return true;
    }

    @Override
    protected boolean useIDAttributes() {
        return false;
    }

    /**
     * Increment the number of load in progress.
     */
    protected static void incrementLoadInProgress() {
        nbLoadInProgress.set(Integer.valueOf(nbLoadInProgress.get().intValue() + 1));
    }

    /**
     * Decrement the number of load in progress.
     */
    protected static void decrementLoadInProgress() {
        nbLoadInProgress.set(Integer.valueOf(nbLoadInProgress.get().intValue() - 1));
    }

    /**
     * Check if there is a load in progress.
     * 
     * @return true if at least one load is in progress, false otherwise.
     */
    protected static boolean hasLoadInProgress() {
        return nbLoadInProgress.get().intValue() != 0;
    }

    /**
     * Overridden to not have {@link XMIResourceImpl} set to true in this
     * constructor because now it is
     * {@link org.eclipse.sirius.business.internal.resource.ResourceModifiedFieldUpdater}
     * which manage {@link org.eclipse.emf.ecore.resource.Resource#isModified()}
     * .
     * 
     * {@inheritDoc}
     */
    @Override
    public void setTrackingModification(boolean isTrackingModification) {
    }

    @Override
    public void load(Map<?, ?> options) throws IOException {
        if (!isLoaded) {
            DslCommonPlugin.PROFILER.startWork(SiriusTasksKey.LOAD_AIRD_KEY);
            AirDResourceImpl.incrementLoadInProgress();
            try {
                Diagnostic migrationMismatchDiagnostic = handleMigrationOptions();
                super.load(options);
                if (migrationMismatchDiagnostic != null) {
                    getErrors().add(migrationMismatchDiagnostic);
                }
            } finally {
                AirDResourceImpl.decrementLoadInProgress();
            }
            DslCommonPlugin.PROFILER.stopWork(SiriusTasksKey.LOAD_AIRD_KEY);
            // AirDResourceMigration migration = new
            // AirDResourceMigration(this);
            // // Notify user only if there is no more load in progress.
            // migration.migrate(!AirDResourceImpl.hasLoadInProgress());
        }
    }

    /**
     * Handle migration options and return an error diagnostic in case of
     * migration version mismatch
     */
    private Diagnostic handleMigrationOptions() {
        Diagnostic migrationMismatchDiagnostic = null;
        RepresentationsFileVersionSAXParser parser = new RepresentationsFileVersionSAXParser(uri);
        boolean migrationIsNeeded = true;
        String loadedVersion = parser.getVersion(new NullProgressMonitor());
        if (loadedVersion != null) {
            Version parsedLoadedVersion = Version.parseVersion(loadedVersion);
            Version lastMigrationVersion = RepresentationsFileMigrationService.getInstance().getLastMigrationVersion();
            boolean attemptToLoadMoreRecentVSM = lastMigrationVersion.compareTo(parsedLoadedVersion) < 0;
            if (attemptToLoadMoreRecentVSM && !MigrationUtil.ignoreVersionMismatch) {
                migrationMismatchDiagnostic = new ResourceVersionMismatchDiagnostic(uri, parsedLoadedVersion, lastMigrationVersion);
            }
            migrationIsNeeded = RepresentationsFileMigrationService.getInstance().isMigrationNeeded(parsedLoadedVersion);
        }
        Object versionOption = this.getDefaultLoadOptions().get(AbstractSiriusMigrationService.OPTION_RESOURCE_MIGRATION_LOADEDVERSION);

        // If the migration options have been installed and we do not need to
        // migrate anymore (a reload following a save for instance), we remove
        // them.
        if (!migrationIsNeeded && versionOption != null) {
            removeMigrationMechanism();
        }
        // If we need to migrate we install the mechanism. If the mechanism
        // was already installed and the loaded version is different, we
        // update it.
        else if (migrationIsNeeded && (versionOption == null || !versionOption.equals(loadedVersion))) {
            addMigrationOptions(loadedVersion, this.getDefaultLoadOptions(), this.getDefaultSaveOptions());
        }
        return migrationMismatchDiagnostic;
    }

    private void removeMigrationMechanism() {
        this.getDefaultLoadOptions().remove(XMLResource.OPTION_EXTENDED_META_DATA);
        this.getDefaultLoadOptions().remove(XMLResource.OPTION_RESOURCE_HANDLER);
        this.getDefaultLoadOptions().remove(AbstractSiriusMigrationService.OPTION_RESOURCE_MIGRATION_LOADEDVERSION);
        this.getDefaultSaveOptions().remove(XMLResource.OPTION_EXTENDED_META_DATA);
        this.getDefaultSaveOptions().remove(XMLResource.OPTION_RESOURCE_HANDLER);
    }

    @Override
    protected XMLHelper createXMLHelper() {
        return new RepresentationsFileXMIHelper(this);
    }

    @Override
    protected XMLLoad createXMLLoad(Map<?, ?> options) {
        Object danalysisOnly = options.get(OPTION_LOAD_DANALYSIS_ONLY);
        if (options != null && options.containsKey(AbstractSiriusMigrationService.OPTION_RESOURCE_MIGRATION_LOADEDVERSION)) {
            // LoadedVersion can be null for old aird files.
            String loadedVersion = null;
            Object mapVersion = options.get(AbstractSiriusMigrationService.OPTION_RESOURCE_MIGRATION_LOADEDVERSION);
            if (mapVersion instanceof String) {
                loadedVersion = (String) mapVersion;
            }
            return new AirdResourceXMILoad(loadedVersion, createXMLHelper(), danalysisOnly == Boolean.TRUE);
        }

        return new AirdResourceXMILoad(createXMLHelper(), danalysisOnly == Boolean.TRUE);
    }

    /**
     * Override to migrate fragment if necessary (when a reference has been
     * renamed) before getting the EObject.
     * 
     * {@inheritDoc}
     */
    @Override
    public EObject getEObject(String uriFragment) {
        Option<String> optionalRewrittenFragment = RepresentationsFileMigrationService.getInstance().getNewFragment(uriFragment);
        if (optionalRewrittenFragment.some()) {
            return getEObject(optionalRewrittenFragment.get());
        } else {
            return super.getEObject(uriFragment);
        }
    }

    /**
     * Add the migration options in the given loadOptions and saveOptions maps.
     * 
     * @param loadedVersion
     *            the loadedVersion.
     * @param loadOptions
     *            the loadOptions map.
     * @param saveOptions
     *            the saveOptions map.
     */
    public static void addMigrationOptions(String loadedVersion, Map<Object, Object> loadOptions, Map<Object, Object> saveOptions) {

        RepresentationsFileExtendedMetaData extendedMetaData = new RepresentationsFileExtendedMetaData(loadedVersion);
        RepresentationsFileResourceHandler resourceHandler = new RepresentationsFileResourceHandler(loadedVersion);

        loadOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
        loadOptions.put(XMLResource.OPTION_RESOURCE_HANDLER, resourceHandler);
        /**
         * This option is passed so that the resource can decide to adapt the
         * load mechanism depending on the loaded version.
         */
        loadOptions.put(AbstractSiriusMigrationService.OPTION_RESOURCE_MIGRATION_LOADEDVERSION, loadedVersion);

        saveOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
        saveOptions.put(XMLResource.OPTION_RESOURCE_HANDLER, resourceHandler);

    }

}
