package com.lasombras.android.scm.view;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lasombras.android.scm.R;


public class MultipleSpinner extends Spinner implements
OnMultiChoiceClickListener, OnCancelListener {

	private List<String> items;
	private boolean[] selected;
	private int allTextResId = R.string.all;
	private int noneTextResId = R.string.none;
	private AlertDialog.Builder builder;

	public MultipleSpinner(Context context) {
		super(context);
	}

	public MultipleSpinner(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}

	public MultipleSpinner(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		if (isChecked)
			selected[which] = true;
		else
			selected[which] = false;
	}

	public void onCancel(DialogInterface dialog) {
		// refresh text on spinner
		performTextUpdate();
	}

	@Override
	public boolean performClick() {
		builder = new AlertDialog.Builder(getContext());
		builder.setMultiChoiceItems(
				items.toArray(new CharSequence[items.size()]), selected, this);
		builder.setPositiveButton(android.R.string.ok,	new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.setOnCancelListener(this);
		builder.show();
		return true;
	}
	
	public void setItems(List<String> items, boolean[] selected) {
		this.items = items;
		this.selected = selected;
		if(this.selected == null || this.selected.length != this.items.size()) {
			this.selected = new boolean[this.items.size()];
			for(int i = 0; i < this.items.size(); i++)
				this.selected[i] = true;
		}
		
		performTextUpdate();
	}
	
	private void performTextUpdate() {
		int nbSelect = 0;
		String textList = getContext().getString(noneTextResId);
		for (int i = 0; i < items.size(); i++) {
			if(selected[i]) {
				nbSelect++;
				textList = items.get(i);
			}
		}
		
		if(nbSelect > 1 && nbSelect == items.size() ) {
			textList = getContext().getString(allTextResId);	
		} else if(nbSelect > 1) {
			textList = getContext().getString(R.string.multiSpinnerCount).replaceAll("#1", ""+nbSelect);				
		}
		
		// all text on the spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_spinner_item, new String[] { textList} );
		setAdapter(adapter);
	}

	public boolean[] getSelected() {
		return selected;
	}

	public int[] getSelectedItemsPositions() {
		ArrayList<Integer> selectedPositions = new ArrayList<Integer>();
		for(int i = 0; i < selected.length; i++)
			if(selected[i])
				selectedPositions.add(new Integer(i));
		int[] result = new int[selectedPositions.size()];
		for(int i = 0; i < selectedPositions.size(); i++) {
			result[i] = selectedPositions.get(i).intValue();
		}
		return result;
	}
	
	
	public void setAllTextResId(int allTextResId) {
		this.allTextResId = allTextResId;
	}

	public void setNoneTextResId(int noneTextResId) {
		this.noneTextResId = noneTextResId;
	}

}

