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

package com.haulmont.cuba.core.sys.jmx.exception;

import com.haulmont.cuba.core.global.SupportedByClient;
import com.haulmont.cuba.core.sys.logging.LogControlException;

@SupportedByClient
public class UnrecognizedLogThresholdException extends LogControlException {

    private static final long serialVersionUID = -5723789683222840034L;

    public UnrecognizedLogThresholdException(String thresholdString) {
        super(String.format("Unrecognized threshold '%s'", thresholdString));
    }
}