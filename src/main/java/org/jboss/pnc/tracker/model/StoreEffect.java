/**
 * Copyright (C) 2022-2023 Red Hat, Inc. (https://github.com/Commonjava/indy-tracking-service)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.pnc.tracker.model;

public enum StoreEffect {
    UPLOAD("U"),
    DOWNLOAD("D");

    private final String dbCode;

    StoreEffect(String dbCode) {
        this.dbCode = dbCode;
    }

    public String getDbCode() {
        return dbCode;
    }

    public static StoreEffect fromDbCode(String dbCode) {
        if ("U".equals(dbCode)) {
            return UPLOAD;
        }
        if ("D".equals(dbCode)) {
            return DOWNLOAD;
        }
        throw new IllegalArgumentException("Unknown DB code for StoreEffect: " + dbCode);
    }
}
