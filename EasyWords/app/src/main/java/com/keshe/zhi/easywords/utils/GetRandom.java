package com.keshe.zhi.easywords.utils;

/**
 * Created by zhi on 2017/5/17 0017.
 */

public class GetRandom {
    public static int getRandNum(int min, int max) {
        int randNum = min + (int)(Math.random() * ((max - min) + 1));
        return randNum;
    }
}
