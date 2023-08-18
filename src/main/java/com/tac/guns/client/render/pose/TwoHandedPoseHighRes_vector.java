package com.tac.guns.client.render.pose;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tac.guns.client.handler.ReloadHandler;
import com.tac.guns.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;


/**
 * Author: ClumsyAlien, codebase and design based off Mr.Crayfish's class concept
 */
public class TwoHandedPoseHighRes_vector extends TwoHandedPose {
	@Override
	public void renderFirstPersonArms(ClientPlayerEntity player, HandSide hand, ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, float partialTicks) {
		matrixStack.translate(0, 0, -1);
		matrixStack.rotate(Vector3f.YP.rotationDegrees(180F));
		
		matrixStack.push();
		
		float reloadProgress = ReloadHandler.get().getReloadProgress(partialTicks, stack);
		matrixStack.translate(reloadProgress * 1.25, -reloadProgress, -reloadProgress * 1.5);
		
		int side = hand.opposite() == HandSide.RIGHT ? 1 : -1;
		matrixStack.translate(6.875 * side * 0.0625, -1.10, -0.28);
		
		if (Minecraft.getInstance().player.getSkinType().equals("slim") && hand.opposite() == HandSide.LEFT) {
			matrixStack.translate(0.03125F * -side, 0, 0);
		}
		
		matrixStack.rotate(Vector3f.XP.rotationDegrees(80F));
		matrixStack.rotate(Vector3f.YP.rotationDegrees(15F * -side));
		matrixStack.rotate(Vector3f.ZP.rotationDegrees(15F * -side));
		matrixStack.rotate(Vector3f.XP.rotationDegrees(-35F));
		matrixStack.scale(1.0F, 1.0F, 1.0F);
		
		RenderUtil.renderFirstPersonArm(player, hand.opposite(), matrixStack, buffer, light);
		
		matrixStack.pop();
		
		double centerOffset = 2.5;
		if (Minecraft.getInstance().player.getSkinType().equals("slim")) {
			centerOffset += hand == HandSide.RIGHT ? 0.2 : 0.8;
		}
		centerOffset = hand == HandSide.RIGHT ? -centerOffset : centerOffset;
		matrixStack.translate(centerOffset * -0.0235, -0.565, -1.15);
		
		matrixStack.rotate(Vector3f.XP.rotationDegrees(80F));
		matrixStack.scale(1F, 1F, 1F);
		
		RenderUtil.renderFirstPersonArm(player, hand, matrixStack, buffer, light);
	}
}
