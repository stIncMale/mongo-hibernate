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

import static com.mongodb.hibernate.type.temporal.CalendarIntegrationTests.assertNotSupported;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalTime;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class LocalTimeIntegrationTests {
    @Test
    void unsupported() {
        assertAll(
                () -> assertNotSupported(ItemWithUnsupportedId.class),
                () -> assertNotSupported(ItemWithUnsupportedBasicPersistentAttribute.class),
                () -> assertNotSupported(ItemWithUnsupportedArrayPersistentAttribute.class),
                () -> assertNotSupported(ItemWithUnsupportedCollectionPersistentAttribute.class),
                () -> assertNotSupported(ItemWithUnsupportedCollectionPersistentAttribute.class));
    }

    @Entity
    record ItemWithUnsupportedId(@Id LocalTime id) {}

    @Entity
    record ItemWithUnsupportedBasicPersistentAttribute(@Id int id, LocalTime v) {}

    @Entity
    record ItemWithUnsupportedArrayPersistentAttribute(@Id int id, LocalTime[] v) {}

    @Entity
    record ItemWithUnsupportedCollectionPersistentAttribute(@Id int id, Collection<LocalTime> v) {}
}
