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

## Run (PowerShell)

```powershell
Set-Location "d:\Inventory Module"
.\scripts\run-inventory.ps1
```

What the script does:
- Downloads FlatLaf dependencies into `lib\` when missing
- Compiles `*.java` files into `out\`
- Starts the app via `javaw`

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
