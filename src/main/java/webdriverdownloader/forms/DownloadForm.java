package webdriverdownloader.forms;

import com.playground.webdriverdownloader.Browser;
import com.playground.webdriverdownloader.DownloaderService;
import com.playground.webdriverdownloader.WebdriverVersion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DownloadForm extends JFrame {

    private static final Browser[] BROWSERS = Browser.values();

    private DownloaderService downloaderService;

    private Browser selectedBrowser = null;
    private List<WebdriverVersion> availableVersions = null;
    private WebdriverVersion selectedVersion = null;

    private JPanel rootPanel;
    private JComboBox browserSelector;
    private JLabel browserSelectLabel;
    private JLabel versionLabel;
    private JComboBox versionSelector;
    private JButton downloadButton;
    private JLabel resultLabel;

    public DownloadForm(DownloaderService downloaderService) {
        setContentPane(rootPanel);
        this.downloaderService = downloaderService;

        browserSelector.setModel(
                new DefaultComboBoxModel(Arrays.stream(BROWSERS)
                        .map(Browser::getLabel).toArray(String[]::new)
                )
        );
        browserSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String browserStr = (String) ((JComboBox<Object>) e.getSource()).getSelectedItem();
                Browser browser = Arrays.stream(BROWSERS)
                        .filter(b -> b.getLabel().equals(browserStr))
                        .findFirst().orElse(null);
                onBrowserSelected(browser);
            }
        });

        versionSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String versionStr = (String) ((JComboBox<Object>) e.getSource()).getSelectedItem();
                WebdriverVersion version = availableVersions.stream()
                        .filter(v -> v.getValue().equals(versionStr))
                        .findFirst().orElse(null);
                onVersionSelected(version);
            }
        });

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDownloadButtonClicked();
            }
        });

        onBrowserSelected(BROWSERS[0]);
    }


    public JPanel getRootPanel() {
        return rootPanel;
    }

    private void onBrowserSelected(Browser browser) {
        selectedBrowser = browser;
        onVersionSelected(null);
        versionSelector.setEnabled(false);
        availableVersions = downloaderService.loadWebdriverVersions(selectedBrowser);
        versionSelector.setModel(new DefaultComboBoxModel(availableVersions.stream().map(WebdriverVersion::getValue).toArray(String[]::new)));
        versionSelector.setEnabled(true);
        onVersionSelected(availableVersions.get(0));
    }

    private void onVersionSelected(WebdriverVersion version) {
        selectedVersion = version;
        resultLabel.setText("");
        if (selectedVersion == null) {
            downloadButton.setEnabled(false);
        } else {
            downloadButton.setEnabled(true);
        }
    }

    private void onDownloadButtonClicked() {
        downloadButton.setEnabled(false);
        resultLabel.setText("Downloading...");
        File file = downloaderService.downloadWebdriver(selectedVersion.getDownloadLink());
        downloadButton.setEnabled(true);
        resultLabel.setText("Downloaded " + file.getName());
    }

}
