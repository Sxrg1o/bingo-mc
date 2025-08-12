package com.bingaso.bingo.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SubCommand {
    boolean execute(@NotNull CommandSender sender, @NotNull String[] args);
    @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String[] args);
}