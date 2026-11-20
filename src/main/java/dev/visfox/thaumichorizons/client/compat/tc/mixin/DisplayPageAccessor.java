package dev.visfox.thaumichorizons.client.compat.tc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thaumcraft.research.ResearchPage;

@Mixin(targets = "thaumcraft.client.research.ResearchPageScreen$DisplayPage")
public interface DisplayPageAccessor {
    @Accessor("source")
    ResearchPage thaumichorizons$getSource();
}
