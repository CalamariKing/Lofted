package com.loftedstudios.loftedmod.entities.client;

import com.loftedstudios.loftedmod.entities.AirShipEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.Collections;

public class AirShipEntityRenderer extends EntityRenderer<AirShipEntity> implements IGeoRenderer<AirShipEntity> {
    private final AnimatedGeoModel<AirShipEntity> modelProvider;
    public AirShipEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.modelProvider = new AirShipEntityModel();
    }

    @Override
    public void render(AirShipEntity entityIn, float entity, float yaw, MatrixStack tickDelta, VertexConsumerProvider matrices, int vertexConsumers) {

        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entityIn));
        tickDelta.push();
        tickDelta.multiply(Vec3f.POSITIVE_Y
                .getDegreesQuaternion(-MathHelper.lerp(yaw, entityIn.prevYaw, entityIn.getYaw())));
        tickDelta.translate(0, 0, 0);
        tickDelta.scale(1,1,1);

        MinecraftClient.getInstance().getTextureManager().bindTexture(getTexture(entityIn));
        Color renderColor = getRenderColor(entityIn, yaw, tickDelta, matrices, null, vertexConsumers);
        RenderLayer renderType = getRenderType(entityIn, yaw, tickDelta, matrices, null, vertexConsumers,
                getTexture(entityIn));
        render(model, entityIn, yaw, renderType, tickDelta, matrices, null, vertexConsumers,
                getPackedOverlay(entityIn, 0), (float) renderColor.getRed() / 255f,
                (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f,
                (float) renderColor.getAlpha() / 255);
        float lastLimbDistance = 0.0F;
        float limbSwing = 0.0F;
        EntityModelData entityModelData = new EntityModelData();
        AnimationEvent<AirShipEntity> predicate = new AnimationEvent<>(entityIn, limbSwing, lastLimbDistance, yaw,
                false, Collections.singletonList(entityModelData));
        ((IAnimatableModel<AirShipEntity>) modelProvider).setLivingAnimations(entityIn, this.getUniqueID(entityIn), predicate);
        tickDelta.pop();
        super.render(entityIn, entity, yaw, tickDelta, matrices, vertexConsumers);
    }


    @Override
    public Identifier getTexture(AirShipEntity entity) {
        return getTextureLocation(entity);
    }

    @Override
    public GeoModelProvider getGeoModelProvider() {
        return this.modelProvider;
    }

    @Override
    public Identifier getTextureLocation(AirShipEntity instance) {
        return this.modelProvider.getTextureLocation(instance);
    }

    public static int getPackedOverlay(Entity livingEntityIn, float uIn) {
        return OverlayTexture.getUv(OverlayTexture.getU(uIn), false);
    }
}
