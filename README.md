# LinkTech Inventory Module

Desktop inventory system built with Java Swing and FlatLaf.

## Features
- Inventory dashboard with product CRUD, search, category filter, and pagination
- Supplier management with CRUD and category filtering
- Purchase order management with supplier dropdown and status filtering
- Analytics panel with summary cards and charts
- Login screen with remember-me and lockout handling

## Requirements
- JDK 8+ available on `PATH` (`javac` and `javaw`)
- PowerShell 5.1+ (for the helper script)
- Supabase Postgres password set in environment variable `SUPABASE_DB_PASSWORD`

## Run (PowerShell)

```powershell
Set-Location "d:\Inventory Module"
$env:SUPABASE_DB_PASSWORD = "<your-supabase-db-password>"
.\scripts\run-inventory.ps1
```

Alternative (recommended for local dev): create `d:\Inventory Module\.env.local` with:

```text
SUPABASE_DB_PASSWORD=<your-supabase-db-password>
# Optional overrides (use Supabase pooler host if direct host fails)
# SUPABASE_DB_HOST=<your-supabase-db-host-or-pooler-host>
# SUPABASE_DB_PORT=5432
# SUPABASE_DB_NAME=postgres
# SUPABASE_DB_USER=postgres
# SUPABASE_DB_URL=jdbc:postgresql://<host>:<port>/<db>?sslmode=require
```

Then run:

```powershell
Set-Location "d:\Inventory Module"
.\scripts\run-inventory.ps1
```

What the script does:
- Downloads FlatLaf dependencies into `lib\` when missing
- Downloads PostgreSQL JDBC driver into `lib\` when missing
- Compiles `*.java` files into `out\`
- Starts the app via `javaw`

## Supabase Configuration

This app is configured for:
- Host: `db.chiqcjhtndeamefkfcjp.supabase.co`
- Port: `5432`
- Database: `postgres`
- User: `postgres`

On startup, the app automatically creates required tables if they do not exist:
- `products`
- `suppliers`
- `purchase_orders`
- `app_users`
- `app_preferences`

Default seeded users (first run):
- `admin` / `admin123`
- `manager` / `manager123`
- `staff` / `staff123`

## Notes on Java Runtime
- The repository is intentionally source-only (no bundled JDK/JRE committed).
- If you keep a local bundled JDK folder named `java-1.8.0-openjdk-1.8.0.482.b08-1.win.jdk.x86_64`,
  the script will use it first; otherwise it falls back to Java on `PATH`.

## Main Files
- `Main.java`
- `MainFrame.java`
- `LoginFrame.java`
- `InventoryPanel.java`
- `SupplierPanel.java`
- `PurchaseOrderPanel.java`
- `AnalyticsPanel.java`
