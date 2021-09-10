package me.william278.huskbungeertp.randomtp.processor;

import org.bukkit.Location;
import org.bukkit.World;

public abstract class AbstractRtp {

    public abstract void initialize();

    public abstract RandomResult getRandomLocation(World world, String targetBiomeString);

    public record RandomResult(Location location, boolean successful, int attemptsTaken) { }

}