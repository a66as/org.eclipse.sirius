/*******************************************************************************
 * Copyright (c) 2009, 2009 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.diagram.ui.tools.internal.layout.semantic;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.diagram.ui.tools.internal.layout.EdgeLayoutDataKey;

/**
 * Kind of key use to store the layout data corresponding to a semantic element
 * represented by a {@link org.eclipse.sirius.diagram.DEdge}. The key is the URI of
 * the semantic element.
 * 
 * @author <a href="mailto:laurent.redor@obeo.fr">Laurent Redor</a>
 */
public class SemanticEdgeLayoutDataKey extends AbstractSemanticLayoutDataKey implements EdgeLayoutDataKey {

    /**
     * Constructor.
     * 
     * @param uriFragment
     *            String to use.
     */
    public SemanticEdgeLayoutDataKey(final String uriFragment) {
        super(uriFragment);
    }

    /**
     * Default constructor.
     * 
     * @param semanticElement
     *            The element to build the key
     */
    public SemanticEdgeLayoutDataKey(final EObject semanticElement) {
        super(semanticElement);
    }
}
