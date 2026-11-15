package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.Endersteed;
import net.minecraft.client.model.animal.equine.BabyHorseModel;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class EndersteedRenderer extends AbstractHorseRenderer<Endersteed, HorseRenderState, HorseModel> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/entity/endersteed.png");
    private static final Identifier IRON_TEXTURE = ThaumicHorizons.id("textures/entity/endersteediron.png");
    private static final Identifier GOLD_TEXTURE = ThaumicHorizons.id("textures/entity/endersteedgold.png");
    private static final Identifier DIAMOND_TEXTURE = ThaumicHorizons.id("textures/entity/endersteeddiamond.png");
    private static final Identifier[] TEXTURES = {TEXTURE, IRON_TEXTURE, GOLD_TEXTURE, DIAMOND_TEXTURE};
    private static final Identifier BABY_TEXTURE = ThaumicHorizons.id("textures/entity/endersteed_baby.png");
    private static final Identifier BABY_IRON_TEXTURE = ThaumicHorizons.id("textures/entity/endersteediron_baby.png");
    private static final Identifier BABY_GOLD_TEXTURE = ThaumicHorizons.id("textures/entity/endersteedgold_baby.png");
    private static final Identifier BABY_DIAMOND_TEXTURE = ThaumicHorizons.id("textures/entity/endersteeddiamond_baby.png");
    private static final Identifier[] BABY_TEXTURES = {BABY_TEXTURE, BABY_IRON_TEXTURE, BABY_GOLD_TEXTURE, BABY_DIAMOND_TEXTURE};

    public EndersteedRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel(context.bakeLayer(ModelLayers.HORSE)), new BabyHorseModel(context.bakeLayer(ModelLayers.HORSE_BABY)));
    }

    @Override
    public Identifier getTextureLocation(HorseRenderState state) {
        Item armor = state.bodyArmorItem.getItem();
        int variant = armor == Items.IRON_HORSE_ARMOR ? 1
            : armor == Items.GOLDEN_HORSE_ARMOR ? 2
            : armor == Items.DIAMOND_HORSE_ARMOR ? 3 : 0;
        return state.isBaby ? BABY_TEXTURES[variant] : TEXTURES[variant];
    }

    @Override
    public HorseRenderState createRenderState() {
        return new HorseRenderState();
    }
}
