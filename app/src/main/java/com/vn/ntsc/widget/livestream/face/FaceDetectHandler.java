package com.vn.ntsc.widget.livestream.face;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.WindowManager;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.nio.ByteBuffer;

import static android.graphics.ImageFormat.NV21;

/**
 * 顔検出実行クラス。
 * <p>
 * 顔検出処理には時間を要するため別スレッドで実行するようにしています。
 * <p>
 * Created by kuroki on 2017/06/15.
 */

public class FaceDetectHandler implements Runnable {

    private static final String TAG = "FaceDetectHandler";

    private final Context applicationContext;

    private Object  sync    = new Object();
    private boolean running = false;

    // フレームデータ
    private byte[] data;
    private int    mImageWidth;
    private int    mImageHeight;

    // 顔検出関連
    private SparseArray<FaceInfo> mFaces;  // ローパスフィルタ適用後の値
    private SparseArray<FaceInfo> mOldFaces;   // ローパスフィルタを適用していない前回測定値
    private SafeFaceDetector      mFaceDetector;
    private              long  mFaceOutTime          = 0;
    private static final int   FACE_OUT_TIME_MILLIS  = 500;
    private static final float LOW_PASS_FILTER_ALPHA = 0.3f;
    private static final float CONTINUOUS_LIMIT      = 100.0f;

    // 顔検出回数の測定用
    private int  detectCount = 0;
    private long detectTime  = 0;

    public FaceDetectHandler(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 顔検出スレッドの実行状態を返却します。
     *
     * @return 実行中の場合はtrue。そうでなければfalse。
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * 顔検出スレッドを起動します。
     * すでに起動済みの場合はなにもしません。
     */
    public void start() {

        Log.i(TAG, "start");

        synchronized (sync) {
            if (this.running) {
                return;
            }

            // 顔認識スレッドを起動
            this.running = true;
            new Thread(this).start();
        }
    }

    /**
     * 顔検出スレッドを停止します。
     */
    public void stop() {

        Log.i(TAG, "stop");

        synchronized (sync) {
            this.running = false;
            this.data = null;
        }
    }

    /**
     * FaceDetectorクラスをリリースします。
     * 顔検出が必要なくなったら呼び出してください。
     */
    public void release() {

        Log.i(TAG, "release");

        if (this.mFaceDetector != null) {
            this.mFaceDetector.release();
            this.mFaceDetector = null;
        }
        synchronized (sync) {
            this.running = false;
            this.data = null;
            sync.notifyAll();
        }
    }

    /**
     * 顔検出したいフレーム画像データを渡します。
     * ここで渡されたデータを顔検出スレッドが取得して顔検出処理を行います。
     * 前回わたしたフレームがまだ解析中の場合はなにもしません。
     *
     * @param data        フレーム画像データ(YUV形式)
     * @param imageWidth  画像サイズ(width)
     * @param imageHeight 画像サイズ(height)
     */
    public void setData(byte[] data, int imageWidth, int imageHeight) {

        // 解析中の画像データがあればなにもしない
        if (this.data != null) {
            return;
        }

        synchronized (sync) {

            // もしスレッドが停止していたら再開させる
            if (!this.running) {
                this.start();
            }

            // データがなければ設定する
            if (this.data == null) {
                this.data = data;
                this.mImageWidth = imageWidth;
                this.mImageHeight = imageHeight;

                // データが設定されたら顔認識スレッドに通知する
                this.sync.notifyAll();
            }
        }
    }

    /**
     * 顔検出結果を返却します。
     *
     * @return 検出したFaceデータ。
     */
    public SparseArray<FaceInfo> getFaces() {
        return mFaces;
    }


    @Override
    public void run() {

        while (running) {

            if (this.mFaceDetector == null) {
                Log.i(TAG, "create face detector");
                // 顔認識オブジェクトの生成
                FaceDetector detector = new FaceDetector.Builder(applicationContext)
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.NO_LANDMARKS)
                        .setMode(FaceDetector.ACCURATE_MODE)
                        .setProminentFaceOnly(true)
                        .build();
                this.mFaceDetector = new SafeFaceDetector(detector);
            }

            synchronized (sync) {

                // フレームがまだ設定されていない場合はWait。
                if (data == null) {
                    try {
                        sync.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }

            // dataがあれば顔を検出する
            if (data != null) {
                detectFaces();

                // 顔検出が終わったらフレームデータを削除する
                synchronized (sync) {
                    data = null;
                }
            }
        }
        Log.i(TAG, "Thread end.");
    }

    /**
     * 顔検出処理
     */
    private void detectFaces() {

        if (data == null) {
            return;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(data);

        int orientation = getDeviceOrientation();

        // デバイスの向きからフレーム画像の向きを設定する。
        // ただしく向きを設定しないと顔検出がされない。
        int rotation;
        switch (orientation) {
            default:
            case Surface.ROTATION_0:    // portrait
                rotation = Frame.ROTATION_270;
                break;
            case Surface.ROTATION_90:   // left
                rotation = Frame.ROTATION_0;
                break;
            case Surface.ROTATION_180:  // upside down
                rotation = Frame.ROTATION_90;
                break;
            case Surface.ROTATION_270:  // right
                rotation = Frame.ROTATION_180;
                break;
        }

        // 顔検出
        Frame frame = new Frame.Builder()
                .setImageData(byteBuffer, mImageWidth, mImageHeight, NV21).setRotation(rotation).build();
        SparseArray<Face> faces = mFaceDetector.detect(frame);

        if (!existFace(faces)) {

            if (mFaces != null && mFaceOutTime == 0) {
                mFaceOutTime = System.currentTimeMillis();
            }

            // 顔を認識しなくなってから FACE_OUT_TIME_MILLIS 経過したのち顔がなくなったと判定する。
            // たまに顔の検出に失敗することを考慮しての対策。
            if (existFace(mFaces) && (System.currentTimeMillis() - mFaceOutTime) >= FACE_OUT_TIME_MILLIS) {
                this.mFaceOutTime = 0;
                mFaces = null;
                mOldFaces = null;
            }
        } else {

            // 顔検出結果の座標と、画面に描画する時の座標がことなるので、座標変換を行う。
            // さらに、ローパスフィルターを適用して、画像位置の細かい振動を滑らかにする。
            // 座標変換後の x, y は顔の中心位置を表す。
            SparseArray<FaceInfo> newFilteredFaces = new SparseArray<>(faces.size()); // ローパスフィルタ適用後の値を新しく入れる配列
            SparseArray<FaceInfo> oldFaces = new SparseArray<>(faces.size()); // 次回のために今回の測定値を入れる配列

            for (int i = 0; i < faces.size(); i++) {

                int key = faces.keyAt(i);

                Face face = faces.get(key);
                if (face == null) {
                    continue;
                }

                float width = face.getWidth();
                float height = face.getHeight();
                float x = (float) this.mImageWidth - (face.getPosition().x + width / 2);
                float y = (float) this.mImageHeight - (face.getPosition().y + height / 2);
                float faceAngleY = face.getEulerY();
                float faceAngleZ = face.getEulerZ();

                // portrait と upside down の時は mImageWidth と mImageHeight が逆になる
                if (rotation == Frame.ROTATION_270 || rotation == Frame.ROTATION_90) {
                    x = this.mImageHeight - (face.getPosition().x + width / 2);
                    y = this.mImageWidth - (face.getPosition().y + height / 2);
                }

                // ScreenAngle
                float screenAngle = 0;
                switch (rotation) {
                    case Frame.ROTATION_0:  // left
                        screenAngle = 0;
                        break;
                    case Frame.ROTATION_90:  // upside down
                        screenAngle = 90;
                        break;
                    case Frame.ROTATION_180:  // right
                        screenAngle = 180;
                        break;
                    case Frame.ROTATION_270:  // portrait
                        screenAngle = 270;
                        break;
                }

                // 座標変換後の生データ
                FaceInfo newFace = new FaceInfo(x, y, width, height, faceAngleY, faceAngleZ, screenAngle);
                oldFaces.append(key, newFace);

                // ローパスフィルタのための前回データ
                FaceInfo oldFace = mOldFaces == null ? null : mOldFaces.get(key);
                FaceInfo oldFilteredFace = mFaces == null ? null : mFaces.get(key);

                // ローパスフィルタの適用
                FaceInfo newFilteredFace = lowPassFilter(oldFace, oldFilteredFace, newFace);
                newFilteredFaces.append(key, newFilteredFace);
            }

            mFaces = newFilteredFaces;
            mOldFaces = oldFaces;

            // 1秒あたりの検出回数を出力(デバッグ用)
            if (detectTime == 0) {
                detectTime = System.currentTimeMillis();
            } else {
                detectCount++;
                long interval = System.currentTimeMillis() - detectTime;
                if (interval > 1000) {
                    detectTime = System.currentTimeMillis();
                    detectCount = 0;
                }
            }
        }
    }

    /**
     * ローパスフィルタ。
     * 位置座標などの細かな振動を滑らかにするために使う。
     * <p>
     * ローパスフィルタの式
     * <pre>
     * S[t] = ALPHA * Y[t-1} + (1 - ALPHA) * S[t-1]
     * Y: 生データ
     * S: フィルタ適用後のデータ
     * </pre>
     *
     * @param oldFace ローパスフィルタを適用した前回の情報
     * @param newFace 最新の情報
     * @return ローパスフィルタ適用後の情報
     */
    private FaceInfo lowPassFilter(FaceInfo oldFace, FaceInfo oldFilterdFace, FaceInfo newFace) {

        // oldがなければローパスフィルターを適用しない
        if (oldFace == null || oldFilterdFace == null) {
            return newFace;
        }

        // スクリーンの向きが変更されていたらローパスフィルターを適用しない
        if (oldFace.getScreenAngle() != newFace.getScreenAngle()) {
            return newFace;
        }

        // X, Y の移動量が閾値以上の場合も適用しない
        if (Math.abs((oldFace.getX() - newFace.getX())) > CONTINUOUS_LIMIT
            || Math.abs((oldFace.getY() - newFace.getY())) > CONTINUOUS_LIMIT) {
            return newFace;
        }

        // ローパスフィルターを適用する
        float x = _lowPassFilter(oldFace.getX(), oldFilterdFace.getX());
        float y = _lowPassFilter(oldFace.getY(), oldFilterdFace.getY());
        float width = _lowPassFilter(oldFace.getWidth(), oldFilterdFace.getWidth());
        float height = _lowPassFilter(oldFace.getHeight(), oldFilterdFace.getHeight());
        float faceAngleY = _lowPassFilter(oldFace.getFaceAngleY(), oldFilterdFace.getFaceAngleY());
        float faceAngleZ = _lowPassFilter(oldFace.getFaceAngleZ(), oldFilterdFace.getFaceAngleZ());

        return new FaceInfo(x, y, width, height, faceAngleY, faceAngleZ, newFace.getScreenAngle());
    }

    private float _lowPassFilter(float oldValue, float oldFilteredValue) {
        return LOW_PASS_FILTER_ALPHA * oldValue + (1.0f - LOW_PASS_FILTER_ALPHA) * oldFilteredValue;
    }

    private boolean existFace(SparseArray<?> faces) {
        return faces != null && faces.size() > 0;
    }

    private int getDeviceOrientation() {
        WindowManager wm = (WindowManager) this.applicationContext.getSystemService("window");
        return wm.getDefaultDisplay().getRotation();
    }
}
