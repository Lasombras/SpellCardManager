package com.lasombras.android.scm.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lasombras.android.scm.R;
import com.lasombras.android.scm.model.Deck;
import com.lasombras.android.scm.utils.RessourceCacheManager;
import com.lasombras.android.scm.utils.SpellDatas;

public class DeckListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Deck> decks;
  
    public DeckListAdapter(Context context) {
   	
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        decks = new ArrayList<Deck>();
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        return decks.size();
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
            convertView = mInflater.inflate(R.layout.list_deck_class_text, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.listDeckClassText.deckTitle);
            holder.playerClassName = (TextView) convertView.findViewById(R.listDeckClassText.deckPlayerClass);
 
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setBackgroundResource(RessourceCacheManager.instance().getListBackgroundId(position));

        // Bind the data efficiently with the holder.
        Deck deck = getDeck(position);
        holder.text.setText(deck.getTitle());
        holder.text.setTypeface(RessourceCacheManager.instance().getTitleFontType());
        if(deck.getPlayerClassId()>0) {
        	holder.playerClassName.setText(SpellDatas.instance().getPlayerClass(deck.getPlayerClassId()).getTitle());
        } else {
        	holder.playerClassName.setText(" ");
        }
    	holder.playerClassName.setTypeface(RessourceCacheManager.instance().getTitleFontType());
        
        return convertView;
    }

    public Deck getDeck(int index) {
    	return decks.get(index);
    }
 
    public List<Deck> getDecks() {
    	return decks;
    }

    static class ViewHolder {
        TextView text;
        TextView playerClassName;
    }
    
    
    public void setDecks(List<Deck> decks) {
    	this.decks = decks;
    	super.notifyDataSetChanged();
    }

}
