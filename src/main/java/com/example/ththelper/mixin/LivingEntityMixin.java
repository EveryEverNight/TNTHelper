package com.example.tnthelper.mixin;

import com.example.tnthelper.util.LootingHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyVariable(
            method = "dropAllDeathLoot",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private DamageSource modifyLootSource(DamageSource original) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!original.is(DamageTypeTags.IS_EXPLOSION)) {
            return original;
        }

        Entity source = original.getDirectEntity();
        if (!(source instanceof PrimedTnt tnt)) {
            return original;
        }
        if (!(tnt.getOwner() instanceof ServerPlayer)) {
            return original;
        }
        if (!(tnt.level() instanceof ServerLevel serverLevel)) {
            return original;
        }

        ServerPlayer lootingHelper = LootingHelper.get(serverLevel);
        ItemStack mainHand = lootingHelper.getMainHandItem();
        if (mainHand.isEmpty() ||
                EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, mainHand) < 3) {
            return original;
        }

        return serverLevel.damageSources().playerAttack(lootingHelper);
    }

    @WrapOperation(
            method = "dropFromLootTable",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V"
            )
    )
    private void wrapGetRandomItems(
            LootTable table,
            LootParams params,
            long seed,
            Consumer<ItemStack> consumer,
            Operation<Void> original
    ) {
        Consumer<ItemStack> wrapped = stack -> {
            consumer.accept(stack);
        };
        original.call(table, params, seed, wrapped);
    }
}