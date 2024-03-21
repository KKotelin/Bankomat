import java.sql.*;
import java.util.Random;
import java.util.Scanner;
import java.sql.SQLException;
public class Operation {
    public  static Scanner scanner = new Scanner(System.in);
    public static void getBalance() {

        System.out.println("Введите номер вашей карты: ");
        String cardNumber = scanner.nextLine();

        try (Connection conn = DataBase.connection()) {
            String selectBalance = "SELECT balance FROM Balance WHERE card_number = ?";
            try (PreparedStatement balanceStatement = conn.prepareStatement(selectBalance)) {
                balanceStatement.setString(1, cardNumber);
                try (ResultSet resultSet = balanceStatement.executeQuery()) {
                    if (resultSet.next()) {
                        double balance = resultSet.getDouble("balance");
                        System.out.println("Баланс на карте " + cardNumber + ": " + balance);
                    } else {
                        System.out.println("Карта " + cardNumber + " не найдена");
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Ошибка при выполнении запроса: " + e.getMessage());
        }
    }

    public static void deposit() {
        System.out.print("Введите сумму для пополнения вашего счета: ");
        int balance = scanner.nextInt();
        String cardNumber = Account.card_number;
        try (Connection conn = DataBase.connection()) {
            String updateBalance = "Update Balance SET balance = ? where card_number = ?";
            try (PreparedStatement depositStatement = conn.prepareStatement(updateBalance)) {
                depositStatement.setInt(1, balance);
                depositStatement.setString(2, cardNumber);
                depositStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка при выполнении запроса к базе данных", e);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка при подключении к базе данных", e);
        }
    }
    public static void depositOther(){
        System.out.print("Введите номер карты, на которую вы хотите перевести денежные средства: ");
        String card_number = scanner.nextLine();
        System.out.print("Введите сумму: ");
        String dep = scanner.nextLine();

        try (Connection conn = DataBase.connection()) {
            String updateBalanceOther = "Update Balance SET balance = ? where card_number = ?";
            try (PreparedStatement depositOtherStatement = conn.prepareStatement(updateBalanceOther)) {
                depositOtherStatement.setString(1, dep);
                depositOtherStatement.setString(2, card_number);
                depositOtherStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка при выполнении запроса к базе данных", e);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка при подключении к базе данных", e);
        }




    }
}
