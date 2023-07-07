package dev.aoutnheub;

import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ResultWidget extends WGridPanel {
    public WLabel label;
    public WButton deleteButton;
    public WButton sayButton;
    public Runnable onDelete;

    @SuppressWarnings("resource")
    public ResultWidget() {
        grid = 20;
        setGaps(4, 4);

        deleteButton = new WButton(Text.literal("-"));
        deleteButton.setOnClick(() -> {
            onDelete.run();
        });
        add(deleteButton, 0, 0, 1, 1);
        label = new WLabel(Text.empty());
        label.setHorizontalAlignment(HorizontalAlignment.CENTER);
        label.setVerticalAlignment(VerticalAlignment.CENTER);
        add(label, 1, 0, 9, 1);
        sayButton = new WButton(Text.literal("Say"));
        sayButton.setOnClick(() -> {
            var msg = label.getText();
            MinecraftClient.getInstance().player.sendChatMessage(msg.getString(), msg);
        });
        add(sayButton, 9, 0, 2, 1);
    }
}
