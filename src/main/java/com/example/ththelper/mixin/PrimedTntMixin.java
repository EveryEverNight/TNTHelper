package com.example.tnthelper.mixin;

import com.example.tnthelper.mixin.accessor.PrimedTntAccessor;
import com.example.tnthelper.util.LootingHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin extends Entity {

    public PrimedTntMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/entity/LivingEntity;)V",
            at = @At("RETURN")
    )
    private void onConstructed(CallbackInfo ci) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        PrimedTnt self = (PrimedTnt) (Object) this;
        PrimedTntAccessor accessor = (PrimedTntAccessor) self;

        Entity existingOwner = accessor.tntownermod$getOwner();
        if (existingOwner instanceof ServerPlayer) {
            return;
        }

        ServerPlayer fakeOwner = LootingHelper.get(serverLevel);
        accessor.tntownermod$setOwner(fakeOwner);
    }
}