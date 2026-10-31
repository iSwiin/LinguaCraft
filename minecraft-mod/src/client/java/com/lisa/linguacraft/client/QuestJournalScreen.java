

package com.lisa.linguacraft.client;

import com.lisa.linguacraft.Quest;
import com.lisa.linguacraft.QuestManager;
import com.lisa.linguacraft.VillagerRole;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class QuestJournalScreen extends Screen {

    private static final int SIDEBAR_WIDTH = 235;
    private static final int NODE_RADIUS = 20;
    private static final int HUB_RADIUS = 42;

    private String selectedQuestId = "baker_greeting";

    private double panX = 0;
    private double panY = 0;
    private double zoom = 1.0;

    private boolean dragging;
    private double lastMouseX;
    private double lastMouseY;

    public QuestJournalScreen() {
        super(Component.literal("LinguaCraft Skill Tree"));
    }

    @Override
    protected void init() {

        addRenderableWidget(
                Button.builder(
                                Component.literal("Close"),
                                button -> onClose()
                        )
                        .bounds(width - 90, 12, 78, 24)
                        .build()
        );

        clampPan();
    }

    private int treeCenterX() {
        return SIDEBAR_WIDTH + (width - SIDEBAR_WIDTH) / 2;
    }

    private int treeCenterY() {
        return height / 2;
    }

    // Circular radial layout.
    // The three villager paths spread outward from the central hub.
    private int nodeX(Quest quest) {

        return switch (quest.getId()) {

            // BAKER: top branch
            case "baker_greeting",
                 "baker_food" -> 0;

            case "baker_order" -> -85;
            case "baker_ingredients" -> 85;

            // CAFE OWNER: bottom-right branch
            case "cafe_greeting" -> 100;
            case "cafe_drinks" -> 205;
            case "cafe_order",
                 "cafe_conversation" -> 335;

            // FARMER: bottom-left branch
            case "farmer_greeting" -> -100;
            case "farmer_vegetables" -> -205;
            case "farmer_numbers",
                 "farmer_seasons" -> -335;
            case "farmer_trade" -> -445;

            default -> 0;
        };
    }

    private int nodeY(Quest quest) {

        return switch (quest.getId()) {

            // BAKER
            case "baker_greeting" -> -115;
            case "baker_food" -> -230;
            case "baker_order" -> -340;
            case "baker_ingredients" -> -340;

            // CAFE OWNER
            case "cafe_greeting" -> 58;
            case "cafe_drinks" -> 118;
            case "cafe_order" -> 60;
            case "cafe_conversation" -> 180;

            // FARMER
            case "farmer_greeting" -> 58;
            case "farmer_vegetables" -> 118;
            case "farmer_numbers" -> 60;
            case "farmer_seasons" -> 180;
            case "farmer_trade" -> 60;

            default -> 0;
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

    private int hubX() {
        return (int) (treeCenterX() + panX);
    }

    private int hubY() {
        return (int) (treeCenterY() + panY);
    }

    private int scaledSize(int size) {
        return Math.max(1, (int) Math.round(size * zoom));
    }

    private Quest findQuest(String id) {
        return QuestManager.findQuest(id);
    }

    private boolean isUnlocked(Quest quest) {

        String prerequisiteId = quest.getPrerequisiteQuestId();

        if (prerequisiteId == null) {
            return true;
        }

        Quest prerequisite = findQuest(prerequisiteId);

        return prerequisite != null
                && prerequisite.getStatus() == Quest.Status.COMPLETED;
    }

    private int branchColor(Quest quest) {

        return switch (quest.getVillagerRole()) {
            case BAKER -> 0xFFE2AD54;
            case CAFE_OWNER -> 0xFF75A9D8;
            case FARMER -> 0xFF76B879;
        };
    }

    private int nodeColor(Quest quest) {

        if (!isUnlocked(quest)) {
            return 0xFF353941;
        }

        return switch (quest.getStatus()) {
            case AVAILABLE -> 0xFF737B89;
            case ACTIVE -> branchColor(quest);
            case COMPLETED -> 0xFF328C58;
        };
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
        graphics.pose().scale(0.85F, 0.85F, 1.0F);
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
        graphics.pose().scale(0.85F, 0.85F, 1.0F);
        graphics.drawString(font, text, 0, 0, color);
        graphics.pose().popPose();
    }

    private void drawCenteredScaledText(
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

    // Draw a filled circle using horizontal scanlines.
    private void drawCircle(
            GuiGraphics graphics,
            int centerX,
            int centerY,
            int radius,
            int color
    ) {

        for (int y = -radius; y <= radius; y++) {

            int halfWidth = (int) Math.sqrt(
                    radius * radius - y * y
            );

            graphics.fill(
                    centerX - halfWidth,
                    centerY + y,
                    centerX + halfWidth + 1,
                    centerY + y + 1,
                    color
            );
        }
    }

    // Draw a smooth-looking connection using small rectangles.
    private void drawLine(
            GuiGraphics graphics,
            int x1,
            int y1,
            int x2,
            int y2,
            int color
    ) {

        double distance = Math.sqrt(
                Math.pow(x2 - x1, 2)
                        + Math.pow(y2 - y1, 2)
        );

        int steps = Math.max(1, (int) distance);

        int thickness = scaledSize(3);

        for (int i = 0; i <= steps; i++) {

            double progress = (double) i / steps;

            int x = (int) (x1 + (x2 - x1) * progress);
            int y = (int) (y1 + (y2 - y1) * progress);

            graphics.fill(
                    x - thickness / 2,
                    y - thickness / 2,
                    x + thickness / 2 + 1,
                    y + thickness / 2 + 1,
                    color
            );
        }
    }

    private void drawConnection(
            GuiGraphics graphics,
            Quest parent,
            Quest child
    ) {

        int x1 = screenX(parent);
        int y1 = screenY(parent);

        int x2 = screenX(child);
        int y2 = screenY(child);

        int color = child.getStatus() == Quest.Status.COMPLETED
                ? 0xFF328C58
                : isUnlocked(child)
                ? branchColor(child)
                : 0xFF444953;

        drawLine(graphics, x1, y1, x2, y2, color);
    }

    private void drawHubConnection(
            GuiGraphics graphics,
            Quest quest
    ) {

        int centerX = hubX();
        int centerY = hubY();

        int x = screenX(quest);
        int y = screenY(quest);

        int color = quest.getStatus() == Quest.Status.COMPLETED
                ? 0xFF328C58
                : branchColor(quest);

        drawLine(graphics, centerX, centerY, x, y, color);
    }

    private void drawNodeLabel(
            GuiGraphics graphics,
            Quest quest,
            int x,
            int y
    ) {

        List<FormattedCharSequence> lines =
                font.split(
                        Component.literal(quest.getTitle()),
                        125
                );

        int labelY = y + scaledSize(NODE_RADIUS + 8);

        int color = isUnlocked(quest)
                ? 0xFFFFFFFF
                : 0xFF999999;

        for (int i = 0; i < Math.min(lines.size(), 2); i++) {

            graphics.pose().pushPose();
            graphics.pose().translate(x, labelY + i * 11, 0);
            graphics.pose().scale((float) zoom, (float) zoom, 1.0F);

            graphics.drawCenteredString(
                    font,
                    lines.get(i),
                    0,
                    0,
                    color
            );

            graphics.pose().popPose();
        }
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
                0, 0,
                SIDEBAR_WIDTH, height,
                0xFF1A1D23
        );

        graphics.fill(
                SIDEBAR_WIDTH - 2, 0,
                SIDEBAR_WIDTH, height,
                0xFF454B55
        );

        drawSmallText(
                graphics,
                "LINGUACRAFT",
                14, 15,
                0xFFFFD780
        );

        drawSmallText(
                graphics,
                "SKILL DETAILS",
                14, 43,
                0xFFB9C5D6
        );

        // Selected skill information.
        Quest selected = findQuest(selectedQuestId);

        if (selected != null) {

            int cardX = 12;
            int cardY = 72;
            int cardWidth = SIDEBAR_WIDTH - 24;

            graphics.fill(
                    cardX, cardY,
                    cardX + cardWidth, height - 24,
                    0xFF20242B
            );

            graphics.fill(
                    cardX, cardY,
                    cardX + cardWidth, cardY + 3,
                    branchColor(selected)
            );

            int y = cardY + 16;

            drawSmallText(
                    graphics,
                    selected.getVillagerRole().getDisplayName(),
                    cardX + 9,
                    y,
                    branchColor(selected)
            );

            y += 25;

            List<FormattedCharSequence> titleLines =
                    font.split(
                            Component.literal(selected.getTitle()),
                            cardWidth - 20
                    );

            for (int i = 0; i < Math.min(titleLines.size(), 3); i++) {

                drawSmallFormattedText(
                        graphics,
                        titleLines.get(i),
                        cardX + 9,
                        y,
                        0xFFFFD780
                );

                y += 15;
            }

            y += 8;

            boolean unlocked = isUnlocked(selected);

            String status = !unlocked
                    ? "LOCKED"
                    : selected.getStatus().toString();

            int statusColor = !unlocked
                    ? 0xFFFF7777
                    : selected.getStatus() == Quest.Status.COMPLETED
                    ? 0xFF65E890
                    : selected.getStatus() == Quest.Status.ACTIVE
                    ? 0xFFFFD780
                    : 0xFFB9C5D6;

            drawSmallText(
                    graphics,
                    status,
                    cardX + 9,
                    y,
                    statusColor
            );

            y += 25;

            drawSmallText(
                    graphics,
                    "DESCRIPTION",
                    cardX + 9,
                    y,
                    0xFFB9C5D6
            );

            y += 17;

            List<FormattedCharSequence> descriptionLines =
                    font.split(
                            Component.literal(selected.getDescription()),
                            cardWidth - 20
                    );

            for (int i = 0;
                 i < Math.min(descriptionLines.size(), 5);
                 i++) {

                drawSmallFormattedText(
                        graphics,
                        descriptionLines.get(i),
                        cardX + 9,
                        y,
                        0xFFFFFFFF
                );

                y += 14;
            }

            y += 10;

            drawSmallText(
                    graphics,
                    "OBJECTIVES",
                    cardX + 9,
                    y,
                    0xFFB9C5D6
            );

            y += 17;

            List<String> objectives = selected.getObjectives();
            int completedCount = selected.getCompletedObjectiveCount();
            int totalCount = selected.getTotalObjectiveCount();

            // Show objective completion count.
            drawSmallText(
                    graphics,
                    completedCount + " / " + totalCount + " completed",
                    cardX + 9,
                    y,
                    completedCount == totalCount && totalCount > 0
                            ? 0xFF65E890
                            : 0xFFB9C5D6
            );

            y += 15;

            // Objective progress bar.
            int barX = cardX + 9;
            int barWidth = cardWidth - 18;
            int barHeight = 5;

            graphics.fill(
                    barX,
                    y,
                    barX + barWidth,
                    y + barHeight,
                    0xFF3A414B
            );

            if (totalCount > 0) {
                int filledWidth = barWidth * completedCount / totalCount;

                if (filledWidth > 0) {
                    graphics.fill(
                            barX,
                            y,
                            barX + filledWidth,
                            y + barHeight,
                            0xFF65E890
                    );
                }
            }

            y += 14;

            // Draw each objective with its completion status.
            for (int objectiveIndex = 0;
                 objectiveIndex < objectives.size();
                 objectiveIndex++) {

                boolean completed =
                        selected.isObjectiveCompleted(objectiveIndex);

                String marker = completed ? "✓ " : "○ ";
                String objective = objectives.get(objectiveIndex);

                List<FormattedCharSequence> objectiveLines =
                        font.split(
                                Component.literal(marker + objective),
                                cardWidth - 22
                        );

                int objectiveColor = completed
                        ? 0xFF65E890
                        : 0xFFFFFFFF;

                for (int i = 0;
                     i < Math.min(objectiveLines.size(), 3);
                     i++) {

                    drawSmallFormattedText(
                            graphics,
                            objectiveLines.get(i),
                            cardX + 9,
                            y,
                            objectiveColor
                    );

                    y += 13;
                }

                y += 5;
            }

            y += 8;

            drawSmallText(
                    graphics,
                    "REWARD",
                    cardX + 9,
                    y,
                    0xFFB9C5D6
            );

            y += 17;

            List<FormattedCharSequence> rewardLines =
                    font.split(
                            Component.literal(selected.getReward()),
                            cardWidth - 20
                    );

            for (int i = 0; i < Math.min(rewardLines.size(), 2); i++) {

                drawSmallFormattedText(
                        graphics,
                        rewardLines.get(i),
                        cardX + 9,
                        y,
                        0xFF65E890
                );

                y += 13;
            }
        }

        graphics.drawCenteredString(
                font,
                "JAPANESE SKILL TREE",
                treeCenterX(),
                18,
                0xFFFFD780
        );

        drawSmallText(
                graphics,
                "Drag to move | Scroll to zoom",
                SIDEBAR_WIDTH + 12,
                height - 20,
                0xFF9BA5B4
        );

        graphics.enableScissor(
                SIDEBAR_WIDTH,
                48,
                width,
                height - 35
        );

        // Draw connections from the central hub to the three starting quests.
        for (Quest quest : QuestManager.getAllQuests()) {

            if (quest.getPrerequisiteQuestId() == null) {
                drawHubConnection(graphics, quest);
            }
        }

        // Draw all quest-to-quest connections.
        for (Quest quest : QuestManager.getAllQuests()) {

            String prerequisiteId =
                    quest.getPrerequisiteQuestId();

            if (prerequisiteId == null) {
                continue;
            }

            Quest parent = findQuest(prerequisiteId);

            if (parent != null) {
                drawConnection(graphics, parent, quest);
            }
        }

        // Draw the central Japanese Foundations hub.
        int centerX = hubX();
        int centerY = hubY();

        int hubRadius = scaledSize(HUB_RADIUS);

        drawCircle(
                graphics,
                centerX,
                centerY,
                hubRadius + scaledSize(5),
                0xFF090B0F
        );

        drawCircle(
                graphics,
                centerX,
                centerY,
                hubRadius,
                0xFFB58A35
        );

        drawCircle(
                graphics,
                centerX,
                centerY,
                hubRadius - scaledSize(5),
                0xFF252A32
        );

        drawCenteredScaledText(
                graphics,
                "日本語",
                centerX,
                centerY - scaledSize(7),
                0xFFFFD780
        );

        drawCenteredScaledText(
                graphics,
                "FOUNDATIONS",
                centerX,
                centerY + scaledSize(10),
                0xFFFFFFFF
        );

        // Draw all quest nodes.
        for (Quest quest : QuestManager.getAllQuests()) {

            int x = screenX(quest);
            int y = screenY(quest);

            boolean isSelected =
                    quest.getId().equals(selectedQuestId);

            int radius = scaledSize(
                    isSelected ? NODE_RADIUS + 3 : NODE_RADIUS
            );

            int border = scaledSize(3);

            drawCircle(
                    graphics,
                    x,
                    y,
                    radius + border,
                    isSelected ? 0xFFFFD780 : 0xFF090B0F
            );

            drawCircle(
                    graphics,
                    x,
                    y,
                    radius,
                    nodeColor(quest)
            );

            String symbol = !isUnlocked(quest)
                    ? "L"
                    : quest.getStatus() == Quest.Status.COMPLETED
                    ? "✓"
                    : quest.getStatus() == Quest.Status.ACTIVE
                    ? "★"
                    : "•";

            drawCenteredScaledText(
                    graphics,
                    symbol,
                    x,
                    y - scaledSize(4),
                    0xFFFFFFFF
            );

            drawNodeLabel(
                    graphics,
                    quest,
                    x,
                    y
            );
        }

        graphics.disableScissor();

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        // Close button.
        if (mouseY < 45 && mouseX >= width - 100) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        // Sidebar.
        if (mouseX < SIDEBAR_WIDTH) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (button == 0) {

            for (Quest quest : QuestManager.getAllQuests()) {

                int x = screenX(quest);
                int y = screenY(quest);

                double dx = mouseX - x;
                double dy = mouseY - y;

                int hitRadius = Math.max(
                        14,
                        scaledSize(NODE_RADIUS + 7)
                );

                if (dx * dx + dy * dy <= hitRadius * hitRadius) {

                    selectedQuestId = quest.getId();

                    dragging = true;
                    lastMouseX = mouseX;
                    lastMouseY = mouseY;

                    return true;
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
                mouseX,
                mouseY,
                button,
                dragX,
                dragY
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

        return super.mouseReleased(
                mouseX,
                mouseY,
                button
        );
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
                    mouseX,
                    mouseY,
                    scrollX,
                    scrollY
            );
        }

        double oldZoom = zoom;

        double newZoom = Math.max(
                0.4,
                Math.min(2.0, zoom + scrollY * 0.1)
        );

        double centerX = treeCenterX();
        double centerY = treeCenterY();

        panX = mouseX - centerX
                - (mouseX - centerX - panX)
                * newZoom / oldZoom;

        panY = mouseY - centerY
                - (mouseY - centerY - panY)
                * newZoom / oldZoom;

        zoom = newZoom;

        clampPan();

        return true;
    }

    private void clampPan() {

        double minX = SIDEBAR_WIDTH + NODE_RADIUS + 12;
        double maxX = width - NODE_RADIUS - 12;

        double minY = 55 + NODE_RADIUS;
        double maxY = height - 40 - NODE_RADIUS;

        double left = Double.POSITIVE_INFINITY;
        double right = Double.NEGATIVE_INFINITY;
        double top = Double.POSITIVE_INFINITY;
        double bottom = Double.NEGATIVE_INFINITY;

        for (Quest quest : QuestManager.getAllQuests()) {

            double x = treeCenterX()
                    + panX
                    + nodeX(quest) * zoom;

            double y = treeCenterY()
                    + panY
                    + nodeY(quest) * zoom;

            double radius = NODE_RADIUS * zoom;

            left = Math.min(left, x - radius);
            right = Math.max(right, x + radius);

            top = Math.min(top, y - radius);
            bottom = Math.max(bottom, y + radius);
        }

        // Include the central hub in the visible bounds.
        double centerX = treeCenterX() + panX;
        double centerY = treeCenterY() + panY;
        double hubRadius = HUB_RADIUS * zoom;

        left = Math.min(left, centerX - hubRadius);
        right = Math.max(right, centerX + hubRadius);
        top = Math.min(top, centerY - hubRadius);
        bottom = Math.max(bottom, centerY + hubRadius);

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

