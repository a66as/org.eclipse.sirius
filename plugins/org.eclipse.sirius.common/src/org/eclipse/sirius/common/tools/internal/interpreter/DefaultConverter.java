/*******************************************************************************
 * Copyright (c) 2017 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.common.tools.internal.interpreter;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.OptionalInt;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Default implementation of {@link IConverter}, to be used unless there is a specific need to keep compatibility with
 * older behavior in some interpreters/contexts.
 * 
 * @author pcdavid
 */
public class DefaultConverter implements IConverter {

    @Override
    public OptionalInt toInt(Object rawValue) {
        try {
            return OptionalInt.of(Integer.parseInt(String.valueOf(rawValue)));
        } catch (NumberFormatException e) {
            return OptionalInt.of(0);
        }
    }

    @Override
    public Optional<Boolean> toBoolean(Object rawValue) {
        final boolean result;
        if (rawValue == null) {
            result = false;
        } else if (rawValue instanceof Boolean) {
            result = ((Boolean) rawValue).booleanValue();
        } else {
            String toString = rawValue.toString();
            if ("true".equalsIgnoreCase(toString)) { //$NON-NLS-1$
                result = true;
            } else if ("false".equalsIgnoreCase(toString)) { //$NON-NLS-1$
                result = false;
            } else {
                /*
                 * raw is != null and its toString is neither true or false, this happens when the user expect the
                 * condition to check that a value is existing, then we consider any non null value returns true and
                 * null returns false.
                 */
                result = true;
            }
        }
        return Optional.of(result);
    }

    @Override
    public Optional<String> toString(Object rawValue) {
        if (rawValue != null) {
            return Optional.of(String.valueOf(rawValue));
        } else {
            return Optional.of(""); //$NON-NLS-1$
        }
    }

    @Override
    public Optional<EObject> toEObject(Object rawValue) {
        if (rawValue instanceof EObject) {
            return Optional.of((EObject) rawValue);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Collection<EObject>> toEObjectCollection(Object rawValue) {
        final Collection<EObject> result;
        if (rawValue instanceof Collection<?>) {
            result = Lists.newArrayList(Iterables.filter((Collection<?>) rawValue, EObject.class));
        } else if (rawValue instanceof EObject) {
            result = Collections.singleton((EObject) rawValue);
        } else if (rawValue != null && rawValue.getClass().isArray()) {
            result = Lists.newArrayList(Iterables.filter(Lists.newArrayList((Object[]) rawValue), EObject.class));
        } else {
            result = Collections.emptySet();
        }
        return Optional.of(result);
    }

}
