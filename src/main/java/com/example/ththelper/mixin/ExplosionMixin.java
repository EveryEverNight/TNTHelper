package com.example.tnthelper.mixin;

import com.example.tnthelper.util.LootingHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow
    public abstract Entity getDirectSourceEntity();

    @ModifyReturnValue(method = "getDamageSource", at = @At("RETURN"))
    private DamageSource modifyDamageSource(DamageSource original) {
        Entity directSource = this.getDirectSourceEntity();

        if (!(directSource instanceof PrimedTnt tnt)) {
            return original;
        }

        if (!(tnt.level() instanceof ServerLevel serverLevel)) {
            return original;
        }

        Entity owner = tnt.getOwner();
        if (!(owner instanceof ServerPlayer)) {
            return original;
        }

        ServerPlayer lootingHelper = LootingHelper.get(serverLevel);
        return serverLevel.damageSources().playerAttack(lootingHelper);
    }
}