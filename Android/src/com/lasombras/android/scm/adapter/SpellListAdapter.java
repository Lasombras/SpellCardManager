package com.lasombras.android.scm.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lasombras.android.scm.R;
import com.lasombras.android.scm.model.Spell;
import com.lasombras.android.scm.utils.RessourceCacheManager;

public class SpellListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Bitmap defaultIcon;
    private List<Spell> spells;
    private boolean showCustomImage;
    private boolean originalNameMode;
 
    public SpellListAdapter(Context context, List<Spell> spells, boolean showCustomImage, boolean originalNameMode) {
   	
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
 
        this.originalNameMode = originalNameMode;
        this.showCustomImage = showCustomImage;
        // Icons bound to the rows.
        defaultIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_icon);

        this.spells = spells;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        return spells.size();
    }

    /**
     * Since the data comes from an array, just returning the index is
     * sufficent to get at the data. If we were using a more complex data
     * structure, we would return whatever object represents one row in the
     * list.
     *
     * @see android.widget.ListAdapter#getItem(int)
     */
    public Object getItem(int position) {
        return position;
    }

    /**
     * Use the array index as a unique id.
     *
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Make a view to hold each row.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_spell_icon_text, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.listSpellIconText.spellListTitleName);
            holder.icon = (ImageView) convertView.findViewById(R.listSpellIconText.spellListIcon);
            holder.originalName = (TextView) convertView.findViewById(R.listSpellIconText.spellListOriginalName);
            holder.text.setTypeface(RessourceCacheManager.instance().getTitleFontType());
            holder.originalName.setTypeface(RessourceCacheManager.instance().getTitleFontType());

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
       	
         	
        // Bind the data efficiently with the holder.
        Spell spell = getSpell(position);
        if(originalNameMode) {
	        holder.text.setText(spell.getOriginalName());
	        holder.originalName.setText(spell.getTitle());
        } else {
	        holder.text.setText(spell.getTitle());
	        holder.originalName.setText(spell.getOriginalName());        	
        }
               
        if(showCustomImage) {	
	        Bitmap mIcon1 = RessourceCacheManager.instance().getSpellImage(spell.getImage());
	        if(mIcon1 != null)
	            holder.icon.setImageBitmap(mIcon1);
	        else 
	            holder.icon.setImageBitmap(defaultIcon);
	     
	        convertView.setBackgroundResource(RessourceCacheManager.instance().getListBackgroundId(position));
        }
        return convertView;
    }

    public Spell getSpell(int index) {
    	return spells.get(index);
    }
    
    static class ViewHolder {
        TextView text;
        TextView originalName;
        ImageView icon;
    }
    
    
    public void setSpells(List<Spell> spells) {
    	this.spells = spells;
    	super.notifyDataSetChanged();
    }

}
