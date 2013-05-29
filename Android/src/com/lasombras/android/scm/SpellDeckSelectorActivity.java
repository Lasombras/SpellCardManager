package com.lasombras.android.scm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.lasombras.android.scm.adapter.DeckListAdapter;
import com.lasombras.android.scm.database.DBAdapter;
import com.lasombras.android.scm.entrepriselayer.facade.DeckFacade;
import com.lasombras.android.scm.model.Deck;
import com.lasombras.android.scm.model.Level;
import com.lasombras.android.scm.model.Spell;
import com.lasombras.android.scm.utils.ListDeckComparator;
import com.lasombras.android.scm.utils.SpellDatas;
import com.lasombras.android.scm.view.DeckEditorView;

public class SpellDeckSelectorActivity extends TabActivity {

	public final static int MODE_ADD_SPELL = 1;
	public final static int MODE_OPEN_DECK = 2;
	
	public final static String PARAM_SELECTION_MODE = "PARAM_SELECTION_MODE";
	public final static String PARAM_SPELL_ORIGINAL_NAME = "PARAM_SPELL_ORIGINAL_NAME";
	
	private final static int DIALOG_DECK_ACTION = 1;
	private final static int DIALOG_DECK_NEW = 2;
	private final static int DIALOG_DECK_DELETE = 3;
	private final static int DIALOG_DECK_EDIT = 4;
	
	
	private final static int RECENT_SIZE = 10;

	private int mode = MODE_OPEN_DECK;
	
	private Deck selectedDeck;
	private DeckEditorView deckEditorView;
	private DBAdapter dbAdapter = new DBAdapter(this);
	
	private DeckListAdapter deckAllAdpater;
	private DeckListAdapter deckRecentAdpater;
	private DeckListAdapter deckFavoriteAdpater;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        try {
	        mode = getIntent().getExtras().getInt(PARAM_SELECTION_MODE);
       } catch (Exception e) {}
 
				
		//Construction des Onglets
        TabHost tabHost = getTabHost();        
        LayoutInflater.from(this).inflate(R.layout.list_deck_selector, tabHost.getTabContentView(), true);

        tabHost.addTab(tabHost.newTabSpec("tabRecent")
                .setIndicator(getString(R.string.recent),  getResources().getDrawable(R.drawable.ic_menu_recent_history))
                .setContent(R.listDeckSelector.tabLayoutRecent));
        tabHost.addTab(tabHost.newTabSpec("tabAll")
                .setIndicator(getString(R.string.all),  getResources().getDrawable(R.drawable.ic_menu_show_list))
                .setContent(R.listDeckSelector.tabLayoutAll));
        tabHost.addTab(tabHost.newTabSpec("tabFavorite")
                .setIndicator(getString(R.string.favorite),  getResources().getDrawable(R.drawable.ic_menu_favorite))
                .setContent(R.listDeckSelector.tabLayoutFavorite));

		
        DeckLongClickListener deckLongClickListener = new DeckLongClickListener();
        DeckClickListener deckClickListener = new DeckClickListener();
        SQLiteDatabase db = dbAdapter.open();
        try {
        	
	        ListView listAllDecks = (ListView)findViewById(R.listDeckSelector.listDeck);
	        listAllDecks.setOnItemLongClickListener(deckLongClickListener);
	        listAllDecks.setOnItemClickListener(deckClickListener);
	        deckAllAdpater = new DeckListAdapter(this);
	        listAllDecks.setAdapter(deckAllAdpater);

        
	        ListView listRecentDecks = (ListView)findViewById(R.listDeckSelector.listRecentDeck);
	        listRecentDecks.setOnItemLongClickListener(deckLongClickListener);
	        listRecentDecks.setOnItemClickListener(deckClickListener);
	        deckRecentAdpater = new DeckListAdapter(this);
	        listRecentDecks.setAdapter(deckRecentAdpater);

	        ListView listFavoriteDecks = (ListView)findViewById(R.listDeckSelector.listFavoriteDeck);
	        listFavoriteDecks.setOnItemLongClickListener(deckLongClickListener);
	        listFavoriteDecks.setOnItemClickListener(deckClickListener);
	        deckFavoriteAdpater = new DeckListAdapter(this);
	        listFavoriteDecks.setAdapter(deckFavoriteAdpater);

        } catch (Exception e) {
		} finally {
			db.close();
		}
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.deck_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.newDeck:
			showDialog(DIALOG_DECK_NEW);
			return true;
		}

		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DECK_ACTION :
			//Afficher la liste des actions			
			String[] actions = new String[]{
				getString(R.string.open),
				selectedDeck.isFavorite()?getString(R.string.removeToFavorite):getString(R.string.addToFavorite),
				getString(R.string.edit),
				getString(R.string.delete)
			};
			return new AlertDialog.Builder(SpellDeckSelectorActivity.this)
	        .setTitle(selectedDeck.getTitle())
	        .setItems(actions, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	switch (which) {
					case 0:
						notifySelectionTime(selectedDeck);
						showDeck(selectedDeck);
						break;
					case 1:
				        SQLiteDatabase db = dbAdapter.open();
				        try {
				        	if(!selectedDeck.isFavorite()) {
					        	selectedDeck.setFavorite(true);
					        	DeckFacade.setFavorite(db, selectedDeck.getId(), selectedDeck.isFavorite());
					        	deckFavoriteAdpater.getDecks().add(selectedDeck);
					        	Collections.sort(deckFavoriteAdpater.getDecks(), new ListDeckComparator(ListDeckComparator.FIELD_TITLE));
								deckFavoriteAdpater.notifyDataSetChanged();
								Toast.makeText(SpellDeckSelectorActivity.this, getString(R.string.deckAddedToFavorite).replaceAll("#1", selectedDeck.getTitle()), Toast.LENGTH_SHORT).show();
				        		
				        	} else {
					        	selectedDeck.setFavorite(false);
					        	DeckFacade.setFavorite(db, selectedDeck.getId(), selectedDeck.isFavorite());
					        	deckFavoriteAdpater.getDecks().remove(selectedDeck);
								deckFavoriteAdpater.notifyDataSetChanged();
								Toast.makeText(SpellDeckSelectorActivity.this, getString(R.string.deckRemovedToFavorite).replaceAll("#1", selectedDeck.getTitle()), Toast.LENGTH_SHORT).show();	
				        	}							
				        } catch (Exception e) {}
				        finally {
				        	db.close();
				        }
		                break;
					case 2:
						showDialog(DIALOG_DECK_EDIT);
		                break;
					case 3:
						showDialog(DIALOG_DECK_DELETE);
		                break;
					default:
						break;
					}
	            	dialog.dismiss();
					removeDialog(DIALOG_DECK_ACTION);
	          }
	        })
	        .setOnCancelListener(new OnCancelListener() {				
				public void onCancel(DialogInterface dialog) {
	            	dialog.dismiss();
					removeDialog(DIALOG_DECK_ACTION);
				}
			})
	        .create();
		case DIALOG_DECK_NEW :

			deckEditorView = new DeckEditorView(this);

			return new AlertDialog.Builder(SpellDeckSelectorActivity.this)
			//.setIcon(R.drawable.alert_dialog_icon)
			.setTitle(R.string.newDeckTitle)
			.setView(deckEditorView)
			.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Deck newDeck = new Deck();
					newDeck.setTitle(deckEditorView.getTitleDeck());
					newDeck.setPlayerClassId(deckEditorView.getPlayerClassFilter());
					newDeck.updateSelectionTime();
					if(newDeck.getTitle().equals("")) {
						Toast.makeText(SpellDeckSelectorActivity.this, getString(R.string.deckTitleError), Toast.LENGTH_SHORT).show();
					} else {
						
				        SQLiteDatabase db = dbAdapter.open();
				        try {
				        	DeckFacade.save(db, newDeck, false);
							Toast.makeText(SpellDeckSelectorActivity.this, getString(R.string.createdDeckSuccess).replaceAll("#1", newDeck.getTitle()), Toast.LENGTH_SHORT).show();
							notifyLists();
				        } catch (Exception e) {}
				        finally {
				        	db.close();
				        }

						
						dialog.dismiss();
						removeDialog(DIALOG_DECK_NEW);
						
						
					}
				}
			})
			.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					removeDialog(DIALOG_DECK_NEW);
				}
			})
			.create();
		case DIALOG_DECK_DELETE:
			return new AlertDialog.Builder(SpellDeckSelectorActivity.this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.deleteDeck)
			.setMessage(getString(R.string.confirmDeleteDeck).replaceAll("#1", selectedDeck.getTitle()))
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
			        SQLiteDatabase db = dbAdapter.open();
			        try {
			        	DeckFacade.delete(db, selectedDeck.getId());
						Toast.makeText(SpellDeckSelectorActivity.this, getString(R.string.deletedDeckSuccess).replaceAll("#1", selectedDeck.getTitle()), Toast.LENGTH_SHORT).show();
						notifyLists();
			        } catch (Exception e) {}
			        finally {
			        	db.close();
			        }
					
					notifyLists();
					dialog.dismiss();
					removeDialog(DIALOG_DECK_DELETE);
				}
			})
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					removeDialog(DIALOG_DECK_DELETE);
				}
			}).create();

		case DIALOG_DECK_EDIT :

			deckEditorView = new DeckEditorView(this, selectedDeck);

			return new AlertDialog.Builder(SpellDeckSelectorActivity.this)
			//.setIcon(R.drawable.alert_dialog_icon)
			.setTitle(R.string.editDeckTitle)
			.setView(deckEditorView)
			.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					selectedDeck.setTitle(deckEditorView.getTitleDeck());
					selectedDeck.setPlayerClassId(deckEditorView.getPlayerClassFilter());
					selectedDeck.updateSelectionTime();
					if(selectedDeck.getTitle().equals("")) {
						Toast.makeText(SpellDeckSelectorActivity.this, getString(R.string.deckTitleError), Toast.LENGTH_SHORT).show();
					} else {
						
				        SQLiteDatabase db = dbAdapter.open();
				        try {
				        	DeckFacade.save(db, selectedDeck, false);
							Toast.makeText(SpellDeckSelectorActivity.this, getString(R.string.editedDeckSuccess).replaceAll("#1", selectedDeck.getTitle()), Toast.LENGTH_SHORT).show();
							notifyLists();
				        } catch (Exception e) {}
				        finally {
				        	db.close();
				        }		
						dialog.dismiss();
						removeDialog(DIALOG_DECK_NEW);
						
						
					}
				}
			})
			.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					removeDialog(DIALOG_DECK_NEW);
				}
			})
			.create();
			
		}
		return super.onCreateDialog(id);
	}

	public void notifyLists() {
        SQLiteDatabase db = dbAdapter.open();
        try {
        	List<Deck> list = DeckFacade.getAll(db, false);
        	ArrayList<Deck> listRecent = new ArrayList<Deck>();
        	ArrayList<Deck> listFavorite = new ArrayList<Deck>();
        	Collections.sort(list, new ListDeckComparator(ListDeckComparator.FIELD_SELECTION_TIME, false));
        	int idxCount = 0;
        	while(idxCount < list.size() && listRecent.size() < RECENT_SIZE) {
        		listRecent.add(list.get(idxCount));
        		idxCount++;
        	}
        	Collections.sort(list, new ListDeckComparator(ListDeckComparator.FIELD_TITLE));
        	for(Deck deck : list) {
        		if(deck.isFavorite())
        			listFavorite.add(deck);
        	}
        	
			deckAllAdpater.setDecks(list);
			deckAllAdpater.notifyDataSetChanged();
			
			deckRecentAdpater.setDecks(listRecent);
			deckRecentAdpater.notifyDataSetChanged();
			
			deckFavoriteAdpater.setDecks(listFavorite);
			deckFavoriteAdpater.notifyDataSetChanged();
		} catch (Exception e) {}
        finally {
        	db.close();
        }
	}
	
	
	@Override
	protected void onDestroy() {
		try {dbAdapter.close();} catch (Exception e) {}
		super.onDestroy();
	}
	
	private class DeckLongClickListener implements OnItemLongClickListener {
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			selectedDeck = ((DeckListAdapter)arg0.getAdapter()).getDeck(arg2);
			showDialog(DIALOG_DECK_ACTION);
			return true;
		}	
	}
	
	private class DeckClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			selectedDeck = ((DeckListAdapter)arg0.getAdapter()).getDeck(arg2);
			
			notifySelectionTime(selectedDeck);
			
			switch (mode) {
				case MODE_ADD_SPELL:
			        Spell spell = SpellDatas.instance().get(getIntent().getExtras().getString(PARAM_SPELL_ORIGINAL_NAME));
			        boolean isValid = false;
			        if(selectedDeck.getPlayerClassId() > 0) {
				        for(Level level : spell.getLevels()) {
				        	if(level.getPlayerClassId() == selectedDeck.getPlayerClassId()) {
				        		isValid = true;
				        		break;
				        	}
				        }
			        } else {
			        	isValid = true;
			        }
			        
			        if(isValid) {
				        SQLiteDatabase db = dbAdapter.open();
				        try {
				        	DeckFacade.addSpell(db, selectedDeck.getId(), spell.getOriginalName());
				        	Toast.makeText(SpellDeckSelectorActivity.this, getString(R.string.spellAddedToDeck).replaceAll("#1", spell.getTitle()).replaceAll("#2", selectedDeck.getTitle()), Toast.LENGTH_LONG).show();
				        } catch (Exception e) {
						} finally {
							db.close();
						}
				        finish();
			        } else {
						new AlertDialog.Builder(SpellDeckSelectorActivity.this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.errorDeckSelectionTitle)
						.setMessage(getString(R.string.errorDeckSelection).replaceAll("#1", spell.getTitle()))
						.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.dismiss();
							}
						})
						.create().show();

			        }
					break;
				case MODE_OPEN_DECK:
					showDeck(selectedDeck);
					break;
				default:
					break;
			}
			

		}
	}
	
	private void notifySelectionTime(Deck deck) {
		deck.updateSelectionTime();
		SQLiteDatabase db = dbAdapter.open();
		try {
			DeckFacade.setSelectionTime(db, deck.getId(), deck.getSelectionTime());
		} finally {
			db.close();
		}
		List<Deck> decks = deckRecentAdpater.getDecks();
		if(decks.contains(deck))
			decks.remove(deck);
		else if(decks.size() >= RECENT_SIZE)
			decks.remove(decks.size()-1);
		decks.add(0, deck);
    	deckRecentAdpater.notifyDataSetChanged();
	}
	
	private void showDeck(Deck deck) {
		Intent deckListActivity = new Intent(SpellDeckSelectorActivity.this, SpellDeckCardListActivity.class);
		Bundle bundle = new Bundle();
		//Add the parameters to bundle as
		bundle.putInt(SpellDeckCardListActivity.PARAM_DECK_ID,deck.getId());
		deckListActivity.putExtras(bundle);
		startActivity(deckListActivity);
		

	}
	
	@Override
	protected void onStart() {
		super.onStart();
        notifyLists();
	}
}
