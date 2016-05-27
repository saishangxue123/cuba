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

package com.haulmont.cuba.core.app.dynamicattributes;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;

@Service(DynamicAttributesCacheService.NAME)
public class DynamicAttributesCacheServiceBean implements DynamicAttributesCacheService {
    @Inject
    protected DynamicAttributesManagerAPI dynamicAttributesManagerAPI;

    @Override
    public void loadCache(){
        dynamicAttributesManagerAPI.loadCache();
    }

    @Override
    public DynamicAttributesCache getCacheIfNewer(Date clientCacheDate) {
        return dynamicAttributesManagerAPI.getCacheIfNewer(clientCacheDate);
    }
}