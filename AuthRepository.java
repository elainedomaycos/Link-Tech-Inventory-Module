import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthRepository {

    public String authenticate(String username, String password) {
        String sql = "select role from app_users where lower(username) = lower(?) and password = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("role");
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to authenticate against Supabase.", exception);
        }

        return null;
    }

    public RememberedLogin loadRememberedLogin() {
        String sql = "select remember_me, remembered_username from app_preferences where id = 1";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return new RememberedLogin(
                    resultSet.getBoolean("remember_me"),
                    resultSet.getString("remembered_username")
                );
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load remembered login from Supabase.", exception);
        }

        return new RememberedLogin(false, "");
    }

    public void saveRememberedLogin(boolean rememberMe, String username) {
        String sql = "update app_preferences set remember_me=?, remembered_username=? where id = 1";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, rememberMe);
            statement.setString(2, rememberMe ? username : null);
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to save remembered login to Supabase.", exception);
        }
    }

    public static final class RememberedLogin {
        private final boolean rememberMe;
        private final String username;

        public RememberedLogin(boolean rememberMe, String username) {
            this.rememberMe = rememberMe;
            this.username = username == null ? "" : username;
        }

        public boolean isRememberMe() {
            return rememberMe;
        }

        public String getUsername() {
            return username;
        }
    }
}
