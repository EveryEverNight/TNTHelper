package com.example.tnthelper.mixin;

import com.example.tnthelper.mixin.accessor.PrimedTntAccessor;
import com.example.tnthelper.util.LootingHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onAddEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof PrimedTnt tnt)) return;

        PrimedTntAccessor accessor = (PrimedTntAccessor) tnt;
        if (accessor.tntownermod$getOwner() instanceof ServerPlayer) return;

        ServerLevel serverLevel = (ServerLevel) (Object) this;
        accessor.tntownermod$setOwner(LootingHelper.get(serverLevel));
    }
}