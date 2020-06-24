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

import de.ipb_halle.lbac.material.biomaterial.TaxonomyService;
import de.ipb_halle.lbac.material.biomaterial.TissueService;
import de.ipb_halle.lbac.material.biomaterial.BiomaterialService;
import de.ipb_halle.lbac.admission.GlobalAdmissionContext;
import de.ipb_halle.lbac.admission.LdapProperties;
import de.ipb_halle.lbac.admission.UserBean;
import de.ipb_halle.lbac.announcement.membership.MembershipOrchestrator;
import de.ipb_halle.lbac.base.TestBase;
import static de.ipb_halle.lbac.base.TestBase.prepareDeployment;
import de.ipb_halle.lbac.entity.User;
import de.ipb_halle.lbac.globals.KeyManager;
import de.ipb_halle.lbac.material.CreationTools;
import de.ipb_halle.lbac.material.common.HazardInformation;
import de.ipb_halle.lbac.material.common.MaterialName;
import de.ipb_halle.lbac.material.common.StorageClassInformation;
import de.ipb_halle.lbac.material.mocks.UserBeanMock;
import de.ipb_halle.lbac.material.biomaterial.BioMaterial;
import de.ipb_halle.lbac.material.biomaterial.Taxonomy;
import de.ipb_halle.lbac.material.biomaterial.Tissue;
import de.ipb_halle.lbac.material.common.service.MaterialService;
import de.ipb_halle.lbac.material.structure.MoleculeService;
import de.ipb_halle.lbac.project.Project;
import de.ipb_halle.lbac.project.ProjectService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
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
    private TissueService tissueService;

    @Inject
    private MaterialService materialService;

    @Inject
    private BiomaterialService bioMaterialService;

    @Inject
    private ProjectService projectService;

    private CreationTools creationTools;

    @Before
    public void init() {
        creationTools = new CreationTools("", "", "", memberService, projectService);
        // Initialisieng the userbean for ownership of material
        UserBeanMock userBean = new UserBeanMock();
        userBean.setCurrentAccount(memberService.loadUserById(UUID.fromString(GlobalAdmissionContext.PUBLIC_ACCOUNT_ID)));
        owner = memberService.loadUserById(UUID.fromString(GlobalAdmissionContext.PUBLIC_ACCOUNT_ID));
        ownerid = owner.getId().toString();
        materialService.setUserBean(userBean);
        project = creationTools.createProject();
    }

    @After
    public void finish() {
        cleanMaterialsFromDB();
    }

    @Test
    public void test001_saveAndLoadBioMaterials() {
        createTaxonomyTreeInDB(project.getUserGroups().getId().toString(), owner.getId().toString());

        List<MaterialName> names = new ArrayList<>();
        names.add(new MaterialName("Wurzel", "de", 1));
        names.add(new MaterialName("Root", "en", 2));
        names.add(new MaterialName("Radix", "la", 3));
        Taxonomy taxo = taxonomyService.loadTaxonomy(new HashMap<>(), true).get(15);
        Tissue tissue = new Tissue(100, names, taxo);
        materialService.saveMaterialToDB(tissue, project.getUserGroups().getId(), new HashMap<>());

        names = new ArrayList<>();
        names.add(new MaterialName("Löwnzahn", "de", 1));
        BioMaterial biomaterial = new BioMaterial(0, names, project.getId(), new HazardInformation(), new StorageClassInformation(), taxo, tissue);
        materialService.saveMaterialToDB(biomaterial, project.getUserGroups().getId(), new HashMap<>());

    }

    @Deployment
    public static WebArchive createDeployment() {
        return prepareDeployment("BiomaterialServiceTest.war")
                .addClass(ProjectService.class)
                .addClass(GlobalAdmissionContext.class)
                .addClass(MaterialService.class)
                .addClass(UserBean.class)
                .addClass(MembershipOrchestrator.class)
                .addClass(MoleculeService.class)
                .addClass(LdapProperties.class)
                .addClass(KeyManager.class)
                .addClass(BiomaterialService.class)
                .addClass(TissueService.class)
                .addClass(TaxonomyService.class);
    }
}