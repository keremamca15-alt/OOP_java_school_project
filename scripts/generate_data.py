#!/usr/bin/env python3
import argparse
import random
from pathlib import Path


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

INSURANCE_OPTIONS = ["BASIC", "STANDARD", "PREMIUM"]
MILEAGE_POLICIES = ["LIMITED", "STANDARD", "UNLIMITED"]
VEHICLE_STATUSES = ["AVAILABLE", "AVAILABLE", "AVAILABLE", "RENTED", "IN_MAINTENANCE"]


def read_branch_ids(branches_path):
    branch_ids = []
    with branches_path.open("r", encoding="utf-8") as file:
        for line in file:
            line = line.strip()
            if not line:
                continue
            parts = line.split("|")
            if len(parts) >= 1:
                branch_ids.append(int(parts[0]))

    if not branch_ids:
        raise ValueError(f"No branches found in {branches_path}")
    return branch_ids


def loyalty_tier_for_points(points):
    if points >= 1500:
        return "GOLD"
    if points >= 500:
        return "SILVER"
    return "BRONZE"


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

        used_emails.add(email)
        loyalty_points = random.randint(0, 2200)
        loyalty_tier = loyalty_tier_for_points(loyalty_points)
        user_id = index + 1
        customers.append(
            f"{user_id}|{display_name}|{surname}|{email}|{loyalty_points}|{loyalty_tier}"
        )

    return customers


def generate_plate(index):
    letters = [
        "ABC",
        "DEF",
        "GHI",
        "JKL",
        "MNO",
        "PRS",
        "TUV",
        "YZK",
    ]
    letter_group = letters[index % len(letters)]
    number = index + 1
    return f"35{letter_group}{number:02d}"


def generate_vehicles(count, branch_ids):
    vehicles = []
    vehicle_types = list(VEHICLE_MODELS.keys())

    for index in range(count):
        vehicle_id = 101 + index
        vehicle_type = vehicle_types[index % len(vehicle_types)]
        brand, model, base_rate = random.choice(VEHICLE_MODELS[vehicle_type])
        year = random.randint(2019, 2026)
        daily_rate = round(base_rate + random.randint(-5, 10), 1)
        insurance_option = random.choice(INSURANCE_OPTIONS)
        mileage_policy = random.choice(MILEAGE_POLICIES)
        status = random.choice(VEHICLE_STATUSES)
        if index < 2:
            status = "AVAILABLE"
        current_mileage = random.randint(5000, 90000)
        maintenance_interval = random.choice([5000, 8000, 10000, 12000])
        last_maintenance_mileage = max(
            0, current_mileage - random.randint(0, maintenance_interval + 3000)
        )
        branch_id = branch_ids[(index // 2) % len(branch_ids)]

        vehicles.append(
            f"{vehicle_id}|{vehicle_type}|{generate_plate(index)}|{brand}|{model}|"
            f"{year}|{daily_rate}|{insurance_option}|{mileage_policy}|{status}|"
            f"{current_mileage}|{maintenance_interval}|{last_maintenance_mileage}|{branch_id}"
        )

    return vehicles


def write_lines(path, lines):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main():
    parser = argparse.ArgumentParser(
        description="Generate customer and vehicle data files for the car rental project."
    )
    parser.add_argument("--customers", type=int, default=25)
    parser.add_argument("--vehicles", type=int, default=50)
    parser.add_argument("--seed", type=int, default=35)
    parser.add_argument("--data-dir", default="data")
    args = parser.parse_args()

    random.seed(args.seed)

    data_dir = Path(args.data_dir)
    branch_ids = read_branch_ids(data_dir / "branches.txt")

    customer_lines = generate_customers(args.customers)
    vehicle_lines = generate_vehicles(args.vehicles, branch_ids)

    write_lines(data_dir / "customers.txt", customer_lines)
    write_lines(data_dir / "vehicles.txt", vehicle_lines)

    print(f"Generated {len(customer_lines)} customers in {data_dir / 'customers.txt'}")
    print(f"Generated {len(vehicle_lines)} vehicles in {data_dir / 'vehicles.txt'}")


if __name__ == "__main__":
    main()
