# Warehouse Allocation Logic

## Goal

Allocate the best warehouse for an order based on stock and location.

## Final Rule

1. Check same pincode warehouse with stock
2. If unavailable, check same city warehouse with stock
3. If GPS is provided, find nearest warehouse within service radius
4. If no warehouse is found, reject order

## Example

Customer:

- City: Hyderabad
- Pincode: 500081
- Latitude: 17.4483
- Longitude: 78.3915

Product:

- Product ID: 1
- Quantity: 2

Warehouses:

| Warehouse | City | Pincode | Stock | Distance |
|----------|------|---------|-------|----------|
| WH-A | Hyderabad | 500081 | 0 | 2 km |
| WH-B | Hyderabad | 500032 | 10 | 6 km |
| WH-C | Bangalore | 560037 | 100 | 570 km |

Decision:

WH-A rejected because stock is unavailable.
WH-B selected because it is in the same city and has stock.
WH-C ignored because it is outside the preferred service area.

## Allocation Flow

Order request
↓
Check same pincode warehouses
↓
Check stock
↓
If not found, check same city warehouses
↓
If GPS is available, calculate nearest warehouse
↓
Check service radius
↓
Allocate warehouse
↓
Reduce stock
↓
Create order