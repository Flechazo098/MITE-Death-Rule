package com.flechazo.mr.mixin;

import com.flechazo.mr.MITERule;
import com.flechazo.mr.config.MDRConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    // 修改 LivingEntity.hurt 的第二个参数 amount（伤害数值）
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float mdr$reducePvpDamage(float amount, DamageSource source) {
        LivingEntity self = (LivingEntity) (Object) this;
        var level = self.getLevel();
        if (level == null) return amount;

        // 必须开启游戏规则，且攻守双方都是玩家
        var rules = level.getGameRules();
        if (!rules.getBoolean(MITERule.PVP_REDUCE_INJURIES)) return amount;
        if (!(self instanceof Player victim)) return amount;
        var attackerEntity = source.getEntity();
        if (!(attackerEntity instanceof Player attacker)) return amount;

        int percent = MDRConfig.getPvpDamageReduction();
        if (percent <= 0) return amount;
        if (percent > 100) percent = 100;

        float reduced = amount * (1.0f - percent / 100.0f);

        MITERule.LOGGER.info("[MDR] PvP damage reduced: attacker='{}', victim='{}', original={}, reduction={}%, final={}",
                attacker.getName().getString(), victim.getName().getString(), amount, percent, reduced);

        return reduced;
    }
}