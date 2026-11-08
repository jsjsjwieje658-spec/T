package com.example.mobesp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MobEspClientMod implements ClientModInitializer {
    private static KeyBinding toggleEspKey;
    private static KeyBinding cycleFilterKey;
    private static KeyBinding toggleOutlineKey;
    private static KeyBinding toggleTracersKey;

    @Override
    public void onInitializeClient() {
        toggleEspKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mobesp.toggle_esp",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.mobesp.general"
        ));

        cycleFilterKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mobesp.cycle_filter",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.mobesp.general"
        ));

        toggleOutlineKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mobesp.toggle_outline",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                "category.mobesp.general"
        ));

        toggleTracersKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mobesp.toggle_tracers",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                "category.mobesp.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleEspKey.wasPressed()) {
                EspConfig.enabled = !EspConfig.enabled;
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§a[MobESP]§r Trạng thái: " + (EspConfig.enabled ? "§2BẬT" : "§cTẮT")),
                            true
                    );
                }
            }

            while (cycleFilterKey.wasPressed()) {
                EspConfig.filterMode = EspConfig.filterMode.next();
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§a[MobESP]§r Bộ lọc: §e" + EspConfig.filterMode.getDisplayName()),
                            true
                    );
                }
            }

            while (toggleOutlineKey.wasPressed()) {
                EspConfig.shaderOutline = !EspConfig.shaderOutline;
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§a[MobESP]§r Viền Xuyên Tường (Outline): " + (EspConfig.shaderOutline ? "§2BẬT" : "§cTẮT")),
                            true
                    );
                }
            }

            while (toggleTracersKey.wasPressed()) {
                EspConfig.tracers = !EspConfig.tracers;
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§a[MobESP]§r Đường chỉ hướng (Tracer): " + (EspConfig.tracers ? "§2BẬT" : "§cTẮT")),
                            true
                    );
                }
            }
        });

        WorldRenderEvents.AFTER_ENTITIES.register(EntityEspRenderer::renderWorld);
        HudRenderCallback.EVENT.register(EntityEspRenderer::renderHud);
    }
}
