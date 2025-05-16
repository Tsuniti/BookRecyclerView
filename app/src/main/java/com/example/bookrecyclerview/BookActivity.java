package com.example.bookrecyclerview;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookrecyclerview.data.DBHelper;
import com.example.bookrecyclerview.databinding.ActivityBookBinding;
import com.example.bookrecyclerview.models.Book;

import java.time.LocalDate;

public class BookActivity extends AppCompatActivity {


    public static final int INSERT = 0;
    public static final int UPDATE = 1;

    public static final String MODE = "mode";
    public static final String POSITION = "position";
    public static final String BOOK = "book";

    private int mode;
    private int bookId;

    private ActivityBookBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //
        binding = ActivityBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //
        Intent intent = getIntent();
        mode = intent.getIntExtra(MODE, -1);

        if (mode == UPDATE) {
            Book book;
            //New variant
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                book = intent.getSerializableExtra(BOOK, Book.class);
            } else {
                //Old variant
                book = (Book) intent.getSerializableExtra(BOOK);
            }
            if (book != null) {
                bookId = book.getId();
                binding.nameBookEdit.setText(String.valueOf(book.getName()));
                binding.authorBookEdit.setText(String.valueOf(book.getAuthor()));
                binding.genreBookEdit.setText(String.valueOf(book.getGenre()));
                binding.publicationDateBookEdit.setText(String.valueOf(book.getPublicationDate()));

            }
        }
        binding.saveBookButton.setOnClickListener(v -> {
            if (
                    !binding.nameBookEdit.getText().toString().isBlank() &&
                            !binding.authorBookEdit.getText().toString().isBlank() &&
                            !binding.genreBookEdit.getText().toString().isBlank() &&
                            !binding.publicationDateBookEdit.getText().toString().isBlank()
            ) {
                Book book = new Book(
                        bookId,
                        binding.nameBookEdit.getText().toString(),
                        binding.authorBookEdit.getText().toString(),
                        binding.genreBookEdit.getText().toString(),
                        LocalDate.parse(
                                binding.publicationDateBookEdit.getText().toString()
                        )
                );
                //Save in db
                try (DBHelper helper = new DBHelper(BookActivity.this)) {
                    if (mode == INSERT)
                        helper.insert(book);
                    else if (mode == UPDATE)
                        helper.update(book);
                    //Save result activity
                    int position = getIntent().getIntExtra(POSITION, -1);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(POSITION, position);
                    resultIntent.putExtra(MODE, mode);
                    resultIntent.putExtra(BOOK, book);
                    //
                    setResult(RESULT_OK, resultIntent);
                    //
                    finish();

                }

            }
        });
    }
}