/*******************************************************************************
 * Copyright (c) 2015, 2017 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.properties.core.api;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.common.interpreter.api.EvaluationResult;
import org.eclipse.sirius.common.interpreter.api.IEvaluationResult;
import org.eclipse.sirius.common.interpreter.api.IInterpreter;
import org.eclipse.sirius.common.tools.api.interpreter.EvaluationException;
import org.eclipse.sirius.properties.core.internal.SiriusToolServices;

/**
 * Provides an implementation of {@link IInterpreter} backed by an old-style {@link IInterpreter}.
 * 
 * @author pcdavid
 */
public class SiriusInterpreter implements IInterpreter {

    private org.eclipse.sirius.common.tools.api.interpreter.IInterpreter interpreter;

    /**
     * The constructor.
     * 
     * @param session
     *            The Sirius session
     */
    public SiriusInterpreter(Session session) {
        this(session.getInterpreter());
    }

    /**
     * The constructor.
     * 
     * @param interpreter
     *            An interpreter
     */
    public SiriusInterpreter(org.eclipse.sirius.common.tools.api.interpreter.IInterpreter interpreter) {
        this.interpreter = Objects.requireNonNull(interpreter);
    }

    @Override
    public IEvaluationResult evaluateExpression(Map<String, Object> variables, String expr) {
        IEvaluationResult result = EvaluationResult.noEvaluation();
        Object self = variables.get("self"); //$NON-NLS-1$
        if (self instanceof EObject) {
            try {
                setupInterpreter(variables);
                org.eclipse.sirius.common.tools.api.interpreter.IEvaluationResult evaluationResult = this.interpreter.evaluateExpression((EObject) self, expr);
                result = EvaluationResult.of(evaluationResult.getValue(), BasicDiagnostic.toDiagnostic(evaluationResult.getStatus()));
            } catch (EvaluationException e) {
                result = EvaluationResult.withError(BasicDiagnostic.toDiagnostic(e));
            } finally {
                tearDownInterpreter(variables);
            }
        }
        return result;
    }

    private void setupInterpreter(Map<String, Object> variables) {
        this.interpreter.addImport(SiriusToolServices.class.getName());
        declareLocals(variables, this.interpreter);
    }

    private void declareLocals(Map<String, Object> variables, org.eclipse.sirius.common.tools.api.interpreter.IInterpreter i) {
        Set<Entry<String, Object>> entries = variables.entrySet();
        for (Entry<String, Object> entry : entries) {
            i.setVariable(entry.getKey(), entry.getValue());
        }
    }

    private void tearDownInterpreter(Map<String, Object> variables) {
        unsetLocals(variables, this.interpreter);
    }

    private void unsetLocals(Map<String, Object> variables, org.eclipse.sirius.common.tools.api.interpreter.IInterpreter i) {
        Set<Entry<String, Object>> entries = variables.entrySet();
        for (Entry<String, Object> entry : entries) {
            i.unSetVariable(entry.getKey());
        }
    }

}
