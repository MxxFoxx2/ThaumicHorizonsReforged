package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.blockentity.ThBlockEntity;
import dev.visfox.thaumichorizons.lib.THSounds;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SoulBeaconBlockEntity extends BlockEntity {
    public SoulBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.SOUL_BEACON.get(), pos, state);
    }

    public boolean activate(Player player) {
        Level level = player.level();
        SoulBeaconKeys.SoulBeaconData data = new SoulBeaconKeys.SoulBeaconData(true, worldPosition.getX(),
            worldPosition.getY(), worldPosition.getZ(),
            level.dimension().identifier().toString());
        SoulBeaconKeys.DATA.set(player, data);
        player.sendSystemMessage(Component.translatable("thaumichorizons.setBeacon")
            .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        Compat.port().fx().arcLightning(level, player.position().add(0.0, player.getEyeHeight(), 0.0),
            new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.75, worldPosition.getZ() + 0.5),
            0x05FF05);
        THSounds.play(level, worldPosition, "zap", SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }
}
