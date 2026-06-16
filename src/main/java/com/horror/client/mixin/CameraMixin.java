package com.horror.client.mixin;

import com.horror.client.module.ModuleManager;
import com.horror.client.module.modules.CameraDistance;
import com.horror.client.module.modules.Freecam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Freecam: overrides the camera position/rotation while active. */
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Invoker("setPos")
    abstract void horror$setPos(double x, double y, double z);

    @Invoker("setRotation")
    abstract void horror$setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    private void horror$freecam(BlockView area, Entity focusedEntity, boolean thirdPerson,
                                boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!Freecam.active) {
            return;
        }
        horror$setPos(Freecam.renderX(tickDelta), Freecam.renderY(tickDelta), Freecam.renderZ(tickDelta));
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            horror$setRotation(player.getYaw(), player.getPitch());
        }
    }

    @ModifyVariable(method = "clipToSpace", at = @At("HEAD"), argsOnly = true)
    private double horror$cameraDistance(double desiredCameraDistance) {
        if (ModuleManager.isCameraDistanceOn()) {
            CameraDistance cameraDistance = ModuleManager.get(CameraDistance.class);
            return cameraDistance.getDistance();
        }
        return desiredCameraDistance;
    }

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void horror$cameraNoClip(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
        if (ModuleManager.isCameraNoClipOn()) {
            cir.setReturnValue(desiredCameraDistance);
        }
    }
}
