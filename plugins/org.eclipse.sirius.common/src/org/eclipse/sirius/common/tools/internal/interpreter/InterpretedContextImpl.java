/*******************************************************************************
 * Copyright (c) 2012, 2018 THALES GLOBAL SERVICES and others.
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
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreterContext;
import org.eclipse.sirius.common.tools.api.interpreter.VariableType;

/**
 * Default implementation for of {@link IInterpreterContext}.
 *
 * @author alagarde
 */
public class InterpretedContextImpl implements IInterpreterContext {

    /**
     * The VSM element on which the target expression is defined. 
     */
    private final EObject element;
    
    /**
     * The feature of {@code element} which defines the target expression.
     */
    private final EStructuralFeature field;

    private final Map<String, VariableType> variables;

    private final Collection<EPackage> avalaiblePackages;

    private final VariableType targetTypes;

    private final Collection<String> dependencies;

    /**
     * Indicates if the expression need all possibles type that can be held by "current" element to be validated. It can
     * not be true, for example when considering a PopupMenuContribution's precondition, that is only evaluated with
     * variables.
     */
    private boolean requiresTargetType;

    /**
     * Default constructor.
     *
     * @param element
     *            the concerned element
     * @param requiresTargetType
     *            indicates whether this expression requires a targetType for the "current" element
     * @param field
     *            the concerned field
     * @param targetTypes
     *            the possible types for the element
     * @param avalaiblePackages
     *            the list of available EPackages
     * @param variables
     *            the defined variables
     * @param dependencies
     *            the list of available dependencies.
     */
    public InterpretedContextImpl(EObject element, boolean requiresTargetType, EStructuralFeature field, VariableType targetTypes, Collection<EPackage> avalaiblePackages,
            Map<String, VariableType> variables, Collection<String> dependencies) {
        this.element = element;
        this.requiresTargetType = requiresTargetType;
        this.targetTypes = targetTypes;
        this.avalaiblePackages = avalaiblePackages;
        this.variables = variables;
        this.field = field;
        this.dependencies = dependencies;
    }

    @Override
    public EObject getElement() {
        return element;
    }

    @Override
    public VariableType getTargetType() {
        return targetTypes;
    }

    @Override
    public Collection<EPackage> getAvailableEPackages() {
        return avalaiblePackages;
    }

    @Override
    public Map<String, VariableType> getVariables() {
        return variables;
    }

    @Override
    public EStructuralFeature getField() {
        return field;
    }

    @Override
    public Collection<String> getDependencies() {
        return dependencies;
    }

    @Override
    public boolean requiresTargetType() {
        return requiresTargetType;
    }
}
