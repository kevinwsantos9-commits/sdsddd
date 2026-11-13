package com.nivek.eclipse.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nivek.eclipse.implant.ImplantSlot;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Desenha os implantes instalados (mascara em EclipseRenderState) sobre o corpo do jogador. */
public class ImplantLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
    private final ModelPart visor;
    private final ModelPart chest;
    private final ModelPart back;
    private final ModelPart arm;
    private final ModelPart leg;

    public ImplantLayer(RenderLayerParent<PlayerRenderState, PlayerModel> parent, EntityModelSet modelSet) {
        super(parent);
        ModelPart root = modelSet.bakeLayer(ImplantModel.LAYER);
        this.visor = root.getChild("visor");
        this.chest = root.getChild("chest");
        this.back = root.getChild("back");
        this.arm = root.getChild("arm");
        this.leg = root.getChild("leg");
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       PlayerRenderState state, float yRot, float xRot) {
        EclipseRenderState data = (EclipseRenderState) state;
        if (data.nivekeclipse$getMutation() < 1 || state.isInvisible) return;
        int mask = data.nivekeclipse$getImplants();
        if (mask == 0) return;

        PlayerModel model = getParentModel();
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(BioArmModel.TEXTURE));

        if ((mask & ImplantSlot.HEAD.bit()) != 0) draw(poseStack, vc, packedLight, model.head, visor);
        if ((mask & ImplantSlot.CHEST.bit()) != 0) draw(poseStack, vc, packedLight, model.body, chest);
        if ((mask & ImplantSlot.BACK.bit()) != 0) draw(poseStack, vc, packedLight, model.body, back);
        if ((mask & ImplantSlot.ARMS.bit()) != 0) draw(poseStack, vc, packedLight, model.leftArm, arm);
        if ((mask & ImplantSlot.LEGS.bit()) != 0) {
            draw(poseStack, vc, packedLight, model.leftLeg, leg);
            draw(poseStack, vc, packedLight, model.rightLeg, leg);
        }
    }

    private static void draw(PoseStack poseStack, VertexConsumer vc, int light, ModelPart bodyPart, ModelPart piece) {
        poseStack.pushPose();
        bodyPart.translateAndRotate(poseStack);
        piece.render(poseStack, vc, light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
