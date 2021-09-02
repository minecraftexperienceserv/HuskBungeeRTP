package me.william278.huskbungeertp.randomtp;

import me.william278.huskbungeertp.HuskBungeeRTP;
import me.william278.huskbungeertp.config.Group;
import org.bukkit.block.Biome;

public class RtpProfile {

    private final Group destinationGroup;
    private final Biome targetBiome;

    public RtpProfile() {
        destinationGroup = HuskBungeeRTP.getSettings().getDefaultRtpDestinationGroup();
        targetBiome = null;
    }

    public RtpProfile(Group group) {
        destinationGroup = group;
        targetBiome = null;
    }

    public RtpProfile(Group group, Biome biome) {
        destinationGroup = group;
        targetBiome = biome;
    }

    public Group getDestinationGroup() {
        return destinationGroup;
    }

    public Biome getTargetBiome() {
        return targetBiome;
    }
}
