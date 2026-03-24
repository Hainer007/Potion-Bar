package hainer.mod.potionbar.mixin;

import hainer.mod.potionbar.config.ModSettings;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class PotionBarMixin {

	@Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
	private void potionbar$hideVanillaOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (!ModSettings.get().showVanillaEffects()) {
			ci.cancel();
		}
	}
}