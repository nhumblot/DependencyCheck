/*
 * This file is part of dependency-check-utils.
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
 * Copyright (c) 2020 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.utils.processing;

/**
 * Simple value store.
 *
 * @author Jeremy Long
 * @param <T> the type of value to store
 */
public class Store<T> {

    /**
     * Stores the value.
     */
    private T value;

    /**
     * Puts a value into storage.
     *
     * @param value the value to store
     */
    public void put(T value) {
        this.value = value;
    }

    /**
     * Retrieves the value from storage.
     *
     * @return the value previously stored; otherwise <code>null</code>
     */
    public T retrieve() {
        return value;
    }
}
