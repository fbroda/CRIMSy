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
package de.ipb_halle.lbac.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class NestingPath implements Serializable, DTO {

    /**
     * identifies different path of nested memberships.
     */
    private final static long serialVersionUID = 1L;

    private UUID id;
    private UUID membership;
    private Set<UUID> path;

    /**
     * Default constructor. Generates a randomized id
     */
    public NestingPath() {
        this.id = UUID.randomUUID();
        this.path = new HashSet<>();
    }

    /**
     * Constructor
     *
     * @param mebershipId
     * @param nestingPathId
     * @param path
     */
    public NestingPath(
            UUID mebershipId,
            UUID nestingPathId, 
            java.util.Collection<UUID> path) {
        this.path = new HashSet<>();
        this.path.addAll(path);
        this.id = nestingPathId;
    }

    @Override
    public Set<NestingPathEntity> createEntity() {
        Set<NestingPathEntity> result = new HashSet<>();
        for (UUID m : path) {
            result.add(
                    new NestingPathEntity(
                            new NestingPathEntityId(
                                    this.id,
                                    m)));
        }
        return result;
    }

    /**
     * two nestingPath objects are equal if their memberships variables are
     * equal, see contract of Set.equals() method this is necessary for the
     * nestingPathSet in class Membership
     *
     * @param o
     */
    @Override
    public boolean equals(Object o) {
        if ((o != null) && (o instanceof NestingPath)) {
            NestingPath np = (NestingPath) o;
            return this.path.equals(np.getPath());
        }
        return false;
    }

    /**
     * hashCode depends only on the memberships. This is necessary for
     * nestingPathSet in class Membership
     */
    @Override
    public int hashCode() {
        return (this.path != null) ? this.path.hashCode() : 0;
    }

    /**
     *
     * @return
     */
    public UUID getId() {
        return this.id;
    }

    public UUID getMembership() {
        return membership;
    }

    public Set<UUID> getPath() {
        return path;
    }

    public void setMembership(UUID membership) {
        this.membership = membership;
    }

   

}
