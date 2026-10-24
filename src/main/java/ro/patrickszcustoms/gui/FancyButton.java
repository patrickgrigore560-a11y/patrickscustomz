package ro.patrickszcustoms.gui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import ro.patrickszcustoms.animation.ScreenAnimator;

public class FancyButton extends ClickableWidget {
    public enum Mode { PLAIN, SIDEBAR, CONTENT }

   private final ButtonWidget.PressAction onPress;
    private Mode mode = Mode.PLAIN;
    private int introDelay = -1;
    private boolean selected;
    private boolean leftAlign;
    private int soundIndex = -1;
    private float clickPitch = 1.0f;
    private float hover;
    private long last;

    public FancyButton(int x, int y, int w, int h, Text message, ButtonWidget.PressAction onPress) { 
        super(x, y, w, h, message);
        this.onPress = onPress;
    }

    public FancyButton mode(Mode m) { this.mode = m; return this; }
    public FancyButton intro(int delayMs) { this.introDelay = delayMs; return this; }
    public FancyButton selected(boolean s) { this.selected = s; return this; }
    public FancyButton sound(int index) { this.soundIndex = index; return this; }
    public FancyButton pitch(float p) { this.clickPitch = p; return this; }
    public FancyButton leftAlign() { this.leftAlign = true; return this; }

    @Override
    public void onClick(net.minecraft.client.gui.Click click, boolean doubled) {
        if (soundIndex >= 0) {
            UiSounds.chime(MinecraftClient.getInstance().getSoundManager(), soundIndex);
        } else {
            UiSounds.click(MinecraftClient.getInstance().getSoundManager(), clickPitch);
        }
        onPress.onPress(this);
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        long now = Util.getMeasuringTimeMs();
        float dt = last == 0L ? 0f : (now - last) / 1000f;
        last = now;
        hover += ((isHovered() && active ? 1f : 0f) - hover) * Math.min(1f, dt * 16f);

        float t = 1f, offX = 0f;
        if (mode == Mode.SIDEBAR && introDelay >= 0) {
            t = ScreenAnimator.easeOut(MathHelper.clamp(
                (now - Ui.screenOpenMs - introDelay) / 320f, 0f, 1f));
            offX = (1f - t) * -26f;
        } else if (mode == Mode.CONTENT) {
            t = Ui.contentT;
            offX = (1f - t) * 28f * Ui.contentDir;
        }

        float outerAlpha = ScreenAnimator.getAlpha();
        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(offX, 0f);
        ScreenAnimator.setAlpha(outerAlpha * t);

        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        int acc = Ui.accent() | 0xFF000000;
        int bg = selected ? Ui.alpha(acc, 0.30f + 0.10f * hover)
            : Ui.mix(0x33101018, Ui.alpha(acc, 0.28f), hover);

        if (!active) bg = 0x22000000;
        Ui.rect(ctx, x, y, w, h, bg);

        if (selected) ctx.fill(x, y + 2, x + 2, y + h - 2, acc);
        if (hover > 0.01f) {
            ctx.fill(x + 2, y + h - 1,
                x + 2 + Math.round((w - 4) * hover), y + h, acc);
        }

        int textColor = !active ? 0xFF777777
            : selected ? 0xFFFFFFFF
            : Ui.mix(0xFFBFC2D6, 0xFFFFFFFF, hover);

        if (leftAlign) {
            ctx.drawTextWithShadow(MinecraftClient.getInstance().textRenderer,
                getMessage(), x + 6, y + (h - 8) / 2, textColor);
        } else {
            ctx.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer,
                getMessage(), x + w / 2 + (selected ? 1 : 0),
                y + (h - 8) / 2, textColor);
        }

        ctx.getMatrices().popMatrix();
        ScreenAnimator.setAlpha(outerAlpha);
    }

    @Override
    protected void appendClickableNarrations(
            net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {
        appendDefaultNarrations(builder);
    }
}
