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
package de.ipb_halle.lbac.base;

import de.ipb_halle.lbac.EntityManagerService;
import de.ipb_halle.lbac.container.Container;
import de.ipb_halle.lbac.project.Project;

/**
 *
 * @author fmauz
 */
public class ItemCreator {

    protected EntityManagerService entityManagerService;

    protected String SQL_INSERT_ITEM = "INSERT INTO items "
            + "(materialid,amount,owner,aclist_id,description,projectid,containerid) "
            + "VALUES(%d,0,%d,%d,'%s',%d,%d)";

    protected String SQL_MAX_ITEM_ID = "SELECT max(id) from items";

    public ItemCreator(EntityManagerService entityManagerService) {
        this.entityManagerService = entityManagerService;
    }

    public int createItem(int userid, int aclid, Integer materialid, String desc) {
        entityManagerService.doSqlUpdate(
                String.format(SQL_INSERT_ITEM, materialid, userid, aclid, desc, null, null));
        return (Integer) entityManagerService.doSqlQuery(SQL_MAX_ITEM_ID).get(0);
    }

    public int createItem(int userid, int aclid, Integer materialid, String desc, Project p) {
        entityManagerService.doSqlUpdate(
                String.format(SQL_INSERT_ITEM, materialid, userid, aclid, desc, p.getId(), null));
        return (Integer) entityManagerService.doSqlQuery(SQL_MAX_ITEM_ID).get(0);
    }

    public int createItem(int userid, int aclid, Integer materialid, String desc, Container c) {
        entityManagerService.doSqlUpdate(
                String.format(SQL_INSERT_ITEM, materialid, userid, aclid, desc, null, c.getId()));
        return (Integer) entityManagerService.doSqlQuery(SQL_MAX_ITEM_ID).get(0);
    }

    public int createItem(int userid, int aclid, Integer materialid, String desc, Project p, Container c) {
        entityManagerService.doSqlUpdate(
                String.format(SQL_INSERT_ITEM, materialid, userid, aclid, desc, p.getId(), c.getId()));
        return (Integer) entityManagerService.doSqlQuery(SQL_MAX_ITEM_ID).get(0);
    }

}