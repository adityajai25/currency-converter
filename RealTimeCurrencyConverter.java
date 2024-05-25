import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class RealTimeCurrencyConverter extends JFrame {
    private JTextField amountField;
    private JComboBox<String> fromCurrency;
    private JComboBox<String> toCurrency;
    private JButton convertButton;
    private JLabel resultLabel;

    private JSONObject exchangeRates;

    public RealTimeCurrencyConverter() {
        setTitle("Real-Time Currency Converter");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(50, 30, 100, 30);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(150, 30, 150, 30);
        add(amountField);

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setBounds(50, 70, 100, 30);
        add(fromLabel);

        fromCurrency = new JComboBox<>(new String[]{"USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CNY", "SEK", "NZD", "INR"});
        fromCurrency.setBounds(150, 70, 90, 30);
        add(fromCurrency);

        JLabel toLabel = new JLabel("To:");
        toLabel.setBounds(250, 70, 50, 30);
        add(toLabel);

        toCurrency = new JComboBox<>(new String[]{"USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CNY", "SEK", "NZD", "INR"});
        toCurrency.setBounds(270, 70, 90, 30);
        add(toCurrency);

        convertButton = new JButton("Convert");
        convertButton.setBounds(150, 110, 100, 30);
        add(convertButton);

        resultLabel = new JLabel("Result: ");
        resultLabel.setBounds(50, 150, 300, 30);
        add(resultLabel);

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchExchangeRates();
            }
        });
    }

    private void fetchExchangeRates() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String apiKey = "c8bfd32e82839f51cc508494"; // Replace with your Open Exchange Rates API key
                    String baseCurrency = "INR"; // Base currency for exchange rates

                    URL url = new URL("https://open.er-api.com/v6/latest/" + baseCurrency + "?apiKey=" + apiKey);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    exchangeRates = new JSONObject(response.toString());
                    convertCurrency();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void convertCurrency() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String from = (String) fromCurrency.getSelectedItem();
            String to = (String) toCurrency.getSelectedItem();

            if (from.equals(to)) {
                resultLabel.setText("From and To currencies must be different.");
                return;
            }

            double fromRate = exchangeRates.getJSONObject("rates").getDouble(from);
            double toRate = exchangeRates.getJSONObject("rates").getDouble(to);

            double result = (amount / fromRate) * toRate;
            resultLabel.setText("Result: " + String.format("%.2f", result) + " " + to);
        } catch (NumberFormatException e) {
            resultLabel.setText("Invalid amount");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RealTimeCurrencyConverter().setVisible(true);
            }
        });
    }
}
