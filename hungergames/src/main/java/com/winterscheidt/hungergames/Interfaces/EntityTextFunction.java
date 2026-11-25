package com.winterscheidt.hungergames.Interfaces;

import java.io.Serializable;
import java.util.function.Function;

import com.winterscheidt.hungergames.Classes.Entities.Entity;

@FunctionalInterface
public interface EntityTextFunction extends Function<Entity, String>, Serializable {}