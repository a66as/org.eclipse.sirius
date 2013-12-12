/*******************************************************************************
 * Copyright (c) 2007, 2008 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.business.internal.color;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

import org.eclipse.sirius.ext.base.Option;
import org.eclipse.sirius.business.api.color.AbstractColorUpdater;
import org.eclipse.sirius.viewpoint.BasicLabelStyle;
import org.eclipse.sirius.viewpoint.Customizable;
import org.eclipse.sirius.viewpoint.RGBValues;
import org.eclipse.sirius.viewpoint.ViewpointPackage;
import org.eclipse.sirius.viewpoint.Style;
import org.eclipse.sirius.viewpoint.description.ColorDescription;
import org.eclipse.sirius.viewpoint.description.style.BasicLabelStyleDescription;
import org.eclipse.sirius.viewpoint.description.style.StyleDescription;
import org.eclipse.sirius.viewpoint.description.style.StylePackage;

/**
 * Class responsible to reflectively update a style color using the given
 * mapping between style features and style description features.
 * 
 * @author cbrun
 * 
 */
public class DiagramStyleColorUpdater extends AbstractColorUpdater {

    /**
     * A mapping between style description color features and style color
     * features.
     */
    private BiMap<EReference, EReference> descToStyleForColorFeatures;

    /**
     * Create a new {@link DiagramStyleColorUpdater} .
     * 
     */
    public DiagramStyleColorUpdater() {
        descToStyleForColorFeatures = HashBiMap.create();
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getBorderedStyleDescription_BorderColor(), ViewpointPackage.eINSTANCE.getBorderedStyle_BorderColor());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getBasicLabelStyleDescription_LabelColor(), ViewpointPackage.eINSTANCE.getBasicLabelStyle_LabelColor());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getBundledImageDescription_Color(), ViewpointPackage.eINSTANCE.getBundledImage_Color());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getDotDescription_BackgroundColor(), ViewpointPackage.eINSTANCE.getDot_BackgroundColor());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getEdgeStyleDescription_StrokeColor(), ViewpointPackage.eINSTANCE.getEdgeStyle_StrokeColor());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getEllipseNodeDescription_Color(), ViewpointPackage.eINSTANCE.getEllipse_Color());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getFlatContainerStyleDescription_BackgroundColor(), ViewpointPackage.eINSTANCE.getFlatContainerStyle_BackgroundColor());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getFlatContainerStyleDescription_ForegroundColor(), ViewpointPackage.eINSTANCE.getFlatContainerStyle_ForegroundColor());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getGaugeSectionDescription_BackgroundColor(), ViewpointPackage.eINSTANCE.getGaugeSection_BackgroundColor());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getGaugeSectionDescription_ForegroundColor(), ViewpointPackage.eINSTANCE.getGaugeSection_ForegroundColor());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getLozengeNodeDescription_Color(), ViewpointPackage.eINSTANCE.getLozenge_Color());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getNoteDescription_Color(), ViewpointPackage.eINSTANCE.getNote_Color());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getSquareDescription_Color(), ViewpointPackage.eINSTANCE.getSquare_Color());
        descToStyleForColorFeatures.put(StylePackage.eINSTANCE.getShapeContainerStyleDescription_BackgroundColor(), ViewpointPackage.eINSTANCE.getShapeContainerStyle_BackgroundColor());
    }

    /**
     * Update the color features of the given style (if necessary) using the
     * style description.
     * 
     * @param context
     *            the current EObject, might be used if it's necessary to
     *            compute expressions.
     * @param style
     *            the style to update.
     * @param description
     *            the style description.
     * @param previousStyle
     *            the previous style (if existing) to keep compatible
     *            customization.
     */
    public void updateColors(final EObject context, final Customizable style, final EObject description, Option<? extends Customizable> previousStyle) {
        updateColorReflectively(context, style, description, previousStyle);
    }

    private void updateColorReflectively(final EObject context, final Customizable style, final EObject description, Option<? extends Customizable> previousStyle) {
        List<EReference> eAllReferences = getAllStyleReferences(style);

        for (final EReference feature : eAllReferences) {
            if (descToStyleForColorFeatures.containsValue(feature) && description != null) {
                if (previousStyle.some() && previousStyle.get().getCustomFeatures().contains(feature.getName())) {
                    EStructuralFeature eStructuralFeature = previousStyle.get().eClass().getEStructuralFeature(feature.getName());
                    // If we don't found a matching feature, we ignore the
                    // customization (probably a not compatible
                    // customization).
                    if (eStructuralFeature != null && eStructuralFeature.getEType() == feature.getEType()) {
                        Object customValue = previousStyle.get().eGet(eStructuralFeature);
                        style.eSet(feature, customValue);
                        style.getCustomFeatures().add(feature.getName());
                    }
                }
                if (!style.getCustomFeatures().contains(feature.getName())) {
                    updateNotCustomFeatureReflectively(context, style, description, feature);
                }
            }
        }
    }

    private void updateNotCustomFeatureReflectively(final EObject context, final Customizable style, final EObject description, final EReference feature) {
        // The feature is a color.
        final Object originalValue = style.eGet(feature);
        if (originalValue instanceof RGBValues || originalValue == null) {
            final Object descValue = description.eGet(descToStyleForColorFeatures.inverse().get(feature));
            if (descValue instanceof ColorDescription) {
                final RGBValues newValues = getRGBValuesFromColorDescription(context, (ColorDescription) descValue);
                if (!AbstractColorUpdater.areEquals((RGBValues) originalValue, newValues)) {
                    style.eSet(feature, newValues);
                }
            }
        }
    }

    /**
     * @param style
     * @param eAllReferences
     */
    private List<EReference> getAllStyleReferences(final EObject style) {
        List<EReference> eAllReferences = Lists.newArrayList();
        EList<EReference> styleReferences = style.eClass().getEAllReferences();
        eAllReferences.addAll(styleReferences);
        return eAllReferences;
    }

    /**
     * Set all the default color values to a given style instance.
     * 
     * @param style
     *            the style to set.
     */
    public void setDefaultValues(final Style style) {
        List<EReference> eAllReferences = getAllStyleReferences(style);
        for (final EReference feature : eAllReferences) {
            if (descToStyleForColorFeatures.containsValue(feature)) {
                //
                // The feature is a color.
                final Object originalValue = style.eGet(feature);
                if (originalValue == null) {
                    style.eSet(feature, createDefaultRGBValue());
                }
            }
        }
    }

    /**
     * Update the colors of a style instance using the specified description.
     * 
     * @param style
     *            the style instance to update.
     * @param description
     *            the style description to use.
     * @param previousStyle
     *            the previous style (if existing) to keep compatible
     *            customization.
     */
    public void updateColors(final Style style, final StyleDescription description, Option<? extends Customizable> previousStyle) {
        updateColorReflectively(null, style, description, previousStyle);
    }

    /**
     * Update the colors of a style instance using the specified description.
     * 
     * @param style
     *            the style instance to update.
     * @param description
     *            the style description to use.
     * @param previousStyle
     *            the previous style (if existing) to keep compatible
     *            customization.
     */
    public void updateColors(final BasicLabelStyle style, final BasicLabelStyleDescription description, Option<? extends Customizable> previousStyle) {
        updateColorReflectively(null, style, description, previousStyle);
    }
}
