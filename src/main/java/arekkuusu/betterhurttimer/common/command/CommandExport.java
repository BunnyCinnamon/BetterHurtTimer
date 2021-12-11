package arekkuusu.betterhurttimer.common.command;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.api.BHTAPI;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo;
import com.google.common.collect.ImmutableList;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CommandExport extends CommandBase {

    @Override
    public String getName() {
        return BHT.MOD_ID + "_export";
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.of("bht_export");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: /" + getName() + " [damageFrames/attackFrames/mobIdListAll]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "damageFrames", "attackFrames", "mobIdListAll");
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        File file = Objects.requireNonNull(server.getServer()).getDataDirectory();
        if (file.exists() && file.canWrite() && file.isDirectory()) {
            try {
                File exportFile = new File(file.getCanonicalPath()
                        + File.separator + "config"
                        + File.separator + "bht",
                        args[0] + ".txt"
                );
                //noinspection ResultOfMethodCallIgnored
                exportFile.getParentFile().mkdirs();
                if (exportFile.createNewFile()) {
                    message(sender, "export.created");
                } else {
                    message(sender, "export.overwritten");
                }
                FileWriter export = new FileWriter(exportFile);
                switch (args[0]) {
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
                        for (ResourceLocation location : GameRegistry.findRegistry(EntityEntry.class).getKeys()) {
                            export.write(location.toString() + "\n");
                        }
                        break;
                }
                export.close();
            } catch (IOException e) {
                message(sender, "export.unsuccessful");
                e.printStackTrace();
            } finally {
                message(sender, "export.successful");
            }
        }
    }

    private void message(ICommandSender sender, String type, Object... args) {
        String key = "command." + BHT.MOD_ID + "." + type;
        sender.sendMessage(new TextComponentTranslation(key, args));
    }
}
