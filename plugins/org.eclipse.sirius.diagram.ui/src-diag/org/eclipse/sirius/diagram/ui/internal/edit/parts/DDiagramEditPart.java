/*******************************************************************************
 * Copyright (c) 2007, 2015 THALES GLOBAL SERVICES and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.diagram.ui.internal.edit.parts;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.tools.AbstractTool;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.INodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.description.tool.EdgeCreationDescription;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractDDiagramEditPart;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.AirXYLayoutEditPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.ContainerCreationEditPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.LaunchBehaviorToolEditPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.LaunchToolEditPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.RefreshSiriusElementEditPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.ResetOriginEditPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.RevealElementsEditPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.RevealSelectedElementsEditPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.SiriusContainerDropPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.SiriusPropertyHandlerEditPolicy;
import org.eclipse.sirius.diagram.ui.internal.edit.policies.DDiagramItemSemanticEditPolicy;
import org.eclipse.sirius.diagram.ui.provider.Messages;
import org.eclipse.sirius.diagram.ui.tools.api.command.GMFCommandWrapper;
import org.eclipse.sirius.diagram.ui.tools.api.policy.CompoundEditPolicy;
import org.eclipse.sirius.diagram.ui.tools.api.requests.RequestConstants;
import org.eclipse.sirius.diagram.ui.tools.internal.ruler.SiriusSnapToHelperUtil;
import org.eclipse.sirius.tools.api.command.SiriusCommand;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.swt.SWT;

/**
 * @was-generated
 */
public class DDiagramEditPart extends AbstractDDiagramEditPart {
    /**
     * Constant used to store a {@link EdgeCreationDescription} in a
     * Request.getExtendedData().
     */
    protected static final String GMF_EDGE_CREATION_DESCRIPTION = "edge.creation.description"; //$NON-NLS-1$

    private static final String CREATE_MAGIC_RELATIONSHIP_ID = "CreateMagicRelationship"; //$NON-NLS-1$

    /**
     * @was-generated
     */
    public final static String MODEL_ID = "Sirius"; //$NON-NLS-1$

    /**
     * @was-generated
     */
    public static final int VISUAL_ID = 1000;

    @Override
    protected void refreshBackgroundColor() {
        super.refreshBackgroundColor();
    };

    /**
     * @was-generated
     */
    public DDiagramEditPart(final View view) {
        super(view);
    }

    /**
     * @not-generated add the compound edit policy
     */
    @Override
    protected void createDefaultEditPolicies() {
        super.createDefaultEditPolicies();
        installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE, new DDiagramItemSemanticEditPolicy());
        installEditPolicy(EditPolicy.CONTAINER_ROLE, new ContainerCreationEditPolicy());
        installEditPolicy(RequestConstants.REQ_LAUNCH_TOOL, new LaunchToolEditPolicy());
        final CompoundEditPolicy compoundEditPolicy = new CompoundEditPolicy();
        compoundEditPolicy.addEditPolicy(new RefreshSiriusElementEditPolicy());
        compoundEditPolicy.addEditPolicy(new RevealElementsEditPolicy());
        compoundEditPolicy.addEditPolicy(new RevealSelectedElementsEditPolicy());
        compoundEditPolicy.addEditPolicy(new LaunchBehaviorToolEditPolicy());
        compoundEditPolicy.addEditPolicy(new ResetOriginEditPolicy());
        if (this.getEditPolicy(EditPolicy.COMPONENT_ROLE) != null) {
            compoundEditPolicy.addEditPolicy(this.getEditPolicy(EditPolicy.COMPONENT_ROLE));
            removeEditPolicy(EditPolicy.COMPONENT_ROLE);
        }
        installEditPolicy(EditPolicy.COMPONENT_ROLE, compoundEditPolicy);
        removeEditPolicy(EditPolicy.LAYOUT_ROLE);
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new AirXYLayoutEditPolicy());
        removeEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE);
        installEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE, new SiriusContainerDropPolicy());

        // Enables Font and Style action
        removeEditPolicy(EditPolicyRoles.PROPERTY_HANDLER_ROLE);
        installEditPolicy(EditPolicyRoles.PROPERTY_HANDLER_ROLE, new SiriusPropertyHandlerEditPolicy());
    }

    @Override
    public Command getCommand(Request request) {
        if (isMagicConnectorRequest(request)) {
            INodeEditPart sourceEditPart = (INodeEditPart) ((CreateConnectionRequest) request).getSourceEditPart();
            final DNodeContainerEditPart sourceNode;
            if (sourceEditPart instanceof DNodeContainerEditPart) {
                DNodeContainerEditPart diagramEP = (DNodeContainerEditPart) sourceEditPart;
                sourceNode = diagramEP.getDiagramNode();
            } else if (sourceEditPart instanceof DNodeEditPart) {
                DNodeEditPart dnodeEP = (DNodeEditPart) sourceEditPart;
                DNode dnode = (DNode) ((Node) dnodeEP.getModel()).getElement();
                if (dnode.getTarget() instanceof DSemanticDecorator) {
                    sourceNode = (DiagramNode) dnode.getTarget();
                } else {
                    // non-archimate
                    return null;
                }
            } else {
                // should not happen
                return null;
            }

            TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(sourceNode.getModel());
            final Point point = ((CreateConnectionRequest) request).getLocation();

            CompoundCommand result = new CompoundCommand();
            org.eclipse.emf.common.command.Command emfCommand = new AbstractCommand() {

                @Override
                public void redo() {
                    // not needed
                }

                @Override
                public void execute() {
                    boolean elementsFirst = true;
                    elementsFirst ^= isModKeyPressed();
                    new MagicConnectorMenu(DDiagramEditPart.this, sourceNode, point, elementsFirst);
                }

                @Override
                public boolean canExecute() {
                    return true;
                }
            };
            result.add(new ICommandProxy(new GMFCommandWrapper(domain, emfCommand)));
            return result;
        }
        return super.getCommand(request);
    }
    private boolean isMagicConnectorRequest(Request request) {
        return request instanceof CreateConnectionRequest
                && (((CreateConnectionRequest)request).getSourceEditPart() instanceof DNodeContainerEditPart
                        || ((CreateConnectionRequest)request).getSourceEditPart() instanceof DNodeEditPart)
                && request.getExtendedData()
                        .get(GMF_EDGE_CREATION_DESCRIPTION) instanceof EdgeCreationDescription
                && ((EdgeCreationDescription)request.getExtendedData().get(GMF_EDGE_CREATION_DESCRIPTION))
                        .getName().equals(CREATE_MAGIC_RELATIONSHIP_ID);
    }

    private boolean isModKeyPressed() {
        boolean res = false;
        if (getEditDomain().getActiveTool() instanceof ConnectionCreationTool) {
            ConnectionCreationTool tool = (ConnectionCreationTool)getEditDomain().getActiveTool();
            // TODO fix when the getCurrentInput will be accessible cf
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=494732
            try {
                Method method = AbstractTool.class.getDeclaredMethod("getCurrentInput"); //$NON-NLS-1$
                method.setAccessible(true);
                Object result = method.invoke(tool);
                if (result instanceof AbstractTool.Input) {
                    res = ((AbstractTool.Input)result).isModKeyDown(SWT.MOD1);
                }
            } catch (Throwable e) {
                // fail silently
            }
        }
        return res;
    }
    @Override
    public void deactivate() {
        deactivateLayoutingMode();
        super.deactivate();
    }

    /**
     * Deactivates the Layouting Mode of the {@link DDiagram} (if needed).
     */
    private void deactivateLayoutingMode() {
        if (getModel() instanceof Diagram) {
            Diagram diagramGMF = (Diagram) getModel();
            try {
                if (diagramGMF.getElement() instanceof DDiagram) {

                    DDiagram dDiagram = (DDiagram) diagramGMF.getElement();
                    if (dDiagram.isIsInLayoutingMode()) {
                        getEditingDomain().getCommandStack().execute(new SetLayoutingModeCommand(getEditingDomain(), dDiagram, false));
                    }
                }
            } catch (IllegalStateException e) {
                // If the DDiagram associated to this GMF Diagram is not
                // accessible any more, we do not modify layouting mode
            }
        }
    }

    /**
     * A command that changes the LayoutingMode of the given {@link DDiagram}.
     *
     * @author <a href="mailto:alex.lagarde@obeo.fr">Alex Lagarde</a>
     *
     */
    public static class SetLayoutingModeCommand extends SiriusCommand {

        /**
         * The {@link DDiagram} on witch the layouting mode should be switched.
         */
        private DDiagram diagram;

        /**
         * Indicates whether the Layouting Mode should be enabled.
         */
        private boolean layoutingModeShouldBeEnabled;

        /**
         * Constructor.
         *
         * @param editingDomain
         *            the editing domain
         * @param diagram
         *            the {@link DDiagram} on witch the layouting mode should be
         *            switched
         * @param layoutingModeShouldBeEnabled
         *            indicates whether the layouting mode should be enabled or
         *            disabled
         */
        public SetLayoutingModeCommand(TransactionalEditingDomain editingDomain, DDiagram diagram, boolean layoutingModeShouldBeEnabled) {
            super(editingDomain, Messages.SetLayoutingModeCommand_deactivateLabel);
            this.diagram = diagram;
            this.layoutingModeShouldBeEnabled = layoutingModeShouldBeEnabled;
            if (layoutingModeShouldBeEnabled) {
                setLabel(Messages.SetLayoutingModeCommand_activateLabel);
            }
        }

        /**
         *
         * {@inheritDoc}
         *
         * @see org.eclipse.sirius.tools.api.command.SiriusCommand#doExecute()
         */
        @Override
        protected void doExecute() {
            this.diagram.setIsInLayoutingMode(layoutingModeShouldBeEnabled);
        }

    }

    @Override
    public Object getAdapter(Class key) {
        if (key == SnapToHelper.class) {
            return SiriusSnapToHelperUtil.getSnapHelper(this);
        }
        return super.getAdapter(key);
    }
}
