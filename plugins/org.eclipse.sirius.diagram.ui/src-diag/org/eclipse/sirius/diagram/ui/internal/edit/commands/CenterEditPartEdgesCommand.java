/*******************************************************************************
 * Copyright (c) 2014 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/

package org.eclipse.sirius.diagram.ui.internal.edit.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.sirius.diagram.ui.internal.operation.CenterEdgeEndModelChangeOperation;

import com.google.common.collect.Iterables;

/**
 * A command that centers (if needed) the incoming and outgoing edges of the
 * given graphicalEditPart.
 * 
 * @author Florian Barbin
 *
 */
public class CenterEditPartEdgesCommand extends AbstractTransactionalCommand {

    private IGraphicalEditPart editPart;

    private ChangeBoundsRequest request;

    /**
     * Constructor.
     * 
     * @param graphicalEditPart
     *            the edit part for which we need to keep center edges (if they
     *            should be)
     * @param request
     *            the {@link ChangeBoundsRequest} if this command is executed
     *            because of this kind of request. Can be null.
     */
    public CenterEditPartEdgesCommand(IGraphicalEditPart graphicalEditPart, ChangeBoundsRequest request) {
        super(graphicalEditPart.getEditingDomain(), "Center Edges", null);
        editPart = graphicalEditPart;
        this.request = request;

    }

    @Override
    protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

        List<Object> edges = new ArrayList<Object>();
        edges.addAll(editPart.getSourceConnections());
        edges.addAll(editPart.getTargetConnections());
        for (ConnectionEditPart connection : Iterables.filter(edges, ConnectionEditPart.class)) {
            Object model = connection.getModel();
            if (model instanceof Edge) {

                CenterEdgeEndModelChangeOperation centerEdgeEndModelChangeOperation = new CenterEdgeEndModelChangeOperation((Edge) model, false);

                // if the changeBoundsRequest is available, it is used to
                // retrieve the real figure size by taking in account the new
                // bounds (more reliable than computing the size from GMF)
                if (request != null) {
                    Dimension newSourceSize = getNewSize(connection.getSource());
                    Dimension newTargetSize = getNewSize(connection.getTarget());
                    centerEdgeEndModelChangeOperation.setSourceAndTargetSize(newSourceSize, newTargetSize);
                }
                centerEdgeEndModelChangeOperation.execute();
            }

        }

        return CommandResult.newOKCommandResult();
    }

    private Dimension getNewSize(EditPart edgeEnd) {
        if (edgeEnd instanceof GraphicalEditPart) {
            IFigure figure = ((GraphicalEditPart) edgeEnd).getFigure();
            Dimension newSize = figure.getSize().getCopy();
            List editPartRequesting = request.getEditParts();
            if (editPartRequesting.contains(edgeEnd)) {
                Rectangle newBounds = request.getTransformedRectangle(figure.getBounds());
                newSize = newBounds.getSize();
            }
            return newSize;
        }
        return null;
    }

    @Override
    public void dispose() {
        editPart = null;
        request = null;
        super.dispose();
    }

}
