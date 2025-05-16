package com.example.bookrecyclerview.adapters;


import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookrecyclerview.BookActivity;
import com.example.bookrecyclerview.R;
import com.example.bookrecyclerview.data.DBHelper;
import com.example.bookrecyclerview.databinding.ItemBookBinding;
import com.example.bookrecyclerview.models.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder> {
    private final List<Book> list;
    private final ActivityResultLauncher<Intent> activityResultLauncher;

    public BookAdapter(List<Book> list, ActivityResultLauncher<Intent> activityResultLauncher) {
        this.list = list;
        this.activityResultLauncher = activityResultLauncher;
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookBinding binding = ItemBookBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new BookHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {
        Book book = list.get(position);
        holder.binding.nameItem.setText(String.valueOf(book.getName()));
        holder.binding.authorItem.setText(String.format("(%s)", book.getAuthor()));
        holder.binding.genreItem.setText(String.valueOf(book.getGenre()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void insert(Book book) {
        list.add(book);
        notifyItemInserted(list.size() - 1);
    }

    public void update(int position, Book book) {
        list.set(position, book);
        notifyItemChanged(position);
    }

    public void delete(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public class BookHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

/*        public BookHolder(@NonNull View itemView) {
            super(itemView);
            TextView surnameItem = itemView.findViewById(R.id.surnameItem);
        }*/

        final ItemBookBinding binding;

        public BookHolder(@NonNull ItemBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
            binding.getRoot().setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), BookActivity.class);
            //
            intent.putExtra(BookActivity.POSITION, getAdapterPosition());
            intent.putExtra(BookActivity.MODE, BookActivity.UPDATE);
            intent.putExtra(BookActivity.BOOK, list.get(getAdapterPosition()));
            activityResultLauncher.launch(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            //PopupMenu
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v, Gravity.END);
            Menu menu = popupMenu.getMenu();
            MenuItem delete = menu.add("Delete");
            delete.setOnMenuItemClickListener(item -> {
                try (DBHelper helper = new DBHelper(v.getContext())) {
                    Book book = list.get(getAdapterPosition());
                    helper.delete(book.getId());
                    delete(getAdapterPosition());
                }
                return true;
            });
            popupMenu.show();
            return false;
        }
    }
}
