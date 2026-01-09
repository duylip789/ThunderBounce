package thunder.hack.injection;

import net.minecraft.block.AbstractBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlockState.class)
public class MixinAbstractBlockState {

    @Inject(method = "isOpaque", at = @At("HEAD"), cancellable = true)
    private void isOpaque(CallbackInfoReturnable<Boolean> cir) {
        // Xray removed – do nothing
    }
}
