package com.lilsort.easyauthextension.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nikitacartes.easyauth.event.AuthEventHandler;
import xyz.nikitacartes.easyauth.interfaces.PlayerAuth;
import com.lilsort.easyauthextension.utils.FailedLoginIpManager;

import java.util.regex.Matcher;
import net.minecraft.server.MinecraftServer;
import java.net.SocketAddress;

@Mixin(AuthEventHandler.class)
public class AuthEventHandlerMixin {

    @Inject(method = "checkCanPlayerJoinServer", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onCheckCanPlayerJoinServer(com.mojang.authlib.GameProfile profile, net.minecraft.server.PlayerManager manager, SocketAddress socketAddress, CallbackInfoReturnable<Text> cir) {
        String ip = socketAddress.toString();
        if (ip.contains("/")) {
            ip = ip.substring(ip.indexOf(47) + 1);
        }
        if (ip.contains(":")) {
            ip = ip.substring(0, ip.indexOf(58));
        }

        if (FailedLoginIpManager.isBanned(ip)) {
            cir.setReturnValue(Text.literal("§cСлишком много неудачных попыток входа. Пожалуйста, подождите некоторое время."));
        }
    }
}
