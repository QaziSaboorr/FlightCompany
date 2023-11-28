
import javax.swing.*;



import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserRegistrationFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField addressField;
    private JButton registerButton;
    private DatabaseConnector databaseConnector;
    private LoginFrame loginFrame;

    public UserRegistrationFrame(DatabaseConnector databaseConnector, LoginFrame loginFrame) {
        this.databaseConnector = databaseConnector;
        this.loginFrame = loginFrame;

        setTitle("User Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Initialize components
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        emailField = new JTextField();
        addressField = new JTextField();
        registerButton = new JButton("Register");

        setLayout(new GridLayout(5, 2));

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Address:"));
        add(addressField);
        add(new JLabel()); // Empty label for spacing
        add(registerButton);

        // Action Listener for register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();
        String passwordString = new String(password);
        String email = emailField.getText();
        String address = addressField.getText();

        if (username.isEmpty() || email.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all the required fields.");
            return;
        }

        // Insert the user data into the database
        String query = "INSERT INTO Users (UserName, UserType, Email, Password, Address) VALUES (?, 'Registered', ?, ?, ?)";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, passwordString);
            preparedStatement.setString(4, address);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful. You can now login.");
                loginFrame.setVisible(true);
                loginFrame.setUserType(UserType.Registered); // Set the user type to "Registered"
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during registration. Please try again.");
        }
    }
}
