package com.philippgitpush.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;

public class TextInputDialog {

  private final String title;
  private final String input_label;
  private final String submit_label;
  private final String submit_text;
  private final String cancel_label;
  private final String cancel_text;

  /**
   * @param title
   * @param input_label
   * @param submit_label
   * @param submit_text
   * @param cancel_label
   * @param cancel_text
   */
  public TextInputDialog(String title, String input_label, String submit_label, String submit_text, String cancel_label, String cancel_text) {
    this.title = title;
    this.input_label = input_label;
    this.submit_label = submit_label;
    this.submit_text = submit_text;
    this.cancel_label = cancel_label;
    this.cancel_text = cancel_text;
  }

  public void open(Player player, Consumer<String> callback) {
    List<ActionButton> action_buttons = new ArrayList<>();

    action_buttons.add(ActionButton.create(
      Component.text(submit_label),
      Component.text(submit_text),
      200,
      DialogAction.customClick((view, audience) -> {
        if (audience instanceof Player) {
          String input = view.getText("user_input");
          callback.accept(input);
        }
      }, ClickCallback.Options.builder().build())
    ));

    ActionButton cancel_button = ActionButton.create(
      Component.text(cancel_label),
      Component.text(cancel_text),
      200, 
      null
    );

    Dialog dialog = Dialog.create(builder -> builder.empty()
      .base(DialogBase.builder(Component.text(title)).inputs(List.of(DialogInput.text("user_input", Component.text(input_label)).width(200).build())).build())
      .type(DialogType.multiAction(action_buttons, cancel_button, 1)));

    player.showDialog(dialog);
  }
}
