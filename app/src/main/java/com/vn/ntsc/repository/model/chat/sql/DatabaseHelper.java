package com.vn.ntsc.repository.model.chat.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.tux.socket.models.Message;
import com.vn.ntsc.repository.model.chat.model.EmojiModel;
import com.vn.ntsc.repository.model.chat.model.EmojiVersionModel;
import com.vn.ntsc.repository.model.emoji.EmojiCategory;
import com.vn.ntsc.repository.model.emoji.EmojiItem;
import com.vn.ntsc.repository.model.emoji.EmojiReponse;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.chats.ChatUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE;

/**
 * Created by Doremon on 12/27/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper mInstance;
    private static SQLiteDatabase mDatabase;

    /**
     * pls call this function first if you don\'t want to face NPE when access {@link #mDatabase}
     *
     * @param ctx to access db
     * @return single instance of database
     */
    public static DatabaseHelper getInstance(Context ctx) {
        // use the application context as suggested by CommonsWare.
        // this will ensure that you dont accidentally leak an Activitys
        // context (see this article for more information:
        // http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
            mDatabase = mInstance.getWritableDatabase();
        }
        return mInstance;
    }

    public static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "db_chat";
    private static final int DATABASE_VERSION = 8;

    /**
     * table name of emoji
     */
    private static final String TABLE_EMOJI = "TB_EMOJI";
    // emoji symbol
    private static final String EMOJI_CODE = "code";
    /**
     * emoji local path if downloaded (nullable)
     */
    private static final String EMOJI_LOCAL = "local";
    /**
     * emoji download url (must)
     */
    private static final String EMOJI_REMOTE = "remote";
    /**
     * category id to request update (must)
     */
    private static final String EMOJI_CAT_ID = "category_id";
    /**
     * category version to check update (must)
     */
    private static final String EMOJI_CAT_VERSION = "category_version";
    // ================================================================ //
    // table version sticker
    private static final String TABLE_STICKER_CAT_VER = "TABLE_STICKER_CAT_VER";

    // Text Table Columns names sticker version
    private static final String STICKER_CAT_ID = "sticker_cat";
    private static final String STICKER_VER = "sticker_ver";

    //  create emoji table statement
    private static final String CREATE_TABLE_EMOJI = "CREATE TABLE "
            + TABLE_EMOJI + "("
            + "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + EMOJI_CODE + " TEXT NOT NULL UNIQUE,"
            + EMOJI_LOCAL + " TEXT,"
            + EMOJI_REMOTE + " TEXT NOT NULL,"
            + EMOJI_CAT_ID + " TEXT NOT NULL,"
            + EMOJI_CAT_VERSION + " INTEGER NOT NULL"
            + ");";

    // create sticker version statement
    private static final String CREATE_STICKER_CAT_VER = "CREATE TABLE "
            + TABLE_STICKER_CAT_VER + "("
            + STICKER_CAT_ID + " TEXT,"
            + STICKER_VER + " INTEGER"
            + ");";

    // SEND MESSAGE ERROR
    // idea: every times before send message
    private static final String TABLE_CHAT_ERROR = "TB_CHAT_ERROR";

    // Text Table Columns names
    private static final String KEY_MESSAGE_ID = "message_id";
    private static final String KEY_RAW_MESSAGE = "raw_message";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TIME = "time";

    private static final String CREATE_TABLE_CHAT_ERROR = "CREATE TABLE "
            + TABLE_CHAT_ERROR + "("
            + "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_MESSAGE_ID + " TEXT unique,"
            + KEY_RAW_MESSAGE + " TEXT,"
            + KEY_TYPE + " TEXT,"
            + KEY_TIME + " DATETIME"
            + ");";

    private static final String TABLE_BANNED_WORDS = "TB_BANNED_WORDS";
    private static final String KEY_BANNED_WORD = "banned_word";
    private static final String KEY_BANNED_WORD_VERSION = "version";
    // banned word
    private static final String CREATE_TABLE_BANNED_WORDS = "CREATE TABLE "
            + TABLE_BANNED_WORDS + "("
            + "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_BANNED_WORD + " TEXT NOT NULL,"
            + KEY_BANNED_WORD_VERSION + " INTEGER NOT NULL"
            + ");";

    // ============================ //

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_EMOJI);
        sqLiteDatabase.execSQL(CREATE_STICKER_CAT_VER);

        sqLiteDatabase.execSQL(CREATE_TABLE_CHAT_ERROR);
        sqLiteDatabase.execSQL(CREATE_TABLE_BANNED_WORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EMOJI);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STICKER_CAT_VER);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_ERROR);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BANNED_WORDS);
        // Create tables again
        onCreate(sqLiteDatabase);
    }

    /**
     * Creating a todo
     */
    public long insertStickerVer(EmojiVersionModel versionModel) {
        ContentValues values = new ContentValues();
        values.put(STICKER_CAT_ID, versionModel.getCatId());
        values.put(STICKER_VER, versionModel.getVersion());
        // insert row

        LogUtils.e(TAG, "getCatId " + versionModel.getCatId());
        LogUtils.e(TAG, "getVersion " + versionModel.getVersion());

        return mDatabase.insert(TABLE_STICKER_CAT_VER, null, values);
    }

    // Getting All emoji from db
    public ArrayList<EmojiModel> getAllEmoji() {
        ArrayList<EmojiModel> emojiModels = new ArrayList<>();
        // Select All Query
        Cursor cursor = mDatabase.query(TABLE_EMOJI, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String code = cursor.getString(cursor.getColumnIndex(EMOJI_CODE));
                String local = cursor.getString(cursor.getColumnIndex(EMOJI_LOCAL));
                String remote = cursor.getString(cursor.getColumnIndex(EMOJI_REMOTE));
                String category = cursor.getString(cursor.getColumnIndex(EMOJI_CAT_ID));
                emojiModels.add(new EmojiModel(
                        code,
                        null,
                        TextUtils.isEmpty(local) ? remote : local,
                        category
                ));

                cursor.moveToNext();
            }
            cursor.close();
        }
        // return contact list
        return emojiModels;
    }

    // Getting All Contacts stiker version
    public List<EmojiVersionModel> getAllStickerVer() {
        List<EmojiVersionModel> versionModels = new ArrayList<EmojiVersionModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STICKER_CAT_VER;
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        // looping through all rows and adding to list
        while (!cursor.isAfterLast()) {
            EmojiVersionModel versionModel = new EmojiVersionModel();
            versionModel.setCatId(cursor.getString(cursor.getColumnIndex(STICKER_CAT_ID)));
            versionModel.setVersion(cursor.getInt(cursor.getColumnIndex(STICKER_VER)));

            // Adding contact to list
            versionModels.add(versionModel);
            cursor.moveToNext();
        }

        cursor.close();
        // return contact list
        return versionModels;
    }

    // delete all emoji in database
    public void deleteAllEmoji() {
        String selectQuery = "DELETE FROM " + TABLE_EMOJI;
        mDatabase.execSQL(selectQuery);
    }

    // delele sticker version old
    public void deleteStickerVerFlllowCatId(String catId) {
//        DELETE FROM Customers WHERE CustomerName='Alfreds Futterkiste';
        String selectQuery = "DELETE FROM " + TABLE_STICKER_CAT_VER + " WHERE " + STICKER_CAT_ID + "=" + "'" + catId + "'";
        mDatabase.execSQL(selectQuery);
    }

    // SEND MESSAGE ERROR

    /**
     * add send error message into db to resend
     *
     * @param messageId   to identify message
     * @param rawMessage  raw to send via socket
     * @param messageType type message
     * @param messageTime time to order
     */
    public void saveErrorMessage(String messageId, String rawMessage, String messageType, String messageTime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_MESSAGE_ID, messageId);
        contentValues.put(KEY_RAW_MESSAGE, rawMessage);
        contentValues.put(KEY_TYPE, messageType);
        contentValues.put(KEY_TIME, messageTime);

        // message id may be conflict, so insert or Throw
        mDatabase.insertWithOnConflict(TABLE_CHAT_ERROR, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * save all sending  or error message into db
     *
     * @param sendingMessageList          sending list
     * @param sendingOrErrorMessageIdList message id list
     */
    public void saveErrorMessage(List<Message> sendingMessageList, List<String> sendingOrErrorMessageIdList) {
        mDatabase.beginTransaction();

        // fill all message have save message id
        for (int i = sendingOrErrorMessageIdList.size(); i > 0; i--) {
            // i - 1 cause index = size of array
            String messageId = sendingOrErrorMessageIdList.get(i - 1);
            for (Message message : sendingMessageList) {
                if (messageId.equals(message.getId())) {
                    saveErrorMessage(messageId, message.getRawText(), message.getMessageType(), message.getOriginTime());
                }
            }
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    /**
     * @return list error message from db
     */
    public List<Message> getErrorMessages() {
        List<Message> listErrorMessage = new ArrayList<>();

        Cursor cursor = mDatabase.query(TABLE_CHAT_ERROR, new String[]{KEY_RAW_MESSAGE}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String rawMessage = cursor.getString(0);
                Message message = Message.parse(rawMessage);
                message.setRawText(rawMessage);

                listErrorMessage.add(message);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listErrorMessage;
    }

    /**
     * delete error if exist
     *
     * @param messageId to find message error
     */
    public void deleteErrorMessage(String messageId) {
        mDatabase.delete(TABLE_CHAT_ERROR, KEY_MESSAGE_ID + "=?", new String[]{messageId});
    }

    /**
     * @param messageId to find error message
     * @return error message with id
     */
    public Message getErrorMessage(String messageId) {
        Cursor cursor = mDatabase.query(TABLE_CHAT_ERROR, new String[]{KEY_RAW_MESSAGE}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String rawMessage = cursor.getString(0);
                Message message = Message.parse(rawMessage);
                if (message.getId().equals(messageId)) {
                    message.setRawText(rawMessage);
                    return message;
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return null;
    }

    /**
     * insert emoji data
     *
     * @param emojiReponse from server
     */
    public Set<String> insertEmoji(EmojiReponse emojiReponse) {
        if (emojiReponse == null) return null;
        Set<String> downloadUrls = new HashSet<>();
        List<EmojiCategory> listCategory = emojiReponse.data;
        if (listCategory == null) return null;

        mDatabase.beginTransaction();
        for (EmojiCategory emojiCategory : listCategory) {
            String categoryId = emojiCategory.getCatId();
            int categoryVersion = emojiCategory.getVersion();

            // delete out of date emoji
            mDatabase.delete(TABLE_EMOJI, EMOJI_CAT_ID + " =? AND " + EMOJI_CAT_VERSION + " <?", new String[]{categoryId, String.valueOf(categoryVersion)});

            for (EmojiItem item : emojiCategory.getLstEmoji()) {
                String remote = item.getEmojiUrl();
                downloadUrls.add(remote);
                for (String code : item.getCodeLst()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(EMOJI_CODE, code);
                    contentValues.put(EMOJI_REMOTE, remote);
                    contentValues.put(EMOJI_CAT_ID, categoryId);
                    contentValues.put(EMOJI_CAT_VERSION, categoryVersion);

                    // don't insert if exist
                    mDatabase.insertWithOnConflict(TABLE_EMOJI, null, contentValues, CONFLICT_IGNORE);
                }
            }
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        Log.d(TAG, "insertEmoji: ");
        return downloadUrls;
    }

    /**
     * update downloaded emoji
     *
     * @param context to get file path emoji
     */
    public void updateEmojiLocal(Context context) {
        mDatabase.beginTransaction();
        Cursor cursor = mDatabase.query(true, TABLE_EMOJI, new String[]{EMOJI_REMOTE}, null, null, EMOJI_REMOTE, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // cause you have 1 column
                String url = cursor.getString(cursor.getColumnIndex(EMOJI_REMOTE));
                File emojiDownloaded = ChatUtils.predicateEmojiFile(context, url);
                if (emojiDownloaded.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(EMOJI_LOCAL, emojiDownloaded.getPath());
                    mDatabase.updateWithOnConflict(TABLE_EMOJI, values, EMOJI_REMOTE + " = ?", new String[]{url}, SQLiteDatabase.CONFLICT_REPLACE);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }
    // ==================

    // ============== BANNED WORDS ============= //

    /**
     * update list banned words
     */
    public void updateBannedWords(List<String> listBannedWord, int version) {
        mDatabase.beginTransaction();
        // truncate banned words table
        mDatabase.delete(TABLE_BANNED_WORDS, null, null);
        // insert each words
        for (String bannedWord : listBannedWord) {
            LogUtils.i(TAG,"update banned Words " + bannedWord);
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_BANNED_WORD, bannedWord);
            contentValues.put(KEY_BANNED_WORD_VERSION, version);
            mDatabase.insert(TABLE_BANNED_WORDS, null, contentValues);
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    /**
     * @return last version banned word or -1 if not exist
     */
    public int getLastBannedWordVersion() {
        Cursor cursor = mDatabase.query(TABLE_BANNED_WORDS, null, null, null, KEY_BANNED_WORD_VERSION, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            int lastVersion = cursor.getInt(cursor.getColumnIndex(KEY_BANNED_WORD_VERSION));
            DatabaseUtils.dumpCurrentRow(cursor);
            cursor.close();
            LogUtils.i(TAG,"Last Banned Word Version " + lastVersion);
            return lastVersion;
        }
        LogUtils.w(TAG,"Last Banned Word Version " + -1);
        return -1;
    }

    /**
     * @return all banned word from db
     */
    public ArrayList<String> getBannedWords() {
        ArrayList<String> bannedWords = new ArrayList<>();
        Cursor cursor = mDatabase.query(TABLE_BANNED_WORDS, new String[]{KEY_BANNED_WORD}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String bannedWord = cursor.getString(cursor.getColumnIndex(KEY_BANNED_WORD));
                LogUtils.i(TAG,"Banned Words " + bannedWord);
                bannedWords.add(bannedWord);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return bannedWords;
    }
    // ========================================= //
}
