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
package de.ipb_halle.lbac.exp;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fbroda
 */
@Entity
@Table(name = "experiments")
public class ExperimentEntity implements Serializable {

    private final static long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer experimentid;

    @Column
    private String code;

    @Column
    private String description;

    @Column
    private Integer ownerid;

    @Column
    private Integer aclist_id;

    @Column
    private boolean template;

    @Column
    private Date ctime;

    public String getCode() {
        return this.code;
    }

    public Date getCtime() {
        return ctime;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getExperimentId() {
        return this.experimentid;
    }

    public Integer getOwnerId() {
        return ownerid;
    }

    public Integer getACListId() {
        return aclist_id;
    }

    public boolean getTemplate() {
        return this.template;
    }

    public ExperimentEntity setCode(String code) {
        this.code = code;
        return this;
    }

    public ExperimentEntity setCtime(Date ctime) {
        this.ctime = ctime;
        return this;
    }

    public ExperimentEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public ExperimentEntity setExperimentId(Integer experimentid) {
        this.experimentid = experimentid;
        return this;
    }

    public ExperimentEntity setOwnerId(Integer ownerid) {
        this.ownerid = ownerid;
        return this;
    }

    public ExperimentEntity setACListId(Integer aclist_id) {
        this.aclist_id = aclist_id;
        return this;
    }

    public ExperimentEntity setTemplate(boolean template) {
        this.template = template;
        return this;
    }
}
