package org.noear.solon.core;

import java.util.List;
import java.util.Optional;

/**
 * @author noear 2023/9/23 created
 */
public class VarGatherSort {
    public static int buildIndex(VarGather g1, List<VarGather> gathers) {
        if (g1.index > 0) {
            return g1.index;
        }

        for (VarHolder v1 : g1.getVars()) {
            if (v1.isDone() == false) {
                Optional<VarGather> tmp = gathers.stream()
                        .filter(g2 -> g2.getOutType().equals(v1.getType()))
                        .findFirst();

                if (tmp.isPresent()) {
                    int index = buildIndex(tmp.get(), gathers) + 1;

                    if (g1.index < index) {
                        g1.index = index;
                    }
                }
            }
        }

        return g1.index;
    }
}
