/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lasombras.android.scm;

import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.lasombras.android.scm.model.PlayerClass;
import com.lasombras.android.scm.model.Source;
import com.lasombras.android.scm.model.SpellFilter;
import com.lasombras.android.scm.utils.SpellDatas;
import com.lasombras.android.scm.view.ListPreferenceMultiSelect;

public class SpellSystemPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setPreferenceScreen(createPreferenceHierarchy());
    }

    private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        
        // List preferences 
        PreferenceCategory listSystem = new PreferenceCategory(this);
        listSystem.setTitle(R.string.list_preferences);
        root.addPreference(listSystem);
        
        // Light Mode preference
        CheckBoxPreference lightModePref = new CheckBoxPreference(this);
        lightModePref.setKey("checkbox_light_mode_preference");
        lightModePref.setTitle(R.string.title_light_mode_preference);
        lightModePref.setSummary(R.string.summary_light_mode_preference);
        lightModePref.setDefaultValue(false);
        listSystem.addPreference(lightModePref);
        
        // Original Title Mode preference
        CheckBoxPreference originalTitleModePref = new CheckBoxPreference(this);
        originalTitleModePref.setKey("checkbox_title_list_preference");
        originalTitleModePref.setTitle(R.string.title_title_list_preference);
        originalTitleModePref.setSummary(R.string.summary_title_list_preference);
        originalTitleModePref.setDefaultValue(false);
        listSystem.addPreference(originalTitleModePref);
  
        // Card preferences 
        PreferenceCategory cardSystem = new PreferenceCategory(this);
        cardSystem.setTitle(R.string.card_preferences);
        root.addPreference(cardSystem);
        // Original Title Mode preference
        CheckBoxPreference originalSpellTitlePref = new CheckBoxPreference(this);
        originalSpellTitlePref.setKey("checkbox_title_card_preference");
        originalSpellTitlePref.setTitle(R.string.title_title_card_preference);
        originalSpellTitlePref.setSummary(R.string.summary_title_card_preference);
        originalSpellTitlePref.setDefaultValue(false);
        cardSystem.addPreference(originalSpellTitlePref);

        
        // Filter preferences 
        PreferenceCategory filterSystem = new PreferenceCategory(this);
        filterSystem.setTitle(R.string.filter_preferences);
        root.addPreference(filterSystem);
        
        //Source Preference
        ListPreferenceMultiSelect sourcePref = new ListPreferenceMultiSelect(this);
 
        List<Source> sourcesList = SpellDatas.instance().getSources();
        Collections.sort(sourcesList);
        CharSequence[] textsSource = new CharSequence[sourcesList.size()];
        CharSequence[] valuesSource = new CharSequence[sourcesList.size()];
        String defaultValue = "";
    	for(int i = 0; i < sourcesList.size(); i++) {
    		textsSource[i] = sourcesList.get(i).getTitle();
    		valuesSource[i] = ""+sourcesList.get(i).getId();
    		defaultValue += sourcesList.get(i).getId() + ListPreferenceMultiSelect.SEPARATOR;
    	}
        
        sourcePref.setEntries(textsSource);
        sourcePref.setEntryValues(valuesSource);
        sourcePref.setDialogTitle(R.string.filter_source_preferences);
        sourcePref.setKey("source_preference");
        sourcePref.setTitle(R.string.title_filter_source_preferences);
	    sourcePref.setSummary(R.string.summary_filter_source_preferences);
	    sourcePref.setDefaultValue(defaultValue);
	    filterSystem.addPreference(sourcePref);

        //PlayerClass Preference
        ListPreferenceMultiSelect playerClassPref = new ListPreferenceMultiSelect(this);
 
        List<PlayerClass> playerClassList = SpellDatas.instance().getPlayerClasses(false);
        Collections.sort(playerClassList);
        CharSequence[] textsPlayerClasse = new CharSequence[playerClassList.size()];
        CharSequence[] valuesPlayerClasse = new CharSequence[playerClassList.size()];
        String defaultPlayerClassesValue = "";
    	for(int i = 0; i < playerClassList.size(); i++) {
    		textsPlayerClasse[i] = playerClassList.get(i).getTitle();
    		valuesPlayerClasse[i] = ""+playerClassList.get(i).getId();
    		defaultPlayerClassesValue += playerClassList.get(i).getId() + ListPreferenceMultiSelect.SEPARATOR;
    	}
        
    	playerClassPref.setEntries(textsPlayerClasse);
    	playerClassPref.setEntryValues(valuesPlayerClasse);
    	playerClassPref.setDialogTitle(R.string.filter_player_class_preferences);
    	playerClassPref.setKey("player_class_preference");
    	playerClassPref.setTitle(R.string.title_filter_player_class_preferences);
    	playerClassPref.setSummary(R.string.summary_filter_player_class_preferences);
    	playerClassPref.setDefaultValue(defaultPlayerClassesValue);
	    filterSystem.addPreference(playerClassPref);
 
        //Recherche Mode
        ListPreference searchPref = new ListPreference(this);
        searchPref.setEntries(new CharSequence[]{getString(R.string.languageSpellName),getString(R.string.originalSpellName),getString(R.string.allSpellName)});
        searchPref.setEntryValues(new CharSequence[]{""+SpellFilter.SEARCH_TITLE,""+SpellFilter.SEARCH_ORIGINAL_NAME,""+SpellFilter.SEARCH_ALL});
        searchPref.setDialogTitle(R.string.dialog_filter_search_preferences);
        searchPref.setKey("search_preference");
        searchPref.setTitle(R.string.title_search_preferences);
        searchPref.setSummary(R.string.summary_search_preferences);
        searchPref.setDefaultValue(""+SpellFilter.SEARCH_TITLE);
        filterSystem.addPreference(searchPref);


        // Application preferences 
        PreferenceCategory applicationSystem = new PreferenceCategory(this);
        applicationSystem.setTitle(R.string.application_preferences);
        root.addPreference(applicationSystem);

        // Original Title Mode preference
        CheckBoxPreference cleanDataPref = new CheckBoxPreference(this);
        cleanDataPref.setKey("clean_data_preference");
        cleanDataPref.setTitle(R.string.title_clean_data_preference);
        cleanDataPref.setSummary(R.string.summary_clean_data_preference);
        cleanDataPref.setDefaultValue(false);
        applicationSystem.addPreference(cleanDataPref);
        
        return root;
   }

}
