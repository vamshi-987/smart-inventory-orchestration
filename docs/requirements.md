# Smart Inventory & Order Orchestration System - Requirements

## Problem

Businesses with multiple warehouses need to decide which warehouse should fulfill an order.

The system should solve:

- Which warehouse should fulfill the order?
- How to avoid overselling?
- How to reduce delivery distance?
- How to track stock per warehouse?
- How to send low-stock alerts?

## Core Features

1. Product management
2. Warehouse management
3. Inventory management
4. Customer location handling
5. Order placement
6. Warehouse allocation engine
7. Low-stock notifications
8. Admin dashboard
9. React frontend
10. Docker setup

## Allocation Modes

### Version 1

City and pincode-based warehouse selection.

### Version 2

GPS-based nearest warehouse selection.

## Final Allocation Rule

1. Check same pincode warehouse with stock
2. If not available, check same city warehouse with stock
3. If GPS is provided, find nearest warehouse within service radius
4. If no warehouse found, reject order