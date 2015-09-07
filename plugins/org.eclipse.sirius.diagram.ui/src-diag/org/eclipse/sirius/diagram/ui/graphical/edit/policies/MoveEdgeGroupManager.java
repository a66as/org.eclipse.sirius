/*******************************************************************************
 * Copyright (c) 2015 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.diagram.ui.graphical.edit.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.SetBoundsCommand;
import org.eclipse.gmf.runtime.diagram.ui.figures.IBorderItemLocator;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.LayoutConstraint;
import org.eclipse.gmf.runtime.notation.Location;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractDiagramBorderNodeEditPart;

/**
 * This class is responsible for managing the move edge group tool. see #471104
 * relevant ticket or specification for more details.
 * 
 * @author Florian Barbin
 *
 */
public class MoveEdgeGroupManager {

    /**
     * Key for extended EDGE_GROUP_MOVE meta-data.
     */
    public static final String EDGE_GROUP_MOVE_KEY = "EDGE_GROUP_MOVE"; //$NON-NLS-1$

    /**
     * Key for extended EDGE_MOVE_DELTA meta-data.
     */
    public static final String EDGE_MOVE_DELTA = "EDGE_MOVE_DELTA"; //$NON-NLS-1$

    /**
     * Key for extended EDGE_GROUP_MOVE_HAS_BEEN_ACTIVATED_KEY meta-data. This
     * key is used to indicate that the tool has been activated at least one
     * time during the edge moving. It's useful to know if it's necessary to
     * erase source and target feedbacks.
     */
    public static final String EDGE_GROUP_MOVE_HAS_BEEN_ACTIVATED_KEY = "EDGE_GROUP_MOVE_HAS_BEEN_ACTIVATED_KEY"; //$NON-NLS-1$

    private Request request;

    private int direction = PositionConstants.NONE;

    /**
     * Default constructor.
     * 
     * @param request
     *            the tool request.
     */
    public MoveEdgeGroupManager(Request request) {
        this.request = request;
    }

    /**
     * Returns whether the move edge group tool is activated or not.
     * 
     * @return true if the tool is activate, otherwise false.
     */
    public boolean isToolActivated() {
        Object value = request.getExtendedData().get(EDGE_GROUP_MOVE_KEY);
        return value != null && Boolean.TRUE.equals(value);
    }

    /**
     * Shows the group feedback.
     */
    public void showGroupFeedback() {
        if (accept()) {
            ConnectionEditPart connectionEditPart = ((BendpointRequest) request).getSource();
            AbstractDiagramBorderNodeEditPart sourceEditPart = (AbstractDiagramBorderNodeEditPart) connectionEditPart.getSource();
            AbstractDiagramBorderNodeEditPart targetEditPart = (AbstractDiagramBorderNodeEditPart) connectionEditPart.getTarget();
            ChangeBoundsRequest sourceChangeBoundsRequest = createChangeBoundsRequest(sourceEditPart);
            ChangeBoundsRequest targetChangeBoundsRequest = createChangeBoundsRequest(targetEditPart);
            sourceEditPart.showSourceFeedback(sourceChangeBoundsRequest);
            targetEditPart.showSourceFeedback(targetChangeBoundsRequest);
        }

    }

    /**
     * Erases the group feedback if this action has been activated.
     */
    public void eraseGroupFeedback() {
        if (request instanceof BendpointRequest) {
            if (isToolActivatedOrHasBeenActivated()) {
                ConnectionEditPart connectionEditPart = ((BendpointRequest) request).getSource();
                EditPart sourceEditPart = connectionEditPart.getSource();
                EditPart targetEditPart = connectionEditPart.getTarget();

                if (sourceEditPart instanceof AbstractDiagramBorderNodeEditPart && targetEditPart instanceof AbstractDiagramBorderNodeEditPart) {
                    AbstractDiagramBorderNodeEditPart sourceBorderNodeEditPart = (AbstractDiagramBorderNodeEditPart) sourceEditPart;
                    AbstractDiagramBorderNodeEditPart targetBorderNodeEditPart = (AbstractDiagramBorderNodeEditPart) targetEditPart;

                    ChangeBoundsRequest changeBoundsRequest = new ChangeBoundsRequest(RequestConstants.REQ_DROP);
                    sourceBorderNodeEditPart.eraseSourceFeedback(changeBoundsRequest);
                    targetBorderNodeEditPart.eraseSourceFeedback(changeBoundsRequest);

                }
            }
        }

    }

    /**
     * Provides the command that will perform the edge move group.
     * 
     * @return the command if the request is authorized, null otherwise.
     */
    public Command getCommand() {
        if (accept()) {
            ConnectionEditPart connectionEditPart = ((BendpointRequest) request).getSource();
            EditPart sourceEditPart = connectionEditPart.getSource();
            EditPart targetEditPart = connectionEditPart.getTarget();
            Command srcCommand = createCommand((AbstractDiagramBorderNodeEditPart) sourceEditPart);
            if (srcCommand != null) {
                Command tgtCommand = createCommand((AbstractDiagramBorderNodeEditPart) targetEditPart);
                if (tgtCommand != null) {
                    srcCommand = srcCommand.chain(tgtCommand);
                    return srcCommand;
                }
            }
        }
        return null;
    }

    /**
     * Returns whether the move edge group tool is activated or has been
     * activated during the drag.
     * 
     * @return true if the tool is activate or has been activated, otherwise
     *         false.
     */
    private boolean isToolActivatedOrHasBeenActivated() {
        Object value = request.getExtendedData().get(EDGE_GROUP_MOVE_HAS_BEEN_ACTIVATED_KEY);
        return value != null && Boolean.TRUE.equals(value);
    }

    /**
     * Determines if the given request is a valid request for this tool.<br/>
     * The edge should respect the following rules:
     * <ul>
     * <li>a border node as source</li>
     * <li>a border node as target</li>
     * <li>source node has only one connection: the moved edge.</li>
     * <li>target node has only one connection: the moved edge.</li>
     * <li>Both border nodes are on the same axe (Horizontal or Vertical)</li>
     * </ul>
     * 
     * @return true if the move edge and border nodes can be activated,
     *         otherwise false.
     */
    private boolean accept() {
        if (request instanceof BendpointRequest) {
            ConnectionEditPart connectionEditPart = ((BendpointRequest) request).getSource();
            EditPart sourceEditPart = connectionEditPart.getSource();
            EditPart targetEditPart = connectionEditPart.getTarget();

            if (sourceEditPart instanceof AbstractDiagramBorderNodeEditPart && targetEditPart instanceof AbstractDiagramBorderNodeEditPart) {
                if (getAllConnections((AbstractDiagramBorderNodeEditPart) sourceEditPart).size() == 1 && getAllConnections((AbstractDiagramBorderNodeEditPart) targetEditPart).size() == 1) {
                    int sourceDirection = getBorderNodeDirection((AbstractDiagramBorderNodeEditPart) sourceEditPart);
                    int targetDirection = getBorderNodeDirection((AbstractDiagramBorderNodeEditPart) targetEditPart);
                    if (sourceDirection == targetDirection) {
                        direction = sourceDirection;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Command createCommand(AbstractDiagramBorderNodeEditPart editPart) {
        Object model = editPart.getModel();
        if (model instanceof Node) {
            LayoutConstraint layoutConstraint = ((Node) model).getLayoutConstraint();
            if (layoutConstraint instanceof Location) {
                PrecisionPoint delta = getDeltaFromRequest();
                double scale = getZoomScale(editPart);
                delta = delta.setPreciseLocation(Math.round(delta.x() / scale), Math.round(delta.y() / scale));
                if (!conflictDetected(editPart, delta)) {
                    Point location = new Point(((Location) layoutConstraint).getX(), ((Location) layoutConstraint).getY());
                    Point newLocation = location.translate(delta);
                    final ICommand moveCommand = new SetBoundsCommand(editPart.getEditingDomain(), DiagramUIMessages.Commands_MoveElement, new EObjectAdapter((View) editPart.getModel()), newLocation);
                    return new ICommandProxy(moveCommand);
                }
            }
        }
        return null;
    }

    private boolean conflictDetected(AbstractDiagramBorderNodeEditPart editPart, Point moveDelta) {
        BorderNodeCollapseManager borderNodeCollapseManager = new BorderNodeCollapseManager();

        IBorderItemLocator locator = editPart.getBorderItemLocator();
        IFigure figure = editPart.getFigure();
        Rectangle bounds = figure.getBounds().getCopy();
        bounds.translate(moveDelta);

        Rectangle newBounds = borderNodeCollapseManager.expandCollapsedNodeBounds(editPart, new PrecisionRectangle(bounds));
        if (newBounds != null) {
            bounds.setBounds(newBounds);
        }

        Rectangle validLocation = locator.getValidLocation(bounds, figure);
        if (borderNodeCollapseManager.hasBeenExpanded()) {
            borderNodeCollapseManager.restoreCollapsedNode(editPart);
        }
        return !validLocation.equals(bounds);
    }

    private double getZoomScale(AbstractDiagramBorderNodeEditPart editPart) {
        ZoomManager zoomManager = editPart.getZoomManager();
        if (zoomManager != null) {
            return zoomManager.getZoom();
        }
        return 1d;
    }

    private ChangeBoundsRequest createChangeBoundsRequest(AbstractDiagramBorderNodeEditPart editPart) {

        ChangeBoundsRequest changeBoundsRequest = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);
        changeBoundsRequest.setEditParts(editPart);
        Point moveDelta = getDeltaFromRequest();

        changeBoundsRequest.setMoveDelta(moveDelta);
        changeBoundsRequest.setLocation(((BendpointRequest) request).getLocation());

        return changeBoundsRequest;
    }

    private PrecisionPoint getDeltaFromRequest() {
        Object delta = request.getExtendedData().get(EDGE_MOVE_DELTA);
        Point moveDelta = new Point();
        if (delta != null) {
            if (delta instanceof Dimension) {
                moveDelta.setX(((Dimension) delta).width());
                moveDelta.setY(((Dimension) delta).height());
            }

            if (direction == PositionConstants.VERTICAL) {
                moveDelta.setX(0);
            } else {
                moveDelta.setY(0);
            }
        }
        return new PrecisionPoint(moveDelta);
    }

    @SuppressWarnings("unchecked")
    private List<Object> getAllConnections(AbstractDiagramBorderNodeEditPart borderNodeEditPart) {
        List<Object> connections = new ArrayList<Object>();
        connections.addAll(borderNodeEditPart.getSourceConnections());
        connections.addAll(borderNodeEditPart.getTargetConnections());
        return connections;
    }

    /**
     * Provides the border node move direction. If the border node is located on
     * the EAST or WEST parent side, the move direction is VERTICAL. If the
     * border node is located on the NORTH or the SOUTH parent side, the move
     * direction is HORIZONTAL.
     * 
     * @param borderNodeEditPart
     *            the border node edit part.
     * @return {@link PositionConstants#VERTICAL} or
     *         {@link PositionConstants#HORIZONTAL} following the border node
     *         location on parent.
     */
    private int getBorderNodeDirection(AbstractDiagramBorderNodeEditPart borderNodeEditPart) {
        IBorderItemLocator borderItemLocator = borderNodeEditPart.getBorderItemLocator();
        int side = borderItemLocator.getCurrentSideOfParent();
        int localDirection = PositionConstants.NONE;
        switch (side) {
        case PositionConstants.EAST:
            localDirection = PositionConstants.VERTICAL;
            break;
        case PositionConstants.WEST:
            localDirection = PositionConstants.VERTICAL;
            break;
        case PositionConstants.NORTH:
            localDirection = PositionConstants.HORIZONTAL;
            break;
        case PositionConstants.SOUTH:
            localDirection = PositionConstants.HORIZONTAL;
            break;
        default:
            break;
        }
        return localDirection;
    }
}