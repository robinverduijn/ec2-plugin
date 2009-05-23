package org.jvnet.hudson.ec2.launcher.gui;

import org.jvnet.hudson.ec2.launcher.gui.CompletionUpdater;
import org.jvnet.hudson.ec2.launcher.OperatorErrorException;
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.KeyPairInfo;
import org.apache.commons.io.FileUtils;
import org.pietschy.wizard.InvalidStateException;

import javax.swing.*;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Selects an existing key or create a new one.
 *
 * @author Kohsuke Kawaguchi
 */
public class SelectKeyPage extends Page {
    private JRadioButton iAlreadyHaveARadioButton;
    private JRadioButton generateANewKeypairRadioButton;
    private JTextField loadKeyFile;
    private JButton selectExistingKey;
    private JTextField saveKeyFile;
    private JButton selectNewKey;
    private JTextField keyName;
    private JPanel panel;

    public SelectKeyPage(WizardState state) {
        super(state);
        add(panel);

        new CompletionUpdater() {
            @Override
            public void update() {
                boolean loadMode = isLoadMode();
                selectExistingKey.setEnabled(loadMode);
                loadKeyFile.setEnabled(loadMode);

                saveKeyFile.setEnabled(!loadMode);
                selectNewKey.setEnabled(!loadMode);
                keyName.setEnabled(!loadMode);

                if (loadMode)
                    setComplete(has(loadKeyFile));
                else
                    setComplete(has(saveKeyFile) && has(keyName));
            }
        }.add(iAlreadyHaveARadioButton).add(generateANewKeypairRadioButton).add(loadKeyFile).add(saveKeyFile).add(keyName).update();

        selectExistingKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                if (fc.showOpenDialog(SelectKeyPage.this) == APPROVE_OPTION) {
                    loadKeyFile.setText(fc.getSelectedFile().getPath());
                }
            }
        });

        selectNewKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                if (fc.showSaveDialog(SelectKeyPage.this) == APPROVE_OPTION) {
                    saveKeyFile.setText(fc.getSelectedFile().getPath());
                }
            }
        });

        loadKeyFile.setText(state.prefs.get(PREFKEY_PRIVATEKEY, ""));
    }

    private boolean isLoadMode() {
        return iAlreadyHaveARadioButton.isSelected();
    }

    @Override
    public void applyState() throws InvalidStateException {
        try {
            busyCursor();
            File f;
            if (isLoadMode()) {
                f = new File(loadKeyFile.getText());
                launcher.setPrivateKey(f);
            } else {
                KeyPairInfo kp = launcher.getEc2().createKeyPair(keyName.getText());
                f = new File(saveKeyFile.getText());
                FileUtils.writeStringToFile(f, kp.getKeyMaterial());
                launcher.setPrivateKey(f);
            }
            state.prefs.put(PREFKEY_PRIVATEKEY, f.getPath());
        } catch (EC2Exception e) {
            reportError("Operations on EC2 failed", e);
            throw new InvalidStateException();
        } catch (IOException e) {
            reportError("Operations on EC2 failed", e);
            throw new InvalidStateException();
        } catch (OperatorErrorException e) {
            throw new InvalidStateException(e.getMessage());
        } finally {
            restoreCursor();
        }
    }

    private static final String PREFKEY_PRIVATEKEY = "privateKeyFile";

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        iAlreadyHaveARadioButton = new JRadioButton();
        iAlreadyHaveARadioButton.setSelected(true);
        iAlreadyHaveARadioButton.setText("I already have a keypair for EC2");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(iAlreadyHaveARadioButton, gbc);
        generateANewKeypairRadioButton = new JRadioButton();
        generateANewKeypairRadioButton.setText("Generate a new keypair for EC2");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(generateANewKeypairRadioButton, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Private key file: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label1, gbc);
        loadKeyFile = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(loadKeyFile, gbc);
        selectExistingKey = new JButton();
        selectExistingKey.setText("...");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(selectExistingKey, gbc);
        saveKeyFile = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(saveKeyFile, gbc);
        selectNewKey = new JButton();
        selectNewKey.setText("...");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(selectNewKey, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Save private key to: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label2, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 999.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(spacer1, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Key name: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label3, gbc);
        keyName = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(keyName, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.weightx = 999.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(spacer2, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 32;
        panel.add(label4, gbc);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(iAlreadyHaveARadioButton);
        buttonGroup.add(generateANewKeypairRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}