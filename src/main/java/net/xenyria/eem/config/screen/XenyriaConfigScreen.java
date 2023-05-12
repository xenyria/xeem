package net.xenyria.eem.config.screen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class XenyriaConfigScreen implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return XenyriaConfigManager.getConfigurationBuilder()
                    .setParentScreen(parent).build();
        };
    }
}
