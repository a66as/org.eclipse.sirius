/**
 * Copyright (c) 2007, 2015 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *
 */
package org.eclipse.sirius.viewpoint;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.viewpoint.description.Viewpoint;

/**
 * <!-- begin-user-doc -->
 * <p>
 * A representation of the model object '<em><b>DView</b></em>'.
 * </p>
 * <p>
 * An analysis is the root element of a view (.aird, .airview). It contains
 * viewpoints and Meta Model extensions.
 * </p>
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc --> An view is the root element <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.sirius.viewpoint.DView#getOwnedRepresentations <em>
 * Owned Representations</em>}</li>
 * <li>{@link org.eclipse.sirius.viewpoint.DView#getOwnedExtensions <em>Owned
 * Extensions</em>}</li>
 * <li>{@link org.eclipse.sirius.viewpoint.DView#getViewpoint <em>Viewpoint
 * </em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.sirius.viewpoint.ViewpointPackage#getDView()
 * @model
 * @generated
 */
public interface DView extends EObject {
    /**
     * Returns the value of the '<em><b>Owned Representations</b></em>'
     * containment reference list. The list contents are of type
     * {@link org.eclipse.sirius.viewpoint.DRepresentation}. <!-- begin-user-doc
     * --> <!-- end-user-doc --> <!-- begin-model-doc --> The viewpoints that
     * are owned by this analysis. <!-- end-model-doc -->
     *
     * @return the value of the '<em>Owned Representations</em>' containment
     *         reference list.
     * @see org.eclipse.sirius.viewpoint.ViewpointPackage#getDView_OwnedRepresentations()
     * @model containment="true" resolveProxies="true"
     * @generated
     */
    EList<DRepresentation> getOwnedRepresentations();

    /**
     * Returns the value of the '<em><b>Owned Extensions</b></em>' containment
     * reference. <!-- begin-user-doc --> <!-- end-user-doc --> <!--
     * begin-model-doc --> The Meta Model extension for this analysis. It may be
     * null. <!-- end-model-doc -->
     *
     * @return the value of the '<em>Owned Extensions</em>' containment
     *         reference.
     * @see #setOwnedExtensions(MetaModelExtension)
     * @see org.eclipse.sirius.viewpoint.ViewpointPackage#getDView_OwnedExtensions()
     * @model containment="true" resolveProxies="true"
     * @generated
     */
    MetaModelExtension getOwnedExtensions();

    /**
     * Sets the value of the '
     * {@link org.eclipse.sirius.viewpoint.DView#getOwnedExtensions
     * <em>Owned Extensions</em>}' containment reference. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Owned Extensions</em>' containment
     *            reference.
     * @see #getOwnedExtensions()
     * @generated
     */
    void setOwnedExtensions(MetaModelExtension value);

    /**
     * Returns the value of the '<em><b>Viewpoint</b></em>' reference. <!--
     * begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> The
     * viewpoint that is used for this view <!-- end-model-doc -->
     *
     * @return the value of the '<em>Viewpoint</em>' reference.
     * @see #setViewpoint(Viewpoint)
     * @see org.eclipse.sirius.viewpoint.ViewpointPackage#getDView_Viewpoint()
     * @model required="true"
     * @generated
     */
    Viewpoint getViewpoint();

    /**
     * Sets the value of the '
     * {@link org.eclipse.sirius.viewpoint.DView#getViewpoint
     * <em>Viewpoint</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @param value
     *            the new value of the '<em>Viewpoint</em>' reference.
     * @see #getViewpoint()
     * @generated
     */
    void setViewpoint(Viewpoint value);

} // DView
