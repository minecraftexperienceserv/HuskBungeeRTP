package me.william278.huskbungeertp.randomtp;

import org.bukkit.Location;
import org.bukkit.World;

public abstract class AbstractRtp {

    public abstract void initialize();
    public abstract Location getRandomLocation(World world);
}
