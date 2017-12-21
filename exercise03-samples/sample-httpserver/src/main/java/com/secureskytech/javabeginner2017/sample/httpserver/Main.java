package com.secureskytech.javabeginner2017.sample.httpserver;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    final JFrame frame;
    JSpinner spnHttpPort;
    JSpinner spnHttpsPort;
    JButton btnStart;
    JButton btnStop;
    File rootDir = new File(".");
    SampleHttpServer httpServer;
    SampleHttpServer secureServer;

    public Main() {
        frame = new JFrame("sample http server");
    }

    private void show() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        JLabel lblHttpPort = new JLabel("http port:");
        lblHttpPort.setHorizontalAlignment(SwingConstants.LEADING);
        pane.add(lblHttpPort);

        spnHttpPort = new JSpinner();
        spnHttpPort.setModel(new SpinnerNumberModel(new Integer(10080), null, null, new Integer(1)));
        spnHttpPort.setPreferredSize(new Dimension(50, 20));
        pane.add(spnHttpPort);

        JLabel lblHttpsPort = new JLabel("https port:");
        lblHttpsPort.setHorizontalAlignment(SwingConstants.LEADING);
        pane.add(lblHttpsPort);

        spnHttpsPort = new JSpinner();
        spnHttpsPort.setModel(new SpinnerNumberModel(new Integer(10081), null, null, new Integer(1)));
        spnHttpsPort.setPreferredSize(new Dimension(50, 20));
        pane.add(spnHttpsPort);

        JButton btnSelectRootDir = new JButton("select root");
        btnSelectRootDir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser filechooser = new JFileChooser(".");
                filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int selected = filechooser.showOpenDialog(frame);
                if (selected == JFileChooser.APPROVE_OPTION) {
                    rootDir = filechooser.getSelectedFile();
                    LOG.info(rootDir.getAbsolutePath());
                }
            }
        });
        pane.add(btnSelectRootDir);

        btnStart = new JButton("start");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LOG.info("starting ...");
                try {
                    httpServer =
                        SampleHttpServer.createHTTP(rootDir, Integer.parseInt(spnHttpPort.getValue().toString()));
                    httpServer.start();
                    secureServer =
                        SampleHttpServer.createHTTPS(rootDir, Integer.parseInt(spnHttpsPort.getValue().toString()));
                    secureServer.start();
                    LOG.info("started.");
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);
                    JOptionPane.showMessageDialog(frame, "started.");
                } catch (Exception ex) {
                    LOG.error("starting failed.", ex);
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "start failed.", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        pane.add(btnStart);

        btnStop = new JButton("stop");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LOG.info("stopping ...");
                try {
                    httpServer.shutdown();
                    secureServer.shutdown();
                    LOG.info("stopped.");
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                    JOptionPane.showMessageDialog(frame, "stopped.");
                } catch (Exception ex) {
                    LOG.error("stopping failed.", ex);
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "stop failed.", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        pane.add(btnStop);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main m = new Main();
                    m.show();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        null,
                        Throwables.getStackTraceAsString(e),
                        "error:" + e.getMessage(),
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });
    }
}
