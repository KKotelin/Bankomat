import java.sql.*;
import java.util.Random;
import java.util.Scanner;
import java.sql.ResultSet;

public class Account {
    public static String first_name;
    public static String last_name;
    public static String card_number;
    public static String pin_code;
    public static int balance = 0;
    public static Scanner scanner = new Scanner(System.in);

    //Логика программы
    public static void startBankomat() {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("Добро пожаловать!");
            System.out.println("Зарегистрируйтесь или войдите в систему:");
            System.out.println("1. Регистрация");
            System.out.println("2. Вход");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    try {
                        loginUser();
                        menu();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, повторите.");
                    break;
            }
            System.out.println("Хотите выполнить еще одну операцию? (yes/no)");
            String answer = scanner.nextLine();
            if (!answer.equalsIgnoreCase("yes")) {
                isRunning = false;
            }
        }
    }

    public static void menu(){
        System.out.println();
        System.out.println(" - - - - - - - - - - -");
        System.out.println("Выберете операцию:");
        System.out.println("1 - Проверка баланса");
        System.out.println("2 - Зачисление денежных средств");
        System.out.println("3 - Перевод денежных средств клиенту Банка");
        int choice_2 = scanner.nextInt();
        if (choice_2 == 1) {
            Operation.getBalance();
        } else if (choice_2 == 2) {
            Operation.deposit();
        } else if (choice_2 == 3) {
            Operation.depositOther();
        }
    }

    // Генерируем 6 случайных цифр для номера карты
    public static String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumberBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            cardNumberBuilder.append(digit);
        }
        return cardNumberBuilder.toString();
    }

    // Метод для генерации случайного PIN-кода

    public static String generatePinCode() {
        Random random = new Random();
        StringBuilder pinCodeBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int digit = random.nextInt(10);
            pinCodeBuilder.append(digit);
        }
        return pinCodeBuilder.toString();
    }

    //Регистрация пользователя и запись его в БД
    public static void registerUser() {

        System.out.print("Введите имя пользователя: ");
        first_name = scanner.nextLine();

        System.out.print("Введите фамилию пользователя: ");
        last_name = scanner.nextLine();

        card_number = generateCardNumber();
        pin_code = generatePinCode();
        System.out.println("Здраввствуйте " + first_name + " " + last_name + ". Ваш аккакунт успешно создан!");
        System.out.println("Номер карты: " + card_number);
        System.out.println("PIN-код карты: " + pin_code);

        try (java.sql.Connection conn = DataBase.connection()) {

            //Записываем данные в таблицу Account
            String insertAccountQuery = "INSERT INTO Account (first_name, last_name) VALUES (?, ?)";
            try (PreparedStatement accountStatement = conn.prepareStatement(insertAccountQuery, Statement.RETURN_GENERATED_KEYS)) {
                accountStatement.setString(1, first_name);
                accountStatement.setString(2, last_name);
                accountStatement.executeUpdate();


                try (ResultSet generatedKeys = accountStatement.getGeneratedKeys()) {
                    int account_id = -1;
                    if (generatedKeys.next()) {
                        account_id = generatedKeys.getInt(1);
                    }

                    //Записываем данные в таблицу Card
                    String insertCardNumber = "INSERT INTO Card (card_number, pin_code, account_id) VALUES (?, ?, ?)";
                    try (PreparedStatement cardStatement = conn.prepareStatement(insertCardNumber)) {
                        cardStatement.setString(1, card_number);
                        cardStatement.setString(2, pin_code);
                        cardStatement.setInt(3, account_id);
                        cardStatement.executeUpdate();

                        String insertBalance = "INSERT INTO Balance (card_number, balance) VALUES (?, ?)";
                        try (PreparedStatement balanceStatement = conn.prepareStatement(insertBalance)) {
                            balanceStatement.setString(1, card_number);
                            balanceStatement.setInt(2, balance);
                            balanceStatement.executeUpdate();

                        }
//


                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Поиск пользователя по номеру карты и пин коду
    public static String loginUser() throws SQLException {
        System.out.println("Введите номер карты: ");
        card_number = scanner.nextLine();
        System.out.print("Введите PIN-код: ");
        pin_code = scanner.nextLine();

        try (java.sql.Connection conn = DataBase.connection()) {
            String findCardNumber = "SELECT a.first_name, a.last_name FROM Card as c JOIN Account as a ON c.account_id = a.id WHERE c.card_number = ? AND c.pin_code = ?";

            try (PreparedStatement findCardStatement = conn.prepareStatement(findCardNumber)) {
                findCardStatement.setString(1, card_number);
                findCardStatement.setString(2, pin_code);
                try (ResultSet resultSet = findCardStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        System.out.println("Приветствую, " + firstName + " " + lastName + "!");
                        return "Приветствую, " + firstName + " " + lastName + "!";
                    } else {
                        System.out.println("Пользователь не найден.");
                        return null; // Пользователь не найден
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null; // Обработка ошибки SQL
        }
    }
}









