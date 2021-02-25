package org.dimdev.dimdoors.client;

import java.util.ArrayList;

import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import org.dimdev.dimdoors.entity.RiftEntity;

public class RiftRenderer extends EntityRenderer<RiftEntity> {
    public RiftRenderer(EntityRenderDispatcher ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(RiftEntity entity) {
        return null;
    }

    @Override
    public void render(RiftEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        VertexConsumer vc = vertexConsumers.getBuffer(TexturedRenderLayers.getEntitySolid());
        ArrayList<RiftCurves.Point> points = RiftCurves.CURVES.get(0).points;

        for (RiftCurves.Point point : points) {
            vc.vertex(point.x, point.y, 0).color(0, 0, 0, 0).next();
        }
    }
}
