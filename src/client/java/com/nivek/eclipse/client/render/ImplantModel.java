package com.nivek.eclipse.client.render;

import com.nivek.eclipse.NivekEclipse;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Pecas visuais dos implantes (caixas simples, provisorias). Cada peca e desenhada
 * sobre a parte do corpo correspondente do jogador (coordenadas relativas ao pivo dela).
 * Usa a textura do braco biomecanico (BioArmModel.TEXTURE) ate existir uma propria.
 */
public final class ImplantModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(NivekEclipse.MOD_ID, "implants"), "main");

    private ImplantModel() {}

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // cabeca: visor na frente do rosto
        root.addOrReplaceChild("visor",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.3F, -5.5F, -4.6F, 8.6F, 2.0F, 0.8F),
                PartPose.ZERO);
        // peitoral: placa frontal
        root.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(0, 12).addBox(-4.4F, 0.5F, -2.6F, 8.8F, 5.0F, 0.8F),
                PartPose.ZERO);
        // costas: modulo nas costas
        root.addOrReplaceChild("back",
                CubeListBuilder.create().texOffs(0, 12).addBox(-3.0F, 1.0F, 2.0F, 6.0F, 8.0F, 1.5F),
                PartPose.ZERO);
        // bracos: manga blindada do braco esquerdo (o direito ja tem o braco biomecanico)
        root.addOrReplaceChild("arm",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.4F, 3.5F, -2.4F, 4.8F, 6.0F, 4.8F),
                PartPose.ZERO);
        // pernas: caneleira (desenhada nas duas pernas)
        root.addOrReplaceChild("leg",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.4F, 6.0F, -2.4F, 4.8F, 5.0F, 4.8F),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 32);
    }
}
