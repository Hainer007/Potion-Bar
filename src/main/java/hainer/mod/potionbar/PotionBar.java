package hainer.mod.potionbar;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import hainer.mod.potionbar.config.ModSettings;
import hainer.mod.potionbar.config.PotionBarCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.*;

public class PotionBar implements ClientModInitializer {

	public static final String MODID = "potion-bar";

	public static final int BG_W = 64;
	public static final int BG_H = 32;

	private static final Identifier BG_TEXTURE =
			Identifier.fromNamespaceAndPath(MODID, "textures/gui/bg/potion_bg.png");
	private static final Identifier DEFAULT_BAR =
			Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/default_bar.png");
	private static final Identifier HUD_ID =
			Identifier.fromNamespaceAndPath(MODID, "potion_bar");





	public static class EffectBarData {
		public final Identifier icon;
		public final Identifier bar;
		public final int        barWidth;
		public final int        barHeight;
		public final boolean    isFallback;

		public EffectBarData(Identifier icon, Identifier bar, int barWidth, int barHeight) {
			this(icon, bar, barWidth, barHeight, false);
		}

		public EffectBarData(Identifier icon, Identifier bar,
							 int barWidth, int barHeight, boolean isFallback) {
			this.icon       = icon;
			this.bar        = bar;
			this.barWidth   = barWidth;
			this.barHeight  = barHeight;
			this.isFallback = isFallback;
		}
	}





	private static final RenderPipeline PIPELINE = RenderPipelines.GUI_TEXTURED;

	private static final Map<Holder<MobEffect>, EffectBarData> BAR_DATA = new HashMap<>();

	static {
		register(MobEffects.ABSORPTION,          "absorption");
		register(MobEffects.BAD_OMEN,            "bad_omen");
		register(MobEffects.BLINDNESS,           "blindness");
		register(MobEffects.CONDUIT_POWER,       "conduit_power");
		register(MobEffects.DARKNESS,            "darkness");
		register(MobEffects.DOLPHINS_GRACE,      "dolphins_grace");
		register(MobEffects.FIRE_RESISTANCE,     "fire_resistance");
		register(MobEffects.GLOWING,             "glowing");
		register(MobEffects.HASTE,               "haste");
		register(MobEffects.HEALTH_BOOST,        "health_boost");
		register(MobEffects.HERO_OF_THE_VILLAGE, "hero_of_the_village");
		register(MobEffects.HUNGER,              "hunger");
		register(MobEffects.INFESTED,            "infested");
		register(MobEffects.INSTANT_DAMAGE,      "instant_damage");
		register(MobEffects.INSTANT_HEALTH,      "instant_health");
		register(MobEffects.INVISIBILITY,        "invisibility");
		register(MobEffects.JUMP_BOOST,          "jump_boost");
		register(MobEffects.LEVITATION,          "levitation");
		register(MobEffects.LUCK,                "luck");
		register(MobEffects.MINING_FATIGUE,      "mining_fatigue");
		register(MobEffects.NAUSEA,              "nausea");
		register(MobEffects.NIGHT_VISION,        "night_vision");
		register(MobEffects.OOZING,              "oozing");
		register(MobEffects.POISON,              "poison");
		register(MobEffects.RAID_OMEN,           "raid_omen");
		register(MobEffects.REGENERATION,        "regeneration");
		register(MobEffects.RESISTANCE,          "resistance");
		register(MobEffects.SATURATION,          "saturation");
		register(MobEffects.SLOW_FALLING,        "slow_falling");
		register(MobEffects.SLOWNESS,            "slowness");
		register(MobEffects.SPEED,               "speed");
		register(MobEffects.STRENGTH,            "strength");
		register(MobEffects.TRIAL_OMEN,          "trial_omen");
		register(MobEffects.UNLUCK,              "unluck");
		register(MobEffects.WATER_BREATHING,     "water_breathing");
		register(MobEffects.WEAKNESS,            "weakness");
		register(MobEffects.WEAVING,             "weaving");
		register(MobEffects.WIND_CHARGED,        "wind_charged");
		register(MobEffects.WITHER,              "wither");
	}

	private static void register(Holder<MobEffect> effect, String name) {
		BAR_DATA.put(effect, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/" + name + "_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/"  + name + "_bar.png"),
				39, 4));
	}





	private static final int BAR_OFFSET_X = 19;
	private static final int BAR_OFFSET_Y = 14;

	private static final int TIMER_GAP_LR = 1;
	private static final int TIMER_GAP_AB = -10;

	private static final int SLOT_GAP       = -16;
	private static final int SLOT_GAP_TIMER = -10;

	private static final float VANILLA_ICON_SCALE = 0.50f;
	private static final int   VANILLA_ICON_SRC   = 18;





	private record EffectKey(Holder<MobEffect> effect, int amplifier) {}

	private final Map<EffectKey, Integer>               maxDurations  = new HashMap<>();
	private final Map<Holder<MobEffect>, EffectBarData> fallbackCache = new HashMap<>();





	@Override
	public void onInitializeClient() {
		ModSettings.get();
		PotionBarCommands.register();

		HudElementRegistry.attachElementBefore(
				VanillaHudElements.CHAT,
				HUD_ID,
				this::onHudRender
		);
	}





	private int slotHeight(Minecraft mc, ModSettings cfg) {
		if (!cfg.isTimerEnabled()) return BG_H + SLOT_GAP;
		return switch (cfg.timerPosition) {
			case ABOVE, BELOW -> BG_H + mc.font.lineHeight + TIMER_GAP_AB + SLOT_GAP_TIMER;
			default           -> BG_H + SLOT_GAP;
		};
	}

	private EffectBarData getBarData(MobEffectInstance eff) {
		ModSettings cfg = ModSettings.get();
		if (cfg.useCustomIcons()) {
			EffectBarData known = BAR_DATA.get(eff.getEffect());
			if (known != null) return known;
		}
		return fallbackCache.computeIfAbsent(eff.getEffect(), this::buildVanillaFallback);
	}

	private EffectBarData buildVanillaFallback(Holder<MobEffect> entry) {
		Identifier icon = entry.unwrapKey()
				.map(k -> {

					Identifier id = k.identifier();
					return Identifier.fromNamespaceAndPath(
							id.getNamespace(),
							"textures/mob_effect/" + id.getPath() + ".png"
					);
				})
				.orElse(BG_TEXTURE);
		return new EffectBarData(icon, DEFAULT_BAR, 39, 4, true);
	}


	private static String effectSortKey(MobEffectInstance e) {
		return e.getEffect().unwrapKey()
				.map(k -> k.identifier().toString())
				.orElse("unknown");
	}

	private static String formatDuration(int ticks) {
		int s = ticks / 20;
		return (s >= 60) ? String.format("%d:%02d", s / 60, s % 60) : s + "s";
	}

	private static int timerColour(int ticks) {
		if (ticks < 200) {
			Minecraft mc = Minecraft.getInstance();
			long gt = (mc.level != null) ? mc.level.getGameTime() : 0L;
			return (gt % 20 < 10) ? 0xFF_FF5555 : 0xFF_FFAA00;
		}
		return 0xFF_FFFFFF;
	}





	private void onHudRender(GuiGraphicsExtractor context, DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;

		ModSettings cfg = ModSettings.get();

		List<MobEffectInstance> effects = new ArrayList<>(mc.player.getActiveEffects());
		effects.sort(Comparator.comparing(PotionBar::effectSortKey));


		Set<EffectKey> currentKeys = new HashSet<>();
		for (MobEffectInstance eff : effects) {
			EffectKey key = new EffectKey(eff.getEffect(), eff.getAmplifier());
			currentKeys.add(key);
			maxDurations.putIfAbsent(key, eff.getDuration());
			if (eff.getDuration() > maxDurations.get(key) + 20)
				maxDurations.put(key, eff.getDuration());
		}
		maxDurations.keySet().removeIf(k -> !currentKeys.contains(k));

		int screenW = mc.getWindow().getGuiScaledWidth();

		int hudX = switch (cfg.position) {
			case LEFT   -> 10;
			case RIGHT  -> screenW - BG_W - 10;
			case CUSTOM -> cfg.customX;
			default     -> (screenW - BG_W) / 2;
		};
		int hudY = (cfg.position == ModSettings.Position.CUSTOM) ? cfg.customY : 10;

		int slot = slotHeight(mc, cfg);

		for (int i = 0; i < effects.size(); i++) {

			MobEffectInstance eff    = effects.get(i);
			EffectKey         key    = new EffectKey(eff.getEffect(), eff.getAmplifier());
			int               maxDur = maxDurations.getOrDefault(key, eff.getDuration());
			EffectBarData     bar    = getBarData(eff);

			int slotTop = hudY + i * slot;

			int textReserved = (cfg.isTimerEnabled()
					&& cfg.timerPosition == ModSettings.TimerPosition.ABOVE)
					? mc.font.lineHeight + TIMER_GAP_AB
					: 0;
			int bgY = slotTop + textReserved;


			context.blit(PIPELINE, BG_TEXTURE, hudX, bgY, 0, 0, BG_W, BG_H, BG_W, BG_H);


			drawIcon(context, cfg, bar, hudX, bgY);


			float progress = (maxDur > 0)
					? Math.max(0f, Math.min(1f, (float) eff.getDuration() / maxDur))
					: 0f;
			int barPx = (int) (bar.barWidth * progress);
			if (barPx > 0) {
				context.blit(PIPELINE, bar.bar,
						hudX + BAR_OFFSET_X, bgY + BAR_OFFSET_Y,
						0, 0, barPx, bar.barHeight,
						bar.barWidth, bar.barHeight);
			}


			if (cfg.isTimerEnabled() && eff.getDuration() != Integer.MAX_VALUE) {
				drawTimer(context, mc, eff.getDuration(), cfg.timerPosition, hudX, bgY);
			}
		}
	}





	private void drawIcon(GuiGraphicsExtractor context, ModSettings cfg,
						  EffectBarData bar, int x, int y) {
		if (!cfg.useCustomIcons() || bar.isFallback) {
			context.pose().pushMatrix();
			context.pose().translate(x + 4.5f, y + 11.5f);
			context.pose().scale(VANILLA_ICON_SCALE, VANILLA_ICON_SCALE);
			context.blit(PIPELINE, bar.icon, 0, 0, 0, 0,
					VANILLA_ICON_SRC, VANILLA_ICON_SRC,
					VANILLA_ICON_SRC, VANILLA_ICON_SRC);
			context.pose().popMatrix();
		} else {
			context.blit(PIPELINE, bar.icon, x, y, 0, 0, BG_W, BG_H, BG_W, BG_H);
		}
	}

	private void drawTimer(GuiGraphicsExtractor context, Minecraft mc,
						   int remainingTicks,
						   ModSettings.TimerPosition pos,
						   int bgX, int bgY) {
		String text   = formatDuration(remainingTicks);
		int    colour = timerColour(remainingTicks);
		int    tw     = mc.font.width(text);
		int    th     = mc.font.lineHeight;

		int drawX, drawY;
		switch (pos) {
			case RIGHT -> { drawX = bgX + BG_W + TIMER_GAP_LR;  drawY = bgY + (BG_H - th) / 2; }
			case LEFT  -> { drawX = bgX - tw - TIMER_GAP_LR;    drawY = bgY + (BG_H - th) / 2; }
			case BELOW -> { drawX = bgX + (BG_W - tw) / 2;      drawY = bgY + BG_H + TIMER_GAP_AB; }
			default    -> { drawX = bgX + (BG_W - tw) / 2;      drawY = bgY - th - TIMER_GAP_AB; }
		}


		context.text(mc.font, text, drawX, drawY, colour, true);
	}
}