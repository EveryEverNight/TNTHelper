package com.example.tnthelper.util;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class LootingHelper {

    private static final UUID HELPER_UUID = UUID.randomUUID();
    private static final GameProfile HELPER_PROFILE =
            new GameProfile(HELPER_UUID, "TNTHelper");

    private static final Map<ServerLevel, ServerPlayer> CACHE = new WeakHashMap<>();

    private LootingHelper() {}

    public static ServerPlayer get(ServerLevel level) {
        return CACHE.computeIfAbsent(level, lvl -> {
            ServerPlayer helper = FakePlayer.get(lvl, HELPER_PROFILE);
            ensureLootingSword(helper);
            return helper;
        });
    }

    private static void ensureLootingSword(ServerPlayer helper) {
        ItemStack mainHand = helper.getMainHandItem();
        int currentLevel = mainHand.isEmpty()
                ? 0
                : EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, mainHand);

        if (mainHand.isEmpty() || currentLevel < 3) {
            ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
            sword.enchant(Enchantments.MOB_LOOTING, 3);
            helper.setItemInHand(InteractionHand.MAIN_HAND, sword);
        }
    }
}
