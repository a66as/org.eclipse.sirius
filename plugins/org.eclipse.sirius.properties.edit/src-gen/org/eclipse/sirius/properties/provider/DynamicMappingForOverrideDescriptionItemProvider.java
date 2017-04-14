/**
 * Copyright (c) 2016 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *
 */
package org.eclipse.sirius.properties.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.sirius.properties.DynamicMappingForOverrideDescription;
import org.eclipse.sirius.properties.PropertiesPackage;

/**
 * This is the item provider adapter for a {@link org.eclipse.sirius.properties.DynamicMappingForOverrideDescription}
 * object. <!-- begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class DynamicMappingForOverrideDescriptionItemProvider extends AbstractDynamicMappingForDescriptionItemProvider {
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public DynamicMappingForOverrideDescriptionItemProvider(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addOverridesPropertyDescriptor(object);
            addFilterIfsFromOverriddenDynamicMappingForExpressionPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Overrides feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addOverridesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
                getString("_UI_DynamicMappingForOverrideDescription_overrides_feature"), //$NON-NLS-1$
                getString("_UI_PropertyDescriptor_description", "_UI_DynamicMappingForOverrideDescription_overrides_feature", "_UI_DynamicMappingForOverrideDescription_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                PropertiesPackage.Literals.DYNAMIC_MAPPING_FOR_OVERRIDE_DESCRIPTION__OVERRIDES, true, false, true, null, null, null));
    }

    /**
     * This adds a property descriptor for the Filter Ifs From Overridden Dynamic Mapping For Expression feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addFilterIfsFromOverriddenDynamicMappingForExpressionPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
                getString("_UI_DynamicMappingForOverrideDescription_filterIfsFromOverriddenDynamicMappingForExpression_feature"), //$NON-NLS-1$
                getString("_UI_PropertyDescriptor_description", "_UI_DynamicMappingForOverrideDescription_filterIfsFromOverriddenDynamicMappingForExpression_feature", //$NON-NLS-1$//$NON-NLS-2$
                        "_UI_DynamicMappingForOverrideDescription_type"), //$NON-NLS-1$
                PropertiesPackage.Literals.DYNAMIC_MAPPING_FOR_OVERRIDE_DESCRIPTION__FILTER_IFS_FROM_OVERRIDDEN_DYNAMIC_MAPPING_FOR_EXPRESSION, true, false, false,
                ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This returns DynamicMappingForOverrideDescription.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return overlayImage(object, getResourceLocator().getImage("full/obj16/DynamicMappingForOverrideDescription")); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getText(Object object) {
        return ((StyledString) getStyledText(object)).getString();
    }

    /**
     * This returns the label styled text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getStyledText(Object object) {
        String label = ((DynamicMappingForOverrideDescription) object).getName();
        StyledString styledLabel = new StyledString();
        if (label == null || label.length() == 0) {
            styledLabel.append(getString("_UI_DynamicMappingForOverrideDescription_type"), StyledString.Style.QUALIFIER_STYLER); //$NON-NLS-1$
        } else {
            styledLabel.append(getString("_UI_DynamicMappingForOverrideDescription_type"), StyledString.Style.QUALIFIER_STYLER).append(" " + label); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return styledLabel;
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached children and by creating
     * a viewer notification, which it passes to {@link #fireNotifyChanged}. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     */
    @Override
    public void notifyChanged(Notification notification) {
        updateChildren(notification);

        switch (notification.getFeatureID(DynamicMappingForOverrideDescription.class)) {
        case PropertiesPackage.DYNAMIC_MAPPING_FOR_OVERRIDE_DESCRIPTION__FILTER_IFS_FROM_OVERRIDDEN_DYNAMIC_MAPPING_FOR_EXPRESSION:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
            return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children that can be created
     * under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
        super.collectNewChildDescriptors(newChildDescriptors, object);
    }

}
