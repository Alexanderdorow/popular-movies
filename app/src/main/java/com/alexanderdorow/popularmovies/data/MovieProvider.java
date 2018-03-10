package com.alexanderdorow.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alexanderdorow.popularmovies.data.entry.MovieEntry;

public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIE = 1;
    public static final int CODE_MOVIE_WITH_ID = 11;
    public static final int CODE_TRAILER = 2;
    public static final int CODE_TRAILER_WITH_MOVIE_ID = 22;
    public static final int CODE_REVIEW = 3;
    public static final int CODE_REVIEW_WITH_MOVIE_ID = 33;


    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MovieDbHelper openHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, CODE_TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", CODE_TRAILER_WITH_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, CODE_REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", CODE_REVIEW_WITH_MOVIE_ID);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        openHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Context context = getContext();
        String tableName;
        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                tableName = MovieEntry.TABLE_NAME;
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        db.beginTransaction();
        int rowsInserted;
        try {
            rowsInserted = insertValuesOnTable(tableName, values, db);
            db.setTransactionSuccessful();
            if (rowsInserted > 0 && context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }
        } finally {
            db.endTransaction();
        }

        return rowsInserted;
    }

    private int insertValuesOnTable(String tableName, @NonNull ContentValues[] values, SQLiteDatabase db) {
        int rowsInserted = 0;
        for (ContentValues value : values) {
            long _id = db.insert(tableName, null, value);
            if (_id != -1) {
                rowsInserted++;
            }
        }
        return rowsInserted;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        String tableName;
        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                tableName = MovieEntry.TABLE_NAME;
                break;
            case CODE_MOVIE_WITH_ID:
                tableName = MovieEntry.TABLE_NAME;
                selection = MovieEntry._ID + " = ?";
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor = openHelper.getReadableDatabase().query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int numberOfRowsDeleted;
        String tableName;
        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
            case CODE_MOVIE_WITH_ID:
                tableName = MovieEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        numberOfRowsDeleted = openHelper.getWritableDatabase().delete(tableName, s, strings);
        return numberOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public void shutdown() {
        openHelper.close();
        super.shutdown();
    }
}
