/*
 * Cloud Resource & Information Management System (CRIMSy)
 * Copyright 2020 Leibniz-Institut f. Pflanzenbiochemie
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
 */
package de.ipb_halle.lbac.search;

import de.ipb_halle.lbac.admission.User;
import de.ipb_halle.lbac.search.lang.Condition;
import de.ipb_halle.lbac.search.lang.EntityGraph;

/**
 *
 * @author fmauz
 */
public class SearchRequestImpl implements SearchRequest {

    private Condition condition;
    private int firstResultIndex;
    private int maxResults;
    private User u;
    private EntityGraph entityGraph;

    public SearchRequestImpl(EntityGraph graph, Condition condition) {
        this.condition = condition;
        this.entityGraph = graph;
    }

    @Override
    public EntityGraph getEntityGraph() {
        return entityGraph;
    }

    @Override
    public Condition getCondition() {
        return condition;
    }

    @Override
    public int getFirstResult() {
        return firstResultIndex;
    }

    @Override
    public int getMaxResults() {
        return maxResults;
    }
}
