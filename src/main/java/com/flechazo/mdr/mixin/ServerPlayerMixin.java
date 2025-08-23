package com.flechazo.mdr.mixin;

import com.flechazo.mdr.MDRExpUtils;
import com.flechazo.mdr.MITEDeathRule;
import com.flechazo.mdr.config.MDRConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void mdr$selectiveRestore(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;

        var level = self.getLevel();
        if (alive || level == null) return;

        var rules = level.getGameRules();
        if (!rules.getBoolean(MITEDeathRule.KEEP_SELECTED_ON_DEATH)) return;
        if (rules.getBoolean(GameRules.RULE_KEEPINVENTORY)) return;


        for (int i = 0; i < 9; i++) {
            ItemStack st = oldPlayer.getInventory().items.get(i);
            if (!st.isEmpty()) self.getInventory().items.set(i, st);
            oldPlayer.getInventory().items.set(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < oldPlayer.getInventory().armor.size(); i++) {
            ItemStack st = oldPlayer.getInventory().armor.get(i);
            if (!st.isEmpty()) self.getInventory().armor.set(i, st);
            oldPlayer.getInventory().armor.set(i, ItemStack.EMPTY);
        }

        if (!oldPlayer.getInventory().offhand.isEmpty()) {
            ItemStack off = oldPlayer.getInventory().offhand.get(0);
            if (!off.isEmpty()) self.getInventory().offhand.set(0, off);
            oldPlayer.getInventory().offhand.set(0, ItemStack.EMPTY);
        }

        int totalExp = oldPlayer.totalExperience;
        int keepPercent = MDRConfig.getExpDropPercent();
        var split = MDRExpUtils.splitExp(totalExp, keepPercent);

        self.totalExperience = 0;
        self.experienceLevel = 0;
        self.experienceProgress = 0.0F;
        self.giveExperiencePoints(split.keep());
    }
}