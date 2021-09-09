package me.william278.huskbungeertp.randomtp.processor;

import org.bukkit.Location;
import org.bukkit.World;

public abstract class AbstractRtp {

    public abstract void initialize();

    public abstract Location getRandomLocation(World world, String targetBiomeString);

    public static final int MAX_RANDOM_ATTEMPTS = 32;

}