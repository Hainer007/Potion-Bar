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
	private static final Identifier BG_TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/gui/bg/potion_bg.png");
	private static final Identifier HUD_ID = Identifier.fromNamespaceAndPath(MODID, "potion_bar");

	public static class EffectBarData {
		public final Identifier icon;
		public final Identifier bar;
		public final int barWidth;
		public final int barHeight;

		public EffectBarData(Identifier icon, Identifier bar, int barWidth, int barHeight) {
			this.icon = icon;
			this.bar = bar;
			this.barWidth = barWidth;
			this.barHeight = barHeight;
		}
	}

	private static final Map<Holder<MobEffect>, EffectBarData> BAR_DATA = new HashMap<>();

	static {
		BAR_DATA.put(MobEffects.ABSORPTION, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/absorption_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/absorption_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.BAD_OMEN, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/bad_omen_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/bad_omen_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.BLINDNESS, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/blindness_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/blindness_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.CONDUIT_POWER, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/conduit_power_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/conduit_power_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.DARKNESS, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/darkness_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/darkness_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.DOLPHINS_GRACE, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/dolphins_grace_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/dolphins_grace_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.FIRE_RESISTANCE, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/fire_resistance_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/fire_resistance_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.GLOWING, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/glowing_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/glowing_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.HASTE, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/haste_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/haste_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.HEALTH_BOOST, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/health_boost_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/health_boost_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.HERO_OF_THE_VILLAGE, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/hero_of_the_village_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/hero_of_the_village_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.HUNGER, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/hunger_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/hunger_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.INFESTED, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/infested_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/infested_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.INSTANT_DAMAGE, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/instant_damage_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/instant_damage_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.INSTANT_HEALTH, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/instant_health_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/instant_health_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.INVISIBILITY, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/invisibility_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/invisibility_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.JUMP_BOOST, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/jump_boost_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/jump_boost_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.LEVITATION, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/levitation_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/levitation_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.LUCK, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/luck_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/luck_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.MINING_FATIGUE, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/mining_fatigue_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/mining_fatigue_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.NAUSEA, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/nausea_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/nausea_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.NIGHT_VISION, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/night_vision_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/night_vision_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.OOZING, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/oozing_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/oozing_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.POISON, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/poison_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/poison_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.RAID_OMEN, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/raid_omen_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/raid_omen_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.REGENERATION, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/regeneration_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/regeneration_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.RESISTANCE, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/resistance_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/resistance_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.SATURATION, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/saturation_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/saturation_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.SLOW_FALLING, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/slow_falling_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/slow_falling_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.SLOWNESS, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/slowness_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/slowness_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.SPEED, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/speed_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/speed_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.STRENGTH, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/strength_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/strength_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.TRIAL_OMEN, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/trial_omen_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/trial_omen_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.UNLUCK, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/unluck_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/unluck_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.WATER_BREATHING, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/water_breathing_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/water_breathing_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.WEAKNESS, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/weakness_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/weakness_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.WEAVING, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/weaving_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/weaving_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.WIND_CHARGED, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/wind_charged_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/wind_charged_bar.png"),
				39, 4
		));
		BAR_DATA.put(MobEffects.WITHER, new EffectBarData(
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/icon/wither_icon.png"),
				Identifier.fromNamespaceAndPath(MODID, "textures/gui/bar/wither_bar.png"),
				39, 4
		));
	}

	private static final int BAR_OFFSET_X = 19;
	private static final int BAR_OFFSET_Y = 14;
	private static final int BAR_SPACING = 15;

	private record EffectKey(Holder<MobEffect> effect, int amplifier) {}

	private final Map<EffectKey, Integer> maxDurations = new HashMap<>();

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

	private void onHudRender(GuiGraphicsExtractor context, DeltaTracker renderTickCounter) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;

		RenderPipeline pipeline = RenderPipelines.GUI_TEXTURED;

		List<MobEffectInstance> displayedEffects = new ArrayList<>();
		for (MobEffectInstance effect : mc.player.getActiveEffects()) {
			if (BAR_DATA.containsKey(effect.getEffect())) {
				displayedEffects.add(effect);
			}
		}

		displayedEffects.sort(Comparator.comparing(
				e -> e.getEffect().unwrapKey().map(k -> k.identifier().toString()).orElse("unknown")
		));

		Set<EffectKey> currentKeys = new HashSet<>();
		for (MobEffectInstance effect : displayedEffects) {
			EffectKey key = new EffectKey(effect.getEffect(), effect.getAmplifier());
			currentKeys.add(key);
			maxDurations.putIfAbsent(key, effect.getDuration());
			if (effect.getDuration() > maxDurations.get(key)) {
				maxDurations.put(key, effect.getDuration());
			}
		}
		maxDurations.keySet().removeIf(key -> !currentKeys.contains(key));

		int screenW = mc.getWindow().getGuiScaledWidth();

		int x = switch (ModSettings.get().position) {
			case LEFT -> 10;
			case RIGHT -> screenW - 64 - 10;
			default -> (screenW - 64) / 2;
		};

		int yStart = 10;

		for (int i = 0; i < displayedEffects.size(); ++i) {
			MobEffectInstance effect = displayedEffects.get(i);
			EffectKey key = new EffectKey(effect.getEffect(), effect.getAmplifier());
			int maxDuration = maxDurations.get(key);

			EffectBarData barData = BAR_DATA.get(effect.getEffect());
			int y = yStart + i * BAR_SPACING;

			context.blit(pipeline, BG_TEXTURE, x, y, 0, 0, 64, 32, 64, 32);
			context.blit(pipeline, barData.icon, x, y, 0, 0, 64, 32, 64, 32);

			float progress = maxDuration > 0
					? Math.max(0f, Math.min(1f, (float) effect.getDuration() / (float) maxDuration))
					: 0f;

			int barWidth = (int) (barData.barWidth * progress);

			if (barWidth > 0) {
				context.blit(
						pipeline,
						barData.bar,
						x + BAR_OFFSET_X,
						y + BAR_OFFSET_Y,
						0,
						0,
						barWidth,
						barData.barHeight,
						barData.barWidth,
						barData.barHeight
				);
			}
		}
	}
}