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

import org.jspecify.annotations.Nullable;

import com.mongodb.hibernate.translate.attachment.YieldingRunnable.ValueDescriptors.ValueDescriptor;

import static com.mongodb.hibernate.internal.MongoAssertions.assertNotNull;
import static com.mongodb.hibernate.internal.MongoAssertions.assertNull;
import static com.mongodb.hibernate.internal.MongoAssertions.assertTrue;

@FunctionalInterface
public interface YieldingRunnable<T, D extends ValueDescriptor<? super T>> {
    static <T, D extends ValueDescriptor<? super T>> T run(D valueDescriptor, YieldingRunnable<T, D> runnable) {
        YieldableValue<T, D> yieldableValue = new YieldableValue<>(valueDescriptor);
        runnable.run(yieldableValue);
        return yieldableValue.get();
    }

    void run(YieldableValue<T, D> yieldableValue);

    final class ValueDescriptors {
        public static final ColumnName COLUMN_NAME = new ColumnName();
        public static final CollectionName COLLECTION_NAME = new CollectionName();
        public static final MyNumber MY_NUMBER = new MyNumber();

        private ValueDescriptors() {
        }

        public static abstract class ValueDescriptor<T> {
            private ValueDescriptor() {
            }

            @Override
            public String toString() {
                return getClass().getSimpleName();
            }
        }

        public static final class ColumnName extends ValueDescriptor<String> {
            private ColumnName() {
            }
        }

        public static final class CollectionName extends ValueDescriptor<String> {
            private CollectionName() {
            }
        }

        public static final class MyNumber extends ValueDescriptor<Number> {
            private MyNumber() {
            }
        }
    }

    final class YieldableValue<T, D extends ValueDescriptor<? super T>> {
        private final D descriptor;
        @Nullable
        private T value;

        private YieldableValue(final D descriptor) {
            this.descriptor = descriptor;
        }

        public void yield(D descriptor, T value) {
            assertTrue(descriptor.equals(this.descriptor));
            assertNull(this.value);
            this.value = assertNotNull(value);
        }

        private T get() {
            return assertNotNull(value);
        }
    }
}
