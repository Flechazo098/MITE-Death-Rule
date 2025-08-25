package com.flechazo.mr;

import com.flechazo.mr.config.MDRConfig;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MITERule implements ModInitializer {

    public static final String MOD_ID = "mite_death_rule";

    public static final GameRules.Key<GameRules.BooleanValue> KEEP_SELECTED_ON_DEATH =
            GameRuleRegistry.register("keepSelectedOnDeath", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanValue> PVP_REDUCE_INJURIES =
            GameRuleRegistry.register("pvpDamageReduction", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

    public static final Logger LOGGER = LoggerFactory.getLogger(MITERule.class);

    @Override
    public void onInitialize() {

        MDRConfig.register();

        TrinketDropCallback.EVENT.register((rule, stack, ref, entity) -> {
            if (entity != null && entity.getLevel().getGameRules().getBoolean(KEEP_SELECTED_ON_DEATH)) {
                return TrinketEnums.DropRule.KEEP;
            }
            return rule;
        });
    }
}
