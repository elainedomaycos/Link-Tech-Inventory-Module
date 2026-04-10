import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "select id, name, category, stock, unit_price, supplier_id, supplier_name from products order by id";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                products.add(new Product(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("category"),
                    resultSet.getInt("stock"),
                    resultSet.getDouble("unit_price"),
                    resultSet.getInt("supplier_id"),
                    resultSet.getString("supplier_name")
                ));
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load products from Supabase.", exception);
        }

        return products;
    }

    public Product insert(Product product) {
        String sql = "insert into products (name, category, stock, unit_price, supplier_id, supplier_name) values (?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setInt(3, product.getStock());
            statement.setDouble(4, product.getUnitPrice());
            statement.setInt(5, product.getSupplierId());
            statement.setString(6, product.getSupplierName());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    return new Product(generatedId, product.getName(), product.getCategory(), product.getStock(),
                        product.getUnitPrice(), product.getSupplierId(), product.getSupplierName());
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to insert product into Supabase.", exception);
        }

        throw new RuntimeException("Failed to insert product into Supabase: no id returned.");
    }

    public void update(Product product) {
        String sql = "update products set name=?, category=?, stock=?, unit_price=?, supplier_id=?, supplier_name=? where id=?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setInt(3, product.getStock());
            statement.setDouble(4, product.getUnitPrice());
            statement.setInt(5, product.getSupplierId());
            statement.setString(6, product.getSupplierName());
            statement.setInt(7, product.getId());
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to update product in Supabase.", exception);
        }
    }

    public void deleteById(int id) {
        String sql = "delete from products where id=?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to delete product from Supabase.", exception);
        }
    }
}
