package name.uwu.feytox.feytweaks.mixin;

import name.uwu.feytox.feytweaks.client.FTConfig;
import name.uwu.feytox.feytweaks.mixin.accessors.TextRendererAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.util.DyeColor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TextRenderer.class)
public class TextRendererMixin {

    @Inject(method = "drawWithOutline", at = @At("HEAD"), cancellable = true)
    public void onDrawWithOutline(OrderedText text, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if ((FTConfig.simpleGlow || FTConfig.fastGlowToShadow) && FTConfig.toggleMod) {
            if (color == DyeColor.BLACK.getSignColor()) {
                color = outlineColor;
            }

            fDrawInternal(text, x, y, color, FTConfig.fastGlowToShadow, matrix, vertexConsumers, false,
                    0, light);
            ci.cancel();
        }
    }

    private void fDrawInternal(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int backgroundColor, int light) {
        color = (color & -67108864) == 0 ? color | -16777216 : color;
        Matrix4f matrix4f = new Matrix4f(matrix);
        TextRenderer textRenderer = ((TextRenderer)(Object) this);
        if (shadow) {
            ((TextRendererAccessor) textRenderer).callDrawLayer(text, x, y, color, true, matrix, vertexConsumerProvider, seeThrough, backgroundColor, light);
            matrix4f.translate(new Vector3f(0.0F, 0.0F, 0.03F));
        }
        ((TextRendererAccessor) textRenderer).callDrawLayer(text, x, y, color, false, matrix, vertexConsumerProvider, seeThrough, backgroundColor, light);
    }


    @ModifyVariable(method = "drawWithOutline", at = @At("STORE"), ordinal = 5)
    private int getOutlineRenderJ(int j) {
        return FTConfig.glowToShadow ? 1 : j;
    }

    @ModifyVariable(method = "drawWithOutline", at = @At("STORE"), ordinal = 6)
    private int getOutlineRenderK(int k) {
        return FTConfig.glowToShadow ? 1 : k;
    }

    @Inject(method = "drawWithOutline", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/text/OrderedText;accept(Lnet/minecraft/text/CharacterVisitor;)Z",
            ordinal = 1),
            cancellable = true)
    private void optimizeOutline(OrderedText text, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (FTConfig.optimizeGlow) {
            Matrix4f matrix4f = new Matrix4f(matrix);
            matrix4f.translate(new Vector3f(0.0F, 0.0F, 0.0051F));

            TextRenderer.Drawer drawer2 = ((TextRenderer) (Object) this).new Drawer(vertexConsumers, x, y,
                    TextRendererAccessor.callTweakTransparency(color), false, matrix4f,
                    false, light);
            text.accept(drawer2);
            drawer2.drawLayer(0, x);

            ci.cancel();
        }
    }
}
