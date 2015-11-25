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
package org.eclipse.sirius.common.interpreter.api;

import org.eclipse.emf.common.util.Diagnostic;

/**
 * The evaluation result.
 *
 * @author sbegaudeau
 */
public interface IEvaluationResult {
	/**
	 * The value returned by the expression.
	 *
	 * @return The value returned by the expression
	 */
	Object getValue();

	/**
	 * The diagnostic of the evaluation.
	 * 
	 * @return The diagnostic
	 */
	Diagnostic getDiagnostic();
}
