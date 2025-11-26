package com.philippgitpush.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;

public class WaypointSettingsDialog {

  private final String dialog_title;

  public WaypointSettingsDialog(String dialog_title) {
    this.dialog_title = dialog_title;
  }

  public void open(Player player, Consumer<String> change_name_callback, Runnable delete_waypoint_callback) {
    List<ActionButton> action_buttons = new ArrayList<>();

    action_buttons.add(ActionButton.create(
      Component.text("Name ändern"),
      Component.text("Hier klicken, um den Wegpunkt umzubenennen."),
      200,
      DialogAction.customClick((view, audience) -> {
        if (audience instanceof Player) {
          String user_input_text = view.getText("user_input");
          change_name_callback.accept(user_input_text);
        }
      }, ClickCallback.Options.builder().build())
    ));

    action_buttons.add(ActionButton.create(
      Component.text("Wegpunkt entfernen", NamedTextColor.RED),
      Component.text("Hier klicken, um den Wegpunkt zu entfernen."),
      200,
      DialogAction.customClick((view, audience) -> {
        if (audience instanceof Player) {
          delete_waypoint_callback.run();
        }
      }, ClickCallback.Options.builder().build()))
    );

    ActionButton cancel_button = ActionButton.create(
      Component.text("Fertig"),
      Component.empty(),
      200, 
      null
    );

    Dialog fast_travel_dialog = Dialog.create(builder -> builder.empty()
      .base(DialogBase.builder(Component.text(dialog_title)).build())
      .type(DialogType.multiAction(action_buttons, cancel_button, 1)));

    player.showDialog(fast_travel_dialog);
  }
}
