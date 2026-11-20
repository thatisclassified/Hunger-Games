package com.winterscheidt.hungergames.Classes.Game;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.winterscheidt.hungergames.Classes.Entities.Entity;

public class Game implements Serializable {
    private List<Location> locations = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();
}
