package com.example.bookrecyclerview.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bookrecyclerview.models.Book;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    private final String TAG = "TAG";

    private final String TABLE = "table_books";
    private final String ID = "id";
    private final String NAME = "name";
    private final String AUTHOR = "author";
    
    private final String GENRE = "genre";
    private final String PUBLICATIONDATE = "publicationDate";

    public DBHelper(@Nullable Context context) {
        super(context, "books_db", null, 1);
        createTable();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private ContentValues convertToContentValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(NAME, book.getName());
        values.put(AUTHOR, book.getAuthor());
        values.put(GENRE, book.getGenre());
        values.put(PUBLICATIONDATE, String.valueOf(book.getPublicationDate()));
        return values;
    }

    @SuppressLint("Range")
    private Book convertToBook(Cursor cursor) {
        return new Book(
                cursor.getInt(cursor.getColumnIndex(ID)),
                cursor.getString(cursor.getColumnIndex(NAME)),
                cursor.getString(cursor.getColumnIndex(AUTHOR)),
                cursor.getString(cursor.getColumnIndex(GENRE)),
                LocalDate.parse(
                        cursor.getString(cursor.getColumnIndex(PUBLICATIONDATE))
                )
        );
    }

    public void createTable() {
        String sql = "create table if not exists %s(%s integer primary key autoincrement, %s text, %s text, %s text, %s text)";
        sql = String.format(sql, TABLE, ID, NAME, AUTHOR, GENRE, PUBLICATIONDATE);
        getWritableDatabase().execSQL(sql);
        Log.d(TAG, "createTable: ");
    }

    public void dropTable() {
        String sql = "drop table if exists  " + TABLE;
        getWritableDatabase().execSQL(sql);
        Log.d(TAG, "dropTable: ");
    }

    public List<Book> selectAll() {
        List<Book> list = new ArrayList<>();
        String sql = "select * from  " + TABLE;
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            Book book;
            while (cursor.moveToNext()) {
                book = convertToBook(cursor);
                list.add(book);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "selectAll: ", e);
        }
        return list;
    }

    //selectById
    public Book select(int bookId) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE,
                null,
                ID + "=?",
                new String[]{String.valueOf(bookId)},
                null,
                null,
                null
        )) {
            if (cursor.moveToNext()) {
                return convertToBook(cursor);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "select: ", e);
        }
        return null;
    }

    public Book insert(Book book) {
        ContentValues values = convertToContentValues(book);
        long id = getWritableDatabase().insert(TABLE, null, values);
        return select((int) id);
    }

    public void insertAll(List<Book> bookList) {
        for (Book book : bookList) {
            insert(book);
        }
    }

    //deleteById
    public boolean delete(int bookId) {
        int count = getWritableDatabase().delete(
                TABLE,
                ID + "=?",
                new String[]{String.valueOf(bookId)}
        );
        return count > 0;
    }

    public boolean update(Book book) {
        int count = getWritableDatabase().update(
                TABLE,
                convertToContentValues(book),
                ID + "=?",
                new String[]{String.valueOf(book.getId())}
        );
        return count > 0;
    }
}
