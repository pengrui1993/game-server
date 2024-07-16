package org.wolf.util;

import java.lang.reflect.Field;

public class Setting {
    public boolean hunterAbilityWhenWolfKill = true;

    public float hunterActionTimeoutLimit = 10;
    public float predictorActionTimeoutLimit = 15;
    public float wolfActionTimeoutLimit = 5;
    public float handsUpTimeoutLimit = 5;
    public float talkingLimit = 30;
    public float orderingLimit = 10;
    public float votingLimit=6;
    Setting older;
    void copy(){
        Setting tmp = new Setting();
        for (Field f : this.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                f.set(tmp,f.get(this));
            } catch (Throwable e) {
                e.printStackTrace(System.out);
            }
        }
        older = tmp;
    }
    public void change(Object... params) {
    }

    public static void main(String[] args) {
        Setting setting = new Setting();
        setting.copy();
        System.out.println(setting.older.wolfActionTimeoutLimit);
    }
}
