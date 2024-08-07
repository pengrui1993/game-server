package org.games.logic.wolf.util;

import java.lang.reflect.Field;

public class Setting {
    public boolean hunterAbilityWhenWolfKill = true;

    public float hunterActionTimeoutLimit = 10;
    public float predictorActionTimeoutLimit = 15;
    public float witchSavingActionTimeoutLimit = 5;
    public float witchKillingActionTimeoutLimit = 5;
    public float wolfActionTimeoutLimit = 5;
    public float handsUpTimeoutLimit = 5;
    public float talkingLimit = 30;
    public float orderingLimit = 10;
    public float votingLimit=6;
    public float lastWordsActionTimeoutLimit = 5;
    public float preparingActionTimeoutLimit = 15;
    public float protectorActionTimeoutLimit = 5;

    public float secondSpeechingTimeLimit = 15;
    public float firstSpeechingTimeLimit = 15;
    public float publishDiedInfoPhaserLimit = 5;
    Setting copy(){
        Setting tmp = new Setting();
        for (Field f : this.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                f.set(tmp,f.get(this));
            } catch (Throwable e) {
                e.printStackTrace(System.out);
            }
        }
        return tmp;
    }
    public void change(Object... params) {
    }

    public static void main(String[] args) {
        Setting setting = new Setting();
        Setting older = setting.copy();
        System.out.println(older.wolfActionTimeoutLimit);
    }
}
