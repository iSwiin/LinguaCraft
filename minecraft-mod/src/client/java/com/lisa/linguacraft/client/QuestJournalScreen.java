
package com.lisa.linguacraft.client;

import com.lisa.linguacraft.Quest;
import com.lisa.linguacraft.QuestManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class QuestJournalScreen extends Screen {

    private static final int SIDEBAR_WIDTH = 210;
    private static final int CARD_X = 10;
    private static final int CARD_Y = 38;
    private static final int CARD_WIDTH = 190;
    private static final int CARD_HEIGHT = 230;
    private static final int NODE_RADIUS = 18;

    private String selectedQuestId = "greet_villager";

    private double panX = 0;
    private double panY = 0;
    private double zoom = 1.0;

    private boolean dragging;
    private double lastMouseX;
    private double lastMouseY;

    private Button actionButton;

    public QuestJournalScreen() {
        super(Component.literal("LinguaCraft Skill Tree"));
    }

    @Override
    protected void init() {
        addRenderableWidget(
                Button.builder(Component.literal("Close"), b -> onClose())
                        .bounds(width - 86, 10, 74, 20)
                        .build()
        );

        // Fixed position inside the quest card.
        actionButton = Button.builder(
                Component.literal("Accept Quest"),
                b -> handleQuestAction()
        ).bounds(
                CARD_X + 8,
                CARD_Y + CARD_HEIGHT - 80,
                CARD_WIDTH - 16,
                20
        ).build();

        addRenderableWidget(actionButton);
        updateActionButton();
        clampPan();
    }

    private int treeCenterX() {
        return SIDEBAR_WIDTH + (width - SIDEBAR_WIDTH) / 2;
    }

    private int treeCenterY() {
        return height / 2;
    }

    private int nodeX(Quest quest) {
        return switch (quest.getId()) {
            case "greet_villager" -> -155;
            case "visit_shop" -> 0;
            case "order_apple" -> 155;
            default -> 0;
        };
    }

    private int nodeY(Quest quest) {
        return switch (quest.getId()) {
            case "greet_villager" -> 0;
            case "visit_shop" -> -100;
            case "order_apple" -> 35;
            default -> 150;
        };
    }

    private int screenX(Quest quest) {
        return (int) (
                treeCenterX() + panX + nodeX(quest) * zoom
        );
    }

    private int screenY(Quest quest) {
        return (int) (
                treeCenterY() + panY + nodeY(quest) * zoom
        );
    }

    private Quest findQuest(String id) {
        for (Quest quest : QuestManager.getAllQuests()) {
            if (quest.getId().equals(id)) {
                return quest;
            }
        }
        return null;
    }

    private boolean isUnlocked(Quest quest) {
        return switch (quest.getId()) {
            case "greet_villager" -> true;

            case "visit_shop" -> {
                Quest previous = findQuest("greet_villager");
                yield previous != null
                        && previous.getStatus() == Quest.Status.COMPLETED;
            }

            case "order_apple" -> {
                Quest previous = findQuest("visit_shop");
                yield previous != null
                        && previous.getStatus() == Quest.Status.COMPLETED;
            }

            default -> true;
        };
    }

    private void handleQuestAction() {
        Quest quest = findQuest(selectedQuestId);

        if (quest == null || !isUnlocked(quest)) {
            return;
        }

        if (quest.getStatus() == Quest.Status.AVAILABLE) {
            QuestManager.acceptQuest(quest.getId());
        } else if (quest.getStatus() == Quest.Status.ACTIVE) {
            // Temporary manual completion for testing.
            QuestManager.completeQuest(quest.getId());
        }

        updateActionButton();
    }

    private void updateActionButton() {
        if (actionButton == null) {
            return;
        }

        Quest quest = findQuest(selectedQuestId);

        if (quest == null) {
            actionButton.visible = false;
            return;
        }

        actionButton.visible = true;

        if (!isUnlocked(quest)) {
            actionButton.setMessage(Component.literal("Quest Locked"));
            actionButton.active = false;
        } else if (quest.getStatus() == Quest.Status.AVAILABLE) {
            actionButton.setMessage(Component.literal("Accept Quest"));
            actionButton.active = true;
        } else if (quest.getStatus() == Quest.Status.ACTIVE) {
            actionButton.setMessage(Component.literal("Complete Quest"));
            actionButton.active = true;
        } else {
            actionButton.setMessage(Component.literal("Completed"));
            actionButton.active = false;
        }
    }

    private int scaledSize(int size) {
        return Math.max(1, (int) Math.round(size * zoom));
    }

    private void drawSmallText(
            GuiGraphics graphics,
            String text,
            int x,
            int y,
            int color
    ) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(0.8F, 0.8F, 1.0F);
        graphics.drawString(font, text, 0, 0, color);
        graphics.pose().popPose();
    }

    private void drawSmallFormattedText(
            GuiGraphics graphics,
            FormattedCharSequence text,
            int x,
            int y,
            int color
    ) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(0.8F, 0.8F, 1.0F);
        graphics.drawString(font, text, 0, 0, color);
        graphics.pose().popPose();
    }

    private void drawScaledText(
            GuiGraphics graphics,
            String text,
            int x,
            int y,
            int color
    ) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale((float) zoom, (float) zoom, 1.0F);
        graphics.drawCenteredString(font, text, 0, 0, color);
        graphics.pose().popPose();
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        graphics.fill(0, 0, width, height, 0xFF101216);

        // Sidebar.
        graphics.fill(
                0, 0, SIDEBAR_WIDTH, height, 0xFF1A1D23
        );
        graphics.fill(
                SIDEBAR_WIDTH - 2, 0,
                SIDEBAR_WIDTH, height, 0xFF454B55
        );

        drawSmallText(
                graphics, "LINGUACRAFT", 12, 12, 0xFFFFD780
        );
        graphics.drawCenteredString(
                font, "SKILL TREE", treeCenterX(), 16, 0xFFFFD780
        );

        // Compact quest details panel.
        Quest selected = findQuest(selectedQuestId);

        if (selected != null) {
            int cardBottom = CARD_Y + CARD_HEIGHT;

            graphics.fill(
                    CARD_X, CARD_Y,
                    CARD_X + CARD_WIDTH, cardBottom,
                    0xFF20242B
            );

            graphics.fill(
                    CARD_X, CARD_Y,
                    CARD_X + CARD_WIDTH, CARD_Y + 2,
                    0xFFFFD780
            );

            drawSmallText(
                    graphics, "QUEST DETAILS",
                    CARD_X + 8, CARD_Y + 9, 0xFFB9C5D6
            );

            graphics.fill(
                    CARD_X + 8, CARD_Y + 27,
                    CARD_X + CARD_WIDTH - 8, CARD_Y + 28,
                    0xFF454B55
            );

            int y = CARD_Y + 35;

            List<FormattedCharSequence> titleLines = font.split(
                    Component.literal(selected.getTitle()),
                    CARD_WIDTH - 20
            );

            for (int i = 0; i < Math.min(titleLines.size(), 2); i++) {
                drawSmallFormattedText(
                        graphics, titleLines.get(i),
                        CARD_X + 8, y, 0xFFFFD780
                );
                y += 14;
            }

            boolean unlocked = isUnlocked(selected);

            String status = unlocked
                    ? selected.getStatus().toString()
                    : "LOCKED";

            int statusColor = !unlocked
                    ? 0xFFFF7777
                    : selected.getStatus() == Quest.Status.COMPLETED
                    ? 0xFF65E890
                    : selected.getStatus() == Quest.Status.ACTIVE
                    ? 0xFFFFD780
                    : 0xFFB9C5D6;

            drawSmallText(
                    graphics, status,
                    CARD_X + 8, y, statusColor
            );

            y += 19;

            drawSmallText(
                    graphics, "DESCRIPTION",
                    CARD_X + 8, y, 0xFFB9C5D6
            );

            y += 14;

            List<FormattedCharSequence> descriptionLines = font.split(
                    Component.literal(selected.getDescription()),
                    CARD_WIDTH - 20
            );

            for (int i = 0; i < Math.min(descriptionLines.size(), 3); i++) {
                drawSmallFormattedText(
                        graphics, descriptionLines.get(i),
                        CARD_X + 8, y, 0xFFFFFFFF
                );
                y += 13;
            }

            y += 3;

            drawSmallText(
                    graphics, "REWARD",
                    CARD_X + 8, y, 0xFFB9C5D6
            );

            y += 14;

            List<FormattedCharSequence> rewardLines = font.split(
                    Component.literal(selected.getReward()),
                    CARD_WIDTH - 20
            );

            if (!rewardLines.isEmpty()) {
                drawSmallFormattedText(
                        graphics, rewardLines.get(0),
                        CARD_X + 8, y, 0xFF65E890
                );
            }

            if (!unlocked) {
                drawSmallText(
                        graphics, "Finish previous quest",
                        CARD_X + 8, cardBottom - 47,
                        0xFFFF7777
                );
            }
        }

        drawSmallText(
                graphics, "Drag to move | Scroll to zoom",
                SIDEBAR_WIDTH + 10, height - 18, 0xFF9BA5B4
        );

        // Tree viewport.
        graphics.enableScissor(
                SIDEBAR_WIDTH, 42, width, height - 28
        );

        Quest first = findQuest("greet_villager");
        Quest second = findQuest("visit_shop");
        Quest third = findQuest("order_apple");

        if (first != null && second != null) {
            drawConnection(graphics, first, second);
        }

        if (second != null && third != null) {
            drawConnection(graphics, second, third);
        }

        for (Quest quest : QuestManager.getAllQuests()) {
            int x = screenX(quest);
            int y = screenY(quest);

            boolean unlocked = isUnlocked(quest);
            boolean isSelected = quest.getId().equals(selectedQuestId);

            int radius = scaledSize(
                    isSelected ? NODE_RADIUS + 3 : NODE_RADIUS
            );
            int border = scaledSize(3);

            int color;

            if (!unlocked) {
                color = 0xFF353941;
            } else {
                color = switch (quest.getStatus()) {
                    case AVAILABLE -> 0xFF737B89;
                    case ACTIVE -> 0xFFC89B38;
                    case COMPLETED -> 0xFF328C58;
                };
            }

            graphics.fill(
                    x - radius - border, y - radius - border,
                    x + radius + border, y + radius + border,
                    isSelected ? 0xFFFFD780 : 0xFF090B0F
            );

            graphics.fill(
                    x - radius, y - radius,
                    x + radius, y + radius, color
            );

            String symbol = !unlocked
                    ? "L"
                    : quest.getStatus() == Quest.Status.COMPLETED
                    ? "✓" : "✦";

            drawScaledText(
                    graphics, symbol, x,
                    y - scaledSize(4), 0xFFFFFFFF
            );

            drawScaledText(
                    graphics, quest.getTitle(), x,
                    y + radius + scaledSize(6),
                    unlocked ? 0xFFFFFFFF : 0xFF999999
            );

            if (!unlocked) {
                drawScaledText(
                        graphics, "LOCKED", x,
                        y + radius + scaledSize(17), 0xFFFF7777
                );
            }
        }

        graphics.disableScissor();

        // Render widgets last so the action and Close buttons stay visible.
        updateActionButton();
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawConnection(
            GuiGraphics graphics,
            Quest from,
            Quest to
    ) {
        int x1 = screenX(from);
        int y1 = screenY(from);
        int x2 = screenX(to);
        int y2 = screenY(to);

        int thickness = scaledSize(3);

        int color = !isUnlocked(to)
                ? 0xFF444953
                : to.getStatus() == Quest.Status.COMPLETED
                ? 0xFF328C58
                : 0xFF777F8C;

        int middleX = (x1 + x2) / 2;

        graphics.fill(
                Math.min(x1, middleX), y1 - thickness / 2,
                Math.max(x1, middleX) + 1, y1 + thickness / 2 + 1,
                color
        );

        graphics.fill(
                middleX - thickness / 2, Math.min(y1, y2),
                middleX + thickness / 2 + 1, Math.max(y1, y2) + 1,
                color
        );

        graphics.fill(
                Math.min(middleX, x2), y2 - thickness / 2,
                Math.max(middleX, x2) + 1, y2 + thickness / 2 + 1,
                color
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        boolean insideCard =
                mouseX >= CARD_X
                        && mouseX <= CARD_X + CARD_WIDTH
                        && mouseY >= CARD_Y
                        && mouseY <= CARD_Y + CARD_HEIGHT;

        if (insideCard || mouseY < 35) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (button == 0 && mouseX >= SIDEBAR_WIDTH) {
            for (Quest quest : QuestManager.getAllQuests()) {
                int x = screenX(quest);
                int y = screenY(quest);

                double dx = mouseX - x;
                double dy = mouseY - y;

                int hitRadius = Math.max(
                        12, scaledSize(NODE_RADIUS + 6)
                );

                if (dx * dx + dy * dy <= hitRadius * hitRadius) {
                    selectedQuestId = quest.getId();
                    updateActionButton();
                    break;
                }
            }

            dragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double dragX,
            double dragY
    ) {
        if (dragging && button == 0) {
            panX += mouseX - lastMouseX;
            panY += mouseY - lastMouseY;

            lastMouseX = mouseX;
            lastMouseY = mouseY;

            clampPan();
            return true;
        }

        return super.mouseDragged(
                mouseX, mouseY, button, dragX, dragY
        );
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button == 0) {
            dragging = false;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double scrollX,
            double scrollY
    ) {
        if (mouseX < SIDEBAR_WIDTH) {
            return super.mouseScrolled(
                    mouseX, mouseY, scrollX, scrollY
            );
        }

        double oldZoom = zoom;
        double newZoom = Math.max(
                0.4, Math.min(2.5, zoom + scrollY * 0.1)
        );

        double centerX = treeCenterX();
        double centerY = treeCenterY();

        panX = mouseX - centerX
                - (mouseX - centerX - panX) * newZoom / oldZoom;

        panY = mouseY - centerY
                - (mouseY - centerY - panY) * newZoom / oldZoom;

        zoom = newZoom;
        clampPan();

        return true;
    }

    private void clampPan() {
        double minX = SIDEBAR_WIDTH + NODE_RADIUS + 12;
        double maxX = width - NODE_RADIUS - 12;
        double minY = 48 + NODE_RADIUS;
        double maxY = height - 32 - NODE_RADIUS;

        double left = Double.POSITIVE_INFINITY;
        double right = Double.NEGATIVE_INFINITY;
        double top = Double.POSITIVE_INFINITY;
        double bottom = Double.NEGATIVE_INFINITY;

        for (Quest quest : QuestManager.getAllQuests()) {
            double x = treeCenterX() + panX + nodeX(quest) * zoom;
            double y = treeCenterY() + panY + nodeY(quest) * zoom;
            double radius = NODE_RADIUS * zoom;

            left = Math.min(left, x - radius);
            right = Math.max(right, x + radius);
            top = Math.min(top, y - radius);
            bottom = Math.max(bottom, y + radius);
        }

        if (left == Double.POSITIVE_INFINITY) {
            return;
        }

        if (right - left <= maxX - minX) {
            panX += Math.max(
                    minX - left,
                    Math.min(0, maxX - right)
            );
        } else if (left > minX) {
            panX += minX - left;
        } else if (right < maxX) {
            panX += maxX - right;
        }

        if (bottom - top <= maxY - minY) {
            panY += Math.max(
                    minY - top,
                    Math.min(0, maxY - bottom)
            );
        } else if (top > minY) {
            panY += minY - top;
        } else if (bottom < maxY) {
            panY += maxY - bottom;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
