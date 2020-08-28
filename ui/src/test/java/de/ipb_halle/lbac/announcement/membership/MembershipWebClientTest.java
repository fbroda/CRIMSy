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
package de.ipb_halle.lbac.announcement.membership;

import de.ipb_halle.lbac.admission.AdmissionSubSystemType;
import de.ipb_halle.lbac.admission.GlobalAdmissionContext;
import de.ipb_halle.lbac.announcement.membership.mock.MembershipWebServiceMock;
import de.ipb_halle.lbac.admission.MembershipWebClient;
import de.ipb_halle.lbac.base.TestBase;
import static de.ipb_halle.lbac.base.TestBase.prepareDeployment;
import de.ipb_halle.lbac.entity.CloudNode;
import de.ipb_halle.lbac.admission.Group;
import de.ipb_halle.lbac.admission.User;
import de.ipb_halle.lbac.file.FileEntityService;
import de.ipb_halle.lbac.globals.KeyManager;
import de.ipb_halle.lbac.service.FileService;
import de.ipb_halle.lbac.admission.MemberService;
import de.ipb_halle.lbac.service.NodeService;
import de.ipb_halle.lbac.webservice.Updater;
import de.ipb_halle.lbac.webservice.service.WebRequestAuthenticator;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author fmauz
 */
@RunWith(Arquillian.class)
public class MembershipWebClientTest extends TestBase {
    
    @Deployment
    public static WebArchive createDeployment() {
        
        return prepareDeployment("MembershipWebServiceTest.war")
                .addPackage(MemberService.class.getPackage())
                .addPackage(NodeService.class.getPackage())
                .addClass(GlobalAdmissionContext.class)
                .addClass(GlobalAdmissionContext.class)
                .addClass(WebRequestAuthenticator.class)
                .addClass(MembershipWebServiceMock.class)
                .addClass(Updater.class)
                .addClass(KeyManager.class)
                .addClass(FileService.class)
                .addClass(FileEntityService.class);
        
    }
    
    @Inject
    KeyManager keymanager;
    
    @Before
    public void setUp() {
        super.setUp();
        initializeBaseUrl();
        initializeKeyStoreFactory();
    }
    
    @Test
    public void announceUserToRemoteNodesTest() throws Exception {
        MembershipWebClient client = new MembershipWebClient();
        Set<Group> groups = new HashSet<>();
        Group g = new Group();
        g.setId(-1000);
        g.setName("testGroup");
        g.setNode(nodeService.getLocalNode());
        g.setSubSystemData("G");
        g.setSubSystemType(AdmissionSubSystemType.LOCAL);
        
        User u = new User();
        u.setId(-1001);
        u.setName("testUser");
        u.setLogin("testUserLogIn");
        
        CloudNode cn = cloudNodeService.loadCloudNode(TESTCLOUD, TEST_NODE_ID);
        
        client.announceUserToRemoteNodes(
                u,
                cn,
                groups,
                nodeService.getLocalNodeId(),
                keymanager.getLocalPrivateKey(TESTCLOUD)
        );
        MembershipWebServiceMock.SUCCESS = false;
        
        client.announceUserToRemoteNodes(
                u,
                cn,
                groups,
                nodeService.getLocalNodeId(),
                keymanager.getLocalPrivateKey(TESTCLOUD)
        );
        
    }
}
