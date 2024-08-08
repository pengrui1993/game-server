package org.games.logic.wolf.role;

public enum Roles {
    NONE(       RoleType.NIL,false, Team.NIL)
    ,WOLF(      RoleType.WLF,true ,Team.WLD)
    ,PREDICTOR( RoleType.HEO,true,Team.KIND)
    ,WITCH(     RoleType.HEO,true,Team.KIND)
    ,HUNTER(    RoleType.HEO,true,Team.KIND)
    ,PROTECTOR( RoleType.HEO,true,Team.KIND)
    ,FARMER(    RoleType.FRM,false,Team.KIND)
    ;
    public final RoleType type;
    public final Team team;
    public final boolean ability;
    Roles(RoleType type,boolean ability,Team team){
        this.type=type;
        this.ability=ability;
        this.team = team;
    }
}
