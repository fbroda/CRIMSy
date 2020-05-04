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
package de.ipb_halle.lbac.device.print;

import de.ipb_halle.lbac.entity.DTO;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fbroda
 */
public class Printer implements DTO {

    private String name;
    private String config;
    private String contact;
    private String driver;
    private String model;
    private String place;
    private PrinterStatus status;


    public PrinterEntity createEntity() {
        return new PrinterEntity()
            .setName(this.name)
            .setConfig(this.config)
            .setContact(this.contact)
            .setDriver(this.driver)
            .setModel(this.model)
            .setPlace(this.place)
            .setStatus(this.status);
    }

    public String getConfig() {
        return this.config;
    }

    public String getContact() {
        return this.contact;
    }

    public String getDriver() { 
        return this.driver;
    }

    public String getModel() {
        return this.model;
    }

    public String getName() {
        return this.name;
    }

    public String getPlace() {
        return this.place;
    }

    public PrinterStatus getStatus() {
        return this.status;
    }

    public Printer setConfig(String config) {
        this.config = config;
        return this;
    }

    public Printer setContact(String contact) {
        this.contact = contact;
        return this;
    }

    public Printer setDriver(String driver) { 
        this.driver = driver; 
        return this; 
    }

    public Printer setModel(String model) {
        this.model = model;
        return this;
    }

    public Printer setName(String name) {
        this.name = name;
        return this;
    }

    public Printer setPlace(String place) {
        this.place = place;
        return this;
    }

    public Printer setStatus(PrinterStatus status) {
        this.status = status;
        return this;
    }

}
