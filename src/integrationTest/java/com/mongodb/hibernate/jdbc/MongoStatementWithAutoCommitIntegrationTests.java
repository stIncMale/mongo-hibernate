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

package com.mongodb.hibernate.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

class MongoStatementWithAutoCommitIntegrationTests extends MongoStatementIntegrationTests {
    @Override
    void doAwareOfAutoCommit(Connection connection, SqlExecutable work) throws SQLException {
        doWithSpecifiedAutoCommit(true, connection, work);
    }
}
