#!/usr/bin/env python3
import argparse
import random
from datetime import date, timedelta
from pathlib import Path


BRANCHES = [
    (1, "Bornova Branch", "Bornova, Izmir"),
    (2, "Karsiyaka Branch", "Karsiyaka, Izmir"),
    (3, "Konak Branch", "Konak, Izmir"),
    (4, "Buca Branch", "Buca, Izmir"),
    (5, "Gaziemir Airport Branch", "Gaziemir, Izmir"),
]

ADDONS = [
    (1, "Child Seat", "Daily child seat add-on", 7.5),
    (2, "GPS", "Navigation device", 5.0),
    (3, "Additional Driver", "Extra authorized driver", 12.5),
    (4, "Snow Chains", "Winter road equipment", 6.0),
    (5, "Wifi Hotspot", "Portable internet device", 8.0),
]

CUSTOMER_NAMES = [
    ("Ayse", "Demir"),
    ("Mehmet", "Yilmaz"),
    ("Zeynep", "Kaya"),
    ("Emre", "Celik"),
    ("Elif", "Sahin"),
    ("Can", "Aydin"),
    ("Deniz", "Arslan"),
    ("Mert", "Ozturk"),
    ("Selin", "Koc"),
    ("Burak", "Aslan"),
    ("Ece", "Yildiz"),
    ("Kerem", "Aksoy"),
    ("Duru", "Polat"),
    ("Berk", "Kara"),
    ("Irem", "Erdem"),
    ("Arda", "Gunes"),
]

EMPLOYEES = [
    (10, "RENTAL_AGENT", 1010, "Ece", "Kaya", "ece.kaya@company.com", 35000.0, 1, 0),
    (11, "RENTAL_AGENT", 1011, "Ozan", "Polat", "ozan.polat@company.com", 36000.0, 2, 0),
    (12, "RENTAL_AGENT", 1012, "Melis", "Kurt", "melis.kurt@company.com", 35500.0, 3, 0),
    (13, "RENTAL_AGENT", 1013, "Bora", "Eren", "bora.eren@company.com", 35250.0, 4, 0),
    (14, "RENTAL_AGENT", 1014, "Ceren", "Tas", "ceren.tas@company.com", 37000.0, 5, 0),
    (20, "MECHANIC", 1020, "Mert", "Aydin", "mert.aydin@company.com", 32000.0, 1, 0),
    (21, "MECHANIC", 1021, "Ali", "Yildiz", "ali.yildiz@company.com", 33000.0, 2, 0),
    (22, "MECHANIC", 1022, "Onur", "Sari", "onur.sari@company.com", 32500.0, 3, 0),
    (23, "MECHANIC", 1023, "Yigit", "Kaplan", "yigit.kaplan@company.com", 32800.0, 4, 0),
    (24, "MECHANIC", 1024, "Ege", "Bozkurt", "ege.bozkurt@company.com", 34000.0, 5, 0),
    (30, "BRANCH_MANAGER", 1030, "Selin", "Arslan", "selin.arslan@company.com", 50000.0, 1, 1),
    (31, "BRANCH_MANAGER", 1031, "Derya", "Celik", "derya.celik@company.com", 51000.0, 2, 2),
    (32, "BRANCH_MANAGER", 1032, "Hakan", "Ozkan", "hakan.ozkan@company.com", 50500.0, 3, 3),
    (33, "BRANCH_MANAGER", 1033, "Buse", "Acar", "buse.acar@company.com", 49500.0, 4, 4),
    (34, "BRANCH_MANAGER", 1034, "Tolga", "Dogan", "tolga.dogan@company.com", 52500.0, 5, 5),
]

VEHICLE_MODELS = {
    "ECONOMY": [
        ("Fiat", "Egea", 42.0),
        ("Renault", "Clio", 40.0),
        ("Hyundai", "i20", 38.0),
        ("Toyota", "Yaris", 44.0),
    ],
    "SUV": [
        ("Toyota", "RAV4", 82.0),
        ("Nissan", "Qashqai", 76.0),
        ("Peugeot", "3008", 78.0),
        ("Hyundai", "Tucson", 80.0),
    ],
    "VAN": [
        ("Ford", "Transit", 70.0),
        ("Mercedes", "Vito", 92.0),
        ("Volkswagen", "Transporter", 88.0),
        ("Renault", "Trafic", 72.0),
    ],
    "LUXURY": [
        ("BMW", "520i", 130.0),
        ("Mercedes", "E200", 145.0),
        ("Audi", "A6", 138.0),
        ("Volvo", "S90", 125.0),
    ],
}

INSURANCE_DAILY_FEES = {
    "BASIC": 10.0,
    "STANDARD": 20.0,
    "PREMIUM": 35.0,
}

MILEAGE_POLICIES = ["LIMITED", "STANDARD", "UNLIMITED"]
VEHICLE_STATUSES = [
    "AVAILABLE",
    "AVAILABLE",
    "AVAILABLE",
    "AVAILABLE",
    "IN_MAINTENANCE",
    "OUT_OF_SERVICE",
]
OPEN_RESERVATION_STATUSES = ["CONFIRMED", "PENDING", "CANCELLED"]
DAMAGE_DESCRIPTIONS = [
    "Small bumper scratch",
    "Door paint mark",
    "Mirror cover damage",
    "Interior cleaning required",
    "Wheel rim scratch",
]


def fmt_day(value):
    if value is None:
        return ""
    return value.isoformat()


def loyalty_tier_for_points(points):
    if points >= 1500:
        return "GOLD"
    if points >= 500:
        return "SILVER"
    return "BRONZE"


def discount_rate_for_tier(tier):
    if tier == "GOLD":
        return 0.10
    if tier == "SILVER":
        return 0.05
    return 0.0


def generate_plate(index):
    letters = ["ABC", "DEF", "GHI", "JKL", "MNO", "PRS", "TUV", "YZK"]
    return f"35{letters[index % len(letters)]}{index + 1:02d}"


def generate_customers(count):
    customers = []
    used_emails = set()
    for index in range(count):
        name, surname = CUSTOMER_NAMES[index % len(CUSTOMER_NAMES)]
        sequence = index // len(CUSTOMER_NAMES)
        display_name = name if sequence == 0 else f"{name}{sequence + 1}"
        email = f"{display_name.lower()}.{surname.lower()}@example.com"
        while email in used_emails:
            sequence += 1
            display_name = f"{name}{sequence + 1}"
            email = f"{display_name.lower()}.{surname.lower()}@example.com"

        loyalty_points = random.randint(0, 2200)
        loyalty_tier = loyalty_tier_for_points(loyalty_points)
        customer = {
            "id": index + 1,
            "name": display_name,
            "surname": surname,
            "email": email,
            "points": loyalty_points,
            "tier": loyalty_tier,
        }
        used_emails.add(email)
        customers.append(customer)
    return customers


def generate_vehicles(count):
    vehicles = []
    vehicle_types = list(VEHICLE_MODELS.keys())
    insurance_options = list(INSURANCE_DAILY_FEES.keys())
    for index in range(count):
        vehicle_id = 101 + index
        vehicle_type = vehicle_types[index % len(vehicle_types)]
        brand, model, base_rate = random.choice(VEHICLE_MODELS[vehicle_type])
        status = random.choice(VEHICLE_STATUSES)
        if index < 2:
            status = "AVAILABLE"

        current_mileage = random.randint(5000, 90000)
        maintenance_interval = random.choice([5000, 8000, 10000, 12000])
        last_maintenance_mileage = max(
            0, current_mileage - random.randint(0, maintenance_interval + 3000)
        )
        vehicles.append({
            "id": vehicle_id,
            "type": vehicle_type,
            "plate": generate_plate(index),
            "brand": brand,
            "model": model,
            "year": random.randint(2019, 2026),
            "daily_rate": round(base_rate + random.randint(-5, 10), 1),
            "insurance": random.choice(insurance_options),
            "mileage_policy": random.choice(MILEAGE_POLICIES),
            "status": status,
            "current_mileage": current_mileage,
            "maintenance_interval": maintenance_interval,
            "last_maintenance_mileage": last_maintenance_mileage,
            "branch_id": BRANCHES[(index // 2) % len(BRANCHES)][0],
        })
    return vehicles


def addon_fee(addon_ids, days):
    prices = {addon_id: daily_price for addon_id, _, _, daily_price in ADDONS}
    return sum(prices[addon_id] * days for addon_id in addon_ids)


def rental_base(vehicle, days):
    return (vehicle["daily_rate"] + INSURANCE_DAILY_FEES[vehicle["insurance"]]) * days


def generate_reservations(customers, vehicles, count):
    reservations = []
    usable_vehicles = vehicles[4:] if len(vehicles) > 4 else vehicles
    start_base = date(2026, 5, 1)

    for index in range(count):
        customer = customers[index % len(customers)]
        vehicle = usable_vehicles[index % len(usable_vehicles)]
        start = start_base + timedelta(days=index * 2)
        days = random.randint(2, 7)
        end = start + timedelta(days=days)
        status = OPEN_RESERVATION_STATUSES[index % len(OPEN_RESERVATION_STATUSES)]
        addon_ids = [ADDONS[index % len(ADDONS)][0]]
        if index % 3 == 0:
            addon_ids.append(ADDONS[(index + 1) % len(ADDONS)][0])

        reservations.append({
            "id": index + 1,
            "customer_id": customer["id"],
            "vehicle_id": vehicle["id"],
            "start": start,
            "end": end,
            "status": status,
            "prepayment": round(random.choice([80.0, 100.0, 120.0, 150.0]), 1),
            "deposit": round(random.choice([250.0, 300.0, 350.0]), 1),
            "prepayment_id": 0,
            "contract_id": 0,
            "addon_ids": addon_ids,
            "days": days,
            "vehicle": vehicle,
            "customer": customer,
        })
    return reservations


def generate_business_data(customers, vehicles, reservation_count, contract_count):
    reservations = generate_reservations(customers, vehicles, reservation_count)
    payments = []
    contracts = []
    assessments = []
    invoices = []
    next_payment_id = 1
    next_assessment_id = 1
    next_invoice_id = 1

    rental_agents = [employee[0] for employee in EMPLOYEES if employee[1] == "RENTAL_AGENT"]
    closed_contract_count = max(1, int(contract_count * 0.78))

    for index, reservation in enumerate(reservations[:contract_count]):
        contract_id = index + 1
        is_closed = index < closed_contract_count
        reservation["status"] = "COMPLETED" if is_closed else "CONFIRMED"
        actual_return = reservation["end"] if is_closed else None
        initial_mileage = reservation["vehicle"]["current_mileage"]
        final_mileage = initial_mileage + random.randint(120, 900) if is_closed else 0
        status = "CLOSED" if is_closed else "ACTIVE"
        pickup_payment_id = next_payment_id
        next_payment_id += 1

        reservation["contract_id"] = contract_id
        if not is_closed:
            reservation["vehicle"]["status"] = "RENTED"

        payments.append({
            "id": pickup_payment_id,
            "amount": reservation["deposit"],
            "date": reservation["start"],
            "purpose": "DEPOSIT",
            "customer_id": reservation["customer_id"],
            "reservation_id": 0,
            "contract_id": contract_id,
            "invoice_id": 0,
        })

        invoice_id = 0
        if is_closed:
            invoice_id = next_invoice_id
            next_invoice_id += 1

        contract = {
            "id": contract_id,
            "reservation_id": reservation["id"],
            "rental_agent_id": rental_agents[index % len(rental_agents)],
            "pickup": reservation["start"],
            "expected_return": reservation["end"],
            "actual_return": actual_return,
            "deposit": reservation["deposit"],
            "initial_mileage": initial_mileage,
            "final_mileage": final_mileage,
            "status": status,
            "invoice_id": invoice_id,
            "pickup_payment_id": pickup_payment_id,
            "addon_ids": reservation["addon_ids"],
            "reservation": reservation,
        }
        contracts.append(contract)

        if not is_closed:
            continue

        damage_cost = 0.0
        assessment_id = 0
        if index % 3 == 0:
            assessment_id = next_assessment_id
            next_assessment_id += 1
            damage_cost = round(random.choice([120.0, 180.0, 250.0, 320.0]), 1)
            assessments.append({
                "id": assessment_id,
                "vehicle_id": reservation["vehicle_id"],
                "rental_agent_id": contract["rental_agent_id"],
                "invoice_id": invoice_id,
                "date": reservation["end"],
                "description": DAMAGE_DESCRIPTIONS[index % len(DAMAGE_DESCRIPTIONS)],
                "damage_cost": damage_cost,
            })

        base_amount = rental_base(reservation["vehicle"], reservation["days"])
        addon_amount = addon_fee(reservation["addon_ids"], reservation["days"])
        discount = round(base_amount * discount_rate_for_tier(reservation["customer"]["tier"]), 1)
        additional_charge = round(random.choice([0.0, 0.0, 25.0, 40.0]), 1)
        total = round(max(0.0, base_amount + addon_amount + damage_cost + additional_charge - discount), 1)
        invoice_payment_id = next_payment_id
        next_payment_id += 1
        invoice_payment_ids = [invoice_payment_id]
        payments.append({
            "id": invoice_payment_id,
            "amount": total,
            "date": reservation["end"],
            "purpose": "ADDITIONAL_CHARGE",
            "customer_id": reservation["customer_id"],
            "reservation_id": 0,
            "contract_id": 0,
            "invoice_id": invoice_id,
        })
        if index % 7 == 0:
            refund_payment_id = next_payment_id
            next_payment_id += 1
            refund_amount = round(random.choice([25.0, 40.0, 50.0]), 1)
            invoice_payment_ids.append(refund_payment_id)
            payments.append({
                "id": refund_payment_id,
                "amount": refund_amount,
                "date": reservation["end"] + timedelta(days=1),
                "purpose": "REFUND",
                "customer_id": reservation["customer_id"],
                "reservation_id": 0,
                "contract_id": 0,
                "invoice_id": invoice_id,
            })
        invoices.append({
            "id": invoice_id,
            "contract_id": contract_id,
            "assessment_id": assessment_id,
            "base": round(base_amount, 1),
            "damage": damage_cost,
            "addon": round(addon_amount, 1),
            "discount": discount,
            "total": total,
            "refund": 0.0,
            "additional": additional_charge,
            "payment_ids": invoice_payment_ids,
        })

    for reservation in reservations:
        if reservation["status"] == "PENDING":
            reservation["prepayment"] = 0.0
            reservation["prepayment_id"] = 0
            continue

        prepayment_id = next_payment_id
        next_payment_id += 1
        reservation["prepayment_id"] = prepayment_id
        payments.append({
            "id": prepayment_id,
            "amount": reservation["prepayment"],
            "date": reservation["start"] - timedelta(days=2),
            "purpose": "PREPAYMENT",
            "customer_id": reservation["customer_id"],
            "reservation_id": reservation["id"],
            "contract_id": 0,
            "invoice_id": 0,
        })

    return reservations, payments, contracts, assessments, invoices


def generate_maintenance_tasks(vehicles, count):
    tasks = []
    mechanics = [employee[0] for employee in EMPLOYEES if employee[1] == "MECHANIC"]
    descriptions = [
        "Oil and brake inspection",
        "Tire replacement",
        "Engine diagnostic",
        "Interior and safety check",
        "Battery inspection",
    ]
    for index in range(count):
        vehicle = vehicles[(index + 2) % len(vehicles)]
        status = ["SCHEDULED", "IN_PROGRESS", "COMPLETED"][index % 3]
        tasks.append({
            "id": index + 1,
            "vehicle_id": vehicle["id"],
            "mechanic_id": mechanics[index % len(mechanics)],
            "date": date(2026, 5, 6) + timedelta(days=index * 3),
            "description": descriptions[index % len(descriptions)],
            "status": status,
        })
    return tasks


def generate_branch_reports(vehicles, reservations, invoices, payments):
    reports = []
    manager_by_branch = {employee[8]: employee[0] for employee in EMPLOYEES if employee[1] == "BRANCH_MANAGER"}
    paid_by_invoice = {}
    for payment in payments:
        if payment["invoice_id"] != 0 and payment["purpose"] not in ["REFUND", "DEPOSIT"]:
            paid_by_invoice[payment["invoice_id"]] = paid_by_invoice.get(payment["invoice_id"], 0.0) + payment["amount"]

    invoice_by_contract = {invoice["contract_id"]: invoice for invoice in invoices}
    vehicle_by_id = {vehicle["id"]: vehicle for vehicle in vehicles}

    for branch_id, _, _ in BRANCHES:
        branch_vehicles = [vehicle for vehicle in vehicles if vehicle["branch_id"] == branch_id]
        branch_vehicle_ids = {vehicle["id"] for vehicle in branch_vehicles}
        branch_reservations = [
            reservation for reservation in reservations if reservation["vehicle_id"] in branch_vehicle_ids
        ]
        revenue = 0.0
        for reservation in branch_reservations:
            invoice = invoice_by_contract.get(reservation["contract_id"])
            if invoice is not None:
                revenue += paid_by_invoice.get(invoice["id"], 0.0)

        reports.append({
            "id": branch_id,
            "branch_id": branch_id,
            "manager_id": manager_by_branch[branch_id],
            "date": date(2026, 5, 1),
            "total_vehicles": len(branch_vehicles),
            "total_reservations": len(branch_reservations),
            "available_vehicles": sum(1 for vehicle in branch_vehicles if vehicle["status"] == "AVAILABLE"),
            "rented_vehicles": sum(1 for vehicle in branch_vehicles if vehicle["status"] == "RENTED"),
            "total_revenue": round(revenue, 1),
        })
    return reports


def join_ids(values):
    return ",".join(str(value) for value in values)


def write_lines(path, lines):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def write_data_files(data_dir, customers, vehicles, reservations, payments, contracts, assessments,
        invoices, maintenance_tasks, branch_reports):
    write_lines(data_dir / "branches.txt", [
        f"{branch_id}|{name}|{address}" for branch_id, name, address in BRANCHES
    ])
    write_lines(data_dir / "customers.txt", [
        f"{customer['id']}|{customer['name']}|{customer['surname']}|{customer['email']}|"
        f"{customer['points']}|{customer['tier']}"
        for customer in customers
    ])
    write_lines(data_dir / "employees.txt", [
        f"{employee_id}|{kind}|{user_id}|{name}|{surname}|{email}|{salary}|{branch_id}|{managed_branch_id}"
        for employee_id, kind, user_id, name, surname, email, salary, branch_id, managed_branch_id in EMPLOYEES
    ])
    write_lines(data_dir / "vehicles.txt", [
        f"{vehicle['id']}|{vehicle['type']}|{vehicle['plate']}|{vehicle['brand']}|{vehicle['model']}|"
        f"{vehicle['year']}|{vehicle['daily_rate']}|{vehicle['insurance']}|{vehicle['mileage_policy']}|"
        f"{vehicle['status']}|{vehicle['current_mileage']}|{vehicle['maintenance_interval']}|"
        f"{vehicle['last_maintenance_mileage']}|{vehicle['branch_id']}"
        for vehicle in vehicles
    ])
    write_lines(data_dir / "maintenance_tasks.txt", [
        f"{task['id']}|{task['vehicle_id']}|{task['mechanic_id']}|{fmt_day(task['date'])}|"
        f"{task['description']}|{task['status']}"
        for task in maintenance_tasks
    ])
    write_lines(data_dir / "addons.txt", [
        f"{addon_id}|{name}|{description}|{daily_price}"
        for addon_id, name, description, daily_price in ADDONS
    ])
    write_lines(data_dir / "payments.txt", [
        f"{payment['id']}|{payment['amount']}|{fmt_day(payment['date'])}|{payment['purpose']}|"
        f"{payment['customer_id']}|{payment['reservation_id']}|{payment['contract_id']}|{payment['invoice_id']}"
        for payment in payments
    ])
    write_lines(data_dir / "reservations.txt", [
        f"{reservation['id']}|{reservation['customer_id']}|{reservation['vehicle_id']}|"
        f"{fmt_day(reservation['start'])}|{fmt_day(reservation['end'])}|{reservation['status']}|"
        f"{reservation['prepayment']}|{reservation['deposit']}|{reservation['prepayment_id']}|"
        f"{reservation['contract_id']}|{join_ids(reservation['addon_ids'])}"
        for reservation in reservations
    ])
    write_lines(data_dir / "rental_contracts.txt", [
        f"{contract['id']}|{contract['reservation_id']}|{contract['rental_agent_id']}|"
        f"{fmt_day(contract['pickup'])}|{fmt_day(contract['expected_return'])}|"
        f"{fmt_day(contract['actual_return'])}|{contract['deposit']}|{contract['initial_mileage']}|"
        f"{contract['final_mileage']}|{contract['status']}|{contract['invoice_id']}|"
        f"{contract['pickup_payment_id']}|{join_ids(contract['addon_ids'])}"
        for contract in contracts
    ])
    write_lines(data_dir / "damage_assessments.txt", [
        f"{assessment['id']}|{assessment['vehicle_id']}|{assessment['rental_agent_id']}|"
        f"{assessment['invoice_id']}|{fmt_day(assessment['date'])}|{assessment['description']}|"
        f"{assessment['damage_cost']}"
        for assessment in assessments
    ])
    write_lines(data_dir / "invoices.txt", [
        f"{invoice['id']}|{invoice['contract_id']}|{invoice['assessment_id']}|{invoice['base']}|"
        f"{invoice['damage']}|{invoice['addon']}|{invoice['discount']}|{invoice['total']}|"
        f"{invoice['refund']}|{invoice['additional']}|{join_ids(invoice['payment_ids'])}"
        for invoice in invoices
    ])
    write_lines(data_dir / "branch_reports.txt", [
        f"{report['id']}|{report['branch_id']}|{report['manager_id']}|{fmt_day(report['date'])}|"
        f"{report['total_vehicles']}|{report['total_reservations']}|{report['available_vehicles']}|"
        f"{report['rented_vehicles']}|{report['total_revenue']}"
        for report in branch_reports
    ])


def validate_counts(args):
    if args.customers < 1:
        raise ValueError("At least 1 customer is required.")
    if args.vehicles < 6:
        raise ValueError("At least 6 vehicles are required.")
    if args.reservations < 1:
        raise ValueError("At least 1 reservation is required.")
    if args.contracts < 1:
        raise ValueError("At least 1 rental contract is required.")
    if args.contracts > args.reservations:
        raise ValueError("Rental contract count cannot be higher than reservation count.")


def main():
    parser = argparse.ArgumentParser(
        description="Generate all data files used by core.FileManager."
    )
    parser.add_argument("--customers", type=int, default=100)
    parser.add_argument("--vehicles", type=int, default=120)
    parser.add_argument("--reservations", type=int, default=80)
    parser.add_argument("--contracts", type=int, default=55)
    parser.add_argument("--maintenance-tasks", type=int, default=25)
    parser.add_argument("--seed", type=int, default=35)
    parser.add_argument("--data-dir", default="data")
    args = parser.parse_args()
    validate_counts(args)

    random.seed(args.seed)
    data_dir = Path(args.data_dir)
    customers = generate_customers(args.customers)
    vehicles = generate_vehicles(args.vehicles)
    reservations, payments, contracts, assessments, invoices = generate_business_data(
        customers, vehicles, args.reservations, args.contracts
    )
    maintenance_tasks = generate_maintenance_tasks(vehicles, args.maintenance_tasks)
    branch_reports = generate_branch_reports(vehicles, reservations, invoices, payments)

    write_data_files(
        data_dir,
        customers,
        vehicles,
        reservations,
        payments,
        contracts,
        assessments,
        invoices,
        maintenance_tasks,
        branch_reports,
    )

    print(f"Generated data files in {data_dir}")
    print(f"Customers: {len(customers)}")
    print(f"Vehicles: {len(vehicles)}")
    print(f"Reservations: {len(reservations)}")
    print(f"Rental contracts: {len(contracts)}")
    print(f"Payments: {len(payments)}")
    print(f"Invoices: {len(invoices)}")
    print(f"Damage assessments: {len(assessments)}")
    print(f"Maintenance tasks: {len(maintenance_tasks)}")
    print(f"Branch reports: {len(branch_reports)}")


if __name__ == "__main__":
    main()
