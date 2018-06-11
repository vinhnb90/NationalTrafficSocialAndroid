package com.vn.ntsc.widget.livestream.face;

import android.util.SparseArray;

/**
 * 顔検出が必要なフィルタはこのインターフェイスを実装してください。
 * <p>
 * Created by kuroki on 2017/06/15.
 */

public interface FaceDetecting {

    /**
     * VideoRenderはこのメソッドを使ってフィルターに検出した顔情報を引き渡します。
     *
     * @param faces 検出した顔情報
     */
    void setFaces(SparseArray<FaceInfo> faces);
}
