package com.lasombras.android.scm;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lasombras.android.scm.adapter.SpellListSeparerAdapter;
import com.lasombras.android.scm.daolayer.DeckDAO;
import com.lasombras.android.scm.database.DBAdapter;
import com.lasombras.android.scm.entrepriselayer.facade.DeckFacade;
import com.lasombras.android.scm.model.Deck;
import com.lasombras.android.scm.model.Spell;
import com.lasombras.android.scm.model.SpellFilter;
import com.lasombras.android.scm.utils.RessourceCacheManager;
import com.lasombras.android.scm.utils.SpellDatas;

public class SpellDeckCardListActivity  extends Activity {

	public final static String PARAM_DECK_ID = "PARAM_DECK_ID";
	private DBAdapter dbAdapter;
	private SpellListSeparerAdapter adapter;	
	private Deck deck; 
	private ImageButton deckFavorite;
	private Spell selectedSpell;
	private EditText filterText;
	private TextWatcher filterTextWatcher;
	private SpellFilter spellFilter;
	private ArrayList<Spell> deckSpells;

	private static final int DIALOG_SPELL_ACTION = 1;

	@Override 
	public void onCreate(Bundle icicle) {  
		super.onCreate(icicle); 
		this.setContentView(R.layout.list_deck_cards);
		dbAdapter = new DBAdapter(this);

		// creation de nom objet de type ListSeparer 
		deckSpells = new ArrayList<Spell>();
		SQLiteDatabase db = dbAdapter.open();
		try {
			DeckDAO deckDAO = new DeckDAO(db);
			deck = deckDAO.get(getIntent().getExtras().getInt(PARAM_DECK_ID));
		} finally {
			db.close();
		}
		for(String originalName : deck.getSpellTitles()) {
			deckSpells.add(SpellDatas.instance().get(originalName));
		}
		TextView deckName = (TextView)findViewById(R.listDeckCards.deckNameText);
		deckName.setText(deck.getTitle());
		deckName.setTypeface(RessourceCacheManager.instance().getTitleFontType());
		TextView playerClassName = (TextView)findViewById(R.listDeckCards.spellPlayerClassText);

		spellFilter = new SpellFilter(PreferenceManager.getDefaultSharedPreferences(this));
		spellFilter.setPlayerClassId(deck.getPlayerClassId());

		filterText = (EditText) findViewById(R.listDeckCards.searchBoxText);
		filterText.setVisibility(View.GONE);
		filterText.setHeight(0);
		filterTextWatcher = new TextWatcher() {
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				spellFilter.setTitleName(s.toString());
				adapter.notifyDataSetChanged(spellFilter.applyFilter(deckSpells));
			}

		};
		filterText.addTextChangedListener(filterTextWatcher);

		if(deck.getPlayerClassId() > 0) {
			playerClassName.setText(SpellDatas.instance().getPlayerClass(deck.getPlayerClassId()).getTitle());
		} else {
			playerClassName.setText("");
		}
		playerClassName.setTypeface(RessourceCacheManager.instance().getTitleFontType());

		deckFavorite = (ImageButton)findViewById(R.listDeckCards.imageFavorite);
		if(deck.isFavorite())
			deckFavorite.setImageResource(R.drawable.deck_favorite);
		else
			deckFavorite.setImageResource(R.drawable.deck_no_favorite);

		deckFavorite.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {		
				changeFavorite();
			}
		});

		adapter = new SpellListSeparerAdapter(this, spellFilter.applyFilter(deckSpells), spellFilter.getPlayerClassId());  

		ListView listSpell =  (ListView) findViewById(R.listDeckCards.CardsList);
		listSpell.setAdapter(adapter);       
		listSpell.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectedSpell = ((SpellListSeparerAdapter)arg0.getAdapter()).getSpell(arg2);
				showSpell(selectedSpell);
			}
		});
		listSpell.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectedSpell = ((SpellListSeparerAdapter)arg0.getAdapter()).getSpell(arg2);
				showDialog(DIALOG_SPELL_ACTION);
				return true;
			}			
		});

	}

	@Override
	protected void onDestroy() {
		try {dbAdapter.close();} catch (Exception e) {}
		filterText.removeTextChangedListener(filterTextWatcher);
		super.onDestroy();		
	}

	private void showSpell(Spell spell) {
		Intent showSpellActivity = new Intent(SpellDeckCardListActivity.this, SpellViewerActivity.class);
		//Next create the bundle and initialize it
		Bundle bundle = new Bundle();
		//Add the parameters to bundle as
		bundle.putString(SpellViewerActivity.PARAM_SPELL_ORIGINAL_NAME, spell.getOriginalName());
		bundle.putInt(SpellViewerActivity.PARAM_SPELL_FILTER_CLASS, deck.getPlayerClassId());
		showSpellActivity.putExtras(bundle);

		startActivity(showSpellActivity);

	}


	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case DIALOG_SPELL_ACTION :
			//Afficher la liste des actions
			String[] actions = new String[]{
					getString(R.string.open),
					getString(R.string.removeToDeck)
			};
			return new AlertDialog.Builder(SpellDeckCardListActivity.this)
			.setTitle(selectedSpell.getTitle())
			.setItems(actions, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						showSpell(selectedSpell);
						break;
					case 1:
						SQLiteDatabase db = dbAdapter.open();
						try {
							DeckFacade.removeSpell(db, deck.getId(), selectedSpell.getOriginalName());
							adapter.removeSpell(selectedSpell);
							Toast.makeText(SpellDeckCardListActivity.this, getString(R.string.spellRemovedToDeck).replaceAll("#1", selectedSpell.getTitle()).replaceAll("#2", deck.getTitle()), Toast.LENGTH_SHORT).show();
						} finally {
							db.close();
						}
						break;
					default:
						break;
					}
					dialog.dismiss();
					removeDialog(DIALOG_SPELL_ACTION);
				}
			})
			.setOnCancelListener(new OnCancelListener() {				
				public void onCancel(DialogInterface dialog) {
					dialog.dismiss();
					removeDialog(DIALOG_SPELL_ACTION);
				}
			})
			.create();
		}
		return super.onCreateDialog(id);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.deck_card_list, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.searchDeckCardList:
			if(filterText.getVisibility() == View.VISIBLE) {
				filterText.setText("");
				filterText.setVisibility(View.GONE);       			
				filterText.clearFocus();
			} else {
				filterText.setVisibility(View.VISIBLE);
				filterText.requestFocus();
			}
			return true;
		case R.id.favoriteDeckCardList:
			changeFavorite();
			return true;
		}

		return false;
	}


	private void changeFavorite() {
		deck.setFavorite(!deck.isFavorite());
		SQLiteDatabase db = dbAdapter.open();
		try {
			DeckFacade.setFavorite(db, deck.getId(), deck.isFavorite());
			if(deck.isFavorite()) {
				Toast.makeText(SpellDeckCardListActivity.this, getString(R.string.deckAddedToFavorite).replaceAll("#1", deck.getTitle()), Toast.LENGTH_SHORT).show();
				deckFavorite.setImageResource(R.drawable.deck_favorite);
			} else {
				Toast.makeText(SpellDeckCardListActivity.this, getString(R.string.deckRemovedToFavorite).replaceAll("#1", deck.getTitle()), Toast.LENGTH_SHORT).show();
				deckFavorite.setImageResource(R.drawable.deck_no_favorite);
			}
		} finally {
			db.close();
		}
	}
}

