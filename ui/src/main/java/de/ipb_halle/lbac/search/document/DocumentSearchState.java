/*
 * Leibniz Bioactives Cloud
 * Copyright 2017 Leibniz-Institut f. Pflanzenbiochemie
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
package de.ipb_halle.lbac.search.document;

import de.ipb_halle.lbac.entity.Document;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.Logger;import org.apache.logging.log4j.LogManager;

/**
 * Holds the current document information from local and remote nodes and has
 * information on which node a search for documents is active.
 *
 * @author fmauz
 */
public class DocumentSearchState implements Serializable {

    private Logger LOGGER = LogManager.getLogger(DocumentSearchState.class);
    private int totalDocs = 0;
    private List<Document> foundDocuments = Collections.synchronizedList(new ArrayList<>());
    private Set<UUID> unfinishedCollectionRequests = new HashSet<>();
    private SearchStatistic stats = new SearchStatistic();

    @PostConstruct
    public void init() {

    }

    /**
     * Signal that all searches for collections are complete.
     *
     * @return
     */
    public boolean isSearchComplete() {
        return unfinishedCollectionRequests.isEmpty();
    }

    public void clearState() {
        this.foundDocuments.clear();
        this.unfinishedCollectionRequests.clear();
        totalDocs = 0;
        stats = new SearchStatistic();

    }

    public synchronized List<Document> getFoundDocuments() {
        return foundDocuments;
    }

    public synchronized void setFoundDocuments(List<Document> foundDocuments) {
        this.foundDocuments = foundDocuments;
    }

    public Set<UUID> getUnfinishedCollectionRequests() {
        return unfinishedCollectionRequests;
    }

    public void setUnfinishedCollectionRequests(Set<UUID> unfinishedNodeRequests) {
        this.unfinishedCollectionRequests = unfinishedNodeRequests;
    }

    public int getTotalDocs() {
        return totalDocs;
    }

    public void setTotalDocs(int totalDocs) {
        this.totalDocs = totalDocs;
    }

    public double getAverageDocLength() {
        return stats.getAverageWordLength();
    }

    public void addToTotalDocs(int amountOfNewDocs) {
        this.totalDocs += amountOfNewDocs;
    }

    public SearchStatistic getStats() {
        return stats;
    }

    public void setStats(SearchStatistic stats) {
        this.stats = stats;
    }

}
