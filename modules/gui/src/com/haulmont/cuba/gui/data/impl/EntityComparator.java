/*
 * Copyright (c) 2008 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.

 * Author: Dmitry Abramov
 * Created: 30.03.2009 12:23:57
 * $Id$
 */

package com.haulmont.cuba.gui.data.impl;

import com.haulmont.chile.core.model.Instance;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.Entity;

public class EntityComparator<T extends Entity> extends AbstractComparator<T> {
    private MetaPropertyPath propertyPath;
    private MetaProperty property;

    public EntityComparator(MetaPropertyPath propertyPath, boolean asc) {
        super(asc);
        this.propertyPath = propertyPath;
        if (propertyPath.get().length == 1) {
            property = this.propertyPath.getMetaProperty();
        }

/*
        Class<?> javaClass = this.propertyPath.getRangeJavaClass();
        if (!Comparable.class.isAssignableFrom(javaClass))
            throw new UnsupportedOperationException(javaClass + " is not comparable");
*/
    }

    public int compare(T o1, T o2) {
        Object v1 = getValue((Instance) o1);
        Object v2 = getValue((Instance) o2);
        
        return __compare(v1, v2);
    }

    private Object getValue(Instance instance) {
        Object value;
        if (property != null) {
            value = instance.getValue(property.getName());
        } else {
            value = instance.getValueEx(propertyPath.toString());
        }

        if (!(value == null || value instanceof Comparable)) {
            value = value.toString();
        }

        return value;
    }
}
