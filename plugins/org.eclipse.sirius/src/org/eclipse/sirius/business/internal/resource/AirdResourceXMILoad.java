/*******************************************************************************
 * Copyright (c) 2015 Obeo.
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
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.UnresolvedReferenceException;
import org.eclipse.emf.ecore.xmi.XMIException;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.SAXWrapper;
import org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;
import org.eclipse.sirius.business.internal.migration.RepresentationsFileMigrationService;
import org.eclipse.sirius.viewpoint.DAnalysis;
import org.eclipse.sirius.viewpoint.ViewpointPackage;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A specialization of {@link XMILoadImpl} to enable hooking into the XML
 * parsing for modeling migration.
 * 
 * @author Cedric Brun <cedric.brun@obeo.fr>
 *
 */
@SuppressWarnings("deprecation")
public class AirdResourceXMILoad extends XMILoadImpl {
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private String loadedVersion;

    private boolean doMigration;

    private Consumer<DAnalysis> listener;

    /**
     * Create a new {@link AirdResourceXMILoad}, suitable for on the fly
     * migration of .aird files.
     * 
     * @param loadedVersion
     *            the original version of the .aird file which is currently
     *            being loaded.
     * @param helper
     *            the xml helper to use during the load.
     * @param listener
     *            if non-<code>null</code>, will be sent the {@link DAnalysis}
     *            at the root of the aird resource as soon as it is available,
     *            before the rest of the resource is parsed.
     */
    public AirdResourceXMILoad(String loadedVersion, XMLHelper helper, Consumer<DAnalysis> listener) {
        super(helper);
        this.loadedVersion = loadedVersion;
        this.doMigration = true;
        this.listener = listener;
    }

    /**
     * Create a new {@link AirdResourceXMILoad}, suitable for on the fly
     * migration of .aird files.
     * 
     * @param helper
     *            the xml helper to use during the load.
     * @param listener
     *            if non-<code>null</code>, will be sent the {@link DAnalysis}
     *            at the root of the aird resource as soon as it is available,
     *            before the rest of the resource is parsed.
     */
    public AirdResourceXMILoad(XMLHelper helper, Consumer<DAnalysis> listener) {
        super(helper);
        this.listener = listener;
    }

    @Override
    protected DefaultHandler makeDefaultHandler() {
        return new SAXWrapper(new AirdHandler(resource, helper, options));
    }

    @Override
    public void load(XMLResource r, InputStream s, Map<?, ?> o) throws IOException {
        try {
            super.load(r, s, o);
        } catch (Resource.IOWrappedException e) {
            if (!(e.getCause() instanceof UnresolvedReferenceException)) {
                throw e;
            }
        }
    }

    /**
     * A specialization of the SAX XMI handler to delegate to the file migration
     * service.
     * 
     * @author cedric
     *
     */
    class AirdHandler extends SAXXMIHandler {
        private boolean abortOnError;

        private boolean finished;

        public AirdHandler(XMLResource xmiResource, XMLHelper helper, Map<?, ?> options) {
            super(xmiResource, helper, options);

            if (Boolean.TRUE.equals(options.get(AirDResourceImpl.OPTION_ABORT_ON_ERROR))) {
                abortOnError = true;
            }
        }

        @Override
        public void startElement(String uri, String localName, String name) {
            if (!finished) {
                super.startElement(uri, localName, name);
            }
        }

        @Override
        public void startCDATA() {
            if (!finished) {
                super.startCDATA();
            }
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if (!finished) {
                super.startElement(uri, localName, name, attributes);
            }
        }

        @Override
        public void startEntity(String name) {
            if (!finished) {
                super.startEntity(name);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (!finished) {
                super.characters(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String name) {
            if (!finished && !(uri.equals("http://www.omg.org/XMI") && "XMI".equals(localName))) { //$NON-NLS-1$ //$NON-NLS-2$
                super.endElement(uri, localName, name);
                if (doMigration) {
                    RepresentationsFileMigrationService.getInstance().postXMLEndElement(objects.peek(), attribs, uri, localName, name, loadedVersion);
                }
                if (listener != null && ViewpointPackage.eNS_URI.equals(uri) && ViewpointPackage.Literals.DANALYSIS.getName().equals(localName)) {
                    //finished = true;
                    this.deferredExtent.stream().findFirst().ifPresent((o) -> {
                        if (o instanceof DAnalysis) {
                            listener.accept((DAnalysis) o);
                            //EXECUTOR.execute(() -> listener.accept((DAnalysis) o));
                        }
                    });
                }
            }
        }

        @Override
        public void error(XMIException e) {
            // This part of code comes from
            // org.eclipse.gmf.runtime.emf.core.resources.GMFHandler.error(XMIException)
            super.error(e);
            if (abortOnError) {
                // Ignore UnresolvedReferenceException, since unresolved
                // references are not a fatal error. We will continue to attempt
                // to load the model and log UnresolvedReferenceException.
                if (!(e instanceof UnresolvedReferenceException)) {
                    if (e.getCause() != null) {
                        throw new RuntimeException(e.getCause());
                    }
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
