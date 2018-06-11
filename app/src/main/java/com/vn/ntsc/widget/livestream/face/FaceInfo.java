package com.vn.ntsc.widget.livestream.face;

/**
 * 顔認識で検出した顔の位置情報などを格納するクラス
 * <p>
 * Created by kuroki on 2017/06/15.
 */
public class FaceInfo {

    private final float x;  // 顔の中心位置のX座標
    private final float y;  // 顔の中心位置のY座標
    private final float width;  // 顔の幅
    private final float height; // 顔の高さ
    private final float faceAngleY;  // 顔の傾き(Y座標を基準) 単位はdegree。傾いていない場合は0。
    private final float faceAngleZ;  // 顔の傾き(Z座標を基準) 単位はdegree。傾いていない場合は0。
    private final float screenAngle;    // 画面の検出向き 0:left 90:upside down 180:right 270:portrait

    public FaceInfo(float x, float y, float width, float height, float faceAngleY, float faceAngleZ, float screenAngle) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.faceAngleY = faceAngleY;
        this.faceAngleZ = faceAngleZ;
        this.screenAngle = screenAngle;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getFaceAngleY() {
        return faceAngleY;
    }

    public float getFaceAngleZ() {
        return faceAngleZ;
    }

    public float getScreenAngle() {
        return screenAngle;
    }
}
