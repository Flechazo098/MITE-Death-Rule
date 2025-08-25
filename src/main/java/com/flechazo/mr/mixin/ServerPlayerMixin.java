package com.flechazo.mr.mixin;

import com.flechazo.mr.MDRExpUtils;
import com.flechazo.mr.MITERule;
import com.flechazo.mr.config.MDRConfig;
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
        if (!rules.getBoolean(MITERule.KEEP_SELECTED_ON_DEATH)) return;
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
        int lossPercent = MDRConfig.getExpDropPercent();
        if (lossPercent < 0) lossPercent = 0;
        if (lossPercent > 100) lossPercent = 100;

        int lostExp = (int) Math.floor(totalExp * (lossPercent / 100.0));
        int keepExp = totalExp - lostExp;

        // 重置并发放保留经验（不掉落经验）
        self.totalExperience = 0;
        self.experienceLevel = 0;
        self.experienceProgress = 0.0F;
        self.giveExperiencePoints(keepExp);

        // 打印日志
        MITERule.LOGGER.info("[MDR] restoreFrom -> player='{}', totalExp={}, lossPercent={}%, lostExp={}, keepExp={}",
                self.getName().getString(), totalExp, lossPercent, lostExp, keepExp);
    }
}