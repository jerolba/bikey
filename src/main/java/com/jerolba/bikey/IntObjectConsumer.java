/**
 * Copyright 2019 Jerónimo López Bezanilla
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.jerolba.bikey;

import java.util.function.Consumer;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result. This is the two-arity specialization of {@link Consumer} but with the
 * first parameter specialized to primitive int values.
 *
 * <p>
 * This is a functional interface whose functional method is
 * {@link #accept(int, Object)}.
 *
 * @param <T>
 *            the type of the second argument to the operation
 */
@FunctionalInterface
public interface IntObjectConsumer<T> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param i
     *            the first input argument
     * @param t
     *            the second input argument
     */
    void accept(int i, T t);

}
