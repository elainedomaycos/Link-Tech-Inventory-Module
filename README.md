# LinkTech ERP – Modern Inventory Module

A modernized Java Swing inventory management UI using FlatLaf.

## Features
- Dark navy sidebar with active nav indicator
- Summary stat cards (total products, low stock, value, suppliers)
- 3-column product entry form
- Searchable, filterable product table
- Color-coded category pills
- Mini stock progress bars with color thresholds
- Inline edit/delete per row
- Low-stock alert dialog

## Requirements
- Java 17+
- Maven 3.6+

## Quick Start

```bash
# 1. Clone / place project
cd InventoryApp

# 2. Build fat JAR (includes FlatLaf)
mvn clean package

# 3. Run
java -jar dist/linktech-erp-inventory.jar
```

## Project Structure

```
src/
  Main.java                  # Entry point, FlatLaf setup
  MainFrame.java             # App window, sidebar + content area
  SidebarPanel.java          # Dark nav sidebar
  InventoryPanel.java        # Full inventory module
  InventoryTableModel.java   # Table model (add/update/delete/filter)
  TableRenderers.java        # Category pills, stock bars, action buttons
  UIComponents.java          # Reusable: RoundedPanel, PillLabel, buttons, inputs
  AppColors.java             # All color constants
  Product.java               # Data model
pom.xml
```

## Customization

### Colors
All colors live in `AppColors.java`. Change `ACCENT` to switch the primary action color.

### Connecting to a Database
Replace the `sampleData()` method in `InventoryPanel.java` with a JDBC call:

```java
private List<Product> sampleData() {
    List<Product> list = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM products");
        while (rs.next()) {
            list.add(new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("stock"),
                rs.getDouble("unit_price"),
                rs.getInt("supplier_id"),
                rs.getString("supplier_name")
            ));
        }
    }
    return list;
}
```
