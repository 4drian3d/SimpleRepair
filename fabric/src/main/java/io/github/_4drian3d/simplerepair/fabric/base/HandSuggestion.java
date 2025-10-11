package io.github._4drian3d.simplerepair.fabric.base;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public enum HandSuggestion implements SuggestionProvider<CommandSourceStack> {
  INSTANCE;

  @Override
  public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
    return builder.suggest("MAIN_HAND").suggest("OFF_HAND").buildFuture();
  }
}
