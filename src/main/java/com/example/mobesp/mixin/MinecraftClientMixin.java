package com.example.mobesp.mixin;

import com.example.mobesp.EspConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void mobesp$forceEntityOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (EspConfig.enabled && EspConfig.shaderOutline && EspConfig.shouldRender(entity)) {
            cir.setReturnValue(true);
        }
    }
}
