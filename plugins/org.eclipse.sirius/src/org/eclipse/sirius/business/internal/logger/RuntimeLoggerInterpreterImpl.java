/*******************************************************************************
 * Copyright (c) 2009, 2015 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.business.internal.logger;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.sirius.business.api.logger.RuntimeLoggerInterpreter;
import org.eclipse.sirius.business.api.logger.RuntimeLoggerManager;
import org.eclipse.sirius.common.tools.api.interpreter.EvaluationException;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreter;
import org.eclipse.sirius.viewpoint.Messages;
import org.eclipse.sirius.viewpoint.SiriusPlugin;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * .
 * 
 * @author mchauvin
 */
public class RuntimeLoggerInterpreterImpl implements RuntimeLoggerInterpreter {

    private IInterpreter interpreter;

    /**
     * Only {@link RuntimeLoggerManagerImpl} should call this constructor.
     * 
     * @param interpreter
     *            the interpreter
     */
    protected RuntimeLoggerInterpreterImpl(final IInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.business.api.logger.RuntimeLoggerInterpreter#evaluate(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public Object evaluate(final EObject context, final EObject descriptionObject, final EStructuralFeature descriptionFeature) {

        final String expression = (String) descriptionObject.eGet(descriptionFeature);
        try {
            final Object result = interpreter.evaluate(context, expression);
            logEvaluation(context, descriptionObject, descriptionFeature, result);
            return result;
        } catch (final EvaluationException e) {
            RuntimeLoggerManager.INSTANCE.error(descriptionObject, descriptionFeature, e);
        }
        return null;
    }

    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.business.api.logger.RuntimeLoggerInterpreter#evaluateBoolean(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public boolean evaluateBoolean(final EObject context, final EObject descriptionObject, final EStructuralFeature descriptionFeature) {
        return evaluateBoolean(context, descriptionObject, descriptionFeature, false);
    }

    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.business.api.logger.RuntimeLoggerInterpreter#evaluateBoolean(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature, boolean)
     */
    @Override
    public boolean evaluateBoolean(final EObject context, final EObject descriptionObject, final EStructuralFeature descriptionFeature, final boolean flagCondition) {
        final String expression = (String) descriptionObject.eGet(descriptionFeature);
        try {
            final boolean result = interpreter.evaluateBoolean(context, expression);
            logEvaluation(context, descriptionObject, descriptionFeature, result);
            return result;
        } catch (final EvaluationException e) {
            RuntimeLoggerManager.INSTANCE.error(descriptionObject, descriptionFeature, e);
            if (flagCondition) {
                SiriusPlugin.getDefault().error(Messages.RuntimeLoggerInterpreterImpl_evaluationConditionErrorMsg, e);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.business.api.logger.RuntimeLoggerInterpreter#evaluateInteger(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public Integer evaluateInteger(final EObject context, final EObject descriptionObject, final EStructuralFeature descriptionFeature) {
        final String expression = (String) descriptionObject.eGet(descriptionFeature);
        try {
            final Integer result = interpreter.evaluateInteger(context, expression);
            logEvaluation(context, descriptionObject, descriptionFeature, result);
            return result;
        } catch (final EvaluationException e) {
            RuntimeLoggerManager.INSTANCE.error(descriptionObject, descriptionFeature, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.business.api.logger.RuntimeLoggerInterpreter#evaluateString(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public String evaluateString(final EObject context, final EObject descriptionObject, final EStructuralFeature descriptionFeature) {
        final String expression = (String) descriptionObject.eGet(descriptionFeature);
        try {
            final String result = interpreter.evaluateString(context, expression);
            logEvaluation(context, descriptionObject, descriptionFeature, result);
            return result;
        } catch (final EvaluationException e) {
            RuntimeLoggerManager.INSTANCE.error(descriptionObject, descriptionFeature, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.business.api.logger.RuntimeLoggerInterpreter#evaluateEObject(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public EObject evaluateEObject(final EObject context, final EObject descriptionObject, final EStructuralFeature descriptionFeature) {
        final String expression = (String) descriptionObject.eGet(descriptionFeature);
        try {
            final EObject result = interpreter.evaluateEObject(context, expression);
            logEvaluation(context, descriptionObject, descriptionFeature, result);
            return result;
        } catch (final EvaluationException e) {
            RuntimeLoggerManager.INSTANCE.error(descriptionObject, descriptionFeature, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.business.api.logger.RuntimeLoggerInterpreter#evaluateCollection(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public Collection<EObject> evaluateCollection(final EObject context, final EObject descriptionObject, final EStructuralFeature descriptionFeature) {
        final String expression = (String) descriptionObject.eGet(descriptionFeature);
        try {
            final Collection<EObject> result = interpreter.evaluateCollection(context, expression);
            logEvaluation(context, descriptionObject, descriptionFeature, result);
            return result;
        } catch (final EvaluationException e) {
            RuntimeLoggerManager.INSTANCE.error(descriptionObject, descriptionFeature, e);
        }
        return Collections.emptySet();
    }

    private void logEvaluation(EObject context, EObject descriptionObject, EStructuralFeature descriptionFeature, Object result) {
        String prefix = "==== "; //$NON-NLS-1$
        String origin = descriptionFeature.getEContainingClass().getEPackage().getNsURI() + "//" + descriptionFeature.getEContainingClass().getName() + "." + descriptionFeature.getName(); //$NON-NLS-1$ //$NON-NLS-2$
        String expression = (String) descriptionObject.eGet(descriptionFeature);
        StringBuilder vars = new StringBuilder();
        String lf = "\n"; //$NON-NLS-1$
        for (Map.Entry<String, ?> binding : interpreter.getVariables().entrySet()) {
            Object val = binding.getValue();
            String type = typeName(val);
            vars.append(prefix).append(" - ").append(binding.getKey()).append(" : ").append(type).append(lf); //$NON-NLS-1$ //$NON-NLS-2$
        }
        StringBuilder message = new StringBuilder();
        message.append(prefix).append("Evaluation of ").append(origin).append(lf); //$NON-NLS-1$
        // message.append(prefix).append("Definition:
        // ").append(expression).append(lf); //$NON-NLS-1$
        message.append(prefix).append("Context (self): ").append(typeName(context)).append(lf); //$NON-NLS-1$
        message.append(prefix).append("Variables:").append(lf); //$NON-NLS-1$
        message.append(vars.toString());
        message.append(prefix).append("Result: ").append(typeName(result)); //$NON-NLS-1$
        System.out.println(message.toString());
    }

    private String typeName(Object val) {
        String type = val != null ? val.getClass().getName() : "<null>"; //$NON-NLS-1$
        if (val instanceof EObject) {
            EClass klass = ((EObject) val).eClass();
            type = klass.getEPackage().getNsURI() + "//" + klass.getName(); //$NON-NLS-1$
        } else if (val instanceof Collection<?>) {
            StringBuilder sb = new StringBuilder("["); //$NON-NLS-1$
            Function<Object, String> f = new Function<Object, String>() {
                public String apply(Object input) {
                    return typeName(input);
                };
            };
            sb.append(Joiner.on(", ").join(Iterables.transform((Collection<?>) val, f))).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
            type = sb.toString();
        }
        return type;
    }
}
