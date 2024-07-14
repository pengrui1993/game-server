package org.wolf.util;

import java.util.Objects;

public class CalcContext {
    //in
    public String killingTargetUserId = null;//wolf
    public String protectedUserId = null;
    public String medicineSavedUserId = null;//witch
    public String drugKilledUserId = null;
    public boolean hunterAction;

    //out
    public String calcDiedUserId;

    public void clear(){
        killingTargetUserId = null;
        protectedUserId = null;
        medicineSavedUserId = null;
        drugKilledUserId = null;
        hunterAction = false;
    }
    public boolean isWitchInvalidOperation(){
        CalcContext cc = this;
        return Objects.nonNull(cc.drugKilledUserId)&&Objects.nonNull(cc.medicineSavedUserId);
    }
    public boolean isDoubleSaved(){
        CalcContext cc = this;
        return Objects.equals(cc.protectedUserId,cc.medicineSavedUserId)
                && Objects.nonNull(cc.protectedUserId);
    }
    public boolean isWolfEmptyKilling(){
        CalcContext cc = this;
        return Objects.isNull(cc.killingTargetUserId);
    }
}
