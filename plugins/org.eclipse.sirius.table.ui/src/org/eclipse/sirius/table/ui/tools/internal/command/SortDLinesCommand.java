/*******************************************************************************
 * Copyright (c) 2012 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.table.ui.tools.internal.command;

import java.util.Comparator;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.SWT;

import org.eclipse.sirius.table.metamodel.table.DColumn;
import org.eclipse.sirius.table.metamodel.table.DLine;
import org.eclipse.sirius.table.metamodel.table.DTable;
import org.eclipse.sirius.table.metamodel.table.LineContainer;

/**
 * A Command to sort {@link DLine}s order according to a {@link DColumn}.
 * 
 * @author <a href="mailto:esteban.dugueperoux@obeo.fr">Esteban Dugueperoux</a>
 */
public class SortDLinesCommand extends RecordingCommand {

    private DTable dTable;

    private Comparator<DLine> dLineSorter;

    /**
     * Default constructor.
     * 
     * @param domain
     *            the {@link TransactionalEditingDomain} on which this command
     *            will be executed
     * @param dTable
     *            the {@link DTable}
     * @param sortDirection
     *            the sort Direction
     * @param dColumn
     *            the {@link DColumn}
     */
    public SortDLinesCommand(TransactionalEditingDomain domain, DTable dTable, DColumn dColumn, int sortDirection) {
        super(domain, (sortDirection == SWT.UP ? "Ascending " : "Descending ") + "lines sorting");
        this.dTable = dTable;
        this.dLineSorter = new DLineSorter(dColumn, sortDirection);
    }

    @Override
    protected void doExecute() {
        sortLinesBy(dTable);
    }

    /**
     * Sort the lines of the lineContainer by the alphabetical order of the
     * label of the cells of the column.
     * 
     * @param lineContainer
     *            The lineContainer (table or line)
     * @param column
     *            The column use to sort or null if the header column must be
     *            use to sort
     */
    private void sortLinesBy(final LineContainer lineContainer) {
        if (!lineContainer.getLines().isEmpty()) {
            ECollections.sort(lineContainer.getLines(), dLineSorter);

            // Sort the sub-lines
            for (final DLine line : lineContainer.getLines()) {
                sortLinesBy(line);
            }
        }
    }
}