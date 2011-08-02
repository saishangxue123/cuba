/*
 * Copyright (c) 2011 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.cuba.gui.data.impl;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.data.*;

/**
 * <p>$Id$</p>
 *
 * @author krivopustov
 */
public class CollectionDsActionsNotifier implements CollectionDatasourceListener {
    
    private Component.ActionsHolder actionsHolder;

    public CollectionDsActionsNotifier(Component.ActionsHolder actionsHolder) {
        this.actionsHolder = actionsHolder;
    }

    @Override
    public void stateChanged(Datasource ds, Datasource.State prevState, Datasource.State state) {
        for (Action action : actionsHolder.getActions()) {
            if (action instanceof DatasourceListener) {
                ((DatasourceListener) action).stateChanged(ds, prevState, state);
            }
        }
    }

    @Override
    public void itemChanged(Datasource ds, Entity prevItem, Entity item) {
        for (Action action : actionsHolder.getActions()) {
            if (action instanceof DatasourceListener) {
                ((DatasourceListener) action).itemChanged(ds, prevItem, item);
            }
        }
    }

    @Override
    public void collectionChanged(CollectionDatasource ds, Operation operation) {
        for (Action action : actionsHolder.getActions()) {
            if (action instanceof CollectionDatasourceListener) {
                ((CollectionDatasourceListener) action).collectionChanged(ds, operation);
            }
        }
    }

    @Override
    public void valueChanged(Object source, String property, Object prevValue, Object value) {
        for (Action action : actionsHolder.getActions()) {
            if (action instanceof ValueListener) {
                ((ValueListener) action).valueChanged(source, property, prevValue, value);
            }
        }
    }
}
