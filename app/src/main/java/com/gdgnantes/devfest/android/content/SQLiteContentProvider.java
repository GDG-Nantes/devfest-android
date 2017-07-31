package com.gdgnantes.devfest.android.content;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class SQLiteContentProvider extends ContentProvider {

    private static final int SLEEP_AFTER_YIELD_DELAY = 4000;
    private static final int MAX_OPERATIONS_PER_YIELD_POINT = Integer.MAX_VALUE;

    private final ThreadLocal<Boolean> mApplyingBatch = new ThreadLocal<>();
    private final Set<Uri> mChangedUris = new HashSet<>();

    private SQLiteOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = createOpenHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri result;
        boolean callerIsSyncAdapter = isCallerSyncAdapter(uri);
        boolean applyingBatch = applyingBatch();
        if (!applyingBatch) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                result = insertInTransaction(uri, values, callerIsSyncAdapter);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            onEndTransaction(callerIsSyncAdapter);
        } else {
            result = insertInTransaction(uri, values, callerIsSyncAdapter);
        }
        return result;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        boolean callerIsSyncAdapter = isCallerSyncAdapter(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                insertInTransaction(uri, value, callerIsSyncAdapter);
                db.yieldIfContendedSafely();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        onEndTransaction(callerIsSyncAdapter);
        return values.length;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        boolean callerIsSyncAdapter = isCallerSyncAdapter(uri);
        boolean applyingBatch = applyingBatch();
        if (!applyingBatch) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                count = updateInTransaction(uri, values, selection, selectionArgs, callerIsSyncAdapter);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            onEndTransaction(callerIsSyncAdapter);
        } else {
            count = updateInTransaction(uri, values, selection, selectionArgs, callerIsSyncAdapter);
        }

        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        boolean callerIsSyncAdapter = isCallerSyncAdapter(uri);
        boolean applyingBatch = applyingBatch();
        if (!applyingBatch) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                count = deleteInTransaction(uri, selection, selectionArgs, callerIsSyncAdapter);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            onEndTransaction(callerIsSyncAdapter);
        } else {
            count = deleteInTransaction(uri, selection, selectionArgs, callerIsSyncAdapter);
        }
        return count;
    }

    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {

        int ypCount = 0;
        int opCount = 0;
        boolean callerIsSyncAdapter = false;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            mApplyingBatch.set(true);
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                if (++opCount >= MAX_OPERATIONS_PER_YIELD_POINT) {
                    throw new OperationApplicationException("Too many content provider " +
                            "operations between yield points. The maximum number of operations " +
                            "per yield point is " + MAX_OPERATIONS_PER_YIELD_POINT,
                            ypCount);
                }
                final ContentProviderOperation operation = operations.get(i);
                if (!callerIsSyncAdapter && isCallerSyncAdapter(operation.getUri())) {
                    callerIsSyncAdapter = true;
                }
                if (i > 0 && operation.isYieldAllowed()) {
                    opCount = 0;
                    if (db.yieldIfContendedSafely(SLEEP_AFTER_YIELD_DELAY)) {
                        ypCount++;
                    }
                }
                results[i] = operation.apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            mApplyingBatch.set(false);
            db.endTransaction();
            onEndTransaction(callerIsSyncAdapter);
        }
    }

    protected void onEndTransaction(boolean callerIsSyncAdapter) {
        final ContentResolver resolver = getContext().getContentResolver();
        if (resolver == null) {
            return;
        }
        final Set<Uri> changed;
        synchronized (mChangedUris) {
            changed = new HashSet<>(mChangedUris);
            mChangedUris.clear();
        }

        for (Uri uri : changed) {
            resolver.notifyChange(uri, null, !callerIsSyncAdapter && shouldSyncToNetwork(uri));
        }
    }

    public SQLiteOpenHelper getOpenHelper() {
        return mOpenHelper;
    }

    /**
     * Returns a {@link SQLiteOpenHelper} that can open the database.
     */
    public abstract SQLiteOpenHelper createOpenHelper(Context context);

    /**
     * The equivalent of the {@link #insert} method, but invoked within a transaction.
     */
    public abstract Uri insertInTransaction(Uri uri, ContentValues values, boolean callerIsSyncAdapter);

    /**
     * The equivalent of the {@link #update} method, but invoked within a transaction.
     */
    public abstract int updateInTransaction(Uri uri, ContentValues values, String selection, String[] selectionArgs,
                                            boolean callerIsSyncAdapter);

    /**
     * The equivalent of the {@link #delete} method, but invoked within a transaction.
     */
    public abstract int deleteInTransaction(Uri uri, String selection, String[] selectionArgs,
                                            boolean callerIsSyncAdapter);

    protected boolean isCallerSyncAdapter(Uri uri) {
        return false;
    }

    protected boolean shouldSyncToNetwork(Uri uri) {
        return false;
    }

    /**
     * Call this to add a URI to the list of URIs to be notified when the transaction is committed.
     */
    protected void postNotifyChange(Uri uri) {
        synchronized (mChangedUris) {
            mChangedUris.add(uri);
        }
    }

    private boolean applyingBatch() {
        return mApplyingBatch.get() != null && mApplyingBatch.get();
    }

}