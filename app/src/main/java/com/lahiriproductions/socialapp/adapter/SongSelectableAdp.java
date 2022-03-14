package com.lahiriproductions.socialapp.adapter;

import android.util.SparseBooleanArray;

import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.fragments.MainSongsListFrag;

import java.util.ArrayList;
import java.util.List;

public abstract class SongSelectableAdp<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

	private SparseBooleanArray selectedItems;
 
	public SongSelectableAdp() {
		selectedItems = new SparseBooleanArray ();
	}

	public boolean isSelected(int position)
	{
		return getSelectedItems().contains(position);
	}

	public void toggleSelection(int position)
	{
		if (selectedItems.get(position,false))
		{
			selectedItems.delete(position);
		}
		else
		{
			selectedItems.put(position, true);
		}

		MainSongsListFrag.togggle();
		notifyItemChanged(position);
	}

	public void clearSelection() {
		try
		{
			List<Integer> selection = getSelectedItems();
			selectedItems.clear();
			for (Integer i : selection) {
                notifyItemChanged(i);
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public int getSelectedItemCount() {
		return selectedItems.size();
	}
 

	public List<Integer> getSelectedItems() {
		List<Integer> items = new ArrayList<>(selectedItems.size());
		for (int i = 0; i < selectedItems.size(); ++i) {
			items.add(selectedItems.keyAt(i));
		}
		return items;
	}
}