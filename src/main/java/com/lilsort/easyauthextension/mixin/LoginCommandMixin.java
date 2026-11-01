package com.lilsort.easyauthextension.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nikitacartes.easyauth.commands.LoginCommand;
import xyz.nikitacartes.easyauth.interfaces.PlayerAuth;
import xyz.nikitacartes.easyauth.storage.PlayerEntryV1;
import com.lilsort.easyauthextension.utils.FailedLoginIpManager;

@Mixin(LoginCommand.class)
public class LoginCommandMixin {

    @Inject(method = "login", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onLoginHead(ServerCommandSource source, String pass, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        String ip = ((PlayerAuth) player).easyAuth$getIpAddress();
        
        if (FailedLoginIpManager.isBanned(ip)) {
            player.networkHandler.disconnect(Text.literal("§cToo many failed login attempts from your IP. Please wait 15 minutes."));
            cir.setReturnValue(0);
        }
    }

    // We inject right before playerData.loginTries++ is called.
    @Inject(method = "login", at = @At(value = "FIELD", target = "Lxyz/nikitacartes/easyauth/storage/PlayerEntryV1;loginTries:J", opcode = org.objectweb.asm.Opcodes.GETFIELD, ordinal = 1), cancellable = true, remap = false)
    private static void onWrongPasswordAttempt(ServerCommandSource source, String pass, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        String ip = ((PlayerAuth) player).easyAuth$getIpAddress();
        
        boolean isNowBanned = FailedLoginIpManager.recordFailure(ip);
        if (isNowBanned) {
            player.networkHandler.disconnect(Text.literal("§cСлишком много неудачных попыток входа. Пожалуйста, подождите некоторое время."));
            cir.setReturnValue(0);
        }
    }

    // We inject into successful login when it updates lastAuthenticatedDate
    @Inject(method = "login", at = @At(value = "FIELD", target = "Lxyz/nikitacartes/easyauth/storage/PlayerEntryV1;lastAuthenticatedDate:Ljava/time/ZonedDateTime;", opcode = org.objectweb.asm.Opcodes.PUTFIELD, ordinal = 0), remap = false)
    private static void onLoginSuccess(ServerCommandSource source, String pass, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        String ip = ((PlayerAuth) player).easyAuth$getIpAddress();
        FailedLoginIpManager.clearFailures(ip);
    }
}
