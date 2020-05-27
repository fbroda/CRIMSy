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

import de.ipb_halle.lbac.material.service.*;
import de.ipb_halle.lbac.admission.GlobalAdmissionContext;
import de.ipb_halle.lbac.admission.LdapProperties;
import de.ipb_halle.lbac.admission.UserBean;
import de.ipb_halle.lbac.announcement.membership.MembershipOrchestrator;
import de.ipb_halle.lbac.base.TestBase;
import static de.ipb_halle.lbac.base.TestBase.prepareDeployment;
import de.ipb_halle.lbac.globals.KeyManager;
import de.ipb_halle.lbac.material.bean.TaxonomyBean;
import de.ipb_halle.lbac.material.common.HazardInformation;
import de.ipb_halle.lbac.material.common.MaterialName;
import de.ipb_halle.lbac.material.common.StorageClassInformation;
import de.ipb_halle.lbac.material.difference.MaterialIndexDifference;
import de.ipb_halle.lbac.material.difference.TaxonomyDifference;
import de.ipb_halle.lbac.project.ProjectService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author fmauz
 */
@RunWith(Arquillian.class)
public class TaxonomyHistoryControllerTest extends TestBase {

    TaxonomyHistoryController historyController;
    TaxonomyLevelController levelController;
    TaxonomyRenderController renderController;
    TaxonomyBean bean;
    @Inject
    private TaxonomyService taxonomyService;
    @Inject
    private MaterialService materialService;

    @Before
    public void init() {

    }

    @Test
    public void test001_applyPositiveHistory() {
        UUID actorid = UUID.randomUUID();
        List<MaterialName> names = new ArrayList<>();
        names.add(new MaterialName("name_1", "de", 0));
        names.add(new MaterialName("name_2", "en", 1));
        Taxonomy t = new Taxonomy(0, names, new HazardInformation(), new StorageClassInformation(), new ArrayList<>(),null,null);
        bean = new TaxonomyBean();
        TreeNode node = new DefaultTreeNode(t);
        bean.setSelectedTaxonomy(node);

        MaterialIndexDifference nameDiff = new MaterialIndexDifference();
        nameDiff.setLanguageNew(Arrays.asList("de", "en"));
        nameDiff.setLanguageOld(Arrays.asList("de", null));
        nameDiff.setValuesOld(Arrays.asList("name_0", null));
        nameDiff.setValuesNew(Arrays.asList("name_1", "name_2"));
        nameDiff.setRankOld(Arrays.asList(1, null));
        nameDiff.setRankNew(Arrays.asList(1, 2));
        nameDiff.setTypeId(Arrays.asList(1, 1));
        Calendar c = Calendar.getInstance();
        c.set(2020, 11, 2, 21, 45, 12);
        Date d1 = c.getTime();
        nameDiff.initialise(0, actorid, c.getTime());

        t.getHistory().addDifference(nameDiff);

        TaxonomyDifference taxoDiff = new TaxonomyDifference();
        taxoDiff.setNewLevelId(3);
        taxoDiff.setOldLevelId(4);
        c.add(Calendar.YEAR, 1);
        Date d2 = c.getTime();
        taxoDiff.initialise(0, actorid, d2);
        t.getHistory().addDifference(taxoDiff);

        TaxonomyNameController nameController = new TaxonomyNameController(bean);
        historyController = new TaxonomyHistoryController(bean, nameController, taxonomyService, memberService);
        levelController = new TaxonomyLevelController(bean);
        renderController = new TaxonomyRenderController(bean, nameController, levelController,memberService);
        TaxonomyTreeController tc = new TaxonomyTreeController(node, taxonomyService, levelController);

        bean.setHistoryController(historyController);
        bean.setLevelController(levelController);
        bean.setMaterialService(materialService);
        bean.setMode(TaxonomyBean.Mode.SHOW);
        bean.setNameController(nameController);
        bean.setRenderController(renderController);
        bean.setTaxonomyService(taxonomyService);
        bean.setTreeController(tc);
        bean.setValidityController(new TaxonomyValidityController(bean));

        bean.initHistoryDate();

        Assert.assertEquals(d2, historyController.getDateOfShownHistory());

        Assert.assertTrue(renderController.isHistoryForwardButtonDisabled());
        Assert.assertFalse(renderController.isHistoryBackButtonDisabled());

        historyController.actionSwitchToEarlierVersion();
        Assert.assertEquals(d1, historyController.getDateOfShownHistory());
        Assert.assertFalse(renderController.isHistoryForwardButtonDisabled());
        Assert.assertFalse(renderController.isHistoryBackButtonDisabled());

        historyController.actionSwitchToEarlierVersion();
        Assert.assertEquals(null, historyController.getDateOfShownHistory());
        Assert.assertFalse(renderController.isHistoryForwardButtonDisabled());
        Assert.assertTrue(renderController.isHistoryBackButtonDisabled());

    }

    @Deployment
    public static WebArchive createDeployment() {
        return prepareDeployment("TaxonomyHistoryControllerTest.war")
                .addClass(ProjectService.class)
                .addClass(GlobalAdmissionContext.class)
                .addClass(MaterialService.class)
                .addClass(UserBean.class)
                .addClass(MembershipOrchestrator.class)
                .addClass(MoleculeService.class)
                .addClass(LdapProperties.class)
                .addClass(KeyManager.class)
                .addClass(TissueService.class)
                .addClass(TaxonomyService.class);
    }
}
