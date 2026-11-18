package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.NightmareTH;
import net.minecraft.client.model.animal.equine.BabyHorseModel;
import net.minecraft.client.model.animal.equine.EquineSaddleModel;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class NightmareRenderer extends AbstractHorseRenderer<NightmareTH, HorseRenderState, HorseModel> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/entity/nightmare.png");
    private static final Identifier IRON_TEXTURE = ThaumicHorizons.id("textures/entity/nightmareiron.png");
    private static final Identifier GOLD_TEXTURE = ThaumicHorizons.id("textures/entity/nightmaregold.png");
    private static final Identifier DIAMOND_TEXTURE = ThaumicHorizons.id("textures/entity/nightmarediamond.png");
    private static final Identifier[] TEXTURES = {TEXTURE, IRON_TEXTURE, GOLD_TEXTURE, DIAMOND_TEXTURE};
    private static final Identifier BABY_TEXTURE = ThaumicHorizons.id("textures/entity/nightmare_baby.png");
    private static final Identifier BABY_IRON_TEXTURE = ThaumicHorizons.id("textures/entity/nightmareiron_baby.png");
    private static final Identifier BABY_GOLD_TEXTURE = ThaumicHorizons.id("textures/entity/nightmaregold_baby.png");
    private static final Identifier BABY_DIAMOND_TEXTURE = ThaumicHorizons.id("textures/entity/nightmarediamond_baby.png");
    private static final Identifier[] BABY_TEXTURES = {
        BABY_TEXTURE, BABY_IRON_TEXTURE, BABY_GOLD_TEXTURE, BABY_DIAMOND_TEXTURE
    };

    public NightmareRenderer(EntityRendererProvider.Context context) {
        super(context,
            new HorseModel(context.bakeLayer(ModelLayers.HORSE)),
            new BabyHorseModel(context.bakeLayer(ModelLayers.HORSE_BABY)));
        // Armor is baked into the selected legacy texture; only draw the saddle
        // as a separate equipment layer to avoid duplicating the armor geometry.
        this.addLayer(new SimpleEquipmentLayer<>(
            this,
            context.getEquipmentRenderer(),
            EquipmentClientInfo.LayerType.HORSE_SADDLE,
            state -> state.saddle,
            new EquineSaddleModel(context.bakeLayer(ModelLayers.HORSE_SADDLE)),
            null
        ));
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
