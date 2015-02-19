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
package org.eclipse.sirius.business.internal.metamodel.helper;

import java.text.MessageFormat;
import java.util.Objects;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;

/**
 * Generic helper to implement the equivalent of an {@code eOpposite} reference in the VSM metamodels as
 * {@code EOperations} instead, which does not have the negative side-effects of a real {@code eOpposite} in terms of
 * modularisation of the VSMs.
 * <p>
 * More specifically, encoding a reference {@code r} between types {@code A} and {@code B}, and an {@code eOpposite}
 * reference {@code r'} means that adding a reference between an {@code A} and a {@code B} in {@code r} requires that
 * {@code B} is in an editable resource, which is not the case when {@code A} is in a VSM developed in the workspace and
 * {@code B} is in a referenced VSM installed as a plug-in.
 * <p>
 * The alternative implemented in Sirius is to have an {@code EOperation} with the same signature as the implementation
 * of {@code r'} would have but to implement it by finding the inverse references (of the right type), relying on an
 * {@code ECrossReferenceAdapter} or equivalent.
 * <p>
 * This helper provides generic code to ease the implementation of such {@code EOperations}. Note that we can not simply
 * use {@code EObjectQuery.getInverseReferences()} as it only works inside a session and these {@code EOperation}s must
 * also work in the context of the VSM editor (among others).
 * 
 * @author pcdavid
 */
public final class DerivedInverseReferenceHelper {
    private DerivedInverseReferenceHelper() {
        // Prevent instantiation.
    }

    /**
     * .
     * 
     * @param target
     *            .
     * @param klass
     *            .
     * @param referencingFeature
     *            .
     * @param <T>
     *            .
     * @return .
     */
    public static <T extends EObject> EList<T> getInverseReferences(EObject target, Class<T> klass, EStructuralFeature referencingFeature) {
        BasicEList<T> result = new BasicEList<>();
        ECrossReferenceAdapter crossReferencer = findCrossReferencer(target);
        for (Setting setting : crossReferencer.getInverseReferences(target, true)) {
            if (Objects.equals(referencingFeature, setting.getEStructuralFeature())) {
                EObject candidate = setting.getEObject();
                /*
                 * We test for eResource() to avoid returning already deleted elements. EMF returns them anyway, see
                 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=445761, but *in the context of the VSM* where we want
                 * to simulate the beahvior of eOpposites, we don't want to see them (see #432495 for example).
                 */
                if (klass.isInstance(candidate) && candidate.eResource() != null) {
                    result.add(klass.cast(candidate));
                }
            }
        }
        return result;
    }

    private static ECrossReferenceAdapter findCrossReferencer(EObject target) {
        Resource r = target != null ? target.eResource() : null;
        if (r == null) {
            throw new UnsupportedOperationException(MessageFormat.format("No crossreferencer available on loose object not contained inside a Resource: {0}", target)); //$NON-NLS-1$
        }
        ECrossReferenceAdapter crossReferencer = ECrossReferenceAdapter.getCrossReferenceAdapter(r);
        if (crossReferencer == null) {
            throw new UnsupportedOperationException(MessageFormat.format("No crossreferencer could be found for element {0}", target)); //$NON-NLS-1$
        }
        return crossReferencer;
    }
}
