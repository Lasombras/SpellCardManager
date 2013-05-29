package com.lasombras.android.scm.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lasombras.android.scm.R;
import com.lasombras.android.scm.model.PlayerClass;
import com.lasombras.android.scm.model.School;
import com.lasombras.android.scm.model.SpellFilter;
import com.lasombras.android.scm.utils.SpellDatas;
import com.lasombras.android.scm.view.RangeSeekBarView.OnRangeSeekBarChangeListener;

public class SpellListFilterView extends LinearLayout {

	private TextView levelFilter;
	private int minLevel;
	private int maxLevel;
	
	private Spinner playerClassFilter;
	private MultipleSpinner schoolFilter;
	private CheckBox ckbLevelSort;
	private List<School> schoolsList;
	
	
	public SpellListFilterView(Context context, SpellFilter spellFilter) {
		super(context);
		
		minLevel = spellFilter.getLevelMin();
		maxLevel = spellFilter.getLevelMax();
		
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.spell_list_filter,this);
        
        levelFilter = (TextView)view.findViewById(R.spellListFilter.levelFilterText);
        levelFilter.setText("Niveau de " + minLevel + " à " + maxLevel);
        
       //Ajout de la seekBar
        RangeSeekBarView<Integer> seekBar = new RangeSeekBarView<Integer>(SpellFilter.LEVEL_MIN, SpellFilter.LEVEL_MAX, view.getContext());
        seekBar.setSelectedMinValue(minLevel);
        seekBar.setSelectedMaxValue(maxLevel);
        seekBar.setNotifyWhileDragging(true);
        seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {         	
                public void rangeSeekBarValuesChanged(Integer minValue, Integer maxValue) {
                	// handle changed range values
                    levelFilter.setText("Niveau de " + minValue + " à " + maxValue);
                    minLevel = minValue;
                    maxLevel = maxValue;
                }
        });

        // add RangeSeekBar to pre-defined layout
        ((LinearLayout)view.findViewById(R.spellListFilter.levelLinearLayout)).addView(seekBar);
        
        //List Player Class
        playerClassFilter = (Spinner)view.findViewById(R.spellListFilter.playerClassSpinner);
        List<PlayerClass> playerClassesList = SpellDatas.instance().getPlayerClasses();
    	Collections.sort(playerClassesList);
    	PlayerClass allPlayerClass = new PlayerClass(-1,"");
    	allPlayerClass.setTitle("Toutes");
    	playerClassesList.add(0, allPlayerClass);    	
        ArrayAdapter<PlayerClass> playerClassesSpinnerAdapter = new ArrayAdapter<PlayerClass>(context, android.R.layout.simple_spinner_item, playerClassesList);
        playerClassesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerClassFilter.setAdapter(playerClassesSpinnerAdapter);
    	for(int i = 0; i < playerClassesList.size(); i++) {
    		if(spellFilter.getPlayerClassId() == playerClassesList.get(i).getId()) {
    			playerClassFilter.setSelection(i);
    			break;
    		}
    	}
    	playerClassFilter.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(((PlayerClass)playerClassFilter.getSelectedItem()).getId() > 0)
					ckbLevelSort.setEnabled(true);
		    	else
					ckbLevelSort.setEnabled(false);
		 	}

			public void onNothingSelected(AdapterView<?> arg0) {
				ckbLevelSort.setEnabled(false);
			}
    		
    	});
    	
    	
    	//Tri par niveau
    	ckbLevelSort = (CheckBox)view.findViewById(R.spellListFilter.checkBoxLevelFilter);
    	ckbLevelSort.setChecked(spellFilter.isSortByLevel());
    	if(spellFilter.getPlayerClassId() > 0)
			ckbLevelSort.setEnabled(true);
    	else
			ckbLevelSort.setEnabled(false);

    	
        //Liste School
        schoolFilter = (MultipleSpinner)view.findViewById(R.spellListFilter.schoolSpinner);
        schoolsList = SpellDatas.instance().getSchools();
    	Collections.sort(schoolsList);
        //ArrayAdapter<School> schoolsSpinnerAdapter = new ArrayAdapter<School>(context, android.R.layout.simple_spinner_item, schoolsList);
        //schoolsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //schoolFilter.setAdapter(schoolsSpinnerAdapter);
    	boolean[] schoolSelection = new boolean[schoolsList.size()];
    	ArrayList<String> schoolName = new ArrayList<String>();
    	for(int i = 0; i < schoolsList.size(); i++) {
   			schoolSelection[i] = spellFilter.availableSchool(schoolsList.get(i).getId()); 
   			schoolName.add(schoolsList.get(i).getTitle());
    	}
    	schoolFilter.setAllTextResId(R.string.all_f);
    	schoolFilter.setNoneTextResId(R.string.none_f);
    	schoolFilter.setItems(schoolName, schoolSelection);
	}
	
	public void updateFilter(SpellFilter filter) {
		filter.setLevelMin(minLevel);
		filter.setLevelMax(maxLevel);
		filter.setPlayerClassId(((PlayerClass)playerClassFilter.getSelectedItem()).getId());
		filter.setSortByLevel(filter.getPlayerClassId() > 0 && ckbLevelSort.isChecked());
		
		filter.clearSchool();
		int[] schoolSelection = schoolFilter.getSelectedItemsPositions();
		for(int position : schoolSelection) {
			filter.addSchool(schoolsList.get(position).getId());
		}
		
	}

}
