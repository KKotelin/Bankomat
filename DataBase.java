import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static final String dbClassName = "com.mysql.cj.jdbc.Driver"; // Драйвер базы данных
    private static final String CONNECTION = "jdbc:mysql://localhost:3306/bankomat"; // Имя базы данных
    private static final String USER = "root"; // Имя базы данных
    private static final String PASSWORD = "1234"; // Имя базы данных

    public static java.sql.Connection connection() throws SQLException, ClassNotFoundException {
        Class.forName(dbClassName);
        return DriverManager.getConnection(CONNECTION, USER, PASSWORD);
    }
}
