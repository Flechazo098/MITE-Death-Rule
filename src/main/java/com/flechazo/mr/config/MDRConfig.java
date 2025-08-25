package com.flechazo.mr.config;

import com.flechazo.mr.MITERule;
import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.ServerConfigManager;
import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer;
import com.iafenvoy.jupiter.config.entry.IntegerEntry;
import com.iafenvoy.jupiter.interfaces.IConfigEntry;
import net.minecraft.resources.ResourceLocation;

public class MDRConfig extends AutoInitConfigContainer {
    public static final MDRConfig INSTANCE = new MDRConfig();

    public final IConfigEntry<Integer> expDropPercent =
            new IntegerEntry("config.mdr.common.exp_percent", 10, 0, 100).json("expDropPercent");
    public final IConfigEntry<Integer> pvpDamageReduction =
            new IntegerEntry("config.mdr.common.pvp_damage_reduction", 50, 0, 100).json("pvpDamageReduction");

    public MDRConfig() {
        super(new ResourceLocation(MITERule.MOD_ID, "mdr_common_config"), "config.mdr.common.title", "./config/mite_rule/common.json");
    }

    @Override
    public void init() {
        this.createTab("death", "config.mdr.common.category.death")
                .add(this.expDropPercent);
        this.createTab("pvp", "config.mdr.common.category.pvp")
                .add(this.pvpDamageReduction);
    }

    public static int getExpDropPercent() {
        return INSTANCE.expDropPercent.getValue();
    }
    public static int getPvpDamageReduction() {
        return INSTANCE.pvpDamageReduction.getValue();
    }

    public static void register() {
        ConfigManager.getInstance().registerConfigHandler(MDRConfig.INSTANCE);
        ServerConfigManager.registerServerConfig(MDRConfig.INSTANCE, ServerConfigManager.PermissionChecker.IS_OPERATOR);
    }
}