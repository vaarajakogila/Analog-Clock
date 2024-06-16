import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class AnalogClock extends JPanel implements ActionListener {
    private int hour;
    private int minute;
    private int second;
    private int alarmHour = -1;
    private int alarmMinute = -1;
    private boolean alarmSet = false;

    private boolean stopwatchRunning = false;
    private long stopwatchStartTime;

    public AnalogClock() {
        Timer timer = new Timer(1000, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        int clockRadius = (int) (Math.min(width, height) * 0.4);
        int xCenter = width / 2;
        int yCenter = height / 2;

        // Draw the clock face
        g.setColor(Color.WHITE);
        g.fillOval(xCenter - clockRadius, yCenter - clockRadius, 2 * clockRadius, 2 * clockRadius);
        g.setColor(Color.BLACK);
        g.drawOval(xCenter - clockRadius, yCenter - clockRadius, 2 * clockRadius, 2 * clockRadius);

        // Draw the hour numbers and seconds lines
        g.setFont(new Font("Arial", Font.BOLD, 20));
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                // Draw hour numbers
                int hourNumber = i / 5 == 0 ? 12 : i / 5;
                String number = String.valueOf(hourNumber);
                double angle = Math.toRadians(i * 6 - 90);
                int x = (int) (xCenter + (clockRadius - 25) * Math.cos(angle));
                int y = (int) (yCenter + (clockRadius - 25) * Math.sin(angle));
                int stringWidth = g.getFontMetrics().stringWidth(number);
                int stringHeight = g.getFontMetrics().getHeight();
                g.drawString(number, x - stringWidth / 2, y + stringHeight / 4);
            } else {
                // Draw seconds lines
                double angle = Math.toRadians(i * 6 - 90);
                int x1 = (int) (xCenter + (clockRadius - 10) * Math.cos(angle));
                int y1 = (int) (yCenter + (clockRadius - 10) * Math.sin(angle));
                int x2 = (int) (xCenter + (clockRadius - 5) * Math.cos(angle));
                int y2 = (int) (yCenter + (clockRadius - 5) * Math.sin(angle));
                g.drawLine(x1, y1, x2, y2);
            }
        }

        // Draw the clock hands
        drawHand(g, xCenter, yCenter, clockRadius * 0.5, hour * 30 + minute / 2, Color.BLACK);
        drawHand(g, xCenter, yCenter, clockRadius * 0.75, minute * 6, Color.BLUE);
        drawHand(g, xCenter, yCenter, clockRadius * 0.9, second * 6, Color.RED);

        // Display the alarm status
        if (alarmSet) {
            g.setColor(Color.RED);
            g.drawString("Alarm set for " + String.format("%02d:%02d", alarmHour, alarmMinute), 10, height - 40);
        }

        // Display the stopwatch time only when running
        if (stopwatchRunning) {
            long elapsedTime = System.currentTimeMillis() - stopwatchStartTime;
            int millis = (int) (elapsedTime % 1000);
            int seconds = (int) (elapsedTime / 1000) % 60;
            int minutes = (int) (elapsedTime / 60000) % 60;
            int hours = (int) (elapsedTime / 3600000);
            g.setColor(Color.BLACK);
            g.drawString(String.format("Stopwatch: %02d:%02d:%02d.%03d", hours, minutes, seconds, millis), 10, height - 20);
        }
    }

    private void drawHand(Graphics g, int xCenter, int yCenter, double length, double angleInDegrees, Color color) {
        int xEnd = (int) (xCenter + length * Math.cos(Math.toRadians(angleInDegrees - 90)));
        int yEnd = (int) (yCenter + length * Math.sin(Math.toRadians(angleInDegrees - 90)));
        g.setColor(color);
        g.drawLine(xCenter, yCenter, xEnd, yEnd);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);

        // Check for alarm
        if (alarmSet && hour == alarmHour && minute == alarmMinute && second == 0) {
            JOptionPane.showMessageDialog(this, "Alarm ringing!");
            alarmSet = false; // Reset the alarm
        }

        repaint();
    }

    public void setAlarm(int hour, int minute) {
        this.alarmHour = hour;
        this.alarmMinute = minute;
        this.alarmSet = true;
    }

    public void startStopwatch() {
        if (!stopwatchRunning) {
            stopwatchRunning = true;
            stopwatchStartTime = System.currentTimeMillis();
        }
    }

    public void stopStopwatch() {
        if (stopwatchRunning) {
            stopwatchRunning = false;
            repaint();
        }
    }

    public void resetStopwatch() {
        stopwatchRunning = false;
        stopwatchStartTime = 0;
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        AnalogClock clock = new AnalogClock();
        frame.add(clock);
        frame.setTitle("Analog Clock");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Add buttons for setting alarm and controlling stopwatch
        JPanel panel = new JPanel();
        JButton setAlarmButton = new JButton("Set Alarm");
        JButton startStopwatchButton = new JButton("Start Stopwatch");
        JButton stopStopwatchButton = new JButton("Stop Stopwatch");
        JButton resetStopwatchButton = new JButton("Reset Stopwatch");

        setAlarmButton.setFocusable(false);
        startStopwatchButton.setFocusable(false);
        stopStopwatchButton.setFocusable(false);
        resetStopwatchButton.setFocusable(false);

        setAlarmButton.addActionListener(e -> {
            String hourStr = JOptionPane.showInputDialog(frame, "Enter hour (0-23):");
            String minuteStr = JOptionPane.showInputDialog(frame, "Enter minute (0-59):");
            int hour = Integer.parseInt(hourStr);
            int minute = Integer.parseInt(minuteStr);
            clock.setAlarm(hour, minute);
        });

        startStopwatchButton.addActionListener(e -> clock.startStopwatch());
        stopStopwatchButton.addActionListener(e -> clock.stopStopwatch());
        resetStopwatchButton.addActionListener(e -> clock.resetStopwatch());

        panel.add(setAlarmButton);
        panel.add(startStopwatchButton);
        panel.add(stopStopwatchButton);
        panel.add(resetStopwatchButton);
        frame.add(panel, BorderLayout.SOUTH);
    }
}
