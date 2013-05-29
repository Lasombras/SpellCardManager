package com.lasombras.android.scm.view;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;

public class ListPreferenceMultiSelect extends ListPreference {
	public static final String SEPARATOR = ";"; 
	private boolean[] mClickedDialogEntryIndices;
		
	@Override
    public void setEntries(CharSequence[] entries) {
    	super.setEntries(entries);
    	// Initialize the array of boolean to the same size as number of entries
        mClickedDialogEntryIndices = new boolean[entries.length];
    }
    
    public ListPreferenceMultiSelect(Context context) {
        super(context);
        //mClickedDialogEntryIndices = new boolean[getEntries().length];
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
    	CharSequence[] entries = getEntries();
    	CharSequence[] entryValues = getEntryValues();
        if (entries == null || entryValues == null || entries.length != entryValues.length ) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");
        }

        restoreCheckedEntries();
        builder.setMultiChoiceItems(entries, mClickedDialogEntryIndices, 
                new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which, boolean val) {
						mClickedDialogEntryIndices[which] = val;
					}
        });
    }
        
    public String[] parseStoredValue(CharSequence val) {
		if (val == null || "".equals(val) ) {
			return null;
		}
		else {
			return ((String)val).split(SEPARATOR);
		}
    }
    
    private void restoreCheckedEntries() {
    	CharSequence[] entryValues = getEntryValues();
    	
    	// Explode the string read in sharedpreferences
    	String[] vals = parseStoredValue(getValue());
    	
    	if ( vals != null ) {
    		List<String> valuesList = Arrays.asList(vals);
        	for ( int i=0; i<entryValues.length; i++ ) {
        		mClickedDialogEntryIndices[i] = valuesList.contains(entryValues[i]);
        	}
    	}
    }

	@Override
    protected void onDialogClosed(boolean positiveResult) {
//        super.onDialogClosed(positiveResult);
		ArrayList<String> values = new ArrayList<String>();
        
    	CharSequence[] entryValues = getEntryValues();
        if (positiveResult && entryValues != null) {
        	for ( int i=0; i<entryValues.length; i++ ) {
        		if ( mClickedDialogEntryIndices[i] == true ) {
        			values.add(entryValues[i].toString());
        		}
        	}

            if (callChangeListener(values)) {
        		setValue(join(values, SEPARATOR));
            }
        }
    }
	
	protected static String join( Iterable< ? extends Object > pColl, String separator )
    {
        Iterator< ? extends Object > oIter;
        if ( pColl == null || ( !( oIter = pColl.iterator() ).hasNext() ) )
            return "";
        StringBuilder oBuilder = new StringBuilder( String.valueOf( oIter.next() ) );
        while ( oIter.hasNext() )
            oBuilder.append( separator ).append( oIter.next() );
        return oBuilder.toString();
    }
	
	/**
	 * 
	 * @param straw String to be found
	 * @param haystack Raw string that can be read direct from preferences
	 * @param separator Separator string. If null, static default separator will be used
	 * @return boolean True if the straw was found in the haystack
	 */
	public static boolean contains( String straw, String haystack ){
		String[] vals = haystack.split(SEPARATOR);
		for( int i=0; i<vals.length; i++){
			if(vals[i].equals(straw)){
				return true;
			}
		}
		return false;
	}
}
