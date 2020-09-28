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
package de.ipb_halle.lbac.items.bean;

import de.ipb_halle.lbac.admission.ACObjectBean;
import de.ipb_halle.lbac.admission.LoginEvent;
import de.ipb_halle.lbac.container.Container;
import de.ipb_halle.lbac.admission.User;
import de.ipb_halle.lbac.items.Item;
import de.ipb_halle.lbac.container.service.ContainerService;
import de.ipb_halle.lbac.admission.ACList;
import de.ipb_halle.lbac.admission.ACObject;
import de.ipb_halle.lbac.globals.ACObjectController;
import de.ipb_halle.lbac.items.service.ItemService;
import de.ipb_halle.lbac.material.common.MaterialName;
import de.ipb_halle.lbac.material.common.service.MaterialService;
import de.ipb_halle.lbac.navigation.Navigator;
import de.ipb_halle.lbac.project.ProjectService;
import de.ipb_halle.lbac.admission.MemberService;
import de.ipb_halle.lbac.items.ItemHistory;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author fmauz
 */
@SessionScoped
@Named
public class ItemOverviewBean implements Serializable, ACObjectBean {
    
    private Logger logger = LogManager.getLogger(this.getClass().getName());
    private final static String MESSAGE_BUNDLE = "de.ipb_halle.lbac.i18n.messages";
    private final int PAGE_SIZE = 10;
    
    @Inject
    protected ContainerService containerService;
    @Inject
    protected ItemBean itemBean;
    @Inject
    protected ItemService itemService;
    @Inject
    protected Navigator navigator;
    @Inject
    protected MaterialService materialService;
    @Inject
    protected MemberService memberService;
    @Inject
    protected ProjectService projectService;
    
    private ACObjectController acObjectController;
    private CMapFactory cmapFactory = new CMapFactory();
    protected User currentUser;
    private int firstResult;
    private int itemAmount;
    private Item itemInFocus;
    private ItemLocaliser itemLocaliser = new ItemLocaliser();
    private List<Item> items;
    private SearchMaskValues searchMaskValues = new SearchMaskValues();
    
    public void actionApplySearchFilter() {
        reloadItems();
    }
    
    public void actionClearSearchFilter() {
        searchMaskValues = new SearchMaskValues();
    }
    
    public void actionFirstItems() {
        firstResult = 0;
        reloadItems();
    }
    
    public void actionLastItems() {
        firstResult -= PAGE_SIZE;
        firstResult = Math.max(0, firstResult);
        reloadItems();
    }
    
    public void actionNextItems() {
        firstResult += PAGE_SIZE;
        firstResult = Math.min(firstResult, itemAmount - PAGE_SIZE);
        reloadItems();
    }
    
    public void actionEndItems() {
        firstResult = itemAmount - PAGE_SIZE;
        firstResult = Math.max(0, firstResult);
        reloadItems();
    }
    
    public void actionStartItemEdit(Item i) {
        itemBean.actionStartItemEdit(i);
        navigator.navigate("/item/itemEdit");
    }
    
    public List<Item> getItems() {
        return items;
    }
    
    public void reloadItems() {
        Map<String, String> cmap = cmapFactory.createCmap(searchMaskValues);
        itemAmount = itemService.getItemAmount(currentUser, cmap);
        items = itemService.loadItems(currentUser, cmap, firstResult, PAGE_SIZE);
        items = itemLocaliser.localiseContainerNamesOf(items);
    }
    
    public void setCurrentAccount(@Observes LoginEvent evt) {
        currentUser = evt.getCurrentAccount();
        firstResult = 0;
        searchMaskValues = new SearchMaskValues();
        reloadItems();
    }
    
    public String getAmountString(Item i) {
        return i.getAmount() + " " + i.getUnit();
    }
    
    public String getMaterialName(Item i) {
        String back = "";
        for (MaterialName mn : i.getMaterial().getNames()) {
            back += mn.getValue() + "<br/>";
        }
        return back;
    }
    
    public String getOwnerString(Item i) {
        String back = "";
        if (i.getProject() != null) {
            back = i.getProject().getName();
        }
        back += "/" + i.getOwner().getName();
        return back;
    }
    
    public String getLocationOfItem(Item i) {
        return i.getNestedLocation();
    }
    
    public String getDatesOfItem(Item i) {
        String back = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (i.getcTime() != null) {
            back = "created     : " + sdf.format(i.getcTime()) + "<br/>";
        }
        if (!i.getHistory().isEmpty()) {
            back += "modified: - " + sdf.format(i.getHistory().firstKey());
        }
        return back;
    }
    
    public int getItemAmount() {
        return itemAmount;
    }
    
    public boolean isBackDeactivated() {
        return firstResult == 0;
    }
    
    public boolean isForwardDeactivated() {
        return (itemAmount - firstResult) <= PAGE_SIZE;
    }
    
    public String getItemNavigationInfo() {
        int leftBorder = firstResult + 1;
        int rightBorder = (int) Math.min(PAGE_SIZE + firstResult, itemAmount);
        if (itemAmount > 0) {
            return String.format("%d - %d of %d items shown", leftBorder, rightBorder, itemAmount);
        } else {
            return "no items with active filters found";
        }
    }
    
    public List<String> getSimilarMaterialNames(String input) {
        List<String> names = materialService.getSimilarMaterialNames(input, currentUser);
        return names;
    }
    
    public List<String> getSimilarProjectNames(String input) {
        return projectService.getSimilarProjectNames(input, currentUser);
    }
    
    public List<String> getSimilarUserNames(String input) {
        return new ArrayList<>(memberService.loadSimilarUserNames(input));
    }
    
    public List<String> getSimilarContainerNames(String input) {
        List<String> names = new ArrayList<>();
        Set<Container> container = containerService.getSimilarContainerNames(input, currentUser);
        for (Container c : container) {
            names.add(c.getLabel());
        }
        return names;
    }
    
    @Override
    public ACObjectController getAcObjectController() {
        return acObjectController;
    }
    
    @Override
    public void applyAclChanges() {
        itemService.saveItem(itemInFocus);
        ItemHistory h = new ItemHistory();
        h.setAction("EDIT");
        h.setActor(currentUser);
        h.setItem(itemInFocus);
        h.setMdate(new Date());
        h.setAcListChange(acObjectController.getOriginalAcList(), itemInFocus.getACList());
        itemService.saveItemHistory(h);
        
        reloadItems();
    }
    
    @Override
    public void cancelAclChanges() {
        
    }
    
    @Override
    public void actionStartAclChange(ACObject aco) {
        itemInFocus = (Item) aco;
        acObjectController = new ACObjectController(aco, memberService.loadGroups(new HashMap<>()), this, itemInFocus.getDescription());
    }
    
    public SearchMaskValues getSearchMaskValues() {
        return searchMaskValues;
    }
    
}
