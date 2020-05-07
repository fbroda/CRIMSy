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
package de.ipb_halle.lbac.material.subtype.taxonomy;

import de.ipb_halle.lbac.material.Material;
import de.ipb_halle.lbac.material.common.HazardInformation;
import de.ipb_halle.lbac.material.common.MaterialName;
import de.ipb_halle.lbac.material.common.StorageClassInformation;
import de.ipb_halle.lbac.material.entity.taxonomy.TaxonomyEntity;
import de.ipb_halle.lbac.material.subtype.MaterialType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author fmauz
 */
public class Taxonomy extends Material {

    private TaxonomyLevel level;
    private List<Taxonomy> taxHierachy = new ArrayList<>();

    public Taxonomy(int id,
            List<MaterialName> names,
            HazardInformation hazards,
            StorageClassInformation storageInformation,
            List<Taxonomy> hierarchy) {
        super(id, names, null, hazards, storageInformation);
        this.type = MaterialType.TAXONOMY;
        this.taxHierachy = hierarchy;

    }

    @Override
    public Taxonomy copyMaterial() {
        List<MaterialName> copiedNames = new ArrayList<>();
        for (MaterialName mn : names) {
            copiedNames.add(new MaterialName(mn.getValue(), mn.getLanguage(), mn.getRank()));
        }
        return new Taxonomy(id, copiedNames, hazards, storageInformation, taxHierachy);

    }

    @Override
    public TaxonomyEntity createEntity() {
        TaxonomyEntity entity = new TaxonomyEntity();
        entity.setId(id);
        entity.setLevel(level.getId());
        return entity;
    }

    public TaxonomyLevel getLevel() {
        return level;
    }

    public List<Taxonomy> getTaxHierachy() {
        return taxHierachy;
    }

    public void setLevel(TaxonomyLevel level) {
        this.level = level;
    }

}
