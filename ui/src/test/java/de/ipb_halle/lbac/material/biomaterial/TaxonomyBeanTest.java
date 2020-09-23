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
import de.ipb_halle.lbac.admission.LoginEvent;
import de.ipb_halle.lbac.base.TestBase;
import static de.ipb_halle.lbac.base.TestBase.prepareDeployment;
import de.ipb_halle.lbac.admission.User;
import de.ipb_halle.lbac.material.mocks.MaterialEditSaverMock;
import de.ipb_halle.lbac.material.common.service.MaterialService;
import de.ipb_halle.lbac.material.mocks.TaxonomyBeanMock;
import de.ipb_halle.lbac.material.structure.MoleculeService;
import de.ipb_halle.lbac.material.structure.StructureInformationSaverMock;
import de.ipb_halle.lbac.project.ProjectService;
import java.util.HashMap;
import java.util.List;
import javax.faces.component.UIViewRoot;
import javax.faces.component.behavior.BehaviorBase;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author fmauz
 */
@RunWith(Arquillian.class)
public class TaxonomyBeanTest extends TestBase {

    @Inject
    private TaxonomyService taxonomyService;

    @Inject
    private MaterialService materialService;

    protected TaxonomyBean bean;

    protected User owner;
    protected TreeNode nodeToOperateOn;

    @Before
    public void init() {

        bean = new TaxonomyBeanMock();
        bean.setTaxonomyService(taxonomyService);
        bean.init();

        UserBeanMock userBean = new UserBeanMock();
        userBean.setCurrentAccount(memberService.loadUserById(GlobalAdmissionContext.PUBLIC_ACCOUNT_ID));
        materialService.setUserBean(userBean);
        materialService.setEditedMaterialSaver(new MaterialEditSaverMock(materialService));
        materialService.setStructureInformationSaver(new StructureInformationSaverMock(materialService.getEm()));
        bean.setMaterialService(materialService);
        owner = memberService.loadUserById(GlobalAdmissionContext.PUBLIC_ACCOUNT_ID);
        Integer userGroups = GlobalAdmissionContext.getPublicReadACL().getId();
        createTaxonomyTreeInDB(userGroups, owner.getId());

    }

    @After
    public void finish() {

    }

    @Test
    public void test001_reloadTaxonomies() {
        LoginEvent event = new LoginEvent(owner);
        bean.setCurrentAccount(event);
        bean.getTreeController().reloadTreeNode();
        TreeNode tree = bean.getTreeController().getTaxonomyTree();

        int i = 0;
    }

    @Test
    public void test002_editTaxonomy() {
        LoginEvent event = new LoginEvent(owner);
        bean.setCurrentAccount(event);
        bean.getTreeController().reloadTreeNode();
        TreeNode tree = bean.getTreeController().getTaxonomyTree();

        bean.onTaxonomySelect(createSelectEvent("Pilze_de", tree));
        Taxonomy taxonomy = (Taxonomy) bean.getSelectedTaxonomy().getData();
        Assert.assertEquals("Pilze_de", taxonomy.getFirstName());

        bean.actionClickFirstButton();
        Assert.assertEquals(TaxonomyBean.Mode.EDIT, bean.getMode());
        bean.actionClickFirstButton();
        Assert.assertEquals(TaxonomyBean.Mode.SHOW, bean.getMode());

        bean.actionClickFirstButton();
        Assert.assertEquals(TaxonomyBean.Mode.EDIT, bean.getMode());

        bean.onTaxonomySelect(createSelectEvent("Leben_de", tree));

        bean.actionClickFirstButton();
        Assert.assertEquals(TaxonomyBean.Mode.SHOW, bean.getMode());

    }

    @Test
    public void test003_newTaxonomy() {
        LoginEvent event = new LoginEvent(owner);
        bean.setCurrentAccount(event);
        bean.getTreeController().reloadTreeNode();

        bean.actionClickSecondButton();
        TreeNode tree = bean.getTreeController().getTaxonomyTree();
        bean.onTaxonomySelect(createSelectEvent("Champignonartige_de", tree));

        Assert.assertEquals(4, bean.getLevelController().getLevels().size());
        Assert.assertEquals(600, (int) bean.getLevelController().getLevels().get(0).getRank());

        bean.nameController.getNames().get(0).setValue("test003_de");
        bean.nameController.getNames().get(0).setLanguage("de");
        bean.nameController.addNewEmptyName(bean.nameController.getNames());
        bean.nameController.getNames().get(1).setLanguage("en");
        bean.nameController.getNames().get(1).setValue("test003_en");
        bean.getLevelController().setSelectedLevel(bean.getLevelController().getLevels().get(0));

        List<Taxonomy> taxos = taxonomyService.loadTaxonomy(new HashMap<>(), true);
        Assert.assertEquals(21, taxos.size());
        bean.actionClickSecondButton();

        taxos = taxonomyService.loadTaxonomy(new HashMap<>(), true);
        Assert.assertEquals(22, taxos.size());
        tree = bean.getTreeController().getTaxonomyTree();
        nodeToOperateOn = null;
        createSelectEvent("test003_de", tree);

        Taxonomy newTaxo = (Taxonomy) nodeToOperateOn.getData();
        Assert.assertNotNull(newTaxo);

        bean.actionClickSecondButton();
        bean.nameController.getNames().get(0).setValue("toCancel");
        bean.nameController.getNames().get(0).setLanguage("en");
        bean.getLevelController().setSelectedLevel(bean.getLevelController().getLevels().get(0));
        bean.actionClickFirstButton();
        taxos = taxonomyService.loadTaxonomy(new HashMap<>(), true);
        Assert.assertEquals(22, taxos.size());
        Assert.assertEquals(TaxonomyBean.Mode.SHOW, bean.getMode());

    }

    private NodeSelectEvent createSelectEvent(String nameOfTaxToSelect, TreeNode tree) {
        setTaxonomyFromTree(nameOfTaxToSelect, tree);

        return new NodeSelectEvent(
                new UIViewRoot(),
                new BehaviorBase(),
                nodeToOperateOn
        );
    }

    private void setTaxonomyFromTree(String name, TreeNode tree) {
        Taxonomy taxo = (Taxonomy) tree.getData();
        System.out.println(taxo.getFirstName());
        if (taxo.getFirstName().equals(name)) {
            nodeToOperateOn = tree;
        }
        for (TreeNode n : tree.getChildren()) {
            setTaxonomyFromTree(name, n);
        }
    }

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive deployment = prepareDeployment("TaxonomyBeanTest.war")
                .addClass(TaxonomyService.class)
                .addClass(TaxonomyBean.class)
                .addClass(MoleculeService.class)
                .addClass(TaxonomyService.class)
                .addClass(TaxonomyNestingService.class)
                .addClass(ProjectService.class)
                .addClass(TissueService.class)
                .addClass(MaterialService.class);
        return UserBeanDeployment.add(deployment);
    }
}
