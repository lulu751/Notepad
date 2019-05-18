package com.example.pc.notepad;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
public class GetCutBitmapLocation {
    private float cutLeft = 0.0F;
    private float cutTop = 0.0F;
    private float cutRight = 0.0F;
    private float cutBottom = 0.0F;

    public GetCutBitmapLocation() {
    }

    public void init(float x, float y) {
        this.cutLeft = x;
        this.cutRight = x;
        this.cutTop = y;
        this.cutBottom = y;
    }

    public void setCutLeftAndRight(float x, float y) {
        this.cutLeft = x < this.cutLeft ? x : this.cutLeft;
        this.cutRight = x > this.cutRight ? x : this.cutRight;
        this.cutTop = y < this.cutTop ? y : this.cutTop;
        this.cutBottom = y > this.cutBottom ? y : this.cutBottom;
    }

    public float getCutLeft() {
        return this.cutLeft;
    }

    public float getCutTop() {
        return this.cutTop;
    }

    public float getCutRight() {
        return this.cutRight;
    }

    public float getCutBottom() {
        return this.cutBottom;
    }
}
