package com.flechazo.mdr.config;

import com.flechazo.mdr.MITEDeathRule;
import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.ServerConfigManager;
import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer;
import com.iafenvoy.jupiter.config.entry.IntegerEntry;
import com.iafenvoy.jupiter.interfaces.IConfigEntry;
import net.minecraft.resources.ResourceLocation;

public class MDRConfig extends AutoInitConfigContainer {
    public static final MDRConfig INSTANCE = new MDRConfig();

    public final IConfigEntry<Integer> expDropPercent =
            new IntegerEntry("config.mdr.common.exp.percent", 10, 0, 100).json("expDropPercent");

    public MDRConfig() {
        super(new ResourceLocation(MITEDeathRule.MOD_ID, "mdr_common_config"), "config.mdr.common.title", "./config/mite_death_rule/common.json");
    }

    @Override
    public void init() {
        this.createTab("death", "config.mdr.common.category.death")
                .add(this.expDropPercent);
    }

    public static int getExpDropPercent() {
        return INSTANCE.expDropPercent.getValue();
    }

    public static void register() {
        ConfigManager.getInstance().registerConfigHandler(MDRConfig.INSTANCE);
        ServerConfigManager.registerServerConfig(MDRConfig.INSTANCE, ServerConfigManager.PermissionChecker.IS_OPERATOR);
    }
}