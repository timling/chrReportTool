package com.promeritage.chtReportTool.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXDatePicker;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.promeritage.chtReportTool.utils.PropertiesUtil;

public class App extends JPanel {
    private static final long serialVersionUID = 1L;

    public App() {
        super(new GridLayout(1, 1));

        JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = null;
        // createImageIcon("images/middle.gif");

        JComponent panel1 = makeTab1();
        tabbedPane.addTab("日報表", icon, panel1,
                "請先在 https://www.google.com/calendar/render?tab=mc 設定工作內容");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent panel2 = makeTab2();
        tabbedPane.addTab("月報表(工作日誌)", icon, panel2,
                "請先在 https://www.google.com/calendar/render?tab=mc 設定工作內容");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        Properties properties = PropertiesUtil
                .loadOrCreateProperties(PropertiesUtil.POPERTIES_NAME);
        if ("tim.ling@promeritage.com.tw".equals(properties.getProperty("email"))) {
            JComponent panel3 = makeTab3();
            tabbedPane.addTab("月請假紀錄", icon, panel3,
                    "請先在 https://www.google.com/calendar/render?tab=mc 設定工作內容");
            tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
        }

        // Add the tabbed pane to this panel.
        add(tabbedPane);

        // The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // JOptionPane.showMessageDialog(tabbedPane, "Eggs are not supposed to be green.",
        // "Inane error", JOptionPane.ERROR_MESSAGE);
    }

    protected JComponent makeTab1() {
        final JPanel panel = new JPanel(false);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        JLabel startLabel = new JLabel("日期");
        startLabel.setHorizontalAlignment(JLabel.RIGHT);
        c.gridx = 0;
        c.gridy = 0;
        panel.add(startLabel, c);

        final JXDatePicker startDatePicker = new JXDatePicker(new Date());
        startDatePicker.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        c.gridx = 1;
        c.gridy = 0;
        panel.add(startDatePicker);

        final JButton count_btn = new JButton("發Mail");
        count_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    DailyReportController.genReport(LocalDate.fromDateFields(startDatePicker
                            .getDate()));
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(panel, e1.getMessage(), "error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        panel.add(count_btn);

        return panel;
    }

    protected JComponent makeTab2() {
        final JPanel panel = new JPanel(false);
        panel.setLayout(new GridLayout(2, 4, 10, 10));

        JLabel startLabel = new JLabel("起日");
        startLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(startLabel);

        final JXDatePicker startDatePicker = new JXDatePicker(LocalDate.now().dayOfMonth()
                .withMinimumValue().toDate());
        startDatePicker.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        panel.add(startDatePicker);

        JLabel endLabel = new JLabel("迄日");
        endLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(endLabel);

        final JXDatePicker endDatePicker = new JXDatePicker(LocalDate.now().dayOfMonth()
                .withMaximumValue().toDate());
        endDatePicker.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        panel.add(endDatePicker);

        final JButton leftBtn = new JButton("<");
        leftBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startDatePicker.setDate(LocalDate.fromDateFields(startDatePicker.getDate())
                        .minusMonths(1).toDate());
                endDatePicker.setDate(LocalDate.fromDateFields(endDatePicker.getDate())
                        .minusMonths(1).dayOfMonth().withMaximumValue().toDate());
            }
        });
        panel.add(leftBtn);

        final JButton count_btn = new JButton("產出月報");
        count_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    MonthReportController.genMonthReport(
                            LocalDate.fromDateFields(startDatePicker.getDate()),
                            LocalDate.fromDateFields(endDatePicker.getDate()));
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(panel, e1.getMessage(), "error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(count_btn);

        final JButton rightBtn = new JButton(">");
        rightBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startDatePicker.setDate(LocalDate.fromDateFields(startDatePicker.getDate())
                        .plusMonths(1).toDate());
                endDatePicker.setDate(LocalDate.fromDateFields(endDatePicker.getDate())
                        .plusMonths(1).dayOfMonth().withMaximumValue().toDate());
            }
        });
        panel.add(rightBtn);

        return panel;
    }

    protected JComponent makeTab3() {
        final JPanel panel = new JPanel(false);
        panel.setLayout(new GridLayout(2, 4, 10, 10));

        JLabel startLabel = new JLabel("起日");
        startLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(startLabel);

        final JXDatePicker startDatePicker = new JXDatePicker(LocalDate.now().dayOfMonth()
                .withMinimumValue().toDate());
        startDatePicker.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        panel.add(startDatePicker);

        JLabel endLabel = new JLabel("迄日");
        endLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(endLabel);

        final JXDatePicker endDatePicker = new JXDatePicker(LocalDate.now().dayOfMonth()
                .withMaximumValue().toDate());
        endDatePicker.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        panel.add(endDatePicker);

        final JButton leftBtn = new JButton("<");
        leftBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startDatePicker.setDate(LocalDate.fromDateFields(startDatePicker.getDate())
                        .minusMonths(1).toDate());
                endDatePicker.setDate(LocalDate.fromDateFields(endDatePicker.getDate())
                        .minusMonths(1).dayOfMonth().withMaximumValue().toDate());
            }
        });
        panel.add(leftBtn);

        final JButton count_btn = new JButton("發Mail");
        count_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    MonthVacationController.sendVacationLog(
                            LocalDateTime.fromDateFields(startDatePicker.getDate()),
                            LocalDateTime.fromDateFields(endDatePicker.getDate()));
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(panel, e1.getMessage(), "error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(count_btn);

        final JButton rightBtn = new JButton(">");
        rightBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startDatePicker.setDate(LocalDate.fromDateFields(startDatePicker.getDate())
                        .plusMonths(1).toDate());
                endDatePicker.setDate(LocalDate.fromDateFields(endDatePicker.getDate())
                        .plusMonths(1).dayOfMonth().withMaximumValue().toDate());
            }
        });
        panel.add(rightBtn);

        return panel;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = App.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked from the event
     * dispatch thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("普瑞德in中華電信tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add content to the window.
        frame.add(new App(), BorderLayout.CENTER);

        frame.setPreferredSize(new Dimension(400, 150));
        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }
}
