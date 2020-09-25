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
package de.ipb_halle.lbac.material.biomaterial;

import de.ipb_halle.lbac.admission.GlobalAdmissionContext;
import de.ipb_halle.lbac.admission.UserBeanDeployment;
import de.ipb_halle.lbac.admission.UserBeanMock;
import de.ipb_halle.lbac.base.TestBase;
import static de.ipb_halle.lbac.base.TestBase.prepareDeployment;
import de.ipb_halle.lbac.admission.User;
import de.ipb_halle.lbac.material.CreationTools;
import de.ipb_halle.lbac.material.common.HazardInformation;
import de.ipb_halle.lbac.material.common.MaterialName;
import de.ipb_halle.lbac.material.common.StorageClassInformation;
import de.ipb_halle.lbac.material.common.service.MaterialService;
import de.ipb_halle.lbac.material.structure.MoleculeService;
import de.ipb_halle.lbac.project.Project;
import de.ipb_halle.lbac.project.ProjectService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author fmauz
 */
@RunWith(Arquillian.class)
public class BiomaterialServiceTest extends TestBase {

    Project project;
    User owner;
    String userGroups;
    String ownerid;

    @Inject
    private TaxonomyService taxonomyService;

    @Inject
    private MaterialService materialService;

    @Inject
    private BiomaterialService bioMaterialService;

    @Inject
    private ProjectService projectService;

    private CreationTools creationTools;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        creationTools = new CreationTools("", "", "", memberService, projectService);
        // Initialisieng the userbean for ownership of material
        UserBeanMock userBean = new UserBeanMock();
        userBean.setCurrentAccount(memberService.loadUserById(GlobalAdmissionContext.PUBLIC_ACCOUNT_ID));
        owner = memberService.loadUserById(GlobalAdmissionContext.PUBLIC_ACCOUNT_ID);
        ownerid = owner.getId().toString();
        materialService.setUserBean(userBean);
        project = creationTools.createProject();
        createTaxonomyTreeInDB(project.getUserGroups().getId(), owner.getId());
    }

    @After
    public void finish() {
        cleanMaterialsFromDB();
    }

    @Test
    public void test001_saveAndLoadBioMaterials() {

        Taxonomy taxo = taxonomyService.loadTaxonomy(new HashMap<>(), true).get(15);
        Tissue tissue = saveTissueInDB(taxo);
        List<MaterialName> names = new ArrayList<>();
        names.add(new MaterialName("Löwnzahn", "de", 1));
        BioMaterial biomaterial = new BioMaterial(0, names, project.getId(), new HazardInformation(), new StorageClassInformation(), taxo, tissue);
        materialService.saveMaterialToDB(biomaterial, project.getUserGroups().getId(), new HashMap<>());

    }

    @Test
    public void test002_editBioMaterial() throws Exception {

        Taxonomy taxo = taxonomyService.loadTaxonomy(new HashMap<>(), true).get(15);
        Tissue tissue = saveTissueInDB(taxo);
        List<MaterialName> names = new ArrayList<>();
        names.add(new MaterialName("Löwnzahn", "de", 1));
        BioMaterial biomaterial = new BioMaterial(0, names, project.getId(), new HazardInformation(), new StorageClassInformation(), taxo, tissue);
        biomaterial.setACList(project.getUserGroups());
        biomaterial.setOwner(owner);

        materialService.saveMaterialToDB(biomaterial, project.getUserGroups().getId(), new HashMap<>());

        BioMaterial editedBioMaterial = biomaterial.copyMaterial();
        editedBioMaterial.setTissue(null);
        Taxonomy newTaxonomy = taxonomyService.loadTaxonomy(new HashMap<>(), true).get(12);
        editedBioMaterial.setTaxonomy(newTaxonomy);
        materialService.saveEditedMaterial(editedBioMaterial, biomaterial, project.getUserGroups().getId(), owner.getId());

        BioMaterial m = (BioMaterial) materialService.loadMaterialById(editedBioMaterial.getId());
        Assert.assertEquals(newTaxonomy.getId(), m.getTaxonomy().getId());
        Assert.assertNull(m.getTissueId());

        Assert.assertFalse(m.getHistory().getChanges().isEmpty());
    }

    private Tissue saveTissueInDB(Taxonomy taxo) {
        List<MaterialName> names = new ArrayList<>();
        names.add(new MaterialName("Wurzel", "de", 1));
        names.add(new MaterialName("Root", "en", 2));
        names.add(new MaterialName("Radix", "la", 3));
        Tissue tissue = new Tissue(100, names, taxo);
        materialService.saveMaterialToDB(tissue, project.getUserGroups().getId(), new HashMap<>());
        return tissue;
    }

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive deployment = prepareDeployment("BiomaterialServiceTest.war")
                .addClass(ProjectService.class)
                .addClass(MaterialService.class)
                .addClass(MoleculeService.class)
                .addClass(TaxonomyNestingService.class)
                .addClass(BiomaterialService.class)
                .addClass(TissueService.class)
                .addClass(TaxonomyService.class);
        return UserBeanDeployment.add(deployment);
    }
}
