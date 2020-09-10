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
package de.ipb_halle.lbac.file.save;

import de.ipb_halle.lbac.file.StemmedWordOrigin;
import de.ipb_halle.lbac.file.TermVector;
import de.ipb_halle.tx.text.LanguageDetectorFilter;
import de.ipb_halle.tx.text.ParseTool;
import de.ipb_halle.tx.text.TermVectorFilter;
import de.ipb_halle.tx.text.properties.Language;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 *
 * @author fmauz
 */
public class FileAnalyser {

    protected ParseTool parseTool = new ParseTool();
    protected InputStream filterDefinition;
    protected Integer fileId;

    public FileAnalyser(InputStream filterDefinition) {
        this.filterDefinition = filterDefinition;
    }

    public String getLanguage() {
        SortedSet<Language> languages = (SortedSet) this.parseTool
                .getFilterData()
                .getValue(LanguageDetectorFilter.LANGUAGE_PROP);
        Map<String, Integer> countMap = new HashMap<> ();
        int maxCount = 0;
        String maxLang = null;
        for (Language lang : languages) {
            String langString = lang.getLanguage();
            int count = lang.getEnd() - lang.getStart();
            Integer totalCount = countMap.get(langString);
            if (totalCount != null) {
                totalCount += count;
            } else {
                totalCount = count;
            }
            if (totalCount > maxCount) {
                maxCount = totalCount;
                maxLang = langString;
            }
            countMap.put(langString, totalCount);
        }

        return (maxLang != null) ? maxLang : "undefined";
    }

    public void analyseFile(String location, Integer fileId) throws FileNotFoundException {
        parseTool.setFilterDefinition(filterDefinition);
        parseTool.setInputStream(new FileInputStream(
                new File(location)));
        parseTool.initFilter();
        parseTool.parse();
        this.fileId = fileId;

    }

    public List<StemmedWordOrigin> getWordOrigins() {
        List<StemmedWordOrigin> wordOrigins = new ArrayList<>();
        Map<String, Set<String>> map = (Map) parseTool.getFilterData().getValue(TermVectorFilter.STEM_DICT);
        for (String s : map.keySet()) {
            wordOrigins.add(new StemmedWordOrigin(s, map.get(s)));
        }
        return wordOrigins;
    }

    public List<TermVector> getTermVector() {
        List<TermVector> termVectors = new ArrayList<>();
        Map<String, Integer> termvectorMap = (Map) parseTool.getFilterData().getValue(TermVectorFilter.TERM_VECTOR);
        for (String tv : termvectorMap.keySet()) {
            termVectors.add(new TermVector(tv, fileId, termvectorMap.get(tv)));
        }
        return termVectors;
    }
}
