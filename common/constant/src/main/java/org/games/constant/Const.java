package org.games.constant;

public interface Const extends RoleTypeConst,EventConst,CmdConst{

    int CLIENT_MAGIC = ('G'<<(32-(1*8)))|('A'<<(32-(2*8)))|('M'<<(32-(3*8)))|('E'<<(32-(4*8)));
    int NODE_MAGIC = ('N'<<(32-(1*8)))|('O'<<(32-(2*8)))|('D'<<(32-(3*8)))|('E'<<(32-(4*8)));

}
