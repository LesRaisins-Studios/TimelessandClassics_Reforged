package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tac.guns.Config;
import com.tac.guns.client.gunskin.GunSkin;
import com.tac.guns.client.gunskin.SkinManager;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.HK416A5AnimationController;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gun.SkinAnimationModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.item.GunItem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;

import static com.tac.guns.client.gunskin.ModelComponent.*;

/*
 * Because the revolver has a rotating chamber, we need to render it in a
 * different way than normal items. In this case we are overriding the model.
 */

/**
 * Author: Timeless Development, and associates.
 */
public class hk416_a5_animation extends SkinAnimationModel {

    public hk416_a5_animation() {
        extraOffset.put(MUZZLE_SILENCER, new Vector3d(0, 0, -0.0125));
    }

    @Override
    public void render(float v, ItemCameraTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, MatrixStack matrices, IRenderTypeBuffer renderBuffer, int light, int overlay) {
        HK416A5AnimationController controller = HK416A5AnimationController.getInstance();
        GunSkin skin = SkinManager.getSkin(stack);

        matrices.push();
        {
            controller.applySpecialModelTransform(getModelComponent(skin, BODY), HK416A5AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) != null) {
                RenderUtil.renderModel(getModelComponent(skin, SIGHT_FOLDED), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(getModelComponent(skin, SIGHT), stack, matrices, renderBuffer, light, overlay);
            }

            renderLaserDevice(stack, matrices, renderBuffer, light, overlay, skin);

            if (transformType.isFirstPerson() || Config.COMMON.gameplay.canSeeLaserThirdSight.get())
                renderLaser(stack, matrices, renderBuffer, light, overlay, skin);

            renderStock(stack, matrices, renderBuffer, light, overlay, skin);

            renderBarrelWithDefault(stack, matrices, renderBuffer, light, overlay, skin);

            renderGrip(stack, matrices, renderBuffer, light, overlay, skin);

            RenderUtil.renderModel(getModelComponent(skin, BODY), stack, matrices, renderBuffer, light, overlay);

            matrices.push();
            {
                if (transformType.isFirstPerson()) {
                    Gun gun = ((GunItem) stack.getItem()).getGun();
                    float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

                    if (Gun.hasAmmo(stack)) {
                        // Math provided by Bomb787 on GitHub and Curseforge!!!
                        matrices.translate(0, 0, 0.205f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                    } else if (!Gun.hasAmmo(stack)) {
                        matrices.translate(0, 0, 0.205f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                    }
                    matrices.translate(0, 0, 0.025F);
                }
                RenderUtil.renderModel(getModelComponent(skin, BOLT), stack, matrices, renderBuffer, light, overlay);
            }
            matrices.pop();
        }
        matrices.pop();
        /*if(Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.STANDARD_FLASHLIGHT.orElse(ItemStack.EMPTY.getItem()))
        {
            RenderUtil.renderModel(SpecialModels.AR_15_CQB_STANDARD_FLASHLIGHT.getModel(), stack, matrices, renderBuffer, light, overlay);
        }*/

        matrices.push();
        {
            controller.applySpecialModelTransform(getModelComponent(skin, BODY), HK416A5AnimationController.INDEX_MAGAZINE, transformType, matrices);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.pop();

        //if(controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_NORMAL)) {
        matrices.push();
        {
            if (transformType.isFirstPerson()) {
                controller.applySpecialModelTransform(getModelComponent(skin, BODY), HK416A5AnimationController.INDEX_EXTRA_MAGAZINE, transformType, matrices);
                renderMag(stack, matrices, renderBuffer, light, overlay, skin);
            }
        }
        matrices.pop();

        matrices.push();
        {
            if (transformType.isFirstPerson() && controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY).equals(controller.getPreviousAnimation())) {
                controller.applySpecialModelTransform(getModelComponent(skin, BODY), HK416A5AnimationController.INDEX_MAGAZINE, transformType, matrices);
                RenderUtil.renderModel(getModelComponent(skin, BULLET), stack, matrices, renderBuffer, light, overlay);
            } else if (transformType.isFirstPerson() && controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL).equals(controller.getPreviousAnimation())) {
                controller.applySpecialModelTransform(getModelComponent(skin, BODY), HK416A5AnimationController.INDEX_EXTRA_MAGAZINE, transformType, matrices);
                RenderUtil.renderModel(getModelComponent(skin, BULLET), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.pop();
        //}
        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}
