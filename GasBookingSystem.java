import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class GasBookingSystem extends Frame implements ActionListener {
    
    Label lblUser, lblPass, lblTitle;
    TextField tfUser, tfPass;
    Button btnLogin, btnRegister;

    Connection conn;

    GasBookingSystem() {
        
        connectDB();

        
        setLayout(null);
        setTitle("Gas Booking Management System");
        setSize(400, 250);
        setBackground(new Color(240, 248, 255));

        lblTitle = new Label("GAS BOOKING LOGIN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(90, 40, 250, 30);
        add(lblTitle);

        lblUser = new Label("Username:");
        lblUser.setBounds(60, 90, 80, 25);
        add(lblUser);
        tfUser = new TextField();
        tfUser.setBounds(150, 90, 180, 25);
        add(tfUser);

        lblPass = new Label("Password:");
        lblPass.setBounds(60, 130, 80, 25);
        add(lblPass);
        tfPass = new TextField();
        tfPass.setEchoChar('*');
        tfPass.setBounds(150, 130, 180, 25);
        add(tfPass);

        btnLogin = new Button("Login");
        btnLogin.setBounds(100, 170, 80, 30);
        add(btnLogin);

        btnRegister = new Button("Register");
        btnRegister.setBounds(200, 170, 80, 30);
        add(btnRegister);

        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);

        setVisible(true);
    }

    void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/gas_booking_db",
                "root", "root" // change password
            );
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "DB Connection Failed: " + e);
            System.exit(0);
        }
    }

    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnLogin) {
            loginUser();
        } else if (ae.getSource() == btnRegister) {
            new RegisterFrame(conn);
        }
    }

    void loginUser() {
        String user = tfUser.getText();
        String pass = tfPass.getText();
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?"
            );
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                new BookingFrame(conn, rs.getInt("id"), rs.getString("name"));
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
        new GasBookingSystem();
    }
}

class RegisterFrame extends Frame implements ActionListener {
    Label lblName, lblUser, lblPass, lblAddr, lblTitle;
    TextField tfName, tfUser, tfPass, tfAddr;
    Button btnSave;
    Connection conn;

    RegisterFrame(Connection c) {
        conn = c;
        setLayout(null);
        setTitle("User Registration");
        setSize(400, 350);
        setBackground(new Color(250, 250, 240));

        lblTitle = new Label("REGISTER NEW USER");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(100, 40, 220, 30);
        add(lblTitle);

        lblName = new Label("Full Name:");
        lblName.setBounds(60, 90, 80, 25);
        add(lblName);
        tfName = new TextField();
        tfName.setBounds(150, 90, 180, 25);
        add(tfName);

        lblUser = new Label("Username:");
        lblUser.setBounds(60, 130, 80, 25);
        add(lblUser);
        tfUser = new TextField();
        tfUser.setBounds(150, 130, 180, 25);
        add(tfUser);

        lblPass = new Label("Password:");
        lblPass.setBounds(60, 170, 80, 25);
        add(lblPass);
        tfPass = new TextField();
        tfPass.setEchoChar('*');
        tfPass.setBounds(150, 170, 180, 25);
        add(tfPass);

        lblAddr = new Label("Address:");
        lblAddr.setBounds(60, 210, 80, 25);
        add(lblAddr);
        tfAddr = new TextField();
        tfAddr.setBounds(150, 210, 180, 25);
        add(tfAddr);

        btnSave = new Button("Register");
        btnSave.setBounds(150, 260, 100, 30);
        add(btnSave);

        btnSave.addActionListener(this);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users(name, username, password, address) VALUES(?, ?, ?, ?)"
            );
            ps.setString(1, tfName.getText());
            ps.setString(2, tfUser.getText());
            ps.setString(3, tfPass.getText());
            ps.setString(4, tfAddr.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Registration successful!");
            dispose();
            new GasBookingSystem();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}

// ----------- BOOKING FRAME ------------
class BookingFrame extends Frame implements ActionListener {
    Label lblTitle, lblType, lblDate;
    Choice chType;
    TextField tfDate;
    Button btnBook, btnView, btnDelete;
    Connection conn;
    int userId;
    String userName;

    BookingFrame(Connection c, int uid, String name) {
        conn = c;
        userId = uid;
        userName = name;

        setLayout(null);
        setTitle("Book Gas Cylinder");
        setSize(450, 300);
        setBackground(new Color(245, 255, 250));

        lblTitle = new Label("Welcome, " + name);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(120, 40, 250, 30);
        add(lblTitle);

        lblType = new Label("Cylinder Type:");
        lblType.setBounds(70, 100, 100, 25);
        add(lblType);

        chType = new Choice();
        chType.add("Domestic");
        chType.add("Commercial");
        chType.add("Industrial");
        chType.setBounds(190, 100, 150, 25);
        add(chType);

        lblDate = new Label("Booking Date (YYYY-MM-DD):");
        lblDate.setBounds(70, 140, 180, 25);
        add(lblDate);

        tfDate = new TextField();
        tfDate.setBounds(260, 140, 100, 25);
        add(tfDate);

        btnBook = new Button("Book Now");
        btnBook.setBounds(80, 190, 100, 30);
        add(btnBook);

        btnView = new Button("View Bookings");
        btnView.setBounds(200, 190, 100, 30);
        add(btnView);

        btnDelete = new Button("Delete Booking");
        btnDelete.setBounds(320, 190, 100, 30);
        add(btnDelete);

        btnBook.addActionListener(this);
        btnView.addActionListener(this);
        btnDelete.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnBook) {
            bookGas();
        } else if (ae.getSource() == btnView) {
            viewBookings();
        } else if (ae.getSource() == btnDelete) {
            deleteBooking();
        }
    }

    void bookGas() {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO bookings(user_id, cylinder_type, booking_date) VALUES(?, ?, ?)"
            );
            ps.setInt(1, userId);
            ps.setString(2, chType.getSelectedItem());
            ps.setString(3, tfDate.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Booking Successful!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e);
        }
    }

    void viewBookings() {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM bookings WHERE user_id=?"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder("Your Bookings:\n\n");
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                  .append(", Type: ").append(rs.getString("cylinder_type"))
                  .append(", Date: ").append(rs.getString("booking_date"))
                  .append(", Status: ").append(rs.getString("status"))
                  .append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deleteBooking() {
        String id = JOptionPane.showInputDialog("Enter Booking ID to delete:");
        try {
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM bookings WHERE id=? AND user_id=?"
            );
            ps.setInt(1, Integer.parseInt(id));
            ps.setInt(2, userId);
            int count = ps.executeUpdate();
            if (count > 0)
                JOptionPane.showMessageDialog(null, "Booking Deleted!");
            else
                JOptionPane.showMessageDialog(null, "No such booking found!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
