package com.example.bookrecyclerview;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookrecyclerview.adapters.BookAdapter;
import com.example.bookrecyclerview.data.DBHelper;
import com.example.bookrecyclerview.databinding.ActivityMainBinding;
import com.example.bookrecyclerview.models.Book;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BookAdapter bookAdapter;

    public final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("TAG", "activityResultLauncher: " + result);
                Intent data = result.getData();
                if(result.getResultCode() ==  RESULT_OK && data != null){
                    int position = data.getIntExtra(BookActivity.POSITION, -1);
                    int mode = data.getIntExtra(BookActivity.MODE, -1);

                    Book book;
                    //New variant
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        book = data.getSerializableExtra(BookActivity.BOOK, Book.class);
                    } else {
                        //Old variant
                        book = (Book) data.getSerializableExtra(BookActivity.BOOK);
                    }
                    //
                    if(book!= null){
                        if(mode==BookActivity.INSERT){
                            bookAdapter.insert(book);
                            Toast.makeText(this, "Insert Success", Toast.LENGTH_SHORT).show();
                        }
                        if(mode==BookActivity.UPDATE){
                            bookAdapter.update(position, book);
                            Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Initialization Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Change R.layout.activity_main to Binding
        setContentView(binding.getRoot());
        //
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<Book> list = new ArrayList<>();
        //
/*        list = new ArrayList<>(List.of(
                new Book(0, "A", "a", "111", LocalDate.now()),
                new Book(0, "B", "b", "222", LocalDate.now()),
                new Book(0, "C", "c", "333", LocalDate.now())
        ));*/

        try (DBHelper helper = new DBHelper(this)) {
/*            helper.dropTable();
            helper.createTable();
            helper.insertAll(list);*/
            list = helper.selectAll();
        }

        bookAdapter = new BookAdapter(list, activityResultLauncher);
        binding.bookRecycler.setAdapter(bookAdapter);

        binding.bookRecycler.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        ));
        //button add book
        binding.addBookButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookActivity.class);
            intent.putExtra(BookActivity.MODE, BookActivity.INSERT);
            activityResultLauncher.launch(intent);
        });
    }
}