package me.krusk.kruskafkteleportation;

import org.bukkit.Location;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class FixedLocationMetadataValue implements MetadataValue {
    public FixedLocationMetadataValue(Location previousLocation, Runnable runnable) {
        
    }

    @Override
    public Object value() {
        return null;
    }

    @Override
    public int asInt() {
        return 0;
    }   

    @Override
    public float asFloat() {
        return 0;
    }

    @Override
    public double asDouble() {
        return 0;
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public short asShort() {
        return 0;
    }

    @Override
    public byte asByte() {
        return 0;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public String asString() {
        return null;
    }

    @Override
    public Plugin getOwningPlugin() {
        return null;
    }

    @Override
    public void invalidate() {
        
    }
}
