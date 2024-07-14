package org.wolf.util;

import org.wolf.*;
import org.wolf.role.Roles;
import org.wolf.role.Witch;

import java.util.function.Supplier;

public class ChangeStateUtil {
    public static void change(WolfKilling ctx, boolean firstTimes
            , Supplier<MajorPhaser> witch
            , Supplier<MajorPhaser> pre
            , Supplier<MajorPhaser> pro
            , Supplier<MajorPhaser> calc
    ){
        if(firstTimes){
            ctx.changeState(witch.get());
            return;
        }
        final Witch w = ctx.get(Roles.WITCH, Witch.class);
        boolean witchCanAction = w.alive()&&w.hasAnyMedicine();
        boolean predictorCanAction = ctx.get(Roles.PREDICTOR).alive();;
        boolean protectorCanAction = ctx.get(Roles.PROTECTOR).alive();
        if(predictorCanAction){
            ctx.changeState(pre.get());
        }else if(witchCanAction){
            ctx.changeState(witch.get());
        }else if(protectorCanAction){
            ctx.changeState(pro.get());
        }else{
            ctx.changeState(calc.get());
        }
    }
}
