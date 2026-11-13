package dev.visfox.thaumichorizons.compat.tc;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import dev.visfox.thaumichorizons.compat.CompatAspectList;

final class TcConversions {
    private TcConversions() {
    }

    static AspectList toAspectList(CompatAspectList compat) {
        AspectList list = new AspectList();
        for (String tag : compat.getTags()) {
            Aspect aspect = Aspect.getAspect(tag);
            if (aspect != null) {
                list.add(aspect, compat.getAmount(tag));
            }
        }
        return list;
    }

    static CompatAspectList toCompat(@Nullable AspectList list) {
        CompatAspectList compat = new CompatAspectList();
        if (list == null) {
            return compat;
        }
        for (Aspect aspect : list.getAspects()) {
            compat.add(aspect.getTag(), list.getAmount(aspect));
        }
        return compat;
    }

    static List<Aspect> resolveAspects(List<String> tags) {
        List<Aspect> aspects = new ArrayList<>();
        for (String tag : tags) {
            Aspect aspect = Aspect.getAspect(tag);
            if (aspect != null) {
                aspects.add(aspect);
            }
        }
        return aspects;
    }
}
