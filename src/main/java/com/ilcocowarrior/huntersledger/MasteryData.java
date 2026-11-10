package com.ilcocowarrior.huntersledger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MasteryData {
    public static final String ROOT = "huntersledger";
    public static final int[] THRESHOLDS = {10, 50, 150, 400};
    public static final int[] BONUS_PERCENT = {5, 10, 20, 35};
    public static final String[] TIER_KEYS = {"novice", "adept", "expert", "master"};

    public static CompoundTag root(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(ROOT, Tag.TAG_COMPOUND)) {
            data.put(ROOT, new CompoundTag());
        }
        return data.getCompound(ROOT);
    }

    public static int get(Player player, String id) {
        return root(player).getInt(id);
    }

    public static int increment(Player player, String id) {
        CompoundTag root = root(player);
        int value = root.getInt(id) + 1;
        root.putInt(id, value);
        return value;
    }

    public static void reset(Player player) {
        player.getPersistentData().remove(ROOT);
    }

    public static int tier(int kills) {
        int result = -1;
        for (int i = 0; i < THRESHOLDS.length; i++) {
            if (kills >= THRESHOLDS[i]) {
                result = i;
            }
        }
        return result;
    }

    public static int percent(int kills) {
        int t = tier(kills);
        return t < 0 ? 0 : BONUS_PERCENT[t];
    }

    public static int total(Player player) {
        CompoundTag root = root(player);
        int sum = 0;
        for (String key : root.getAllKeys()) {
            sum += root.getInt(key);
        }
        return sum;
    }

    public static List<Map.Entry<String, Integer>> top(Player player, int limit) {
        CompoundTag root = root(player);
        List<Map.Entry<String, Integer>> list = new ArrayList<>();
        for (String key : root.getAllKeys()) {
            list.add(Map.entry(key, root.getInt(key)));
        }
        list.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        if (list.size() > limit) {
            return new ArrayList<>(list.subList(0, limit));
        }
        return list;
    }

    public static String idOf(Entity entity) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return key == null ? "unknown" : key.toString();
    }

    public static Component nameOf(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location != null && ForgeRegistries.ENTITY_TYPES.containsKey(location)) {
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(location);
            if (type != null) {
                return type.getDescription();
            }
        }
        return Component.literal(id);
    }

    public static Component tierName(int kills) {
        int t = tier(kills);
        if (t < 0) {
            return Component.translatable("tier.huntersledger.none");
        }
        return Component.translatable("tier.huntersledger." + TIER_KEYS[t]);
    }

    public static Component nextAt(int kills) {
        for (int threshold : THRESHOLDS) {
            if (kills < threshold) {
                return Component.literal(String.valueOf(threshold));
            }
        }
        return Component.translatable("tier.huntersledger.max");
    }

    private MasteryData() {
    }
          }
