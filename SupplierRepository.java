import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupplierRepository {

    public List<Supplier> findAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "select id, name, category, email, phone, address, rating from suppliers order by id";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                suppliers.add(new Supplier(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("category"),
                    resultSet.getString("email"),
                    resultSet.getString("phone"),
                    resultSet.getString("address"),
                    resultSet.getString("rating")
                ));
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load suppliers from Supabase.", exception);
        }

        return suppliers;
    }

    public Supplier insert(Supplier supplier) {
        String sql = "insert into suppliers (name, category, email, phone, address, rating) values (?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getCategory());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getPhone());
            statement.setString(5, supplier.getAddress());
            statement.setString(6, supplier.getRating());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    return new Supplier(generatedId, supplier.getName(), supplier.getCategory(), supplier.getEmail(),
                        supplier.getPhone(), supplier.getAddress(), supplier.getRating());
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to insert supplier into Supabase.", exception);
        }

        throw new RuntimeException("Failed to insert supplier into Supabase: no id returned.");
    }

    public void update(Supplier supplier) {
        String sql = "update suppliers set name=?, category=?, email=?, phone=?, address=?, rating=? where id=?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getCategory());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getPhone());
            statement.setString(5, supplier.getAddress());
            statement.setString(6, supplier.getRating());
            statement.setInt(7, supplier.getId());
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to update supplier in Supabase.", exception);
        }
    }

    public void deleteById(int id) {
        String sql = "delete from suppliers where id=?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to delete supplier from Supabase.", exception);
        }
    }
}
