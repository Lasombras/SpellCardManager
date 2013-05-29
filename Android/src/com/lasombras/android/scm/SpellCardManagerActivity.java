package com.lasombras.android.scm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lasombras.android.scm.adapter.SpellListSeparerAdapter;
import com.lasombras.android.scm.model.Spell;
import com.lasombras.android.scm.model.SpellFilter;
import com.lasombras.android.scm.utils.RessourceCacheManager;
import com.lasombras.android.scm.utils.SpellDatas;
import com.lasombras.android.scm.view.SpellListFilterView;

public class SpellCardManagerActivity extends Activity implements OnSharedPreferenceChangeListener {
	private SpellCardManagerActivity activity;
	private ProgressDialog dialog;
	private EditText filterText;
	private SpellListSeparerAdapter adapter;
	private final Handler uiThreadCallback = new Handler();
	private TextWatcher filterTextWatcher;
	private int backCount = 0;
	
	private SpellFilter spellFilter;
	private SpellListFilterView spellListFilterView;

	private Spell selectedSpell;
	private SharedPreferences prefs;
	
	private static final int DIALOG_SPELL_ACTION = 1;
	private static final int DIALOG_ADVANCED_FILTER = 2;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		setContentView(R.layout.main);
		
		//Chargement du gestionnaire de font
		RessourceCacheManager.instance().load(this);
		
		dialog = new ProgressDialog(this);
		dialog.setIcon(R.drawable.icon);
        dialog.setTitle(getString(R.string.appTitle));
        dialog.setMessage(getString(R.string.loading));
        dialog.setCancelable(true);
        // set the progress to be horizontal
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // reset the bar to the default value of 0
        dialog.setProgress(0);
        dialog.setMax(1);
       // display the progressbar
        dialog.show();
		
		filterText = (EditText) findViewById(R.id.searchBoxText);
		filterText.setVisibility(View.GONE);
		filterText.setHeight(0);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		
		final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                 if(msg.getData().containsKey("max")) {
                	dialog.setMax(msg.getData().getInt("max"));
                   	dialog.setTitle(msg.getData().getString("title"));
                   	dialog.setMessage(msg.getData().getString("message"));
                   	dialog.setProgress(0);
                } else {
                	dialog.incrementProgressBy(msg.getData().getInt("increment"));                	
                }
                
            }
        };

        
		final Runnable runInUIThread = new Runnable() {
			public void run() {
				showSpellList();    	     
			}
		};

		new Thread() {
			@Override
			public void run() {
				SpellDatas.instance().load(activity, handler);			
				spellFilter = new SpellFilter(prefs);
				dialog.dismiss();
				uiThreadCallback.post(runInUIThread);
			}
		}.start();
	}

	public void incrementStep(String message, int increment) {
		if(dialog != null) {
			dialog.setMessage(message);
			dialog.incrementProgressBy(increment);
		}
	}
	
	public void setNewStep(String title, String message, int max) {
		if(dialog != null) {
			dialog.setTitle(title);
			dialog.setMessage(message);
			dialog.setProgress(0);
			dialog.setMax(max);
		}
		
	}
	
	private void showSpellList() {
		ListView list1 = (ListView) findViewById(R.main.listSpell);
		adapter = new SpellListSeparerAdapter(this, spellFilter.applyFilter(SpellDatas.instance().getList()), spellFilter.getPlayerClassId());
		
		list1.setAdapter(adapter);
		list1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectedSpell = ((SpellListSeparerAdapter)arg0.getAdapter()).getSpell(arg2);
				showItem(selectedSpell);
			}
		});
		list1.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectedSpell = ((SpellListSeparerAdapter)arg0.getAdapter()).getSpell(arg2);
				showDialog(DIALOG_SPELL_ACTION);
				return true;
			}			
		});
		filterTextWatcher = new TextWatcher() {
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				spellFilter.setTitleName(s.toString());
				notifyList();
			}

		};
		filterText.addTextChangedListener(filterTextWatcher);
	}
	
	private void notifyList() {
		if(spellFilter.isSortByLevel() && spellFilter.getPlayerClassId() > 0) {
			adapter.setLevelMode(spellFilter.getPlayerClassId());
		} else {
			adapter.setAlphaMode();			
		}
		adapter.notifyDataSetChanged(spellFilter.applyFilter(SpellDatas.instance().getList()));
	}

	private void showItem(Spell spell) {
		Intent showSpellActivity = new Intent(SpellCardManagerActivity.this, SpellViewerActivity.class);
		//Next create the bundle and initialize it
		Bundle bundle = new Bundle();
		//Add the parameters to bundle as
		bundle.putString(SpellViewerActivity.PARAM_SPELL_ORIGINAL_NAME, spell.getOriginalName());
		//bundle.putInt(SpellViewerActivity.PARAM_SPELL_FILTER_CLASS, spellFilter.getPlayerClassId());
		showSpellActivity.putExtras(bundle);

		startActivity(showSpellActivity);

	}

	private void showDeckList(int mode) {
		Intent showDeckListActivity = new Intent(SpellCardManagerActivity.this, SpellDeckSelectorActivity.class);
		//Next create the bundle and initialize it
		Bundle bundle = new Bundle();
		bundle.putInt(SpellDeckSelectorActivity.PARAM_SELECTION_MODE, mode);
		if(mode == SpellDeckSelectorActivity.MODE_ADD_SPELL)
			bundle.putString(SpellDeckSelectorActivity.PARAM_SPELL_ORIGINAL_NAME, selectedSpell.getOriginalName());
		showDeckListActivity.putExtras(bundle);

		startActivity(showDeckListActivity);
		
	}
		
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(prefs.getBoolean("clean_data_preference", false)) {
			SpellDatas.instance().unload();
			RessourceCacheManager.instance().unload();
		}
		filterText.removeTextChangedListener(filterTextWatcher);
		prefs.unregisterOnSharedPreferenceChangeListener(this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.spell_list, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.searchList:
			if(filterText.getVisibility() == View.VISIBLE) {
				filterText.setText("");
				filterText.setVisibility(View.GONE);       			
				filterText.clearFocus();
			} else {
				filterText.setVisibility(View.VISIBLE);
				filterText.requestFocus();
			}
			return true;
		case R.id.filterList:
			showDialog(DIALOG_ADVANCED_FILTER);

			return true;
		case R.id.deckList:
			showDeckList(SpellDeckSelectorActivity.MODE_OPEN_DECK);
			return true;
		case R.id.optionsApp:
			startActivity(new Intent(SpellCardManagerActivity.this, SpellSystemPreferenceActivity.class));
			return true;
		}

		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case DIALOG_ADVANCED_FILTER :

			spellListFilterView = new SpellListFilterView(this, spellFilter);

			return new AlertDialog.Builder(SpellCardManagerActivity.this)
			//.setIcon(R.drawable.alert_dialog_icon)
			.setTitle(R.string.advancedFilter)
			.setView(spellListFilterView)
			.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					spellListFilterView.updateFilter(spellFilter);
					notifyList();
					removeDialog(DIALOG_ADVANCED_FILTER);
				}
			})
			.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					removeDialog(DIALOG_ADVANCED_FILTER);
				}
			})
			.setNeutralButton(R.string.dialog_reset, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					spellFilter.reset();
					notifyList();
					removeDialog(DIALOG_ADVANCED_FILTER);
				}
			})
			.create();

		case DIALOG_SPELL_ACTION :
			//Afficher la liste des actions
			//1. Visualiser
			//2. Ajouter a un deck
			//3+ Modifier/Supprimer
			String[] actions = new String[]{
				getString(R.string.open),
				getString(R.string.addToDeck)
			};
			return new AlertDialog.Builder(SpellCardManagerActivity.this)
            .setTitle(selectedSpell.getTitle())
            .setItems(actions, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	switch (which) {
					case 0:
						showItem(selectedSpell);
						break;
					case 1:
						showDeckList(SpellDeckSelectorActivity.MODE_ADD_SPELL);
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
	public void onBackPressed() {
		if(backCount == 0) {
			backCount++;
			Toast.makeText(SpellCardManagerActivity.this, getString(R.string.confirmQuit), Toast.LENGTH_LONG).show();
			new Thread(new Runnable() {
			    public void run() {
			    	try {Thread.sleep(3500);}
			    	catch (InterruptedException e) {}
			    	finally {backCount = 0;}
			    }
			  }).start();

		} else {
			super.onBackPressed();
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		if(key.equals("search_preference") || key.equals("source_preference")) {
			spellFilter.applyPreferences();
			notifyList();
		} else if(key.equals("player_class_preference")) {
			SpellDatas.instance().applyPreferenceFilter();
		}
	}
	
}