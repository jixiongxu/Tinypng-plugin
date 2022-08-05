package com.tinypng.ui;

import com.tinypng.source.ConfigUtils;
import com.tinypng.source.LogCall;
import com.tinypng.source.MainDev;
import com.tinypng.source.ProjectConfig;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class ConfigDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tvConfigPath;
    private JButton buttonConfig;
    private JTextArea log_message;
    private JTextField mRecordPath;
    private JButton recordButton;

    private ProjectConfig mProjectConfig;

    public ConfigDialog() {
        setTitle("Tinypng-Plugin");
        setSize(500, 500);
        setLocationRelativeTo(null);
        contentPane.setPreferredSize(new Dimension(500, 300));
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        mProjectConfig = ConfigUtils.getProjectConfig();
        tvConfigPath.setText(mProjectConfig == null ? "" : mProjectConfig.projectConfigPath);
        mRecordPath.setText(mProjectConfig == null ? "" : mProjectConfig.projectRecordPath);
        initConfigButton();
        initRecordBtn();

        log_message.setLineWrap(true);
        log_message.setWrapStyleWord(true);
    }

    private void initConfigButton() {
        buttonConfig.addActionListener(actionEvent -> {
            getProjectPath();
        });
    }

    private void initRecordBtn() {
        recordButton.addActionListener(actionEvent -> {
            getRecordPath();
        });
    }

    private void onOK() {
        log_message.setText("");
        String path = tvConfigPath.getText();
        String recordPath = mRecordPath.getText();
        if (path.isEmpty() || recordPath.isEmpty()) {
            setLogMessage("必须设置配置文件和记录文件");
            return;
        }
        MainDev.start(path, recordPath, new LogCall() {
            @Override
            public void onLog(@NotNull String log) {
                setLogMessage(log);
            }
        });
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        ConfigDialog dialog = new ConfigDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

    private String mLastMsg = "";

    private void setLogMessage(String message) {
        log_message.setText(mLastMsg);
        log_message.append("\n");
        log_message.append(message);
        mLastMsg = message;
    }

    // 获取项目路径
    private void getProjectPath() {
        JFileChooser chooser = new JFileChooser("选择工程路径");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addActionListener(actionEvent ->
                {
                    if (Objects.equals(actionEvent.getActionCommand(), "CancelSelection")) {
                        return;
                    }
                    tvConfigPath.setText(chooser.getSelectedFile().getAbsolutePath());
                    if (mProjectConfig == null) {
                        mProjectConfig = new ProjectConfig();
                    }
                    mProjectConfig.projectConfigPath = tvConfigPath.getText();
                    ConfigUtils.saveProjectConfig(mProjectConfig);
                }
        );
        chooser.showOpenDialog(chooser);
    }

    private void getRecordPath() {
        JFileChooser chooser = new JFileChooser("选择工程路径");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addActionListener(actionEvent ->
                {
                    if (Objects.equals(actionEvent.getActionCommand(), "CancelSelection")) {
                        return;
                    }
                    mRecordPath.setText(chooser.getSelectedFile().getAbsolutePath());
                    if (mProjectConfig == null) {
                        mProjectConfig = new ProjectConfig();
                    }
                    mProjectConfig.projectRecordPath = mRecordPath.getText();
                    ConfigUtils.saveProjectConfig(mProjectConfig);
                }
        );
        chooser.showOpenDialog(chooser);
    }

}
