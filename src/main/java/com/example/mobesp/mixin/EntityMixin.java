package com.example.mobesp.mixin;

import com.example.mobesp.EspConfig;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void mobesp$customCategoryOutlineColor(CallbackInfoReturnable<Integer> cir) {
        Entity self = (Entity) (Object) this;
        if (EspConfig.enabled && EspConfig.shaderOutline && EspConfig.shouldRender(self)) {
            cir.setReturnValue(EspConfig.classify(self).rgbHex);
        }
    }
}
