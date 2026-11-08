package com.example.mobesp;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

public final class EspConfig {
    public enum FilterMode {
        ALL("Tất cả sinh vật"),
        HOSTILE_ONLY("Chỉ Quái thù địch (Hostile)"),
        PASSIVE_ONLY("Chỉ Thú hiền (Passive/Water)"),
        PLAYERS_ONLY("Chỉ Người chơi (Players)");

        private final String displayName;

        FilterMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public FilterMode next() {
            FilterMode[] values = values();
            return values[(this.ordinal() + 1) % values.length];
        }
    }

    public enum EntityCategory {
        HOSTILE(1.00f, 0.20f, 0.20f, 0xFF3333),
        PLAYER(1.00f, 0.85f, 0.10f, 0xFFD91A),
        PASSIVE(0.20f, 0.95f, 0.35f, 0x33F259),
        WATER_AMBIENT(0.20f, 0.80f, 1.00f, 0x33CCFF),
        NEUTRAL_OTHER(0.80f, 0.35f, 1.00f, 0xCC59FF);

        public final float r;
        public final float g;
        public final float b;
        public final int rgbHex;

        EntityCategory(float r, float g, float b, int rgbHex) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.rgbHex = rgbHex;
        }
    }

    public static boolean enabled = true;
    public static boolean shaderOutline = true;
    public static boolean tracers = true;
    public static double maxDistanceSq = 96.0 * 96.0;
    public static FilterMode filterMode = FilterMode.ALL;

    private EspConfig() {
    }

    public static EntityCategory classify(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return EntityCategory.PLAYER;
        }
        if (entity instanceof Monster) {
            return EntityCategory.HOSTILE;
        }
        if (entity instanceof PassiveEntity) {
            return EntityCategory.PASSIVE;
        }
        if (entity instanceof WaterCreatureEntity || entity instanceof AmbientEntity) {
            return EntityCategory.WATER_AMBIENT;
        }
        return EntityCategory.NEUTRAL_OTHER;
    }

    public static boolean shouldRender(Entity entity) {
        if (!enabled || entity == null || !entity.isAlive() || entity.isRemoved()) {
            return false;
        }
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || entity == client.player) {
            return false;
        }
        if (client.player.squaredDistanceTo(entity) > maxDistanceSq) {
            return false;
        }
        EntityCategory cat = classify(entity);
        return switch (filterMode) {
            case ALL -> true;
            case HOSTILE_ONLY -> cat == EntityCategory.HOSTILE;
            case PASSIVE_ONLY -> cat == EntityCategory.PASSIVE || cat == EntityCategory.WATER_AMBIENT;
            case PLAYERS_ONLY -> cat == EntityCategory.PLAYER;
        };
    }
}
