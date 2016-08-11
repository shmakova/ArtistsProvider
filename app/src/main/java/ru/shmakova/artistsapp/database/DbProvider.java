package ru.shmakova.artistsapp.database;

import android.content.Context;
import android.database.Cursor;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.shmakova.artistsapp.network.models.Artist;

class DbProvider {

    private final DbBackend dbBackend;
    private final CustomExecutor executor;

    DbProvider(Context context) {
        dbBackend = new DbBackend(context);
        executor = new CustomExecutor();
    }

    Cursor getArtistsList(String[] projection,
                          String selection,
                          String[] selectionArgs,
                          String sortOrder) throws ExecutionException, InterruptedException {
        Future<Cursor> result = executor.submit(() -> dbBackend.getArtistsList(projection, selection, selectionArgs, sortOrder));
        return result.get();
    }

    void insertArtistsList(final List<Artist> artistList) {
        executor.execute(() -> dbBackend.insertArtistsList(artistList));
    }


    private class CustomExecutor extends ThreadPoolExecutor {
        CustomExecutor() {
            super(
                    Runtime.getRuntime().availableProcessors() * 2,
                    Runtime.getRuntime().availableProcessors() * 2,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>());
        }
    }
}
