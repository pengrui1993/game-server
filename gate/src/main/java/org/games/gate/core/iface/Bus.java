package org.games.gate.core.iface;

public interface Bus extends Node{
    @Override
    default Type type(){return Type.BUS;}
}
