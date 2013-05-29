package com.lasombras.android.scm.view;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lasombras.android.scm.R;
import com.lasombras.android.scm.model.Deck;
import com.lasombras.android.scm.model.PlayerClass;
import com.lasombras.android.scm.utils.SpellDatas;

public class DeckEditorView extends LinearLayout {

	private View view;
	
	
	public DeckEditorView(Context context, Deck deck) {
		super(context);
		
		
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.deck_editor,this);
                 
        //Liste Player Class
        Spinner playerClassFilter = (Spinner)view.findViewById(R.deckEditor.playerClassSpinner);
        List<PlayerClass> playerClassesList = SpellDatas.instance().getPlayerClasses();
    	Collections.sort(playerClassesList);
    	PlayerClass allPlayerClass = new PlayerClass(-1,"");
    	allPlayerClass.setTitle(context.getString(R.string.all_f));
    	playerClassesList.add(0, allPlayerClass);    	
        ArrayAdapter<PlayerClass> playerClassesSpinnerAdapter = new ArrayAdapter<PlayerClass>(context, android.R.layout.simple_spinner_item, playerClassesList);
        playerClassesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerClassFilter.setAdapter(playerClassesSpinnerAdapter);
        
        if(deck != null) {
        	((TextView)view.findViewById(R.deckEditor.titleDeckText)).setText(deck.getTitle());
        	for(int position = 0; position < playerClassesList.size(); position++) {
        		if(playerClassesList.get(position).getId() == deck.getPlayerClassId()) {
        			playerClassFilter.setSelection(position);
        			break;
        		}
        	}
        }
        
 	}

	public DeckEditorView(Context context) {
		this(context, null);        
 	}
	
	public String getTitleDeck() {
		return ((TextView)view.findViewById(R.deckEditor.titleDeckText)).getText().toString();
	}
	
	public int getPlayerClassFilter() {
		return ((PlayerClass)((Spinner)view.findViewById(R.deckEditor.playerClassSpinner)).getSelectedItem()).getId();
	}
	


}
