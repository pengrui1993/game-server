package org.games.logic.wolf.util;

import org.games.logic.wolf.core.Write;
import org.games.logic.wolf.core.Read;

import java.util.Objects;

public class CalcContext {
    //in
    public String killingTargetUserId = null;//wolf
    public String protectedUserId = null;
    public String medicineSavedUserId = null;//witch
    public String drugKilledUserId = null;

    //out
    public String calcDiedUserId;
    public String calcDiedUserIdByWitch;
    @Write
    public void clear(){
        killingTargetUserId = null;
        protectedUserId = null;
        medicineSavedUserId = null;
        drugKilledUserId = null;

        calcDiedUserId = null;
        calcDiedUserIdByWitch = null;
    }
    @Read
    public boolean isWitchInvalidOperation(){
        CalcContext cc = this;
        return Objects.nonNull(cc.drugKilledUserId)&&Objects.nonNull(cc.medicineSavedUserId);
    }
    @Read
    public boolean isDoubleSaved(){
        CalcContext cc = this;
        return !isWolfEmptyKilling()
                &&Objects.nonNull(cc.protectedUserId)
                &&Objects.equals(cc.protectedUserId,cc.killingTargetUserId)
                &&Objects.equals(cc.protectedUserId,cc.medicineSavedUserId)
                ;
    }
    @Read
    public boolean isWolfEmptyKilling(){
        CalcContext cc = this;
        return Objects.isNull(cc.killingTargetUserId);
    }

    @Read
    public boolean isTargetDied() {
        return Objects.nonNull(calcDiedUserId);
    }
    public boolean isProtectSaved() {
        return !isWolfEmptyKilling()
                &&Objects.nonNull(protectedUserId)
                &&Objects.equals(protectedUserId,killingTargetUserId)
                &&!Objects.equals(medicineSavedUserId,protectedUserId)
        ;
    }

    public boolean isWitchSaved() {
        return !isWolfEmptyKilling()
                &&Objects.nonNull(medicineSavedUserId)
                &&Objects.equals(medicineSavedUserId,killingTargetUserId)
                &&!Objects.equals(medicineSavedUserId,protectedUserId)
                ;
    }
    public boolean isWitchKilled() {
        return Objects.nonNull(drugKilledUserId);
    }

    public boolean isWolfWitchKillSame() {
        return isTargetDied()&&Objects.equals(calcDiedUserId,calcDiedUserIdByWitch);
    }
}
