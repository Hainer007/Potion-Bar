package hainer.mod.potionbar.mixin;

import hainer.mod.potionbar.config.ModSettings;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class PotionBarMixin {
	@Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
	private void potionbar$hideVanillaOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		// hide (default) -> cancel, show -> allow
		if (!ModSettings.get().showVanillaEffects()) {
			ci.cancel();
		}
	}
}