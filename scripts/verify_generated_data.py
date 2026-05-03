#!/usr/bin/env python3
import subprocess
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[1]


EXPECTED_OUTPUT = [
    "Loaded branches: 5",
    "Loaded customers: 100",
    "Loaded employees: 15",
    "Loaded vehicles: 120",
    "Loaded maintenance tasks: 25",
    "Loaded addons: 5",
    "Loaded payments: 174",
    "Loaded reservations: 80",
    "Loaded rental contracts: 55",
    "Loaded damage assessments: 14",
    "Loaded invoices: 42",
    "Loaded branch reports: 5",
]


def run(command):
    return subprocess.run(
        command,
        cwd=PROJECT_ROOT,
        check=True,
        text=True,
        capture_output=True,
    )


def read_rows(file_name):
    rows = []
    path = PROJECT_ROOT / "data" / file_name
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if line:
            rows.append(line.split("|"))
    return rows


def validate_payment_relations():
    reservations = {
        int(row[0]): {
            "customer_id": int(row[1]),
            "vehicle_id": int(row[2]),
            "prepayment_id": int(row[8]),
            "contract_id": int(row[9]),
            "status": row[5],
        }
        for row in read_rows("reservations.txt")
    }
    contracts = {
        int(row[0]): {
            "reservation_id": int(row[1]),
            "rental_agent_id": int(row[2]),
            "status": row[9],
            "invoice_id": int(row[10]),
            "pickup_payment_id": int(row[11]),
        }
        for row in read_rows("rental_contracts.txt")
    }
    invoices = {
        int(row[0]): {
            "contract_id": int(row[1]),
            "assessment_id": int(row[2]),
            "payment_ids": [int(value) for value in row[10].split(",") if value],
        }
        for row in read_rows("invoices.txt")
    }
    payments = {
        int(row[0]): {
            "purpose": row[3],
            "customer_id": int(row[4]),
            "reservation_id": int(row[5]),
            "contract_id": int(row[6]),
            "invoice_id": int(row[7]),
        }
        for row in read_rows("payments.txt")
    }
    assessments = {
        int(row[0]): {
            "vehicle_id": int(row[1]),
            "rental_agent_id": int(row[2]),
            "invoice_id": int(row[3]),
        }
        for row in read_rows("damage_assessments.txt")
    }

    errors = []
    for payment_id, payment in payments.items():
        relation_count = sum(
            1
            for relation_id in (
                payment["reservation_id"],
                payment["contract_id"],
                payment["invoice_id"],
            )
            if relation_id != 0
        )
        if relation_count != 1:
            errors.append(f"Payment {payment_id} should have exactly one relation target.")
            continue

        expected_customer_id = find_payment_customer_id(payment, reservations, contracts, invoices)
        if expected_customer_id is None:
            errors.append(f"Payment {payment_id} points to a missing relation target.")
        elif payment["customer_id"] != expected_customer_id:
            errors.append(
                f"Payment {payment_id} customer {payment['customer_id']} should be {expected_customer_id}."
            )

    for reservation_id, reservation in reservations.items():
        prepayment_id = reservation["prepayment_id"]
        if reservation["status"] == "PENDING" and prepayment_id != 0:
            errors.append(f"Pending reservation {reservation_id} should not have a prepayment.")
        if prepayment_id != 0:
            payment = payments.get(prepayment_id)
            if payment is None:
                errors.append(f"Reservation {reservation_id} prepayment is missing.")
            elif (
                payment["purpose"] != "PREPAYMENT"
                or payment["reservation_id"] != reservation_id
                or payment["customer_id"] != reservation["customer_id"]
            ):
                errors.append(f"Reservation {reservation_id} prepayment relation is inconsistent.")

    for contract_id, contract in contracts.items():
        reservation = reservations.get(contract["reservation_id"])
        if reservation is None:
            errors.append(f"Contract {contract_id} reservation is missing.")
            continue

        if reservation["contract_id"] != contract_id:
            errors.append(f"Contract {contract_id} is not linked back from its reservation.")

        deposit = payments.get(contract["pickup_payment_id"])
        if deposit is None:
            errors.append(f"Contract {contract_id} deposit payment is missing.")
        elif (
            deposit["purpose"] != "DEPOSIT"
            or deposit["contract_id"] != contract_id
            or deposit["customer_id"] != reservation["customer_id"]
        ):
            errors.append(f"Contract {contract_id} deposit relation is inconsistent.")

        if contract["status"] == "CLOSED" and contract["invoice_id"] == 0:
            errors.append(f"Closed contract {contract_id} should have an invoice.")
        if contract["status"] == "ACTIVE" and contract["invoice_id"] != 0:
            errors.append(f"Active contract {contract_id} should not have an invoice yet.")

    for invoice_id, invoice in invoices.items():
        contract = contracts.get(invoice["contract_id"])
        if contract is None:
            errors.append(f"Invoice {invoice_id} contract is missing.")
            continue
        if contract["invoice_id"] != invoice_id:
            errors.append(f"Invoice {invoice_id} is not linked back from its contract.")

        reservation = reservations.get(contract["reservation_id"])
        expected_customer_id = reservation["customer_id"] if reservation is not None else None
        for payment_id in invoice["payment_ids"]:
            payment = payments.get(payment_id)
            if payment is None:
                errors.append(f"Invoice {invoice_id} payment {payment_id} is missing.")
            elif payment["invoice_id"] != invoice_id or payment["customer_id"] != expected_customer_id:
                errors.append(f"Invoice {invoice_id} payment {payment_id} relation is inconsistent.")

        assessment_id = invoice["assessment_id"]
        if assessment_id != 0:
            assessment = assessments.get(assessment_id)
            if assessment is None or assessment["invoice_id"] != invoice_id:
                errors.append(f"Invoice {invoice_id} damage assessment relation is inconsistent.")

    if errors:
        raise SystemExit("\n".join(errors))


def find_payment_customer_id(payment, reservations, contracts, invoices):
    if payment["reservation_id"] != 0:
        reservation = reservations.get(payment["reservation_id"])
        return None if reservation is None else reservation["customer_id"]

    if payment["contract_id"] != 0:
        contract = contracts.get(payment["contract_id"])
        if contract is None:
            return None
        reservation = reservations.get(contract["reservation_id"])
        return None if reservation is None else reservation["customer_id"]

    if payment["invoice_id"] != 0:
        invoice = invoices.get(payment["invoice_id"])
        if invoice is None:
            return None
        contract = contracts.get(invoice["contract_id"])
        if contract is None:
            return None
        reservation = reservations.get(contract["reservation_id"])
        return None if reservation is None else reservation["customer_id"]

    return None


def main():
    run(["python3", "scripts/generate_data.py"])
    validate_payment_relations()
    run(["javac", "-d", "out", *[str(path) for path in sorted((PROJECT_ROOT / "src/core").glob("*.java"))]])
    result = run(["java", "-cp", "out", "core.Main"])

    missing_lines = [line for line in EXPECTED_OUTPUT if line not in result.stdout]
    if missing_lines:
        print(result.stdout)
        raise SystemExit("Missing expected output lines: " + ", ".join(missing_lines))

    print("Generated data compiles, loads, saves, and runs with core.Main.")


if __name__ == "__main__":
    main()
