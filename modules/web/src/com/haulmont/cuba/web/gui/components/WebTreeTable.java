/*
 * Copyright (c) 2008-2016 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.haulmont.cuba.web.gui.components;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.TreeTable;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.data.TreeTableDatasource;
import com.haulmont.cuba.web.gui.data.CollectionDsWrapper;
import com.haulmont.cuba.web.gui.data.HierarchicalDsWrapper;
import com.haulmont.cuba.web.gui.data.ItemWrapper;
import com.haulmont.cuba.web.gui.data.PropertyWrapper;
import com.haulmont.cuba.web.toolkit.data.AggregationContainer;
import com.haulmont.cuba.web.toolkit.data.TreeTableContainer;
import com.haulmont.cuba.web.toolkit.ui.CubaTreeTable;
import com.vaadin.data.Item;
import com.vaadin.server.Resource;

import java.util.*;

public class WebTreeTable<E extends Entity> extends WebAbstractTable<CubaTreeTable, E> implements TreeTable<E> {

    protected String hierarchyProperty;

    public WebTreeTable() {
        component = createTreeTableComponent();
        initComponent(component);
    }

    protected CubaTreeTable createTreeTableComponent() {
        return new CubaTreeTableExt();
    }

    @Override
    public HierarchicalDatasource getDatasource() {
        return (HierarchicalDatasource) super.getDatasource();
    }

    @Override
    public void setRowHeaderMode(RowHeaderMode rowHeaderMode) {
        // Row Header mode for TreeTable ignored
    }

    @Override
    public void setDatasource(CollectionDatasource datasource) {
        super.setDatasource(datasource);
        this.hierarchyProperty = ((HierarchicalDatasource) datasource).getHierarchyPropertyName();

        // if showProperty is null, the Tree will use itemId.toString
        MetaProperty metaProperty = hierarchyProperty == null ? null : datasource.getMetaClass().getProperty(hierarchyProperty);
        component.setItemCaptionPropertyId(metaProperty);
    }

    @Override
    public String getHierarchyProperty() {
        return hierarchyProperty;
    }

    @Override
    public void setDatasource(HierarchicalDatasource datasource) {
        setDatasource((CollectionDatasource) datasource);
    }

    @Override
    protected CollectionDsWrapper createContainerDatasource(
            CollectionDatasource datasource, Collection<MetaPropertyPath> columns) {
        return new TreeTableDsWrapper((HierarchicalDatasource) datasource);
    }

    @Override
    public void setIconProvider(IconProvider iconProvider) {
        this.iconProvider = iconProvider;
        component.refreshRowCache();
    }

    @Override
    public void expandAll() {
        component.expandAllItems();
    }

    @Override
    public void expand(Object itemId) {
        if (component.containsId(itemId)) {
            component.expandItemWithParents(itemId);
        }
    }

    @Override
    public void collapseAll() {
        component.collapseAll();
    }

    @Override
    public void collapse(Object itemId) {
        if (component.containsId(itemId)) {
            component.collapseItemRecursively(itemId);
        }
    }

    @Override
    public void expandUpTo(int level) {
        component.expandUpTo(level);
    }

    @Override
    public int getLevel(Object itemId) {
        return component.getLevel(itemId);
    }

    @Override
    public boolean isExpanded(Object itemId) {
        if (component.containsId(itemId)) {
            return !component.isCollapsed(itemId);
        }
        return false;
    }

    protected class TreeTableDsWrapper
            extends HierarchicalDsWrapper
            implements TreeTableContainer, com.vaadin.data.Container.Sortable, AggregationContainer {

        protected boolean treeTableDatasource;

        protected List<Object> aggregationProperties = null;

        public TreeTableDsWrapper(HierarchicalDatasource datasource) {
            super(datasource);
            treeTableDatasource = (datasource instanceof TreeTableDatasource);
        }

        @Override
        protected void createProperties(View view, MetaClass metaClass) {
            if (columns.isEmpty()) {
                super.createProperties(view, metaClass);
            } else {
                for (Map.Entry<Object, Column> entry : columns.entrySet()) {
                    if (entry.getKey() instanceof MetaPropertyPath) {
                        properties.add((MetaPropertyPath) entry.getKey());
                    }
                }
            }
        }

        @Override
        protected ItemWrapper createItemWrapper(Object item) {
            return new ItemWrapper(item, datasource.getMetaClass(), properties) {
                @Override
                protected PropertyWrapper createPropertyWrapper(Object item, MetaPropertyPath propertyPath) {
                    return new TablePropertyWrapper(item, propertyPath);
                }
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isCaption(Object itemId) {
            return treeTableDatasource && ((TreeTableDatasource) datasource).isCaption(itemId);
        }

        @SuppressWarnings("unchecked")
        @Override
        public String getCaption(Object itemId) {
            if (treeTableDatasource) {
                return ((TreeTableDatasource) datasource).getCaption(itemId);
            }
            return null;
        }

        @Override
        public boolean setCaption(Object itemId, String caption) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLevel(Object itemId) {
            return getItemLevel(itemId);
        }

        protected int getItemLevel(Object itemId) {
            Object parentId;
            if ((parentId = getParent(itemId)) == null) {
                return 0;
            }
            return getItemLevel(parentId) + 1;
        }

        @Override
        public void sort(Object[] propertyId, boolean[] ascending) {
            List<CollectionDatasource.Sortable.SortInfo> infos = new ArrayList<>();
            for (int i = 0; i < propertyId.length; i++) {
                final MetaPropertyPath propertyPath = (MetaPropertyPath) propertyId[i];

                final CollectionDatasource.Sortable.SortInfo<MetaPropertyPath> info =
                        new CollectionDatasource.Sortable.SortInfo<>();
                info.setPropertyPath(propertyPath);
                info.setOrder(ascending[i] ? CollectionDatasource.Sortable.Order.ASC : CollectionDatasource.Sortable.Order.DESC);

                infos.add(info);
            }
            ((CollectionDatasource.Sortable) datasource).sort(infos.toArray(new CollectionDatasource.Sortable.SortInfo[infos.size()]));
        }

        @Override
        public Collection getSortableContainerPropertyIds() {
            return properties;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object nextItemId(Object itemId) {
            return ((CollectionDatasource.Sortable) datasource).nextItemId(itemId);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object prevItemId(Object itemId) {
            return ((CollectionDatasource.Sortable) datasource).prevItemId(itemId);
        }

        @Override
        public Object firstItemId() {
            return ((CollectionDatasource.Sortable) datasource).firstItemId();
        }

        @Override
        public Object lastItemId() {
            return ((CollectionDatasource.Sortable) datasource).lastItemId();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isFirstId(Object itemId) {
            return ((CollectionDatasource.Sortable) datasource).isFirstId(itemId);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isLastId(Object itemId) {
            return ((CollectionDatasource.Sortable) datasource).isLastId(itemId);
        }

        @Override
        public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection getAggregationPropertyIds() {
            if (aggregationProperties != null) {
                return Collections.unmodifiableList(aggregationProperties);
            }
            return Collections.emptyList();
        }

        @Override
        public Type getContainerPropertyAggregation(Object propertyId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addContainerPropertyAggregation(Object propertyId, Type type) {
            if (aggregationProperties == null) {
                aggregationProperties = new LinkedList<>();
            } else if (aggregationProperties.contains(propertyId)) {
                throw new IllegalStateException("Such aggregation property is already exists");
            }
            aggregationProperties.add(propertyId);
        }

        @Override
        public void removeContainerPropertyAggregation(Object propertyId) {
            if (aggregationProperties != null) {
                aggregationProperties.remove(propertyId);
                if (aggregationProperties.isEmpty()) {
                    aggregationProperties = null;
                }
            }
        }

        @Override
        public Map<Object, Object> aggregate(Context context) {
            return __aggregate(this, context);
        }

        @Override
        public void resetSortOrder() {
            if (datasource instanceof CollectionDatasource.Sortable) {
                ((CollectionDatasource.Sortable) datasource).resetSortOrder();
            }
        }
    }

    protected class CubaTreeTableExt extends CubaTreeTable {
        @Override
        public Resource getItemIcon(Object itemId) {
            return WebTreeTable.this.getItemIcon(itemId);
        }

        @Override
        protected boolean changeVariables(Map<String, Object> variables) {
            boolean b = super.changeVariables(variables);
            b = handleSpecificVariables(variables) || b;
            return b;
        }
    }
}