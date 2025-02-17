package ru.feytox.feytweaks.mixin;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.feytweaks.client.FTConfig;
import ru.feytox.feytweaks.client.FeytweaksClient;

import java.util.function.Function;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin {

    @Inject(method = "isGlowingText", at = @At("HEAD"), cancellable = true)
    public void onIsGlowingText(CallbackInfoReturnable<Boolean> cir) {
        if (FTConfig.toggleMod && FTConfig.hideGlow
                && ((SignBlockEntity)(Object) this).getPos().getSquaredDistance(MinecraftClient.getInstance().player.getPos()) >= Math.pow(FTConfig.hideGlowDistance, 2)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateSign", at = @At("HEAD"), cancellable = true)
    public void onUpdateSign(boolean filterText, Function<Text, OrderedText> textOrderingFunction, CallbackInfoReturnable<OrderedText[]> cir) {
        if (FeytweaksClient.shouldHasText(((SignBlockEntity)(Object) this))) {
            OrderedText[] emptyRows = new OrderedText[4];
            emptyRows[0] = OrderedText.EMPTY;
            emptyRows[1] = OrderedText.EMPTY;
            emptyRows[2] = OrderedText.EMPTY;
            emptyRows[3] = OrderedText.EMPTY;

            cir.setReturnValue(emptyRows);
        }
    }
}
