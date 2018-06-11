package com.vn.ntsc.widget.views.edittext;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.Map;
import java.util.regex.Pattern;

public final class EmojiManager {
    private static EmojiManager mInstance = null;

    public static EmojiManager getInstance() {
        if (mInstance == null) {
            mInstance = new EmojiManager();
        }
        return mInstance;
    }

    private ArrayMap<String, String> emojiMap = new ArrayMap<>();
    private StringBuilder emojiPattens = new StringBuilder();

    public ArrayMap<String, String> getEmojiMap() {
        return emojiMap;
    }

    public String getEmojiPattens() {
        return emojiPattens.toString();
    }

    public void addEmoji(String key, String path) {
        if (!emojiMap.containsKey(key)) {
            if (emojiPattens.length() > 0) {
                emojiPattens.append("|");
            }
            emojiPattens.append(Pattern.quote(key));
        }
        emojiMap.put(key, path);
    }

    /**
     * @param emojiPath path to find emoji code
     * @return emoji code from path
     */
    public String getEmojiCode(String emojiPath) {
        if (emojiMap != null && emojiPath != null && emojiPath.length() > 0) {
            long current = System.currentTimeMillis();
            for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
                if (emojiPath.equals(entry.getValue()))
                    return entry.getKey();
            }
            Log.d("aaaaaa", (System.currentTimeMillis() - current) + "getEmojiCode");
        }
        return null;
    }

    /**
     * @param emojiCode code
     * @return emoji path or remote
     * @see #addEmoji(String, String)
     */
    public String getEmojiPath(String emojiCode) {
        if (emojiMap != null && emojiCode != null) {
            return emojiMap.get(emojiCode);
        }
        return null;
    }
}
