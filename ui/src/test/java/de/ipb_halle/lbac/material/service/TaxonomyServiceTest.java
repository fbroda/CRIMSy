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
package de.ipb_halle.lbac.material.service;

import de.ipb_halle.lbac.admission.GlobalAdmissionContext;
import de.ipb_halle.lbac.admission.LdapProperties;
import de.ipb_halle.lbac.admission.UserBean;
import de.ipb_halle.lbac.announcement.membership.MembershipOrchestrator;
import de.ipb_halle.lbac.base.TestBase;
import static de.ipb_halle.lbac.base.TestBase.prepareDeployment;
import de.ipb_halle.lbac.entity.User;
import de.ipb_halle.lbac.globals.KeyManager;
import de.ipb_halle.lbac.material.CreationTools;
import de.ipb_halle.lbac.material.mocks.UserBeanMock;
import de.ipb_halle.lbac.material.subtype.taxonomy.Taxonomy;
import de.ipb_halle.lbac.material.subtype.taxonomy.TaxonomyLevel;
import de.ipb_halle.lbac.project.Project;
import de.ipb_halle.lbac.project.ProjectService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author fmauz
 */
@RunWith(Arquillian.class)
public class TaxonomyServiceTest extends TestBase {

    Project project;
    User owner;
    String userGroups;
    String ownerid;

    @Inject
    private TaxonomyService service;

    @Inject
    private MaterialService materialService;
    @Inject
    private ProjectService projectService;

    private CreationTools creationTools;

    @Before
    public void init() {
        creationTools = new CreationTools("", "", "", memberService, projectService);
        // Initialisieng the userbean for ownership of material
        UserBeanMock userBean = new UserBeanMock();
        userBean.setCurrentAccount(memberService.loadUserById(UUID.fromString(GlobalAdmissionContext.PUBLIC_ACCOUNT_ID)));
        materialService.setUserBean(userBean);
    }

    @Test
    public void test001_loadTaxonomyLevels() {
        List<TaxonomyLevel> levels = service.loadTaxonomyLevel();
        Assert.assertEquals("test001: 8 levels must be found", 8, levels.size());
    }

    @Test
    public void test002_loadTaxonomies() {
        owner = memberService.loadUserById(UUID.fromString(GlobalAdmissionContext.PUBLIC_ACCOUNT_ID));
        project = creationTools.createProject();
        userGroups = project.getUserGroups().getId().toString();
        ownerid = owner.getId().toString();
        createTaxonomyTreeInDB(userGroups, owner.getId().toString());
        List<Taxonomy> taxonomies = service.loadTaxonomy(new HashMap<>(), true);
        Assert.assertEquals("test001: 21 taxonomies must be found", 21, taxonomies.size());

        Taxonomy life = taxonomies.get(0);
        Assert.assertTrue(life.getTaxHierachy().isEmpty());
        Assert.assertEquals(1, life.getLevel().getId());
        Assert.assertEquals("Leben_de", life.getFirstName());

        Taxonomy wulstlinge = taxonomies.get(5);
        Assert.assertEquals(7, wulstlinge.getLevel().getId());
        Assert.assertEquals("Wulstlinge_de", wulstlinge.getFirstName());
        Assert.assertEquals(6, wulstlinge.getId());
        Assert.assertEquals(5, wulstlinge.getTaxHierachy().size());
        Assert.assertEquals(5, wulstlinge.getTaxHierachy().get(0).getId());
        Assert.assertEquals(4, wulstlinge.getTaxHierachy().get(1).getId());
        Assert.assertEquals(3, wulstlinge.getTaxHierachy().get(2).getId());
        Assert.assertEquals(2, wulstlinge.getTaxHierachy().get(3).getId());
        Assert.assertEquals(1, wulstlinge.getTaxHierachy().get(4).getId());

        Taxonomy ohrlappenpilze = taxonomies.get(10);
        Assert.assertEquals(6, ohrlappenpilze.getLevel().getId());
        Assert.assertEquals("Gallerttränenverwandte_de", ohrlappenpilze.getFirstName());
        Assert.assertEquals(11, ohrlappenpilze.getId());
        Assert.assertEquals(3, ohrlappenpilze.getTaxHierachy().size());
        Assert.assertEquals(8, ohrlappenpilze.getTaxHierachy().get(0).getId());
        Assert.assertEquals(2, ohrlappenpilze.getTaxHierachy().get(1).getId());
        Assert.assertEquals(1, ohrlappenpilze.getTaxHierachy().get(2).getId());

        cleanTaxonomyFromDb();
    }

    @Deployment
    public static WebArchive createDeployment() {
        return prepareDeployment("TaxonomyServiceTest.war")
                .addClass(ProjectService.class)
                .addClass(GlobalAdmissionContext.class)
                .addClass(MaterialService.class)
                .addClass(UserBean.class)
                .addClass(MembershipOrchestrator.class)
                .addClass(MoleculeService.class)
                .addClass(LdapProperties.class)
                .addClass(KeyManager.class)
                .addClass(TaxonomyService.class);
    }
}
