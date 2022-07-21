package com.tinypng.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.tinypng.ui.ConfigDialog;
import org.jetbrains.annotations.NotNull;


public class TinyConfigAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        new Thread(() -> {
            ConfigDialog dialog = new ConfigDialog();
            dialog.pack();
            dialog.setVisible(true);
        }).start();
    }

    @Override
    public boolean isDumbAware() {
        return super.isDumbAware();
    }
}
