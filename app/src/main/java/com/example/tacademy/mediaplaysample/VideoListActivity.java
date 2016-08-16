package com.example.tacademy.mediaplaysample;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class VideoListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    ListView listView;
    SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        listView = (ListView)findViewById(R.id.listView);
        String[] from = {MediaStore.Video.Media.DISPLAY_NAME};
        int[] to ={android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);
        listView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(0, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                Intent intent = new Intent();
                intent.setData(uri);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
