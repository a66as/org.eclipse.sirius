/*******************************************************************************
 * Copyright (c) 2013, 2014 THALES GLOBAL SERVICES and others.
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
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.sirius.common.tools.api.interpreter.EvaluationException;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreter;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreterContext;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreterStatus;
import org.eclipse.sirius.common.tools.api.interpreter.TypedValidation;
import org.eclipse.sirius.ecore.extender.business.api.accessor.MetamodelDescriptor;
import org.eclipse.sirius.ecore.extender.business.api.accessor.ModelAccessor;

/**
 * Abstract base class for implementations of {@link IInterpreter}.
 *
 * @author pcdavid
 */
public abstract class AbstractInterpreter implements IInterpreter, TypedValidation {

    /** The separator between EPackage name and EClass name for domain class. */
    protected static final String SEPARATOR = "."; //$NON-NLS-1$

    /**
     * The converter to use to coerce raw values.
     */
    private IConverter converter = new DefaultConverter();

    @Override
    public boolean provides(String expression) {
        return expression != null && expression.startsWith(getPrefix());
    }

    @Override
    public Collection<EObject> evaluateCollection(EObject context, String expression) throws EvaluationException {
        Object raw = evaluate(context, expression);
        return converter.toEObjectCollection(raw).orElse(Collections.emptySet());
    }

    @Override
    public boolean evaluateBoolean(EObject context, String expression) throws EvaluationException {
        Object raw = evaluate(context, expression);
        return converter.toBoolean(raw).orElse(Boolean.FALSE);
    }

    @Override
    public EObject evaluateEObject(EObject context, String expression) throws EvaluationException {
        Object raw = evaluate(context, expression);
        return converter.toEObject(raw).orElse(null);
    }

    @Override
    public String evaluateString(EObject context, String expression) throws EvaluationException {
        Object raw = evaluate(context, expression);
        return converter.toString(raw).orElse(""); //$NON-NLS-1$
    }

    @Override
    public Integer evaluateInteger(EObject context, String expression) throws EvaluationException {
        Object raw = evaluate(context, expression);
        return converter.toInt(raw).orElse(0);
    }

    @Override
    public void clearImports() {
        // Nothing to do.
    }

    @Override
    public void addImport(String dependency) {
        // Nothing to do.
    }

    @Override
    public void setProperty(Object key, Object value) {
        // Nothing to do.
    }

    @Override
    public void setVariable(String name, Object value) {
        // Nothing to do.
    }

    @Override
    public void unSetVariable(String name) {
        // Nothing to do.
    }

    @Override
    public Object getVariable(String name) {
        return null;
    }

    @Override
    public void clearVariables() {
        // Nothing to do.
    }

    @Override
    public void dispose() {
        // Nothing to do.
    }

    @Override
    public Map<String, ?> getVariables() {
        return Collections.emptyMap();
    }

    @Override
    public void setModelAccessor(ModelAccessor modelAccessor) {
        // Nothing to do.
    }

    @Override
    public String getVariablePrefix() {
        return ""; //$NON-NLS-1$
    }

    @Override
    public void setCrossReferencer(ECrossReferenceAdapter crossReferencer) {
        // Nothing to do.
    }

    @Override
    public Collection<String> getImports() {
        return Collections.emptySet();
    }

    @Override
    public void removeImport(String dependency) {
        // Nothing to do.
    }

    @Override
    public boolean supportsValidation() {
        return false;
    }

    @Override
    public Collection<IInterpreterStatus> validateExpression(IInterpreterContext context, String expression) {
        return analyzeExpression(context, expression).getStatuses();
    }

    @Override
    public void activateMetamodels(Collection<MetamodelDescriptor> metamodels) {
        // Nothing to do.
    }
}
