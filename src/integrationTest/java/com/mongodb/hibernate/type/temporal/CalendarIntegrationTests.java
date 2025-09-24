/*
 * Copyright 2025-present MongoDB, Inc.
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

package com.mongodb.hibernate.type.temporal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.mongodb.hibernate.internal.FeatureNotSupportedException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Calendar;
import java.util.Collection;
import org.hibernate.boot.MetadataSources;
import org.junit.jupiter.api.Test;

class CalendarIntegrationTests {
    @Test
    void unsupported() {
        assertAll(
                () -> assertNotSupported(ItemWithUnsupportedId.class),
                () -> assertNotSupported(ItemWithUnsupportedBasicPersistentAttribute.class),
                () -> assertNotSupported(ItemWithUnsupportedArrayPersistentAttribute.class),
                () -> assertNotSupported(ItemWithUnsupportedCollectionPersistentAttribute.class),
                () -> assertNotSupported(ItemWithUnsupportedCollectionPersistentAttribute.class));
    }

    static void assertNotSupported(Class<?> entityClass) {
        assertThatThrownBy(() ->
                        new MetadataSources().addAnnotatedClass(entityClass).buildMetadata())
                .isInstanceOf(FeatureNotSupportedException.class)
                .hasMessageMatching(".*persistent attribute .* has .*type .* that is not supported");
    }

    @Entity
    record ItemWithUnsupportedId(@Id Calendar id) {}

    @Entity
    record ItemWithUnsupportedBasicPersistentAttribute(@Id int id, Calendar v) {}

    @Entity
    record ItemWithUnsupportedArrayPersistentAttribute(@Id int id, Calendar[] v) {}

    @Entity
    record ItemWithUnsupportedCollectionPersistentAttribute(@Id int id, Collection<Calendar> v) {}
}
