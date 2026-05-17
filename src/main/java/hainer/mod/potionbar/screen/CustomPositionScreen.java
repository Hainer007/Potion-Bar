package hainer.mod.potionbar.screen;

import hainer.mod.potionbar.PotionBar;
import hainer.mod.potionbar.config.ModSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class CustomPositionScreen extends Screen {

    private static final int PREVIEW_W = PotionBar.BG_W;
    private static final int PREVIEW_H = PotionBar.BG_H;

    private static final int PREVIEW_ROWS = 3;
    private static final int PREVIEW_ROW_SPACING = PREVIEW_H + 2;

    private static final int COL_OVERLAY = 0x88000000;
    private static final int COL_PREVIEW_BG = 0xDD1C1C2E;
    private static final int COL_PREVIEW_BORDER = 0xFF4E9AF1;
    private static final int COL_ICON_BG = 0xFF2A2A42;
    private static final int COL_BAR_TRACK = 0xFF3A3A52;
    private static final int COL_BAR_FILL = 0xFF6AB0F5;
    private static final int COL_TEXT = 0xFFFFFFFF;
    private static final int COL_TEXT_DIM = 0xFFAAAAAA;

    private int previewX;
    private int previewY;

    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public CustomPositionScreen() {
        super(Component.literal("PotionBar - Custom Position"));

        ModSettings cfg = ModSettings.get();
        this.previewX = cfg.customX;
        this.previewY = cfg.customY;
    }

    @Override
    protected void init() {
        int btnW = 80;
        int btnH = 20;
        int gap = 10;

        int bx = (this.width - (btnW * 2 + gap)) / 2;
        int by = this.height - btnH - 16;

        this.addRenderableWidget(
                Button.builder(Component.literal("Save"), b -> onSave())
                        .bounds(bx, by, btnW, btnH)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Cancel"), b -> onCancel())
                        .bounds(bx + btnW + gap, by, btnW, btnH)
                        .build()
        );
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {

    }


    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {

        g.fill(0, 0, width, height, COL_OVERLAY);

        drawPreview(g);

        String text = "Drag to move PotionBar";


        g.text(font, text,
                (width - font.width(text)) / 2,
                10,
                COL_TEXT,
                true);

        g.text(font,
                "X: " + previewX + " Y: " + previewY,
                8,
                height - 12,
                COL_TEXT_DIM,
                false);

        super.extractRenderState(g, mouseX, mouseY, delta);
    }

    private void drawPreview(GuiGraphicsExtractor g) {

        for (int i = 0; i < PREVIEW_ROWS; i++) {

            int x = previewX;
            int y = previewY + i * PREVIEW_ROW_SPACING;

            g.fill(x, y, x + PREVIEW_W, y + PREVIEW_H, COL_PREVIEW_BG);


            g.fill(x, y, x + PREVIEW_W, y + 1, COL_PREVIEW_BORDER);
            g.fill(x, y + PREVIEW_H - 1, x + PREVIEW_W, y + PREVIEW_H, COL_PREVIEW_BORDER);
            g.fill(x, y, x + 1, y + PREVIEW_H, COL_PREVIEW_BORDER);
            g.fill(x + PREVIEW_W - 1, y, x + PREVIEW_W, y + PREVIEW_H, COL_PREVIEW_BORDER);

            int icon = 14;
            g.fill(x + 2, y + 2, x + 2 + icon, y + 2 + icon, COL_ICON_BG);

            int trackX = x + 19;
            int trackY = y + 14;
            int trackW = 39;
            int trackH = 4;

            g.fill(trackX, trackY, trackX + trackW, trackY + trackH, COL_BAR_TRACK);

            float f = Math.max(0f, 1.0f - i * 0.30f);
            int fillW = Math.max(1, (int)(trackW * f));

            g.fill(trackX, trackY, trackX + fillW, trackY + trackH, COL_BAR_FILL);
        }
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {

        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        int totalH = PREVIEW_H + (PREVIEW_ROWS - 1) * PREVIEW_ROW_SPACING;

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT
                && mouseX >= previewX
                && mouseX < previewX + PREVIEW_W
                && mouseY >= previewY
                && mouseY < previewY + totalH) {

            dragging = true;
            dragOffsetX = (int) mouseX - previewX;
            dragOffsetY = (int) mouseY - previewY;
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }


    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) dragging = false;
        return super.mouseReleased(event);
    }


    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {

        if (dragging) {
            double mouseX = event.x();
            double mouseY = event.y();
            previewX = clamp((int) mouseX - dragOffsetX, 0, width - PREVIEW_W);
            previewY = clamp((int) mouseY - dragOffsetY, 0, height - PREVIEW_H);
            return true;
        }

        return super.mouseDragged(event, dragX, dragY);
    }


    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) { // або просто event.isEscape()
            onCancel();
            return true;
        }
        return super.keyPressed(event);
    }

    private void onSave() {
        ModSettings cfg = ModSettings.get();
        cfg.setCustomXY(previewX, previewY);
        cfg.setPosition(ModSettings.Position.CUSTOM);
        Minecraft.getInstance().setScreen(null);
    }

    private void onCancel() {
        Minecraft.getInstance().setScreen(null);
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
