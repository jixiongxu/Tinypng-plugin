package com.tinypng.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.tinypng.source.ConfigUtils;
import com.tinypng.ui.ConfigDialog;
import org.jetbrains.annotations.NotNull;


public class TinyConfigAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        ConfigUtils.PROJECT_PATH = project == null ? "" : project.getBasePath();
        ConfigDialog dialog = new ConfigDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

    @Override
    public boolean isDumbAware() {
        return super.isDumbAware();
    }
}
