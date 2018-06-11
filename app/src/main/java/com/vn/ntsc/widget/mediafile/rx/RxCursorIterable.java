package com.vn.ntsc.widget.mediafile.rx;

import android.database.Cursor;

import java.util.Iterator;

/**
 * Created by nankai on 4/9/2018.
 */

public class RxCursorIterable implements Iterable<Cursor> {

    private Cursor mIterableCursor;

    public RxCursorIterable(Cursor c) {
        mIterableCursor = c;
    }

    public static RxCursorIterable from(Cursor c) {
        return new RxCursorIterable(c);
    }

    @Override
    public Iterator<Cursor> iterator() {
        return RxCursorIterator.from(mIterableCursor);
    }

    static class RxCursorIterator implements Iterator<Cursor> {

        private final Cursor mCursor;

        public RxCursorIterator(Cursor cursor) {
            mCursor = cursor;
        }

        public static Iterator<Cursor> from(Cursor cursor) {
            return new RxCursorIterator(cursor);
        }

        @Override
        public boolean hasNext() {
            return !mCursor.isClosed() && mCursor.moveToNext();
        }

        @Override
        public Cursor next() {
            return mCursor;
        }
    }
}