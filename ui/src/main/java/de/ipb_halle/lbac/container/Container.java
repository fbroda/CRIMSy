/*
 * Leibniz Bioactives Cloud
 * Copyright 2017 Leibniz-Institut f. Pflanzenbiochemie
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
package de.ipb_halle.lbac.container;

import de.ipb_halle.lbac.items.Item;
import de.ipb_halle.lbac.container.entity.ContainerEntity;
import de.ipb_halle.lbac.entity.DTO;
import de.ipb_halle.lbac.project.Project;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author fmauz
 */
public class Container implements DTO, Serializable {

    public enum DimensionType {
        NONE,
        ZERO_DIMENSION,
        ONE_DIMENSION,
        TWO_DIMENSION
    }
    private Logger logger = LogManager.getLogger(this.getClass().getName());

    private int id;
    private Container parentContainer;
    private String label;
    private Project project;
    private String dimension;
    private ContainerType type;
    private String fireSection;
    private String gmosavety;
    private String barCode;
    private Item[][][] items;
    List<Container> containerHierarchy = new ArrayList<>();
    private boolean deactivated;

    public Container() {
        deactivated = false;
    }

    public Container(
            ContainerEntity dbentity,
            Project p,
            Container parentContainer) {
        this.id = dbentity.getId();
        this.parentContainer = parentContainer;
        this.label = dbentity.getLabel();
        this.project = p;
        this.dimension = dbentity.getDimension();
        this.fireSection = dbentity.getFiresection();
        this.gmosavety = dbentity.getGmosavety();
        this.barCode = dbentity.getBarcode();
        this.deactivated = dbentity.isDeactivated();
        if (dimension != null) {
            String[] size = dimension.split(";");
            if (size.length > 2) {
                items = new Item[Integer.valueOf(size[0])][Integer.valueOf(size[1])][Integer.valueOf(size[2])];
            } else if (size.length == 2) {
                items = new Item[Integer.valueOf(size[0])][Integer.valueOf(size[1])][1];
            } else if (size.length == 1) {
                items = new Item[Integer.valueOf(size[0])][1][1];
            }
        }
    }

    public DimensionType getDimensionType() {
        if (dimension == null) {
            return DimensionType.NONE;
        } else {
            return DimensionType.TWO_DIMENSION;
        }
    }

    public Item[][][] getItems() {
        return items;
    }

    public void setItems(Item[][][] items) {
        this.items = items;
    }

    public Container(
            ContainerEntity dbentity) {
        this.id = dbentity.getId();
        this.label = dbentity.getLabel();
        this.dimension = dbentity.getDimension();
        this.fireSection = dbentity.getFiresection();
        this.gmosavety = dbentity.getGmosavety();
        this.barCode = dbentity.getBarcode();
        this.deactivated = dbentity.isDeactivated();

    }

    public int getId() {
        return id;
    }

    public Item getItemAtPos(int x, int y, int z) {
        if (dimension == null) {
            return null;
        }
        return items[x][y][z];
    }

    public void setId(int id) {
        this.id = id;
    }

    public Container getParentContainer() {
        if (parentContainer == null && containerHierarchy.size() > 0) {
            return containerHierarchy.get(0);
        }
        return parentContainer;
    }

    public void setParentContainer(Container parentContainer) {
        this.parentContainer = parentContainer;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public ContainerType getType() {
        return type;
    }

    public void setType(ContainerType type) {
        this.type = type;
    }

    public String getFireSection() {
        return fireSection;
    }

    public void setFireSection(String fireSection) {
        this.fireSection = fireSection;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getLocation(boolean inclusiveItsself) {
        if (containerHierarchy.isEmpty()) {
            return label;
        } else {
            return containerHierarchy
                    .stream()
                    .map(r -> r.getLabel())
                    .collect(Collectors.joining("<br>"));

        }

    }

    public int[] getDimensionIndex() {
        if (dimension != null) {
            String[] size = dimension.split(";");
            if (size.length > 2) {
                int x = Integer.valueOf(size[0]);
                int y = Integer.valueOf(size[1]);
                int z = Integer.valueOf(size[2]);
                return new int[]{x, y, z};
            } else if (size.length == 2) {
                int x = Integer.valueOf(size[0]);
                int y = Integer.valueOf(size[1]);
                return new int[]{x, y, 1};
            } else if (size.length == 1) {
                int x = Integer.valueOf(size[0]);
                return new int[]{x, 1, 1};
            }
        }
        return null;
    }

    public List<Container> getContainerHierarchy() {
        return containerHierarchy;
    }

    public String getGmosavety() {
        return gmosavety;
    }

    public void setGmosavety(String securitylevel) {
        this.gmosavety = securitylevel;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    @Override
    public ContainerEntity createEntity() {
        ContainerEntity dbe = new ContainerEntity();
        if (id > 0) {
            dbe.setId(id);
        }
        if (this.getParentContainer() != null) {
            dbe.setParentcontainer(this.getParentContainer().getId());
        }
        dbe.setLabel(this.getLabel());
        if (this.getProject() != null) {
            dbe.setProjectid(this.getProject().getId());
        }
        dbe.setDimension(this.getDimension());
        dbe.setType(this.getType().getName());
        dbe.setFiresection(this.getFireSection());
        dbe.setGmosavety(this.getGmosavety());
        dbe.setBarcode(this.getBarCode());
        dbe.setDeactivated(this.isDeactivated());
        return dbe;
    }

    public Container copy() {
        Container c = new Container();
        c.setBarCode(barCode);
        c.setDeactivated(deactivated);
        c.setDimension(dimension);
        c.setFireSection(fireSection);
        c.setGmosavety(gmosavety);
        c.setId(id);
        c.setItems(items);
        c.setLabel(label);
        c.setParentContainer(parentContainer);
        c.setProject(project);
        c.setType(type);
        c.getContainerHierarchy().addAll(containerHierarchy);
        return c;
    }

}