package org.wolf.util;

public class CalcContext {
    public String killingTargetUserId = null;
    public String protectedUserId = null;
    public String medicineSavedUserId = null;
    public String drugKilledUserId = null;
    public boolean hunterAction;
    public String calcDiedUserId;

    public void clear(){
        killingTargetUserId = null;
        protectedUserId = null;
        medicineSavedUserId = null;
        drugKilledUserId = null;
        hunterAction = false;
    }
}
