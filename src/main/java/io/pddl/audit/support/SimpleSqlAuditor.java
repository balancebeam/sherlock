/**
 * Copyright 1999-2011 Alibaba Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package io.pddl.audit.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.pddl.audit.SqlAuditor;

/**
 * A simple {@link SqlAuditor} implementation that will just simple print the
 * SQL and its parameter.<br>
 * 
 * @author fujohnwang
 */
public class SimpleSqlAuditor implements SqlAuditor {

    private transient final Logger logger = LoggerFactory.getLogger(SimpleSqlAuditor.class);

    public void audit(String id, String sql, Object sqlContext) {
        logger.info("SQL id:{} SQL:{} - Parameter:{}", new Object[] { id, sql, sqlContext });
    }

}
