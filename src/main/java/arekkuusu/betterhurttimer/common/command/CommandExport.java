package arekkuusu.betterhurttimer.common.command;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.api.BHTAPI;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class CommandExport {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> argument = Commands.literal("bht_export").requires(commandSource -> commandSource.hasPermissionLevel(2));
        String[] args = {"damageFrames", "attackFrames", "mobIdListAll"};
        for (String arg : args) {
            argument.then(Commands.argument(arg, StringArgumentType.word()).executes(context -> export(context.getSource(), StringArgumentType.getString(context, arg))));
        }
        dispatcher.register(argument);
    }

    public static int export(CommandSource commandSource, String argument) {
        File file = Objects.requireNonNull(commandSource.getServer()).getDataDirectory();
        if (file.exists() && file.canWrite() && file.isDirectory()) {
            try {
                File exportFile = new File(file.getCanonicalPath()
                        + File.separator + "config"
                        + File.separator + "bht",
                        argument + ".txt"
                );
                //noinspection ResultOfMethodCallIgnored
                exportFile.getParentFile().mkdirs();
                if (exportFile.createNewFile()) {
                    message(commandSource, "export.created");
                } else {
                    message(commandSource, "export.overwritten");
                }
                FileWriter export = new FileWriter(exportFile);
                switch (argument) {
                    case "damageFrames":
                        for (Map.Entry<CharSequence, HurtSourceInfo> entry : BHTAPI.DAMAGE_SOURCE_INFO_MAP.entrySet()) {
                            HurtSourceInfo hurtSourceInfo = entry.getValue();
                            export.write(hurtSourceInfo.sourceName + ":" + hurtSourceInfo.waitTime + ":" + hurtSourceInfo.doFrames + "\n");
                        }
                        break;
                    case "attackFrames":
                        for (Map.Entry<ResourceLocation, Double> entry : BHTAPI.ATTACK_THRESHOLD_MAP.entrySet()) {
                            ResourceLocation location = entry.getKey();
                            Double timer = entry.getValue();
                            export.write(location.toString() + ":" + timer + "\n");
                        }
                        break;
                    case "mobIdListAll":
                        for (ResourceLocation location : Registry.ENTITY_TYPE.keySet()) {
                            export.write(location.toString() + "\n");
                        }
                        break;
                }
                export.close();
            } catch (IOException e) {
                message(commandSource, "export.unsuccessful");
                e.printStackTrace();
            } finally {
                message(commandSource, "export.successful");
            }
        }
        return 0;
    }

    private static void message(CommandSource commandSource, String type, Object... args) {
        String key = "command." + BHT.MOD_ID + "." + type;
        commandSource.sendFeedback(new TranslationTextComponent(key, args), true);
    }
}
