package com.flechazo.mdr.mixin;

import com.flechazo.mdr.MDRExpUtils;
import com.flechazo.mdr.MITEDeathRule;
import com.flechazo.mdr.config.MDRConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow public int totalExperience;

    @Shadow
    @Final
    private Inventory inventory;
    @Shadow protected abstract void destroyVanishingCursedItems();

    @Inject(method = "getExperienceReward", at = @At("HEAD"), cancellable = true)
    private void mdr$customXpToDrop(CallbackInfoReturnable<Integer> cir) {
        Player self = (Player) (Object) this;
        if (self.getLevel() != null && self.getLevel().getGameRules().getBoolean(MITEDeathRule.KEEP_SELECTED_ON_DEATH)) {
            int keepPercent = MDRConfig.getExpDropPercent();
            var split = MDRExpUtils.splitExp(this.totalExperience, keepPercent);

            cir.setReturnValue(split.drop());
        }
    }


    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    private void mdr$selectiveDropEquipment(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (self.getLevel() == null) return;

        var rules = self.getLevel().getGameRules();
        if (rules.getBoolean(MITEDeathRule.KEEP_SELECTED_ON_DEATH) && !rules.getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            this.destroyVanishingCursedItems();

            for (int i = 9; i < this.inventory.items.size(); i++) {
                ItemStack stack = this.inventory.items.get(i);
                if (!stack.isEmpty()) {
                    self.drop(stack, true, false);
                    this.inventory.items.set(i, ItemStack.EMPTY);
                }
            }

            ci.cancel();
        }
    }
}