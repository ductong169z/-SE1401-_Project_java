/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wibu
 */
class UserInfo {

    public int user_id;
    public String user_name;
    public int role_id;

    public UserInfo() {
    }

    public UserInfo(int user_id, String user_name, int role_id) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.role_id = role_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public int getRole_id() {
        return role_id;
    }

}

class Auth {

    public Auth() {
    }

    public static UserInfo loginUser() throws SQLException, NullPointerException, ClassNotFoundException {
        Connection conn = DataConnection.getConnection();
        Statement stmt = conn.createStatement();
        int status = 0;
        boolean check = true;
        int pin = 0;
        int card_id = 0;
        ResultSet rs;

        // loop until card ID is correctly inputted
        do {
            System.out.println("Please enter CARD ID:");
            try {
                Scanner input = new Scanner(System.in);
                check = true; // by default input is valid

                card_id = input.nextInt();
                input.nextLine();

                if (card_id < 10000000 || card_id > 10099999) {
                    check = false;
                    System.out.println("Card ID is from 10000000 to 10099999");
                }

            } catch (InputMismatchException ex) {
                check = false;
                System.out.println("Please input 8-digit numbers only for card ID! ");
            } catch (Exception ex) {
                check = false;
                System.out.println("An error occured! Please try again later! ");
            }

            rs = stmt.executeQuery("SELECT users.id, card_id, pin, contact_number, gender,"
                    + " address, users.name, role_id FROM users JOIN user_role ON users.id= user_id WHERE card_id =" + card_id);
            if (rs.next()) {

                System.out.println("Please enter PIN:");

                // loop until PIN is correctly inputted
                do {
                    try {
                        Scanner input = new Scanner(System.in);
                        check = true;

                        pin = input.nextInt();
                        input.nextLine();

                        if (pin < 1000 || pin > 9999) {
                            check = false;
                            System.out.println("PIN consists of 4 numbers! ");
                        }

                    } catch (InputMismatchException e) {
                        check = false;
                        System.out.println("Only numbers here !!");
                    } catch (Exception e) {
                        check = false;
                        System.out.println("An error occured! Please try again later! ");
                    }
                } while (!check);

            } else {

                System.out.println("Card ID not found !");
                check = false;
            }
        } while (!check);

        // show data
        if (card_id == rs.getInt(2) && pin == rs.getInt(3)) {

            UserInfo user = new UserInfo(rs.getInt(1), rs.getString(7), rs.getInt(8));
            return user;

        } else {
            UserInfo user2 = new UserInfo();
            return user2;
        }

    }

}

class Menu {

    public Menu() {
    }

    public static void changePassword(UserInfo user) throws SQLException, ClassNotFoundException {
        Connection conn = DataConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rS = stmt.executeQuery("Select * FROM users WHERE id=" + user.user_id);
        rS.next();
        int PIN = rS.getInt(3);
        int newPIN2 = 0;
        int newPIN1 = 0;
        boolean check = true; // by default input is valid
        System.out.print("Please input current PIN: ");

        // loop until PIN is a valid number from 1000 to 9999
        do {
            try {
                Scanner input = new Scanner(System.in);
                check = true; // by default input is valid

                PIN = input.nextInt();
                input.nextLine();

                if (PIN < 1000 || PIN > 9999) {
                    check = false;
                    System.out.println("PIN is from 1000 to 9999! ");
                }

            } catch (InputMismatchException ex) {
                check = false;
                System.out.println("Please input PIN as a number of 4 digits! ");
            } catch (Exception ex) {
                check = false;
                System.out.println("An error occured! Please try again later! ");
            }
        } while (!check);

        ResultSet rS1 = stmt.executeQuery("SELECT pin FROM users WHERE id =" + user.user_id);
        rS1.next();

        if (PIN == rS1.getInt(1)) {
            System.out.print("Please enter new PIN: ");
            // loop until PIN is a valid number from 1000 to 9999
            do {
                try {
                    Scanner input1 = new Scanner(System.in);
                    check = true; // by default input is valid
                    newPIN1 = input1.nextInt();

                    if (newPIN1 < 1000 || newPIN1 > 9999) {
                        check = false;
                        System.out.println("PIN is from 1000 to 9999! ");
                    }

                } catch (InputMismatchException ex) {
                    check = false;
                    System.out.println("Please input PIN as a number of 4 digits! ");
                } catch (Exception ex) {
                    check = false;
                    System.out.println("An error occured! Please try again later! ");
                }
            } while (!check);

            do {
                System.out.print("Please confirm new PIN : ");
                // loop until PIN is a valid number from 1000 to 9999
                do {
                    try {
                        Scanner input2 = new Scanner(System.in);
                        check = true; // by default input is valid

                        newPIN2 = input2.nextInt();

                        if (newPIN2 < 1000 || newPIN2 > 9999) {
                            check = false;
                            System.out.println("PIN is from 1000 to 9999! ");
                        }

                    } catch (InputMismatchException ex) {
                        check = false;
                        System.out.println("Please input PIN as a number of 4 digits! ");
                    } catch (Exception ex) {
                        check = false;
                        System.out.println("An error occured! Please try again later! ");
                    }
                } while (!check);

                if (newPIN1 == newPIN2) {
                    stmt.executeUpdate("UPDATE users SET pin= " + newPIN1 + " WHERE id= " + user.user_id);
                    check = true;
                    System.out.println("PIN change successfully");
                } else {
                    System.out.println("New password confirmation doesn't match!");
                    check = false;

                }
            } while (!check);
        } else {
            System.out.println("Wrong password!!");
        }
    }

}

class userMenu extends Menu {

    public void userMenu(UserInfo user) throws SQLException, ClassNotFoundException {
        int choice = 0;
        boolean check = true;

        do {
            System.out.println(" ♥(。O ω O。)WELCOME TO WIBU ATM(。O ω O。)♥");
            System.out.println("            -----USERS MENU-----");
            System.out.print("1. Deposit                ");
            System.out.println("2. Withdrawal");
            System.out.print("3. Balance Enquiry        ");
            System.out.println("4. Change Password.");
            System.out.println("5. Exit");
            System.out.print("Please input your choice: ");

            // loop until choice is correctly inputted
            do {
                try {
                    Scanner input = new Scanner(System.in);
                    check = true; // by default input is valid

                    choice = input.nextInt();
                    input.nextLine();

                    if (choice < 1 || choice > 5) {
                        check = false;
                        System.out.println("Please input a number from 1 to 5! ");
                    }

                } catch (InputMismatchException ex) {
                    check = false;
                    System.out.println("Please input a number! ");
                } catch (Exception ex) {
                    check = false;
                    System.out.println("An error occured! Please try again later!");
                }
            } while (!check);

            switch (choice) {
                case 1:
                    deposit(user);

                    break;

                case 2:
                    withdraw(user);

                    break;

                case 3:
                    performBalanceEnquiry(user);

                    break;

                case 4:
                    changePassword(user);

                    break;

                case 5:

            }
        } while (choice != 5);
    }

    public void deposit(UserInfo user) throws ClassNotFoundException, SQLException {
        Connection conn = DataConnection.getConnection();
        Statement stmt = conn.createStatement();

        ResultSet rS = stmt.executeQuery("Select * FROM setting WHERE id=1");
        rS.next();

        int maxmoney = rS.getInt(1);
        int msxcount_limit = rS.getInt(2);

        ResultSet rS1 = stmt.executeQuery("Select count(*) FROM user_deposit WHERE user_id=" + user.user_id + " AND created_at LIKE '" + java.time.LocalDate.now() + "%'");
        rS1.next();

        int count = rS1.getInt(1);

        ResultSet rS2 = stmt.executeQuery("SELECT total_money FROM user_money WHERE user_id =" + user.user_id);
        rS2.next();

        long currentmoney = rS2.getInt(1);
        System.out.println("Your balance: " + currentmoney);

        if (count > msxcount_limit) {
            System.out.println("Sorry, you have reach the limit of deposit times today.");
        } else {
            System.out.println("Please enter the amount of money you want to send: ");
            Scanner input = new Scanner(System.in);
            int money = input.nextInt();

            if (money >= 10 && money <= maxmoney) {
                stmt.executeUpdate("INSERT INTO user_deposit (deposit_money,user_id,created_at) VALUES (" + money + "," + user.user_id + ",'" + java.time.LocalDate.now() + "')");
                stmt.executeUpdate("UPDATE user_money SET total_money = " + (currentmoney + money) + " WHERE user_id = " + user.user_id);
                System.out.println("Deposit successfully!!!");
                System.out.println("Your current balance: " + (currentmoney + money));
            } else {
                System.out.println("Error!! Input number must be in range from 10 to " + maxmoney);
            }
        }

    }

    public void withdraw(UserInfo user) throws SQLException, ClassNotFoundException {
        Connection conn = DataConnection.getConnection();
        Statement stmt = conn.createStatement();

        ResultSet rS = stmt.executeQuery("Select * FROM settting WHERE id=1");
        rS.next();

        int maxmoney = rS.getInt(1);
        int msxcount_limit = rS.getInt(2);

        ResultSet rS1 = stmt.executeQuery("Select count(*) FROM user_withdraw WHERE user_id=" + user.user_id + " AND created_at LIKE '" + java.time.LocalDate.now() + "%'");
        rS1.next();

        int count = rS1.getInt(1);

        ResultSet rS2 = stmt.executeQuery("SELECT total_money FROM user_money WHERE user_id =" + user.user_id);
        rS2.next();

        long currentmoney = rS2.getInt(1);
        System.out.println("Your balance: " + currentmoney);

        if (count > msxcount_limit) {
            System.out.println("Sorry, you have reach the limit of withdrawal times today.");
        } else {
            System.out.println("Please enter the amount of money you want to take out: ");
            Scanner input = new Scanner(System.in);
            int money = input.nextInt();

            if (money >= 1 && money <= maxmoney) {
                stmt.executeUpdate("INSERT INTO user_withdraw (withdraw_money,user_id,created_at) VALUES (" + money + "," + user.user_id + ",'" + java.time.LocalDate.now() + "')");
                stmt.executeUpdate("UPDATE user_money SET total_money = " + (currentmoney - money) + " WHERE user_id = " + user.user_id);
                System.out.println("Withdrawal successfully!!!");
                System.out.println("Your current balance: " + (currentmoney - money));
            } else {
                System.out.println("Error!! Money withdrawn must between 10 " + "and " + maxmoney);
            }
        }
    }

    public void performBalanceEnquiry(UserInfo user) throws SQLException, ClassNotFoundException {

        Connection conn = DataConnection.getConnection();
        Statement stmt = conn.createStatement();

        ResultSet rS = stmt.executeQuery("SELECT name, gender, card_id, contact_number, address, total_money FROM users JOIN user_money on users.id=user_id WHERE users.id=" + user.user_id);
        rS.next();

        System.out.println("Name: " + rS.getString(1));
        if (rS.getInt(2) == 1) {
            System.out.println("Gender: Male");
        } else {
            System.out.println("Gender: Female");
        }
        System.out.println("Card ID: " + rS.getInt(3));
        System.out.println("Contact number: " + rS.getString(4));
        System.out.println("Address: " + rS.getString(5));
        System.out.println("Balance Enquiry: " + rS.getString(6));

    }

}

class adminMenu extends Menu {

// random from 10000000 to 10099999
    public int randomCardID() {
        Random rand = new Random(System.currentTimeMillis());
        return rand.nextInt(100000) + 10000000;
    }

    // random from 1000 to 9999
    public int randomPIN() {
        Random rand = new Random(System.currentTimeMillis());
        return rand.nextInt(9000) + 1000;
    }

    public void createAccount() throws SQLException, ClassNotFoundException {
        boolean check = true; // validate input
        int roleID = 2; // store input role ID for user (1 for Admin and 2 for User), by default it's user

        System.out.println("Do you want to create an account for an admin or a user?");
        // input and check role ID

        // loop until role ID is correctly inputted
        do {
            try {
                Scanner input = new Scanner(System.in);
                check = true; // by default input is valid

                System.out.print("Input your choice (1 - Admin or 2 - User): ");
                roleID = input.nextInt();
                input.nextLine();

                if (roleID != 1 && roleID != 2) {
                    check = false;
                    System.out.println("Please input 1 (Admin) or 2 (User) only! ");
                }

            } catch (InputMismatchException ex) {
                check = false;
                System.out.println("Please input an integer! ");
            } catch (Exception ex) {
                check = false;
                System.out.println("An error occured! Please try again later! ");
            }
        } while (!check);

        int cardID;
        Connection conn = DataConnection.getConnection();
        Statement stmt = conn.createStatement();

        // loop until card ID is unique
        do {
            cardID = randomCardID();
            ResultSet getCardID = stmt.executeQuery("SELECT * FROM users WHERE card_id = " + cardID);
            // if there is already such card ID in database, random again
            if (getCardID.next()) {
                check = false;
            }

        } while (!check);

        int PIN = randomPIN();

        String name = "";
        String contactNumber = "";
        int gender = 1;
        String address = "";

        /**
         * input and fill account basic details
         */
        System.out.println("Filling the new account's basic details: ");

        // input and check account's name
        System.out.print("Input the account's name: ");

        // loop until account's name is correctly inputted
        do {
            try {
                Scanner input = new Scanner(System.in);
                check = true; // by default input is valid
                name = input.nextLine();

            } catch (Exception ex) {
                check = false;
                System.out.println("An error occured! Please try again later! ");
            }
        } while (!check);

        // input and check account's contactNumber
        System.out.print("Input the account's contact number: ");

        // loop until account's contact number is correctly inputted
        do {
            try {
                Scanner input = new Scanner(System.in);
                check = true; // by default input is valid

                contactNumber = input.nextLine();

                // check if there is a character in string not a number
                for (int i = 0; i < contactNumber.length(); i++) {
                    if (contactNumber.charAt(i) < 48 || contactNumber.charAt(i) > 57) {
                        check = false;
                        System.out.println("Please input numbers only! ");
                        break;
                    }
                }

            } catch (Exception ex) {
                check = false;
                System.out.println("An error occured! Please try again later! ");
            }
        } while (!check);

        // input and check account's gender
        System.out.print("Input the account's gender (0 - Female or 1 - Male): ");

        // loop until account's gender is correctly inputted
        do {
            try {
                Scanner input = new Scanner(System.in);
                check = true; // by default input is valid

                gender = input.nextInt();
                input.nextLine();

                if (gender != 0 && gender != 1) {
                    check = false;
                    System.out.println("Please input 0 (female) or 1 (male)! ");
                }

            } catch (InputMismatchException ex) {
                check = false;
                System.out.println("Please input an integer! ");
            } catch (Exception ex) {
                check = false;
                System.out.println("An error occured! Please try again later! ");
            }
        } while (!check);

        // input and check account's address
        System.out.print("Input the account's address: ");

        // loop until account's address is correctly inputted
        do {
            try {
                Scanner input = new Scanner(System.in);
                check = true; // by default input is valid

                address = input.nextLine();

            } catch (Exception ex) {
                check = false;
                System.out.println("An error occured! Please try again later! ");
            }
        } while (!check);

        // SQL statements
        stmt.executeUpdate("INSERT INTO users(card_id, pin, contact_number, gender, address, name) VALUES(" + cardID + "," + PIN + ", \"" + contactNumber + "\" ," + gender + ", \"" + address + "\" , \"" + name + "\")");
        ResultSet insertInfo = stmt.executeQuery("SELECT id FROM users WHERE card_id = " + cardID);
        insertInfo.next();
        int userID = insertInfo.getInt(1);
        stmt.executeUpdate("INSERT INTO user_role(user_id, role_id) VALUES(" + userID + "," + roleID + ")");
        stmt.executeUpdate("INSERT INTO user_money(user_id, total_money) VALUES(" + userID + "," + 0 + ")");

    }

    /**
     * @param mode as operation mode (1 for changing deposit value and 2 for changing number of deposits)
     * @param depositLimit value of deposits to change
     * @param depositNumLimit value of number of deposits to change
     */
    public void changeDepositLimit(int mode, int depositLimit, int depositNumLimit) throws SQLException, ClassNotFoundException {
        Connection conn = DataConnection.getConnection();
        Statement stmt = conn.createStatement();
        if (mode == 1) {
            // set depositLimit in database to inputted deposit limit

            stmt.executeUpdate("UPDATE setting SET deposit_lim=" + depositLimit + " WHERE ID=1");

        } else {
            // set depositNumLimit in database to inputted number of deposits limit
            stmt.executeUpdate("UPDATE setting SET num_deposit_lim=" + depositNumLimit + " WHERE ID=1");

        }
    }

    public void changeDepositRelatedLimits() throws SQLException, ClassNotFoundException {
        int choice = 0; // store the choice
        boolean check = true; // validate if input is valid (by default input is valid)
        int depositLimit = 25000; // store deposit limit
        int depositNumLimit = 5; // store the limit of number of deposits

        // loop until "exit" is chosen
        do {
            System.out.println("What would you like to do?");
            System.out.println("1. Change deposit limit");
            System.out.println("2. Change the amount of deposits limit");
            System.out.println("3. Revert deposit limit to default value (25000$)");
            System.out.println("4. Revert the amount of deposits limit to default value (5 times)");
            System.out.println("5. Exit");
            System.out.print("Input your choice: ");

            // loop until choice is correctly inputted
            do {
                try {
                    Scanner input = new Scanner(System.in);
                    check = true; // by default input is valid

                    choice = input.nextInt();
                    input.nextLine();

                    if (choice < 1 || choice > 5) {
                        check = false;
                        System.out.println("Please input a number from 1 to 5 ");
                    }

                } catch (InputMismatchException ex) {
                    check = false;
                    System.out.println("Please input a number ");
                } catch (Exception ex) {
                    check = false;
                    System.out.println("An error occured! Please try again later!");
                }
            } while (!check);

            switch (choice) {
                case 1:
                    // loop until depositLimit is correctly inputted
                    do {
                        try {
                            Scanner input = new Scanner(System.in);
                            check = true; // by default input is valid

                            depositLimit = input.nextInt();
                            input.nextLine();

                            if (depositLimit < 25000) {
                                check = false;
                                System.out.println("Please input a number equal to or larger than 25000! ");
                            }

                        } catch (InputMismatchException ex) {
                            check = false;
                            System.out.println("Please input a number ");
                        } catch (Exception ex) {
                            check = false;
                            System.out.println("An error occured! Please try again later!");
                        }
                    } while (!check);

                    changeDepositLimit(1, depositLimit, 0);

                    break;

                case 2:
                    // loop until depositNumLimit is correctly inputted
                    do {
                        try {
                            Scanner input = new Scanner(System.in);
                            check = true; // by default input is valid

                            depositNumLimit = input.nextInt();
                            input.nextLine();

                            if (depositNumLimit < 5) {
                                check = false;
                                System.out.println("Please input a number equal to or larger than 5! ");
                            }

                        } catch (InputMismatchException ex) {
                            check = false;
                            System.out.println("Please input a number ");
                        } catch (Exception ex) {
                            check = false;
                            System.out.println("An error occured! Please try again later!");
                        }
                    } while (!check);

                    changeDepositLimit(2, 0, depositNumLimit);

                    break;

                case 3:
                    changeDepositLimit(1, 25000, 0);

                    break;

                case 4:
                    changeDepositLimit(2, 0, 5);

                    break;

                case 5:

            }
        } while (choice != 5);
    }

    /**
     * @param mode as operation mode (1 for changing deposit value and 2 for changing number of withdrawals)
     * @param withdrawLimit value of withdrawals to change
     * @param withdrawNumLimit value of number of withdrawals to change
     */
    public void changeWithdrawLimit(int mode, int withdrawLimit, int withdrawNumLimit) throws SQLException, ClassNotFoundException {
        Connection conn = DataConnection.getConnection();
        Statement stmt = conn.createStatement();

        if (mode == 1) {
            // set withdrawLimit in database to inputted withdrawal limit
            stmt.executeUpdate("UPDATE setting SET withdraw_lim=" + withdrawLimit + " WHERE ID=1");

        } else {
            // set withdrawNumLimit in database to inputted number of withdrawals limit
            stmt.executeUpdate("UPDATE setting SET num_withdraw_lim=" + withdrawNumLimit + " WHERE ID=1");

        }
    }

    public void changeWithdrawRelatedLimits() throws SQLException, ClassNotFoundException {
        int choice = 0;
        boolean check = true; // validate if input is valid (by default input is valid)
        int withdrawLimit = 25000; // store withdraw limit
        int withdrawNumLimit = 5; // store the limit of number of withdrawals

        // loop until "exit" is chosen
        do {
            System.out.println("What would you like to do?");
            System.out.println("1. Change withdraw limit");
            System.out.println("2. Change the amount of withdraws limit");
            System.out.println("3. Revert withdraw limit to default value (25000$)");
            System.out.println("4. Revert the amount of withdraws limit to default value (5 times)");
            System.out.println("5. Exit");
            System.out.print("Input your choice: ");

            // loop until choice is correctly inputted
            do {
                try {
                    Scanner input = new Scanner(System.in);
                    check = true; // by default input is valid

                    choice = input.nextInt();
                    input.nextLine();

                    if (choice < 1 || choice > 5) {
                        check = false;
                        System.out.println("Please input a number from 1 to 5! ");
                    }

                } catch (InputMismatchException ex) {
                    check = false;
                    System.out.println("Please input a number! ");
                } catch (Exception ex) {
                    check = false;
                    System.out.println("An error occured! Please try again later!");
                }
            } while (!check);

            switch (choice) {
                case 1:
                    // loop until withdrawLimit is correctly inputted
                    do {
                        try {
                            Scanner input = new Scanner(System.in);
                            check = true; // by default input is valid

                            withdrawLimit = input.nextInt();
                            input.nextLine();

                            if (withdrawLimit < 25000) {
                                check = false;
                                System.out.println("Please input a number equal to or larger than 25000! ");
                            }

                        } catch (InputMismatchException ex) {
                            check = false;
                            System.out.println("Please input a number ");
                        } catch (Exception ex) {
                            check = false;
                            System.out.println("An error occured! Please try again later!");
                        }
                    } while (!check);

                    changeWithdrawLimit(1, withdrawLimit, 0);

                    break;

                case 2:
                    // loop until withdrawNumLimit is correctly inputted
                    do {
                        try {
                            Scanner input = new Scanner(System.in);
                            check = true; // by default input is valid

                            withdrawNumLimit = input.nextInt();
                            input.nextLine();

                            if (withdrawNumLimit < 5) {
                                check = false;
                                System.out.println("Please input a number equal to or larger than 5! ");
                            }

                        } catch (InputMismatchException ex) {
                            check = false;
                            System.out.println("Please input a number ");
                        } catch (Exception ex) {
                            check = false;
                            System.out.println("An error occured! Please try again later!");
                        }
                    } while (!check);

                    changeWithdrawLimit(2, 0, withdrawNumLimit);

                    break;

                case 3:
                    changeWithdrawLimit(1, 25000, 0);

                    break;

                case 4:
                    changeWithdrawLimit(2, 0, 5);

                    break;

                case 5:

            }
        } while (choice != 5);
    }

    public void setTransCount() throws SQLException, ClassNotFoundException {
        int transCount = 5; // store number of transactions from input (default value: 5)
        boolean check = true; // validate input

        // loop until input is valid
        System.out.print("Input your choice: ");

        // loop until transCount is correctly inputted
        do {
            try {
                Scanner input = new Scanner(System.in);
                check = true; // by default input is valid

                transCount = input.nextInt();
                input.nextLine();

                if (transCount < 1) {
                    check = false;
                    System.out.println("The number of displayed transactions must be at least 1! ");
                }

            } catch (InputMismatchException ex) {
                check = false;
                System.out.println("Please input a number ");
            } catch (Exception ex) {
                check = false;
                System.out.println("An error occured! Please try again later!");
            }
        } while (!check);
        Connection conn = DataConnection.getConnection();
        Statement stmt = conn.createStatement();
        // code to set number of transactions to display (for balance enquiry method
        stmt.executeUpdate("UPDATE setting SET num_trans_display=" + transCount + " WHERE ID=1");

    }

    public void createDepositReport() {
        boolean check = false;
        System.out.println("Deposit Report: ");
        Scanner input = new Scanner(System.in);

        do {
            try {
                System.out.print("Enter Date:");
                String date = input.nextLine();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                Date inputDate = dateFormat.parse(date);
                check = true;
                Connection conn;
                try {
                    conn = DataConnection.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rS = stmt.executeQuery("SELECT  card_id, name, deposit_money, total_money  \n"
                            + "From user_deposit w \n"
                            + "	join users u \n"
                            + "		on w.user_id=u.id \n"
                            + "	JOIN user_money m \n"
                            + "		on w.user_id = m.user_id\n"
                            + " where created_at like '" + dateFormat.format(inputDate) + "%'");
                    int i;
                    System.out.println("Card_id    | Name               | Deposit Amount      | Balance      |");
                    while (rS.next()) {
                        i = 1;
                        System.out.print(rS.getString(i++) + "    ");
                        System.out.print(rS.getString(i++) + "       ");
                        System.out.print(rS.getString(i++) + "                  ");
                        System.out.println(rS.getString(i++));
                    }
                } catch (ClassNotFoundException | SQLException ex) {
                    check = false;
                    System.out.println("Cannot connect to database. ");
                }

            } catch (ParseException ex) {
                System.out.println("Please enter the right format!!!");
                check = false;
            }
        } while (!check);
    }

    public void createWithdrawReport() {
        boolean check = false;
        System.out.println("Withdrawal Report: ");
        Scanner input = new Scanner(System.in);

        do {
            try {
                System.out.print("Enter Date:");
                String date = input.nextLine();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                Date inputDate = dateFormat.parse(date);
                check = true;
                Connection conn;
                try {
                    conn = DataConnection.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rS = stmt.executeQuery("SELECT  card_id, name, user_withdraw, total_money  \n"
                            + "From user_withdraw w \n"
                            + "	join users u \n"
                            + "		on w.user_id=u.id \n"
                            + "	JOIN user_money m \n"
                            + "		on w.user_id = m.user_id\n"
                            + " where created_aat like '" + dateFormat.format(inputDate) + "%'");
                    int i;
                    System.out.println("Card_id    | Name               | Withdraw Amount      | Balance      |");
                    while (rS.next()) {
                        i = 1;
                        System.out.print(rS.getString(i++) + "    ");
                        System.out.print(rS.getString(i++) + "       ");
                        System.out.print(rS.getString(i++) + "                  ");
                        System.out.println(rS.getString(i++));
                    }
                } catch (ClassNotFoundException | SQLException ex) {
                    check = false;
                    System.out.println("Cannot connect to database. ");
                }

            } catch (ParseException ex) {
                System.out.println("Please enter the right format!!!");
                check = false;
            }
        } while (!check);

    }

    public void changeUserInfo() throws ClassNotFoundException, SQLException {
        int cardID = 0;
        boolean check = true; // by default input is valid
        int choice = 0;

        Connection conn = DataConnection.getConnection();
        Statement stmt = conn.createStatement();

        String name = "";
        String contactNumber = "";
        int gender = 1;
        String address = "";
        int pin = 0;
        char confirm = 'Y';
        String masterPasword = "";

        // loop until card ID exists in database
        // loop until card ID is valid
        do {
            System.out.print("Please input user's card ID: ");

            // loop until card ID is correctly inputted
            do {
                try {
                    Scanner input = new Scanner(System.in);
                    check = true; // by default input is valid

                    cardID = input.nextInt();
                    input.nextLine();

                    if (cardID < 10000000 || cardID > 10099999) {
                        check = false;
                        System.out.println("Card ID is from 10000000 to 10099999");
                    }

                } catch (InputMismatchException ex) {
                    check = false;
                    System.out.println("Please input user card ID as a number of 8 digits! ");
                } catch (Exception ex) {
                    check = false;
                    System.out.println("An error occured! Please try again later! ");
                }
            } while (!check);

            ResultSet rS = stmt.executeQuery("SELECT card_id,pin,name,contact_number,gender,address,role_id"
                    + " FROM users JOIN user_role on users.id=user_id where card_id = " + cardID);
            if (rS.next()) {
                if (rS.getInt(7) == 1) {
                    ResultSet rSmpass = stmt.executeQuery("SELECT master_password FROM setting WHERE id=1");
                    rSmpass.next();
                    // confirm if admin knows the master password
                    System.out.println("Do you really remember the master password?");
                    do {
                        try {
                            Scanner input = new Scanner(System.in);
                            check = true; // by default input is valid

                            confirm = input.next().charAt(0);
                            input.nextLine();

                            if (confirm != 'Y' && confirm != 'N') {
                                check = false;
                                System.out.println("Enter Y or N only! ");
                            }

                        } catch (InputMismatchException ex) {
                            check = false;
                            System.out.println("Please enter 1 character only ");
                        } catch (Exception ex) {
                            check = false;
                            System.out.println("An error occured! Please try again later! ");
                        }
                    } while (!check);

                    // run this only if user chooses 'Y'
                    if (confirm == 'Y') {
                        System.out.print("Please input master password: ");
                        // loop until inputted master password is valid
                            // loop until master password is inputted correctly
                            do {
                                try {
                                    Scanner input = new Scanner(System.in);
                                    check = true; // by default input is valid

                                    masterPasword = input.nextLine();
                                    input.nextLine();

                                    // if master password is not correct, request input again
                                    if (masterPasword.compareTo(rSmpass.getString(1)) != 0) {
                                        check = false;
                                        System.out.println("Master Password is incorrect, try again! ");
                                    }

                                } catch (InputMismatchException ex) {
                                    check = false;
                                    System.out.println("Please input master password as a number! ");
                                } catch (Exception ex) {
                                    check = false;
                                    System.out.println("An error occured! Please try again later! ");
                                }
                            } while (!check);
                    }
                }
                // assign current info to temp variables
                name = rS.getString(3);
                contactNumber = rS.getString(4);
                gender = rS.getInt(5);
                address = rS.getString(6);
                pin = rS.getInt(2);
                check = true;
            } else {
                System.out.println("Card ID not found!!");
                check = false;
            }

        } while (!check);
        // loop until exit is chosen
        do {
            System.out.println("Select one of following options to change for user: ");
            System.out.println("1. Change PIN");
            System.out.println("2. Change name ");
            System.out.println("3. Change contact number");
            System.out.println("4. Change gender");
            System.out.println("5. Change address");
            System.out.println("6. Exit");
            System.out.println("Please input your choice: ");

            do {
                try {
                    Scanner input = new Scanner(System.in);
                    check = true; // by default input is valid

                    choice = input.nextInt();
                    input.nextLine();

                    if (choice < 1 || choice > 6) {
                        check = false;
                        System.out.println("Please input a number from 1 to 6 ");
                    }

                } catch (InputMismatchException ex) {
                    check = false;
                    System.out.println("Please input a number ");
                } catch (Exception ex) {
                    check = false;
                    System.out.println("An error occured! Please try again later!");
                }
            } while (!check);

            switch (choice) {
                case 1:
                    System.out.println("Input new PIN: ");
                    // loop until PIN is correctly inputted
                    do {
                        try {
                            Scanner input = new Scanner(System.in);
                            check = true;

                            pin = input.nextInt();
                            input.nextLine();

                            if (pin < 1000 || pin > 9999) {
                                check = false;
                                System.out.println("PIN consists of 4 numbers! ");
                            }

                        } catch (InputMismatchException e) {
                            check = false;
                            System.out.println("Only numbers here !!");
                        } catch (Exception e) {
                            check = false;
                            System.out.println("An error occured! Please try again later! ");
                        }
                    } while (!check);
                    // SQL statements to update in database
                    stmt.executeUpdate("UPDATE users SET pin= " + pin + " WHERE card_id= " + cardID);
                    System.out.println("Change user's pin successfully!!");
                    break;

                case 2:
                    // input and check account's name
                    System.out.print("Input new name: ");

                    // loop until account's name is correctly inputted
                    do {
                        try {
                            Scanner input = new Scanner(System.in);
                            check = true; // by default input is valid
                            name = input.nextLine();
                            if (name.isEmpty()) {
                                check = false;
                            }
                        } catch (Exception ex) {
                            check = false;
                            System.out.println("An error occured! Please try again later! ");
                        }
                    } while (!check);
                    // SQL statements to update in database
                    stmt.executeUpdate("UPDATE users SET name = '" + name + "' WHERE card_id= " + cardID);
                    System.out.println("Change user's name successfully!!");
                    break;

                case 3:
                    // input and check account's contactNumber
                    System.out.print("Input new contact number: ");

                    // loop until account's contact number is correctly inputted
                    do {
                        try {
                            Scanner input = new Scanner(System.in);
                            check = true; // by default input is valid

                            contactNumber = input.nextLine();
                            if (contactNumber.isEmpty()) {
                                check = false;
                            }
                            // check if there is a character in string not a number
                            for (int i = 0; i < contactNumber.length(); i++) {
                                if (contactNumber.charAt(i) < 48 || contactNumber.charAt(i) > 57) {
                                    check = false;
                                    System.out.println("Please input numbers only! ");
                                    break;
                                }
                            }

                        } catch (Exception ex) {
                            check = false;
                            System.out.println("An error occured! Please try again later! ");
                        }
                    } while (!check);

                    // SQL statements to update in database
                    stmt.executeUpdate("UPDATE users SET contact_number= '" + contactNumber + "' WHERE card_id= " + cardID);
                    System.out.println("Change user's contact number successfully!!");
                    break;

                case 4:
                    // input and check account's gender
                    System.out.print("Input new gender (0 - Female or 1 - Male): ");

                    // loop until account's gender is correctly inputted
                    do {
                        try {
                            Scanner input = new Scanner(System.in);
                            check = true; // by default input is valid

                            gender = input.nextInt();
                            input.nextLine();

                            if (gender != 0 && gender != 1) {
                                check = false;
                                System.out.println("Please input 0 (female) or 1 (male)! ");
                            }

                        } catch (InputMismatchException ex) {
                            check = false;
                            System.out.println("Please input an integer! ");
                        } catch (Exception ex) {
                            check = false;
                            System.out.println("An error occured! Please try again later! ");
                        }
                    } while (!check);
                    // SQL statements to update in database
                    stmt.executeUpdate("UPDATE users SET gender= " + gender + " WHERE card_id= " + cardID);
                    System.out.println("Change user's gender successfully!!");
                    break;

                case 5:
                    // input and check account's address
                    System.out.print("Input new address: ");

                    // loop until account's address is correctly inputted
                    do {
                        try {
                            Scanner input = new Scanner(System.in);
                            check = true; // by default input is valid
                            address = input.nextLine();
                            if (address.isEmpty()) {
                                check = false;
                            }

                        } catch (Exception ex) {
                            check = false;
                            System.out.println("An error occured! Please try again later! ");
                        }
                    } while (!check);
                    stmt.executeUpdate("UPDATE users SET address= '" + address + "' WHERE card_id= " + cardID);
                    System.out.println("Change user's address successfully!!");
                    // SQL statements to update in database
                    break;

                case 6:
                    System.out.println("Exiting changing user info ...");
            }
        } while (choice != 6);

    }

    public void displayAdminMenu(UserInfo user) throws SQLException, ClassNotFoundException {
        int choice = 0;
        boolean check = true; // validate if input is valid (by default input is valid)

        do {
            System.out.println("------------ ADMINISTRATION ------------");
            System.out.println("1. Create new user account");
            System.out.println("2. Change deposit-related limitations");
            System.out.println("3. Change withdrawal-related limitations");
            System.out.println("4. Change the number of last transactions to display");
            System.out.println("5. Create deposit report");
            System.out.println("6. Create withdrawal report");
            System.out.println("7. Change user info");
            System.out.println("8. Change current account's password");
            System.out.println("9. Exit");
            System.out.print("Input your choice: ");

            // loop until choice is correctly inputted
            do {
                try {
                    Scanner input = new Scanner(System.in);
                    check = true; // by default input is valid

                    choice = input.nextInt();
                    input.nextLine();

                    if (choice < 1 || choice > 9) {
                        check = false;
                        System.out.println("Please input a number from 1 to 9 ");
                    }

                } catch (InputMismatchException ex) {
                    check = false;
                    System.out.println("Please input a number ");
                } catch (Exception ex) {
                    check = false;
                    System.out.println("An error occured! Please try again later!");
                }
            } while (!check);

            switch (choice) {
                case 1:
                    createAccount();

                    break;

                case 2:
                    changeDepositRelatedLimits();

                    break;

                case 3:
                    changeWithdrawRelatedLimits();

                    break;

                case 4:
                    setTransCount();

                    break;

                case 5:
                    createDepositReport();

                    break;

                case 6:
                    createWithdrawReport();

                    break;

                case 7:
                    changeUserInfo();

                    break;

                case 8:
                    changePassword(user);

                    break;

                case 9:
                    System.out.println("Exiting... Thank you for using our service!! ");

            }
        } while (choice != 9);
    }

}

public class ATM {

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        System.out.println("--------WIBU BANK----------");

        boolean check = true;

        // loop until login credentials are correct
        do {
            try {
                check = true;
                UserInfo user = Auth.loginUser();
                adminMenu admin_menu = new adminMenu();
                userMenu users_menu = new userMenu();

                switch (user.getRole_id()) {
                    case 1:
                        System.out.println("Admin login successfully!");
                        System.out.println("Hello " + user.getUser_name());
                        admin_menu.displayAdminMenu(user);
                        break;
                    case 2:
                        System.out.println("User login successfully!!");
                        System.out.println("Hello " + user.getUser_name());
                        users_menu.userMenu(user);
                        break;
                    case 0:
                        System.out.println("Card ID or PIN incorrect !!");
                        check = false;

                        break;
                }

            } catch (NullPointerException | SQLException ex) {
                System.out.println("Cannot connect to database.");
            } catch (InputMismatchException e) {
                System.out.println("Input numbers only!");
            }
        } while (!check);

    }

}
