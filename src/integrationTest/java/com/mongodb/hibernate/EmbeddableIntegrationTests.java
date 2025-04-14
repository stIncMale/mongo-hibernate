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

package com.mongodb.hibernate;

import static com.mongodb.hibernate.MongoTestAssertions.assertEquals;
import static com.mongodb.hibernate.internal.MongoConstants.ID_FIELD_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mongodb.client.MongoCollection;
import com.mongodb.hibernate.junit.InjectMongoCollection;
import com.mongodb.hibernate.junit.MongoExtension;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.hibernate.annotations.Struct;
import org.hibernate.boot.MetadataSources;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.hibernate.testing.orm.junit.SessionFactoryScopeAware;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@SessionFactory(exportSchema = false)
@DomainModel(
        annotatedClasses = {
            EmbeddableIntegrationTests.ItemWithFlattenedValues.class,
            EmbeddableIntegrationTests.ItemWithAggregateValues.class
        })
@ExtendWith(MongoExtension.class)
class EmbeddableIntegrationTests implements SessionFactoryScopeAware {
    @InjectMongoCollection("items")
    private static MongoCollection<BsonDocument> mongoCollection;

    private SessionFactoryScope sessionFactoryScope;

    @Test
    void flattenedValues() {
        var item = new ItemWithFlattenedValues();
        item.id = new EmbeddableValue();
        item.id.a = 1;
        item.flattened1 = new EmbeddableValue();
        item.flattened1.a = 2;
        item.flattened2 = new EmbeddablePairValue(3, new EmbeddableValue());
        item.flattened2.b.a = 4;
        sessionFactoryScope.inTransaction(session -> session.persist(item));
        assertThat(mongoCollection.find())
                .containsExactly(new BsonDocument()
                        .append(ID_FIELD_NAME, new BsonInt32(item.id.a))
                        .append("a1", new BsonInt32(2))
                        .append("a2", new BsonInt32(3))
                        .append("a3", new BsonInt32(4)));
        var loadedItem =
                sessionFactoryScope.fromTransaction(session -> session.get(ItemWithFlattenedValues.class, item.id));
        assertEquals(item, loadedItem);
    }

    @Test
    void aggregateValues() {
        var item = new ItemWithAggregateValues();
        item.id = new AggregateEmbeddableValue();
        item.id.a = 1;
        item.aggregate1 = new AggregateEmbeddableValue();
        item.aggregate1.a = 2;
        item.aggregate2 = new AggregateEmbeddablePairValue(3, new AggregateEmbeddableValue());
        item.aggregate2.b.a = 4;
        sessionFactoryScope.inTransaction(session -> session.persist(item));
        assertThat(mongoCollection.find())
                .containsExactly(new BsonDocument()
                        // VAKOTODO `_id` is incorrectly flattened
                        .append(ID_FIELD_NAME, new BsonInt32(item.id.a))
                        .append("aggregate1", new BsonDocument("a", new BsonInt32(2)))
                        .append(
                                "aggregate2",
                                new BsonDocument()
                                        .append("a", new BsonInt32(3))
                                        .append("b", new BsonDocument("a", new BsonInt32(4)))));
        var loadedItem =
                sessionFactoryScope.fromTransaction(session -> session.get(ItemWithAggregateValues.class, item.id));
        assertEquals(item, loadedItem);
    }

    @Override
    public void injectSessionFactoryScope(SessionFactoryScope sessionFactoryScope) {
        this.sessionFactoryScope = sessionFactoryScope;
    }

    @Entity
    @Table(name = "items")
    static class ItemWithFlattenedValues {
        @Id
        EmbeddableValue id;

        @AttributeOverride(name = "a", column = @Column(name = "a1"))
        EmbeddableValue flattened1;

        @AttributeOverride(name = "a", column = @Column(name = "a2"))
        @AttributeOverride(name = "b.a", column = @Column(name = "a3"))
        EmbeddablePairValue flattened2;
    }

    @Embeddable
    static class EmbeddableValue {
        int a;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            EmbeddableValue that = (EmbeddableValue) o;
            return a == that.a;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(a);
        }
    }

    @Embeddable
    record EmbeddablePairValue(int a, EmbeddableValue b) {}

    @Entity
    @Table(name = "items")
    static class ItemWithAggregateValues {
        @Id
        AggregateEmbeddableValue id;

        AggregateEmbeddableValue aggregate1;

        AggregateEmbeddablePairValue aggregate2;
    }

    @Embeddable
    @Struct(name = "AggregateEmbeddableValue")
    static class AggregateEmbeddableValue {
        int a;
    }

    @Embeddable
    @Struct(name = "AggregateEmbeddablePairValue")
    record AggregateEmbeddablePairValue(int a, AggregateEmbeddableValue b) {}

    @Nested
    class Unsupported {
        @Test
        void idSpanningMultipleFields() {
            assertThatThrownBy(() -> new MetadataSources()
                            .addAnnotatedClass(ItemWithFlattenedPairValueAsId.class)
                            .buildMetadata())
                    .hasMessageContaining("does not support [_id] spanning multiple columns");
        }

        @Entity
        @Table(name = "items")
        static class ItemWithFlattenedPairValueAsId {
            @Id
            EmbeddablePairValue id;
        }
    }
}
