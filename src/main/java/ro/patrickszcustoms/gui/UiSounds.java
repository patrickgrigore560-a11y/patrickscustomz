import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import ro.patrickszcustoms.config.ConfigManager;

public final class UiSounds {
private static final float[] PENTATONIC = {
1.000f, 1.122f, 1.260f, 1.498f, 1.682f, 2.000f
};
private static long lastTick;

private UiSounds() {}

private static boolean enabled() {
    return ConfigManager.get().uiSounds;
}

public static void click(SoundManager sm, float pitch) {
    if (!enabled()) return;
    sm.play(new PositionedSoundInstance(
        SoundEvents.UI_BUTTON_CLICK.value(),
        SoundCategory.MASTER,
        0.25f,
        pitch,
        net.minecraft.util.math.random.Random.create(),
        true,
        PositionedSoundInstance.AttenuationType.NONE,
        0.0, 0.0, 0.0,
        true
    ));
}

public static void chime(SoundManager sm, int index) {
    if (!enabled()) return;
    float pitch = PENTATONIC[Math.floorMod(index, PENTATONIC.length)];
    sm.play(new PositionedSoundInstance(
        SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(),
        SoundCategory.MASTER,
        0.35f,
        pitch,
        net.minecraft.util.math.random.Random.create(),
        true,
        PositionedSoundInstance.AttenuationType.NONE,
        0.0, 0.0, 0.0,
        true
    ));
}

public static void tick(double value01) {
    if (!enabled()) return;
    long now = System.currentTimeMillis();
    if (now - lastTick < 80L) return;
    lastTick = now;

    float pitch = (float) (
        0.7 + 0.9 * Math.max(0.0, Math.min(1.0, value01))
    );

    click(MinecraftClient.getInstance().getSoundManager(), pitch);
}

}
