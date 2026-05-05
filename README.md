# Kepler Car Rental Management System

Kepler is a Java Swing application for managing a multi-branch car rental agency. The project covers the required car rental workflow: vehicle inventory, reservation handling, pickup/return operations, invoice generation, loyalty tiers, and text-file persistence.

## How to Run

Compile:

```bash
javac -d out src/core/*.java src/gui/*.java
```

Run the GUI:

```bash
java -cp out gui.MainApp
```

Optional core smoke demo:

```bash
java -cp out core.Main
```

Note: the GUI and demo use the `.txt` files in the `data/` folder.

## Example Logins

The application uses a pseudo-login system. Enter an email address or full name.

| Role | Example Login |
| --- | --- |
| Customer | `ayse.demir@example.com` |
| Rental Agent | `ece.kaya@company.com` |
| Mechanic | `mert.aydin@company.com` |
| Branch Manager | `selin.arslan@company.com` |
| Guest | Use `Continue as Guest` |

## Required Features Mapping

### Vehicle Inventory by Type and Branch

Implemented with:

- `Vehicle` as the base class.
- `Economy`, `SUV`, `Luxury`, and `Van` as vehicle types.
- `Branch` stores branch vehicles.
- `FileManager` loads/saves vehicles from `data/vehicles.txt`.
- GUI support:
  - Guest vehicle search
  - Customer vehicle search
  - Branch manager fleet overview

Each vehicle stores:

- daily rate
- insurance option
- mileage policy
- current status
- current mileage
- branch assignment

### Reservation Creation and Management

Implemented with:

- `Customer.makeReservation(...)`
- `Reservation.confirmReservation()`
- `Reservation.cancelReservation()`
- `RentalAgent.processPickup(...)`
- `RentalAgent.processReturn(...)`
- `Vehicle.isAvailable(...)` for conflict checks

GUI support:

- Customer creates pending reservations.
- Rental agent confirms reservations.
- Rental agent processes pickup and return.
- Customer and rental agent can cancel valid reservations.
- Rented, maintenance, and out-of-service vehicles are not shown as available.

Custom exceptions used:

- `InvalidReservationException`
- `VehicleNotAvailableException`
- `BranchNotFoundException`

### Invoice Generation with Rate Calculations

Implemented with:

- `Invoice`
- `RentalContract`
- `Payment`
- `InsuranceOption`
- `MileagePolicy`
- `Addon`
- `DamageAssessment`

Invoice calculations include:

- base rental cost
- insurance cost
- addon cost
- loyalty discount
- damage fee
- extra kilometer charge
- deposit settlement
- refund or additional charge

GUI support:

- Rental agent creates invoices during return.
- Customer can view invoice/payment history.
- Rental agent can view invoice details and payment breakdown.
- Branch manager reports use paid invoice amounts for revenue.

### Loyalty Tier System

Implemented with:

- `LoyaltyTier`
  - `BRONZE`
  - `SILVER`
  - `GOLD`
- `Customer.earnPoints(...)`
- `LoyaltyTier.fromPoints(...)`

Rules:

- Customers earn points from paid invoice amounts.
- Tier updates automatically after points increase.
- Loyalty discount is applied to the base rental cost.

## Bonus Features

### Vehicle Maintenance Scheduling and Mechanic Assignment

Implemented with:

- `Mechanic`
- `MaintenanceTask`
- `MaintenanceStatus`
- `Vehicle.needsMaintenance()`

GUI support:

- Mechanic sees assigned maintenance queue.
- Mechanic schedules eligible vehicles for maintenance.
- Vehicle becomes `IN_MAINTENANCE` when a task is scheduled.
- Vehicle becomes `AVAILABLE` after maintenance completion.
- `lastMaintenanceMileage` is updated on completion.
- Vehicles that exceed their maintenance mileage become `OUT_OF_SERVICE`.

### Damage Assessment Workflow on Return

Implemented with:

- `DamageAssessment`
- `RentalAgent.assessDamage(...)`
- invoice damage fee integration

GUI support:

- Rental agent enters damage description and damage cost during return.
- Damage assessment is connected to vehicle, rental agent, and invoice.
- Vehicles with unresolved damage can be scheduled for maintenance.

## Data Persistence

The project uses `.txt` files under `data/`.

Main required files:

- `data/vehicles.txt`
- `data/reservations.txt`
- `data/customers.txt`
- `data/invoices.txt`

Additional files used by the full system:

- `data/branches.txt`
- `data/employees.txt`
- `data/addons.txt`
- `data/payments.txt`
- `data/rental_contracts.txt`
- `data/damage_assessments.txt`
- `data/maintenance_tasks.txt`
- `data/branch_reports.txt`

Persistence is handled by `FileManager`.

## Project Structure

```text
src/core/   Domain classes, enums, exceptions, file manager, smoke demo
src/gui/    Java Swing GUI panels and application state
data/       Text-file persistence data
out/        Compiled output
```

## Notes

- The project does not use a real password system. Login is based on email or full name to keep the focus on OOP workflows.
- The system is designed as a local desktop application.
- `data/` may change while testing because GUI actions are saved back to text files.
