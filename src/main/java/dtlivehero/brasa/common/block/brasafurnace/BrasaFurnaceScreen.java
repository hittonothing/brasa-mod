package dtlivehero.brasa.common.block.brasafurnace;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dtlivehero.brasa.Brasa;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrasaFurnaceScreen extends AbstractContainerScreen<BrasaFurnaceContainer> {

    private final BrasaFurnaceContainer brasaFurnaceContainer;

    public BrasaFurnaceScreen(BrasaFurnaceContainer brasaFurnaceContainer, Inventory inventory, Component title) {
        super(brasaFurnaceContainer, inventory, title);
        this.brasaFurnaceContainer = brasaFurnaceContainer;
        imageWidth = 176;
        imageHeight = 207;
    }

    final static int COOK_BAR_XPOS = 49;
    final static int COOK_BAR_YPOS = 40;
    final static int COOK_BAR_ICON_U = 0;
    final static int COOK_BAR_ICON_V = 207;
    final static int COOK_BAR_WIDTH = 80;
    final static int COOK_BAR_HEIGHT = 17;

    final static int FLAME_XPOS = 72;
    final static int FLAME_YPOS = 59;
    final static int FLAME_ICON_U = 176; //staring position
    final static int FLAME_ICON_V = 0; //starting position
    final static int FLAME_WIDTH = 14;
    final static int FLAME_HEIGHT = 14;
    final static int FLAME_X_SPACING = 18;

    final static int FONT_Y_SPACING = 10;
    final static int PLAYER_INV_LABEL_XPOS = BrasaFurnaceContainer.PLAYER_INVENTORY_XPOS;
    final static int PLAYER_INV_LABEL_YPOS = BrasaFurnaceContainer.PLAYER_INVENTORY_YPOS - FONT_Y_SPACING;

    @Override
    public void render(PoseStack poseStack, int xMouse, int yMouse, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, xMouse, yMouse, partialTicks);
        this.renderTooltip(poseStack, xMouse, yMouse);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int xMouse, int yMouse) {
        if(!this.getMinecraft().player.inventoryMenu.getCarried().isEmpty()) return;

        List<Component> hoveringText = new ArrayList<>();

        if(isInRect(getGuiLeft() + COOK_BAR_XPOS, getGuiTop() + COOK_BAR_YPOS, COOK_BAR_WIDTH, COOK_BAR_HEIGHT, xMouse, yMouse)) {
            hoveringText.add(new TextComponent("Progress:"));
            int cookPercentage = (int)(brasaFurnaceContainer.fractionOfCookTimeComplete() * 100);
            hoveringText.add(new TextComponent(cookPercentage + "%"));
        }

        for(int i = 0; i < 2; ++i) {
            if(isInRect(getGuiLeft() + FLAME_XPOS + FLAME_X_SPACING * i, getGuiTop() + FLAME_YPOS, FLAME_WIDTH, FLAME_HEIGHT, xMouse, yMouse)) {
                hoveringText.add(new TextComponent("Fuel Time:"));
                hoveringText.add(new TextComponent(brasaFurnaceContainer.secondsOfFuelRemaining(i) + ("s")));
            }
        }

        if(!hoveringText.isEmpty()) {
            renderTooltip(poseStack, hoveringText, Optional.empty(), xMouse, yMouse);
        } else {
            super.renderTooltip(poseStack, xMouse, yMouse);
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int xMouse, int yMouse) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int edgeSpacingX = (this.width - this.getXSize()) / 2;
        int edgeSpacingY = (this.height - this.getYSize()) / 2;
        this.blit(poseStack, edgeSpacingX, edgeSpacingY, 0, 0, this.getXSize(), this.getYSize());

        double cookProgress = brasaFurnaceContainer.fractionOfCookTimeComplete();
        this.blit(poseStack, getGuiLeft() + COOK_BAR_XPOS, getGuiTop() + COOK_BAR_YPOS, COOK_BAR_ICON_U, COOK_BAR_ICON_V, (int)(cookProgress * COOK_BAR_WIDTH), COOK_BAR_HEIGHT);

        for(int i = 0; i < 2; ++i) {
            double burnRemaining = brasaFurnaceContainer.fractionOfFuelRemaining(i);
            int yOffset = (int)((1.0 - burnRemaining) * FLAME_HEIGHT);
            this.blit(poseStack, getGuiLeft() + FLAME_XPOS + FLAME_X_SPACING * i, getGuiTop() + FLAME_YPOS + yOffset,
                    FLAME_ICON_U, FLAME_ICON_V + yOffset, FLAME_WIDTH, FLAME_HEIGHT - yOffset);
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int xMouse, int yMouse) {
        final int LABEL_XPOS = 5;
        final int LABEL_YPOS = 5;
        this.font.draw(poseStack, this.title, LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());
        this.font.draw(poseStack, this.getMinecraft().player.getInventory().getDisplayName(), PLAYER_INV_LABEL_XPOS, PLAYER_INV_LABEL_YPOS, Color.darkGray.getRGB());
    }

    private static boolean isInRect(int x, int y, int xSize, int ySize, int xMouse, int yMouse) {
        return ((xMouse >= x && xMouse <= x + xSize) && (yMouse >= y && yMouse <= y + ySize));
    }

    public static final ResourceLocation TEXTURE = new ResourceLocation(Brasa.MOD_ID, "textures/gui/brasa_furnace_container.png");
}
