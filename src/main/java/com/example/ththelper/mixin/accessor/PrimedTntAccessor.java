package com.example.tnthelper.mixin.accessor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PrimedTnt.class)
public interface PrimedTntAccessor {

    @Accessor("owner")
    void tntownermod$setOwner(LivingEntity owner);

    @Accessor("owner")
    LivingEntity tntownermod$getOwner();
}
