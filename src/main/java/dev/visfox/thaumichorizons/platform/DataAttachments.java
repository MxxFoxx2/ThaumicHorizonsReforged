package dev.visfox.thaumichorizons.platform;

import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import dev.visfox.thaumichorizons.ThaumicHorizons;

public final class DataAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ThaumicHorizons.MODID);

    private DataAttachments() {
    }

    static <T> Supplier<AttachmentType<T>> register(String name, Supplier<AttachmentType<T>> factory) {
        return ATTACHMENTS.register(name, factory);
    }

    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}
