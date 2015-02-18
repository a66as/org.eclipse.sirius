/*******************************************************************************
 * Copyright (c) 2009 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.diagram.ui.edit.internal.part;

import java.util.Iterator;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IBorderItemEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.draw2d.ui.figures.IBorderItemLocator;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.LabelPosition;
import org.eclipse.sirius.diagram.NodeStyle;
import org.eclipse.sirius.diagram.WorkspaceImage;
import org.eclipse.sirius.diagram.business.api.query.DDiagramElementQuery;
import org.eclipse.sirius.diagram.ui.edit.api.part.IAbstractDiagramNodeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IDiagramBorderNodeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IDiagramNodeEditPart;
import org.eclipse.sirius.diagram.ui.tools.api.figure.IWorkspaceImageFigure;
import org.eclipse.sirius.diagram.ui.tools.api.figure.SiriusWrapLabel;
import org.eclipse.sirius.diagram.ui.tools.api.figure.WorkspaceImageFigure;
import org.eclipse.sirius.diagram.ui.tools.api.figure.locator.DBorderItemLocator;
import org.eclipse.sirius.diagram.ui.tools.api.graphical.edit.styles.IBorderItemOffsets;
import org.eclipse.sirius.diagram.ui.tools.api.graphical.edit.styles.IStyleConfigurationRegistry;
import org.eclipse.sirius.diagram.ui.tools.api.graphical.edit.styles.StyleConfiguration;
import org.eclipse.sirius.diagram.ui.tools.api.layout.LayoutUtils;
import org.eclipse.sirius.ui.tools.api.color.VisualBindingManager;
import org.eclipse.sirius.viewpoint.LabelStyle;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Class which handles the refresh on visuals.
 * 
 * @author mchauvin
 */
public class AbstractDiagramNodeEditPartRefreshVisualsOperation {

    /**
     * The editPart to refresh.
     */
    protected IAbstractDiagramNodeEditPart editPart;

    /**
     * The node of the edit part to refresh.
     */
    protected DNode node;

    /**
     * Construct a new instance.
     * 
     * @param editPart
     *            the editPart to refresh
     */
    protected AbstractDiagramNodeEditPartRefreshVisualsOperation(final IAbstractDiagramNodeEditPart editPart) {
        this.editPart = editPart;
        final EObject eObj = editPart.resolveSemanticElement();
        if (eObj instanceof DNode) {
            node = (DNode) eObj;
        }
    }

    /**
     * Check if refresh can occur.
     * 
     * @return <code>true</code> if refresh methods could be called safely,
     *         <code>false</code> otherwise
     */
    public boolean canRefresh() {
        return node != null;
    }

    /**
     * Refresh the size.
     */
    public void refreshSize() {

        // Width
        int width = refreshWidth();

        // Height
        int height = computeHeight(width);

        Dimension d = new Dimension(width, height);

        AbstractDiagramNodeEditPartOperation.setChildrenSize(editPart, d);

        if (DiagramElementEditPartOperation.getStyledFigure(editPart.getFigure()) != null) {
            DiagramElementEditPartOperation.getStyledFigure(editPart.getFigure()).setSize(width, height);
        }
        if (getNodePlate() != null) {
            getNodePlate().setSize(width, height);
            getNodePlate().setDefaultSize(width, height);
            getNodePlate().getParent().setSize(width, height);
            getNodePlate().setPreferredSize(width, height);
            getNodePlate().setMinimumSize(d);
            getNodePlate().setMaximumSize(d);
        }
        editPart.getFigure().setSize(d);
        editPart.getFigure().setSize(d);
        editPart.getFigure().setMinimumSize(d);
        editPart.getFigure().setPreferredSize(d);

        final int x = ((Integer) getStructuralFeatureValue(NotationPackage.eINSTANCE.getLocation_X())).intValue();
        final int y = ((Integer) getStructuralFeatureValue(NotationPackage.eINSTANCE.getLocation_Y())).intValue();

        // we should not set the layout on a ViewNode2EditPart
        // or we'll get a classcast exception
        if (!AbstractDiagramNodeEditPartOperation.isBordered(editPart) && !AbstractDiagramNodeEditPartOperation.isBordered(editPart.getParent())) {
            ((GraphicalEditPart) editPart.getParent()).setLayoutConstraint(editPart, editPart.getFigure(), new Rectangle(x, y, width, height));
        } else if (editPart instanceof IBorderItemEditPart) {
            borderRefreshSizeAndLocation(x, y, width, height);
        }
    }

    private void borderRefreshSizeAndLocation(final int x, final int y, final int width, final int height) {
        final IBorderItemEditPart borderPart = (IBorderItemEditPart) editPart;
        final IBorderItemLocator borderItemLocator = borderPart.getBorderItemLocator();
        if (borderItemLocator != null) {
            borderItemLocator.setConstraint(new Rectangle(x, y, width, height));
            if (new DDiagramElementQuery(node).isIndirectlyCollapsed()) {
                if (borderItemLocator instanceof DBorderItemLocator) {
                    ((DBorderItemLocator) borderItemLocator).setBorderItemOffset(IBorderItemOffsets.COLLAPSE_FILTER_OFFSET);
                }
            } else {
                if (borderItemLocator instanceof DBorderItemLocator) {
                    ((DBorderItemLocator) borderItemLocator).setBorderItemOffset(IBorderItemOffsets.DEFAULT_OFFSET);
                }
            }
            ((GraphicalEditPart) editPart.getParent()).setLayoutConstraint(editPart, editPart.getFigure(), borderItemLocator);
        }
    }

    private Dimension getWorkspaceImageFigureSize(final Dimension d) {
        final Iterator<EditPart> it = editPart.getChildren().iterator();
        while (it.hasNext()) {
            final EditPart child = it.next();
            if (child instanceof ShapeNodeEditPart && ((ShapeNodeEditPart) child).getContentPane() instanceof IWorkspaceImageFigure) {
                ((ShapeNodeEditPart) child).getContentPane().setSize(d);
                return ((ShapeNodeEditPart) child).getContentPane().getSize();
            }
        }
        return d;
    }

    private int refreshWidth() {
        int width = LayoutUtils.DEFAULT_WIDTH;

        // Width
        width = 1;
        if (node.getWidth() != null) {
            width = node.getWidth().intValue();
        }
        if (width == 0) {
            width = 1;
        }

        // Workspace image figure ? real image size ?
        if (width == -1 && node.getOwnedStyle() instanceof WorkspaceImage) {
            WorkspaceImage workspaceImage = (WorkspaceImage) node.getStyle();
            final String path = workspaceImage.getWorkspacePath();
            final Image image = WorkspaceImageFigure.getImageInstanceFromPath(path);
            width = image.getBounds().width;
        } else {
            width = width * LayoutUtils.SCALE;
        }

        final int tmpWidth = ((Integer) getStructuralFeatureValue(NotationPackage.eINSTANCE.getSize_Width())).intValue();
        if (tmpWidth > 0) {
            width = tmpWidth;
        }

        // style
        if (node.getStyle() != null && ((NodeStyle) node.getStyle()).getLabelPosition().getValue() == LabelPosition.NODE) {
            // The label is in the graphical element. We must adapt the
            // size.
            final SiriusWrapLabel nodeLabel = getNodeLabel();
            if (nodeLabel != null) {
                final StyleConfiguration styleConfiguration = IStyleConfigurationRegistry.INSTANCE.getStyleConfiguration(node.getDiagramElementMapping(), node.getStyle());
                width = styleConfiguration.adaptViewNodeSizeWithLabel(node, nodeLabel, width);
            }
        }
        return width;
    }

    private SiriusWrapLabel getNodeLabel() {
        SiriusWrapLabel wrapLabel = null;
        if (editPart instanceof IDiagramNodeEditPart) {
            wrapLabel = ((IDiagramNodeEditPart) editPart).getNodeLabel();
        } else if (editPart instanceof IDiagramBorderNodeEditPart) {
            wrapLabel = ((IDiagramBorderNodeEditPart) editPart).getNodeLabel();
        }
        return wrapLabel;
    }

    private int computeHeight(final int width) {
        int height = 0;

        // Height
        if (node.getHeight() != null) {
            height = node.getHeight().intValue() * LayoutUtils.SCALE;
        }
        if (height == 0) {
            height = width;
        }

        final int tmpHeight = ((Integer) getStructuralFeatureValue(NotationPackage.eINSTANCE.getSize_Height())).intValue();
        if (tmpHeight > 0) {
            height = tmpHeight;
        }

        // workspace image ? keep ratio
        if (node.getStyle() instanceof WorkspaceImage) {
            height = getWorkspaceImageFigureSize(new Dimension(width, height)).height;
        }

        return height;
    }

    /**
     * Refresh the font.
     */
    public void refreshFont() {
        if (node != null) {
            if (node.getOwnedStyle() != null) {
                final LabelStyle style = node.getOwnedStyle();
                final Font newFont = VisualBindingManager.getDefault().getFontFromValue(style.getLabelSize());
                // if (editPart.getPrimaryChildEditPart() instanceof
                // DNodeNameEditPart) {
                // (((DNodeNameEditPart)
                // editPart.getPrimaryChildEditPart()).getFigure()).setFont(newFont);
                // }
            }
        }
    }

    /**
     * Refresh the color.
     */
    public void refreshColor() {
        // nothing to do;
    }

    /**
     * Convenience method to retreive the value for the supplied value from the
     * editpart's associated view element. Same as calling
     * <code> ViewUtil.getStructuralFeatureValue(getNotationView(),feature)</code>
     * .
     * 
     * @param feature
     *            the feature
     * @return the value
     */
    private Object getStructuralFeatureValue(final EStructuralFeature feature) {
        if (editPart.getNotationView() != null) {
            return ViewUtil.getPropertyValue((View) editPart.getModel(), feature, feature.getEContainingClass());
        } else {
            return null;
        }
    }

    /**
     * Return the node plate of the edit part.
     * 
     * @return the node plate of the edit part.
     */
    protected DefaultSizeNodeFigure getNodePlate() {
        return AbstractDiagramNodeEditPartOperation.getNodePlate(editPart);
    }

}
