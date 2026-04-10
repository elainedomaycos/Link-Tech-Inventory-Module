import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderRepository {

    public List<PurchaseOrder> findAll() {
        List<PurchaseOrder> orders = new ArrayList<>();
        String sql = "select id, supplier_name, quantity, unit_price, status, order_date from purchase_orders order by id";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                orders.add(new PurchaseOrder(
                    resultSet.getInt("id"),
                    resultSet.getString("supplier_name"),
                    resultSet.getInt("quantity"),
                    resultSet.getDouble("unit_price"),
                    resultSet.getString("status"),
                    resultSet.getDate("order_date").toLocalDate()
                ));
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load purchase orders from Supabase.", exception);
        }

        return orders;
    }

    public PurchaseOrder insert(PurchaseOrder order) {
        String sql = "insert into purchase_orders (supplier_name, quantity, unit_price, status, order_date) values (?, ?, ?, ?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, order.getSupplier());
            statement.setInt(2, order.getQuantity());
            statement.setDouble(3, order.getUnitPrice());
            statement.setString(4, order.getStatus());
            statement.setDate(5, Date.valueOf(order.getOrderDate()));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    return new PurchaseOrder(generatedId, order.getSupplier(), order.getQuantity(),
                        order.getUnitPrice(), order.getStatus(), order.getOrderDate());
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to insert purchase order into Supabase.", exception);
        }

        throw new RuntimeException("Failed to insert purchase order into Supabase: no id returned.");
    }

    public void update(PurchaseOrder order) {
        String sql = "update purchase_orders set supplier_name=?, quantity=?, unit_price=?, status=?, order_date=? where id=?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, order.getSupplier());
            statement.setInt(2, order.getQuantity());
            statement.setDouble(3, order.getUnitPrice());
            statement.setString(4, order.getStatus());
            statement.setDate(5, Date.valueOf(order.getOrderDate()));
            statement.setInt(6, order.getOrderId());
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to update purchase order in Supabase.", exception);
        }
    }

    public void deleteById(int id) {
        String sql = "delete from purchase_orders where id=?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to delete purchase order from Supabase.", exception);
        }
    }
}
