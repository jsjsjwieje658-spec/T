package com.example.mobesp;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class EntityEspRenderer {
    private static int lastHostileCount = 0;
    private static int lastPassiveCount = 0;
    private static int lastPlayerCount = 0;
    private static int lastOtherCount = 0;

    private EntityEspRenderer() {
    }

    public static void renderWorld(WorldRenderContext context) {
        if (!EspConfig.enabled) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) {
            return;
        }
        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider consumers = context.consumers();
        if (matrices == null || consumers == null) {
            return;
        }

        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();
        float tickDelta = context.tickCounter().getTickDelta(false);
        VertexConsumer lineConsumer = consumers.getBuffer(RenderLayer.getLines());

        // Điểm gốc đường chỉ hướng (Tracer) phía trước tầm nhìn camera
        Vec3d lookVec = Vec3d.fromPolar(camera.getPitch(), camera.getYaw()).multiply(0.5);

        int hostile = 0;
        int passive = 0;
        int players = 0;
        int other = 0;

        for (Entity entity : client.world.getEntities()) {
            if (!EspConfig.shouldRender(entity)) {
                continue;
            }
            EspConfig.EntityCategory category = EspConfig.classify(entity);
            switch (category) {
                case HOSTILE -> hostile++;
                case PASSIVE, WATER_AMBIENT -> passive++;
                case PLAYER -> players++;
                case NEUTRAL_OTHER -> other++;
            }

            double interpX = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - cameraPos.x;
            double interpY = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - cameraPos.y;
            double interpZ = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - cameraPos.z;

            Box box = entity.getBoundingBox()
                    .offset(-entity.getX(), -entity.getY(), -entity.getZ())
                    .offset(interpX, interpY, interpZ)
                    .expand(0.03);

            WorldRenderer.drawBox(
                    matrices,
                    lineConsumer,
                    box,
                    category.r,
                    category.g,
                    category.b,
                    0.90f
            );

            if (EspConfig.tracers) {
                drawTracerLine(
                        matrices,
                        lineConsumer,
                        (float) lookVec.x,
                        (float) lookVec.y,
                        (float) lookVec.z,
                        (float) interpX,
                        (float) (interpY + entity.getHeight() * 0.5),
                        (float) interpZ,
                        category.r,
                        category.g,
                        category.b,
                        0.65f
                );
            }
        }

        lastHostileCount = hostile;
        lastPassiveCount = passive;
        lastPlayerCount = players;
        lastOtherCount = other;
    }

    private static void drawTracerLine(
            MatrixStack matrices,
            VertexConsumer consumer,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float r, float g, float b, float a
    ) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f posMatrix = entry.getPositionMatrix();
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float len = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        if (len > 1.0e-4f) {
            dx /= len;
            dy /= len;
            dz /= len;
        } else {
            dx = 0.0f;
            dy = 1.0f;
            dz = 0.0f;
        }
        consumer.vertex(posMatrix, x1, y1, z1).color(r, g, b, a).normal(entry, dx, dy, dz);
        consumer.vertex(posMatrix, x2, y2, z2).color(r, g, b, a).normal(entry, dx, dy, dz);
    }

    public static void renderHud(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (!EspConfig.enabled) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) {
            return;
        }
        TextRenderer tr = client.textRenderer;
        String statusLine = String.format(
                "[MobESP 1.21] ON | Lọc: %s | Outline: %s | Tracer: %s",
                EspConfig.filterMode.getDisplayName(),
                EspConfig.shaderOutline ? "ON" : "OFF",
                EspConfig.tracers ? "ON" : "OFF"
        );
        String countLine = String.format(
                "Quái: %d | Thú: %d | Người chơi: %d | Khác: %d",
                lastHostileCount, lastPassiveCount, lastPlayerCount, lastOtherCount
        );
        drawContext.drawTextWithShadow(tr, Text.literal(statusLine), 6, 6, 0x55FF55);
        drawContext.drawTextWithShadow(tr, Text.literal(countLine), 6, 17, 0xFFFFFF);
    }
}
