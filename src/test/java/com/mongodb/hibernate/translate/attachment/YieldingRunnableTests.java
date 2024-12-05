/*
 * Copyright 2024-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.hibernate.translate.attachment;

import com.mongodb.hibernate.translate.attachment.YieldingRunnable.ValueDescriptors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YieldingRunnableTests {
    @Test
    void example() {
        assertEquals(
                "my string",
                YieldingRunnable.run(ValueDescriptors.COLLECTION_NAME, yieldableValue -> {
                    voidMethod();
                    yieldableValue.yield(ValueDescriptors.COLLECTION_NAME, "my string");
                }));
    }

    private static void voidMethod() {
    }

    @Test
    void features() {
        String string = YieldingRunnable.run(ValueDescriptors.COLUMN_NAME, yieldableValue ->
                // Note how the compiler does not allow you to use `COLLECTION_NAME`,
                // despite it also being of the `ValueDescriptor<String>` type.
                // The compiler forces you to specify `COLUMN_NAME` here, and nothing else.
                //
                // We still require `COLUMN_NAME` to be passed here,
                // because this way we can check that the passed `ValueDescriptor` matches the required one,
                // regardless of whether the calling code uses raw types, or unchecked conversions.
                yieldableValue.yield(ValueDescriptors.COLUMN_NAME, "my string"));

        // Note how we can return `Integer` despite `MY_NUMBER` being of the `ValueDescriptor<Number>` type.
        // Such versatility is good. At the same time, we cannot return `Object` or `String`, i.e., we are type-safe.
        Integer integer = YieldingRunnable.run(ValueDescriptors.MY_NUMBER, todo ->
                todo.yield(ValueDescriptors.MY_NUMBER, Integer.parseInt("1")));
    }
}
