package dev.aoutnheub;

import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

public class MainScreen extends CottonClientScreen {
    public MainScreen(GuiDescription description) {
        super(Text.empty(), description);
    }
}
