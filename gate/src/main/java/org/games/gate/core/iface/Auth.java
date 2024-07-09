package org.games.gate.core.iface;


public interface Auth extends Node{
    @Override
    default Type type(){return Type.AUTH;}
}
