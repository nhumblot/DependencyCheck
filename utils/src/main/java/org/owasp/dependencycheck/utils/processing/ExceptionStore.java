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
 * Simple store for exceptions.
 *
 * @author Jeremy Long
 * @param <T> The exception type to store.
 */
public class ExceptionStore<T extends Throwable> extends Store<T> {

    /**
     * Puts a value into storage.
     *
     * @param value the value to store
     */
    @Override
    public void put(T value) {
        if (retrieve() != null) {
            retrieve().addSuppressed(value);
        } else {
            super.put(value);
        }
    }

    /**
     * If the store contains an exception calling `checkException()` will throw
     * the exception.
     *
     * @throws T thrown if the store contains an exception
     */
    public void checkException() throws T {
        if (retrieve() != null) {
            throw retrieve();
        }
    }
}
