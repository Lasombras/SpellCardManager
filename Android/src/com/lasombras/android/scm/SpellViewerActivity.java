/*
 * Copyright (C) 2009 The Android Open Source Project
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

import java.nio.charset.Charset;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.lasombras.android.scm.database.DBAdapter;
import com.lasombras.android.scm.entrepriselayer.facade.SpellFacade;
import com.lasombras.android.scm.model.Level;
import com.lasombras.android.scm.model.Spell;
import com.lasombras.android.scm.utils.RessourceCacheManager;
import com.lasombras.android.scm.utils.SpellDatas;

public final class SpellViewerActivity extends Activity  {

	public final static String PARAM_SPELL_ORIGINAL_NAME = "spellOriginalName";
	public final static String PARAM_SPELL_FILTER_CLASS = "spellFilterClass";
	private Spell spell;
	private final static int DIALOG_EDIT_COMMENT = 1;
	private String currentSpellComment = "";
	private ImageView spellCommentImage;
	private  DBAdapter dba;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_spell);
        dba = new DBAdapter(this);

        
        Bundle bundle = getIntent().getExtras();
        
        spell = SpellDatas.instance().get(bundle.getString(PARAM_SPELL_ORIGINAL_NAME));
        
        if(spell == null) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Sort inconnu !")
        	       .setCancelable(false)
        	       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   SpellViewerActivity.this.finish();
        	           }
        	       });
        	builder.create().show();
        } else {
	        int playerClassId = bundle.getInt(PARAM_SPELL_FILTER_CLASS);
	        
	        
	        Bitmap mIconSpell = RessourceCacheManager.instance().getSpellImage(spell.getImage());
	        if(mIconSpell == null)
	        	mIconSpell = BitmapFactory.decodeResource(this.getResources(), R.drawable.empty_icon);
	        ((ImageView) this.findViewById(R.viewSpell.spellImage)).setImageBitmap(mIconSpell);

	        	
	        TextView spellNameText = (TextView) this.findViewById(R.viewSpell.spellNameText);
	        TextView spellOriginalNameText = (TextView) this.findViewById(R.viewSpell.spellOriginalNameText);

	        spellNameText.setTypeface(RessourceCacheManager.instance().getTitleFontType());
	        String formatedOriginalName = spell.getOriginalName();
	        if(spell.getOriginalName().length() > 0)
	        	formatedOriginalName = spell.getOriginalName().substring(0, 1).toUpperCase() + spell.getOriginalName().toLowerCase().substring(1);
	        spellOriginalNameText.setTypeface(RessourceCacheManager.instance().getTitleFontType());
	        
			if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("checkbox_title_list_preference", false)) {
		        spellNameText.setText(formatedOriginalName);
		        spellOriginalNameText.setText(spell.getTitle());		        
			} else {
		        spellNameText.setText(spell.getTitle());
		        spellOriginalNameText.setText(formatedOriginalName);				
			}
			if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("checkbox_title_card_preference", false)) {
		        ViewFlipper mFlipper = ((ViewFlipper) this.findViewById(R.viewSpell.flipperSpellNameText));
	            mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
	            mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
		        mFlipper.startFlipping();				
			}

	        TextView spellSourceText = (TextView) this.findViewById(R.viewSpell.spellSourceText);
	        String sourceName = SpellDatas.instance().getSource(spell.getSourceId()).getShortName();
	        if(sourceName != null && sourceName.length() > 0) {
	            spellSourceText.setText(sourceName);        	
	        } else {
	        	spellSourceText.setVisibility(View.INVISIBLE);
	        }
	        	
	        TextView spellSchoolText = (TextView) this.findViewById(R.viewSpell.spellSchoolText);
	        String schoolName = SpellDatas.instance().getSchool(spell.getSchoolId()).getTitle();
	        if(spell.getDescriptor().length() > 0)
	        	schoolName += " [" + spell.getDescriptor() + "]";
	        spellSchoolText.setText(schoolName);        	
	        	
	        TextView levels = (TextView) this.findViewById(R.viewSpell.spellClassText);
	        StringBuffer levelsInfos = new StringBuffer();
	        for(Level level : spell.getLevels()) {
	        	if(playerClassId > 0) {
	        		if(level.getPlayerClassId() == playerClassId)
	            		levelsInfos.append(SpellDatas.instance().getPlayerClass(level.getPlayerClassId()).getTitle() + " " + level.getLevel());
	        	} else {
	            	if(levelsInfos.length() != 0)
	            		levelsInfos.append(", ");
	            	levelsInfos.append(SpellDatas.instance().getPlayerClass(level.getPlayerClassId()).getShortName() + " " + level.getLevel());
	        	}
	        }
	        levels.setText(levelsInfos);
	         
	        for(int idxComponent = 0; idxComponent < spell.getComponentsId().size(); idxComponent++) {
	        	TextView component = null;
		       	switch (idxComponent) {
					case 0:
						component = (TextView) this.findViewById(R.viewSpell.spellComponent01);
						break;
					case 1:
						component = (TextView) this.findViewById(R.viewSpell.spellComponent02);
						break;
					case 2:
						component = (TextView) this.findViewById(R.viewSpell.spellComponent03);
						break;
					case 3:
						component = (TextView) this.findViewById(R.viewSpell.spellComponent04);
						break;
					case 4:
						component = (TextView) this.findViewById(R.viewSpell.spellComponent05);
						break;
					default:
						break;
				}
		       	if(component != null) {
		       		component.setVisibility(View.VISIBLE);
		       		component.setText(SpellDatas.instance().getComponent(spell.getComponentsId().get(idxComponent).intValue()).getShortName());
		       	}
	        }
	                
	        
	    	StringBuffer infosText = new StringBuffer();
	    	if(spell.getCastingTime().length() > 0)
	    		infosText.append("<b><i>" + getString(R.string.castingTime) + ": </i></b>" + spell.getCastingTime() + "<br/>");
	    	if(spell.getMaterial().length() > 0)
	    		infosText.append("<b><i>" + getString(R.string.material) + ": </i></b>" + spell.getMaterial() + "<br/>");
	    	if(spell.getDuration().length() > 0)
	    		infosText.append("<b><i>" + getString(R.string.duration) + ": </i></b>" + spell.getDuration() + "<br/>");
	    	if(spell.getRange().length() > 0)
	    		infosText.append("<b><i>" + getString(R.string.range) + ": </i></b>" + spell.getRange() + "<br/>");
	    	if(spell.getTarget().length() > 0)
	    		infosText.append("<b><i>" + getString(R.string.target) + ": </i></b>" + spell.getTarget() + "<br/>");
	    	if(spell.getEffect().length() > 0)
	    		infosText.append("<b><i>" + getString(R.string.effect) + ": </i></b>" + spell.getEffect() + "<br/>");
	    	if(spell.getArea().length() > 0)
	    		infosText.append("<b><i>" + getString(R.string.area) + ": </i></b>" + spell.getArea() + "<br/>");
	    	if(spell.getSavingThrow().length() > 0)
	    		infosText.append("<b><i>" + getString(R.string.savingThrow) + ": </i></b>" + spell.getSavingThrow() + "<br/>");
	    	infosText.append("<b><i>" + getString(R.string.spellResistance) + ": </i></b>" + (spell.isSpellResistance()?getString(R.string.yes):getString(R.string.no)));
	        TextView spellInfosText = (TextView) this.findViewById(R.viewSpell.spellInfosText);
	        spellInfosText.setText(Html.fromHtml(infosText.toString()));
	
	        
	        //spellInfosText.setTypeface(fontType);
	        SQLiteDatabase db = dba.open();
	        try {
	        	currentSpellComment = SpellFacade.getComment(db, spell.getOriginalName());
	        } finally {
	        	db.close();
	        }
	        spellCommentImage = (ImageView)this.findViewById(R.viewSpell.spellComment);
	        spellCommentImage.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					showDialog(DIALOG_EDIT_COMMENT);
				}
	        });
	        if(currentSpellComment.length() == 0)
	        	spellCommentImage.setVisibility(View.GONE);
	        
	        
	        WebView spellContentText = ((WebView) this.findViewById(R.viewSpell.spellContentText));
	        int textSize = 11;
	        if(spellContentText.getTag() != null && spellContentText.getTag().equals("xlarge"))
	        	textSize = 16;
	        spellContentText.setBackgroundColor(0x00000000);
	        spellContentText.loadData(encodeText(spell.getDetail(),"Arial",textSize, Charset.defaultCharset().displayName()),
	        		"text/html",
	        		Charset.defaultCharset().displayName()); //UTF-8
	        spellContentText.setWebViewClient(new WebViewClient() {
	            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
	 
	              if (url.contains("spell#")) {           	  
	 	             Log.i("URI PARSE", url.substring("spell#".length()));
	 	             
					Intent showSpellActivity = new Intent(SpellViewerActivity.this, SpellViewerActivity.class);
					//Next create the bundle and initialize it
					Bundle bundle = new Bundle();
					//Add the parameters to bundle as
					bundle.putString(SpellViewerActivity.PARAM_SPELL_ORIGINAL_NAME, url.substring("spell#".length()));
					bundle.putString(SpellViewerActivity.PARAM_SPELL_FILTER_CLASS, getIntent().getExtras().getString(PARAM_SPELL_FILTER_CLASS));
					showSpellActivity.putExtras(bundle);
					
					startActivity(showSpellActivity);
	
					return true;
	              }
	              return false;
	            }
	        });
	//      TextView spellContentText = ((TextView) this.findViewById(R.viewSpell.spellContentText));
	//      spellContentText.setBackgroundColor(0x00000000);
	//      spellContentText.setText(Html.fromHtml("<?xml version='1.0' encoding='utf-8'?><BODY style=\"background-color:transparent\">" +
	//      		spell.getDetail().replaceAll("\n", "<BR>")
	//      		+ "</BODY>"));
        }
     }

	private String encodeText(String text, String defaultFont, int fontSize, String charset) {
		String style = "<style>"+
		"table {border: 1px solid black;border-collapse: collapse;font-size: " + fontSize + "pt;white-space: nowrap;}" +
		"caption {font-variant: small-caps;}"+
		"td {vertical-align: middle;}"+
		"td, th {border: 1px solid black;padding: 1px 5px 1px 5px;margin: 0;text-align: center;}"+
		"th {border-color:  black;background-color: #3f0a00;color: #ece5b2;white-space: normal;vertical-align: bottom;}"+
		"tr {background-color: #ece5b2;}"+
		"tr.alt {background-color: #f7f5df;}"+
		"tr.titre {border-color:  black;background-color: #3f0a00;color: #ece5b2;white-space: normal;vertical-align: bottom;}"+
		"tfoot td {border-style: none;white-space: normal;background-color: white;}"+
		"a:link, a:visited {color: #3f0a00;	text-decoration: underline;font-weight:bold;}"+
		"body {background-color:transparent; margin: 0px;border-width: 0px;padding: 0px;text-align: justify; text-justify:auto; font-size: " + fontSize + "pt;font-family: " + defaultFont + ", Arial;}"+
		"</style>";
		
		text = text.replaceAll("</table>\\r\\n", "</table>");
		text = text.replaceAll("</table>\\n", "</table>");
		String startTab = "<table>";
		String endTag = "</table>";
		while(text.indexOf(startTab) > -1) {
			String strStart = text.substring(0,text.indexOf(startTab));
			String content = text.substring(text.indexOf(startTab), text.indexOf(endTag)+endTag.length());
			content = content.replaceAll("\\r\\n", "");
			content = content.replaceAll("\\n", "");
			content = content.replaceAll(startTab, "<table_ok>");
			content = content.replaceAll(endTag, "</table_ok>");
			String strEnd = text.substring(text.indexOf(endTag) + endTag.length());
			text = strStart + content + strEnd;
		}
		
		text = text.replaceAll("<table_ok>", "<table>");
		text = text.replaceAll("</table_ok>", "</table>");
		text = text.replaceAll("\\r\\n", "<br/>");
		text = text.replaceAll("\\n", "<br/>");
		return "<?xml version='1.0' encoding='" + charset + "'?><html><head>" + style + "</head><body>" + Uri.encode(text) + "</body></html>";
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.spell_view, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addToDeck:
			Intent showDeckListActivity = new Intent(SpellViewerActivity.this, SpellDeckSelectorActivity.class);
			//Next create the bundle and initialize it
			Bundle bundle = new Bundle();
			bundle.putInt(SpellDeckSelectorActivity.PARAM_SELECTION_MODE, SpellDeckSelectorActivity.MODE_ADD_SPELL);
			bundle.putString(SpellDeckSelectorActivity.PARAM_SPELL_ORIGINAL_NAME, spell.getOriginalName());
			showDeckListActivity.putExtras(bundle);
			startActivity(showDeckListActivity);	
			return true;
		case R.id.spellComment:
			showDialog(DIALOG_EDIT_COMMENT);
			return true;
		}

		return false;
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_EDIT_COMMENT :
			// This example shows how to add a custom layout to an AlertDialog
			final EditText textEntryView = new EditText(this);
			textEntryView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			textEntryView.setText(currentSpellComment);
			return new AlertDialog.Builder(SpellViewerActivity.this)
			//.setIcon(R.drawable.ic_menu_comment)
			.setTitle(R.string.spellComment)
			.setView(textEntryView)
			.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					if(!textEntryView.getText().toString().equals(currentSpellComment)) {
						currentSpellComment = textEntryView.getText().toString();
						saveComment();
					}
				}
			})
			.setNeutralButton(R.string.dialog_reset, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					textEntryView.setText("");
					currentSpellComment = "";
					saveComment();					
				}
			})           
			.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					if(!textEntryView.getText().toString().equals(currentSpellComment)) {
						textEntryView.setText(currentSpellComment);
					}
				}
			})
			.create();
		}
		return null;
	}

	private void saveComment() {
		if(currentSpellComment.length() > 0)
			spellCommentImage.setVisibility(View.VISIBLE);
		else
			spellCommentImage.setVisibility(View.GONE);
		SQLiteDatabase db = dba.open();
		try {
			SpellFacade.save(db, spell.getOriginalName(), currentSpellComment);
		} finally {
			db.close();
		}
	}
	
	@Override
	protected void onDestroy() {
		try {dba.close();}catch (Exception e) {}
		super.onDestroy();
	}
}
