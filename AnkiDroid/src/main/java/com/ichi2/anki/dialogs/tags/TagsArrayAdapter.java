package com.ichi2.anki.dialogs.tags;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;

import com.ichi2.anki.R;
import com.ichi2.utils.FilterResultsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class TagsArrayAdapter extends  RecyclerView.Adapter<TagsArrayAdapter.ViewHolder> implements Filterable {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckedTextView mTagItemCheckedTextView;
        public ViewHolder(CheckedTextView ctv) {
            super(ctv);
            mTagItemCheckedTextView = ctv;
        }
    }

    @NonNull
    public final TagsList mTagsList;
    @Nullable
    public List<String> mFilteredList;

    public TagsArrayAdapter(@NonNull TagsList tagsList) {
        mTagsList = tagsList;
        sortData();
    }

    public void sortData() {
        mTagsList.sort();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tags_item_list_dialog, parent, false);

        ViewHolder vh = new ViewHolder(v.findViewById(R.id.tags_dialog_tag_item));
        vh.mTagItemCheckedTextView.setOnClickListener(view -> {
            CheckedTextView ctv = (CheckedTextView) view;
            ctv.toggle();
            String tag = ctv.getText().toString();
            if (ctv.isChecked()) {
                mTagsList.check(tag);
            } else if (!ctv.isChecked()) {
                mTagsList.uncheck(tag);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String tag = getTagAtPosition(position);
        holder.mTagItemCheckedTextView.setText(tag);
        holder.mTagItemCheckedTextView.setChecked(mTagsList.isChecked(tag));
    }


    private String getTagAtPosition(int position) {
        if (mFilteredList != null) {
            return mFilteredList.get(position);
        }
        return mTagsList.get(position);
    }

    @Override
    public int getItemCount() {
        if (mFilteredList != null) {
            return mFilteredList.size();
        }
        return mTagsList.size();
    }

    @Override
    public Filter getFilter() {
        return new TagsFilter();
    }

    /* Custom Filter class - as seen in http://stackoverflow.com/a/29792313/1332026 */
    private class TagsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint.length() == 0) {
                mFilteredList = null;
            } else {
                mFilteredList = new ArrayList<>();
                final String filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();
                for (String tag : mTagsList) {
                    if (tag.toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                        mFilteredList.add(tag);
                    }
                }
            }

            return FilterResultsUtils.fromCollection(mFilteredList);
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            sortData();
            notifyDataSetChanged();
        }
    }
}
