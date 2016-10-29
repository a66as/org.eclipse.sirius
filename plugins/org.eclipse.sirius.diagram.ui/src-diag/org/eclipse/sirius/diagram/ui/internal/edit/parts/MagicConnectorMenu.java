package org.eclipse.sirius.diagram.ui.internal.edit.parts;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.DiagramUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.sirius.diagram.ui.business.api.view.SiriusLayoutDataManager;
import org.eclipse.sirius.diagram.ui.business.internal.view.RootLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;

public class MagicConnectorMenu {

    private Diagram diagram;

    private DiagramNode source;

    private ArchimateElement sourceElement;

    private Point point;

    private EditPart editPart;

    public MagicConnectorMenu(EditPart editPart, DiagramNode source, Point point, boolean elementsFirst) {
        super();
        this.editPart = editPart;
        this.source = source;
        this.point = point;
        this.diagram = DiagramUtil.getDiagram(source);
        this.sourceElement = DiagramNodeUtil.getModel(source, ArchimateElement.class);
        Menu menu = new Menu(editPart.getViewer().getControl());
        if (elementsFirst) {
            addElementActions(menu);
        } else {
            addConnectionActions(menu);
        }
        menu.setVisible(true);

        // Modal menu
        Display display = menu.getDisplay();
        while (!menu.isDisposed() && menu.isVisible()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        if (!menu.isDisposed()) {
            menu.dispose();
        }
    }

    /**
     * Add Connection Actions going in both directions. Entry point #1
     * 
     * @param result
     */
    private void addConnectionActions(Menu menu) {
        for (EClass type : ArchiUtil.getValidRelationshipsFrom(sourceElement.eClass())) {
            if (ViewpointsManager.INSTANCE.isAllowedType(diagram, type)) {
                addConnectionAction(menu, type, false);
            }
        }
    }

    /**
     * Add a Connection Action with a relationship type
     * 
     * @param result
     */
    private void addConnectionAction(Menu menu, final EClass relationshipType, boolean reverseDirection) {
        final MenuItem item = new MenuItem(menu, SWT.CASCADE);
        item.setText(ArchiLabelProvider.INSTANCE.getRelationshipPhrase(relationshipType, reverseDirection));
        item.setImage(ArchiLabelProvider.INSTANCE.getImage(relationshipType));

        Menu subMenu = new Menu(item);
        item.setMenu(subMenu);
        addConnectionActions(subMenu, ArchiUtil.getClasses(FolderType.BUSINESS), relationshipType);
        addConnectionActions(subMenu, ArchiUtil.getClasses(FolderType.APPLICATION), relationshipType);
        addConnectionActions(subMenu, ArchiUtil.getClasses(FolderType.TECHNOLOGY), relationshipType);
        addConnectionActions(subMenu, ArchiUtil.getClasses(FolderType.MOTIVATION), relationshipType);
        addConnectionActions(subMenu, ArchiUtil.getClasses(FolderType.IMPLEMENTATION_MIGRATION), relationshipType);
        addConnectionActions(subMenu, ArchiUtil.getClasses(FolderType.CONNECTORS), relationshipType);

        // Remove the very last separator if there is one
        int itemCount = subMenu.getItemCount() - 1;
        if (itemCount > 0 && (subMenu.getItem(itemCount).getStyle() & SWT.SEPARATOR) != 0) {
            subMenu.getItem(itemCount).dispose();
        }

        if (subMenu.getItemCount() == 0) {
            item.dispose(); // Nothing there
        }
    }

    private void addConnectionActions(Menu menu, EClass[] list, EClass relationshipType) {
        boolean added = false;
        for (EClass type : list) {
            if (!ViewpointsManager.INSTANCE.isAllowedType(diagram, type)) {
                continue;
            }
            if (RelationshipsMatrix.INSTANCE.isValidRelationship(sourceElement.eClass(), type, relationshipType)) {
                added = true;
                addElementAction(menu, relationshipType, type);
            }
        }
        if (added) {
            new MenuItem(menu, SWT.SEPARATOR);
        }
    }

    /**
     * Final action
     */
    private MenuItem addElementAction(Menu menu, final EClass relationshipType, final EClass targetType) {
        final MenuItem item = addElementAction(menu, targetType);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createSemantic(relationshipType, targetType);
            }
        });
        return item;
    }

    private MenuItem addElementAction(Menu menu, EClass targetType) {
        final MenuItem item = new MenuItem(menu, SWT.CASCADE);
        item.setText(ArchiLabelProvider.INSTANCE.getDefaultName(targetType));
        item.setImage(ArchiLabelProvider.INSTANCE.getImage(targetType));
        return item;
    }

    /**
     * Add Element to Connection Actions. Entry point #2
     */
    private void addElementActions(Menu menu) {
        MenuItem item = new MenuItem(menu, SWT.CASCADE);
        item.setText(FolderType.BUSINESS.getName());
        Menu subMenu = new Menu(item);
        item.setMenu(subMenu);
        addElementActions(subMenu, ArchiUtil.getClasses(FolderType.BUSINESS));

        if (subMenu.getItemCount() == 0) {
            item.dispose(); // Nothing there
        }

        item = new MenuItem(menu, SWT.CASCADE);
        item.setText(FolderType.APPLICATION.getName());
        subMenu = new Menu(item);
        item.setMenu(subMenu);
        addElementActions(subMenu, ArchiUtil.getClasses(FolderType.APPLICATION));

        if (subMenu.getItemCount() == 0) {
            item.dispose(); // Nothing there
        }

        item = new MenuItem(menu, SWT.CASCADE);
        item.setText(FolderType.TECHNOLOGY.getName());
        subMenu = new Menu(item);
        item.setMenu(subMenu);
        addElementActions(subMenu, ArchiUtil.getClasses(FolderType.TECHNOLOGY));

        if (subMenu.getItemCount() == 0) {
            item.dispose(); // Nothing there
        }

        item = new MenuItem(menu, SWT.CASCADE);
        item.setText(FolderType.MOTIVATION.getName());
        subMenu = new Menu(item);
        item.setMenu(subMenu);
        addElementActions(subMenu, ArchiUtil.getClasses(FolderType.MOTIVATION));

        if (subMenu.getItemCount() == 0) {
            item.dispose(); // Nothing there
        }

        item = new MenuItem(menu, SWT.CASCADE);
        item.setText(FolderType.IMPLEMENTATION_MIGRATION.getName());
        subMenu = new Menu(item);
        item.setMenu(subMenu);
        addElementActions(subMenu, ArchiUtil.getClasses(FolderType.IMPLEMENTATION_MIGRATION));

        if (subMenu.getItemCount() == 0) {
            item.dispose(); // Nothing there
        }

        item = new MenuItem(menu, SWT.CASCADE);
        item.setText(FolderType.CONNECTORS.getName());
        subMenu = new Menu(item);
        item.setMenu(subMenu);
        addElementActions(subMenu, ArchiUtil.getClasses(FolderType.CONNECTORS));

        if (subMenu.getItemCount() == 0) {
            item.dispose(); // Nothing there
        }
    }

    private void addElementActions(Menu menu, EClass[] list) {

        for (EClass type : list) {
            MenuItem item = addElementAction(menu, type);
            Menu subMenu = new Menu(item);
            item.setMenu(subMenu);
            for (EClass typeRel : RelationshipsMatrix.INSTANCE.getRelationshipsValueMap().keySet()) {
                if (!ViewpointsManager.INSTANCE.isAllowedType(diagram, type)) {
                    continue;
                }
                if (RelationshipsMatrix.INSTANCE.isValidRelationship(sourceElement.eClass(), type, typeRel)) {
                    addConnectionAction(subMenu, typeRel, type, false);
                }
            }
            if (subMenu.getItemCount() == 0) {
                item.dispose(); // Nothing there
            }
        }
    }

    /**
     * Add a Connection Action with a relationship type
     */
    private MenuItem addConnectionAction(Menu menu, final EClass relationshipType, final EClass targetType,
            final boolean reverseDirection) {
        final MenuItem item = new MenuItem(menu, SWT.CASCADE);
        item.setText(ArchiLabelProvider.INSTANCE.getRelationshipPhrase(relationshipType, reverseDirection));
        item.setImage(ArchiLabelProvider.INSTANCE.getImage(relationshipType));
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createSemantic(relationshipType, targetType);
            }
        });
        return item;
    }

    protected void createSemantic(EClass relationshipType, EClass targetType) {
        DiagramNode target = createArchimateElementAndNode(targetType);
        new DiagramEdgeServices().createRelationshipAndEdge(source, target, relationshipType);
    }

    /**
     * Create an archimate element and a node.
     * 
     * @param eClass
     *            object class
     * @return created diagram node
     */
    protected DiagramNode createArchimateElementAndNode(EClass type) {
        DiagramNode node = DiagramNodeServices.createArchimateElementAndNode(diagram, type);

        // layout to put the new node at the right place
        IElementUIProvider provider = ElementUIRegistry.INSTANCE.getProvider(node);
        Dimension defaultSize = provider.getDefaultSize();
        SiriusLayoutDataManager.INSTANCE.addData(new RootLayoutData(editPart, point.getCopy(), defaultSize));
        return node;
    }

}