package qa;

import core.Addon;
import core.Branch;
import core.BranchManager;
import core.BranchNotFoundException;
import core.BranchReport;
import core.ContractStatus;
import core.Customer;
import core.DamageAssessment;
import core.Employee;
import core.FileManager;
import core.InsuranceOption;
import core.InvalidFileFormatException;
import core.InvalidReservationException;
import core.Invoice;
import core.LoyaltyTier;
import core.MaintenanceStatus;
import core.MaintenanceTask;
import core.Mechanic;
import core.MileagePolicy;
import core.Payment;
import core.PaymentPurpose;
import core.RentalAgent;
import core.RentalContract;
import core.Reservation;
import core.ReservationStatus;
import core.Vehicle;
import core.VehicleNotAvailableException;
import core.VehicleStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Read-only QA harness. Loads everything via FileManager, runs feature checks,
 * but never calls saveAll, so data files stay untouched.
 */
public class QaDemo {

    private static int passed = 0;
    private static int failed = 0;
    private static int warnings = 0;
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) throws Exception {
        FileManager fm = new FileManager();
        ArrayList<Branch> branches = fm.loadBranches();
        ArrayList<Customer> customers = fm.loadCustomers();
        ArrayList<Employee> employees = fm.loadEmployees(branches);
        ArrayList<Vehicle> vehicles = fm.loadVehicles(branches);

        ArrayList<RentalAgent> agents = new ArrayList<>();
        ArrayList<Mechanic> mechanics = new ArrayList<>();
        ArrayList<BranchManager> managers = new ArrayList<>();
        for (Employee e : employees) {
            if (e instanceof RentalAgent) agents.add((RentalAgent) e);
            if (e instanceof Mechanic) mechanics.add((Mechanic) e);
            if (e instanceof BranchManager) managers.add((BranchManager) e);
        }

        ArrayList<MaintenanceTask> tasks = fm.loadMaintenanceTasks(vehicles, mechanics);
        ArrayList<Addon> addons = fm.loadAddons();
        ArrayList<Payment> payments = fm.loadPayments(customers);
        ArrayList<Reservation> reservations = fm.loadReservations(customers, vehicles, payments, addons);
        ArrayList<RentalContract> contracts = fm.loadRentalContracts(reservations, agents, payments, addons);
        ArrayList<DamageAssessment> assessments = fm.loadDamageAssessments(vehicles, agents);
        ArrayList<Invoice> invoices = fm.loadInvoices(contracts, assessments, payments);
        ArrayList<BranchReport> reports = fm.loadBranchReports(branches, managers);

        section("Load roundtrip");
        check("Branches loaded", branches.size() == 5);
        check("Customers loaded", customers.size() == 12);
        check("Employees loaded", employees.size() == 12);
        check("Vehicles loaded", vehicles.size() == 12);
        check("Reservations loaded", reservations.size() == 15);
        check("Contracts loaded", contracts.size() == 11);
        check("Invoices loaded", invoices.size() == 10);
        check("Payments loaded", payments.size() == 35);
        check("Maintenance tasks loaded", tasks.size() == 3);
        check("Damage assessments loaded", assessments.size() == 7);
        check("Branch reports loaded", reports.size() == 2);
        check("Addons loaded", addons.size() == 7);

        // -----------------------------------------------------------
        section("Feature 1: Vehicle Inventory by Type and Branch");
        for (Branch b : branches) {
            int c = b.getVehicles().size();
            System.out.println("  - " + b.getName() + ": " + c + " vehicle(s)");
        }
        long unassigned = vehicles.stream().filter(v -> v.getBranch() == null).count();
        System.out.println("  - Unassigned: " + unassigned + " vehicle(s)");
        check("Total vehicles == sum of branches+unassigned",
              vehicles.size() == branches.stream().mapToInt(b -> b.getVehicles().size()).sum() + unassigned);

        Vehicle v101 = byID(vehicles, 101);
        check("v101 belongs to Bornova", v101 != null && v101.getBranch() != null
              && v101.getBranch().getBranchID() == 1);
        check("v112 unassigned", byID(vehicles, 112).getBranch() == null);
        check("v102 RENTED status preserved",
              byID(vehicles, 102).getStatus() == VehicleStatus.RENTED);
        check("v105 AVAILABLE after maintenance",
              byID(vehicles, 105).getStatus() == VehicleStatus.AVAILABLE);
        check("v103 IN_MAINTENANCE after scheduled task",
              byID(vehicles, 103).getStatus() == VehicleStatus.IN_MAINTENANCE);

        // -----------------------------------------------------------
        section("Feature 1b: Vehicle.isAvailable() rules");
        Vehicle v101_ = byID(vehicles, 101);
        Date future1 = mkDate(2026, Calendar.AUGUST, 1);
        Date future2 = mkDate(2026, Calendar.AUGUST, 4);
        check("v101 free in Aug 1-4", v101_.isAvailable(future1, future2));

        // overlap with confirmed reservation 7 (Jul 1-5)
        Date julyOverlapStart = mkDate(2026, Calendar.JULY, 2);
        Date julyOverlapEnd   = mkDate(2026, Calendar.JULY, 6);
        check("v101 BLOCKED for Jul 2-6 (overlaps confirmed res 7)",
              !v101_.isAvailable(julyOverlapStart, julyOverlapEnd));

        // RENTED vehicle should never be available
        check("RENTED vehicle (v102) reports !available",
              !byID(vehicles, 102).isAvailable(future1, future2));
        check("IN_MAINTENANCE vehicle (v103) reports !available",
              !byID(vehicles, 103).isAvailable(future1, future2));

        // ⚠ Note: Vehicle.isAvailable does NOT skip COMPLETED reservations.
        Vehicle vWith101 = byID(vehicles, 101);
        Date pastStart = mkDate(2026, Calendar.APRIL, 11);
        Date pastEnd   = mkDate(2026, Calendar.APRIL, 12);
        boolean treatedBlocked = !vWith101.isAvailable(pastStart, pastEnd);
        warn("isAvailable() treats COMPLETED reservation as a blocker (overlap with res 1): "
              + treatedBlocked);

        // -----------------------------------------------------------
        section("Feature 2: Reservation creation");
        Customer ayse = byUserID(customers, 1);
        try {
            Reservation r = ayse.makeReservation(999, byID(vehicles, 110),
                    daysFromToday(2), daysFromToday(5));
            check("Customer can make a future reservation", r.getStatus() == ReservationStatus.PENDING);
        } catch (Exception ex) {
            fail("Future reservation creation failed: " + ex.getMessage());
        }

        // expect VehicleNotAvailableException for RENTED vehicle
        try {
            ayse.makeReservation(998, byID(vehicles, 102),
                    daysFromToday(2), daysFromToday(5));
            fail("Reserving RENTED vehicle should throw");
        } catch (VehicleNotAvailableException ex) {
            pass("Reserving RENTED vehicle properly rejected");
        } catch (Exception other) {
            fail("Reserving RENTED vehicle threw wrong type: " + other.getClass().getSimpleName());
        }

        // expect InvalidReservationException for past dates
        try {
            ayse.makeReservation(997, byID(vehicles, 110),
                    daysFromToday(-5), daysFromToday(-2));
            fail("Past dates should throw");
        } catch (InvalidReservationException ex) {
            pass("Past dates properly rejected");
        } catch (Exception other) {
            fail("Past dates threw wrong type: " + other.getClass().getSimpleName());
        }

        // BranchNotFoundException when branch=null
        try {
            ayse.searchAvailableVehicles(null, daysFromToday(1), daysFromToday(3));
            fail("null branch should throw");
        } catch (BranchNotFoundException ex) {
            pass("null branch search properly rejected");
        }

        // search returns vehicles for valid range
        Branch bornova = byBranchID(branches, 1);
        ArrayList<Vehicle> hits = ayse.searchAvailableVehicles(bornova,
                daysFromToday(1), daysFromToday(3));
        check("Bornova search returns at least 1 vehicle", hits.size() >= 1);

        // -----------------------------------------------------------
        section("Feature 2b: Reservation status / cancellation rules");
        Reservation res6 = byResID(reservations, 6);
        check("Reservation 6 already CANCELLED", res6.getStatus() == ReservationStatus.CANCELLED);
        Reservation res4 = byResID(reservations, 4);
        check("Reservation 4 PENDING", res4.getStatus() == ReservationStatus.PENDING);
        Reservation res3 = byResID(reservations, 3);
        check("Reservation 3 CONFIRMED with active contract",
              res3.getStatus() == ReservationStatus.CONFIRMED
              && res3.getRentalContract() != null
              && res3.getRentalContract().getStatus() == ContractStatus.ACTIVE);

        // -----------------------------------------------------------
        section("Feature 3: Invoice / rate calculations");
        // v101 = Economy 45/day, STANDARD insurance (20/day)
        check("Vehicle.calculateRentalCost(v101, 3 days) == 195.0",
              eq(byID(vehicles, 101).calculateRentalCost(3), 195.0));
        // v104 Luxury 140/day, PREMIUM 35/day, 4 days = 700
        check("Vehicle.calculateRentalCost(v104, 4 days) == 700.0",
              eq(byID(vehicles, 104).calculateRentalCost(4), 700.0));

        // discount: bronze=0%, silver=5%, gold=10%
        Invoice probe = new Invoice(0, 200.0, 0.0, 0.0);
        probe.applyDiscount(byUserID(customers, 1));   // bronze
        check("BRONZE discount = 0",      eq(probe.getDiscountAmount(), 0.0));
        probe.applyDiscount(byUserID(customers, 2));   // silver
        check("SILVER discount = 5%",     eq(probe.getDiscountAmount(), 10.0));
        probe.applyDiscount(byUserID(customers, 3));   // gold
        check("GOLD discount = 10%",      eq(probe.getDiscountAmount(), 20.0));

        // mileage policy
        check("LIMITED extra charge correct",
              eq(MileagePolicy.LIMITED.calculateExtraCharge(2, 250), 150.0)); // 250-200=50, 50*3=150
        check("STANDARD no extra at limit",
              eq(MileagePolicy.STANDARD.calculateExtraCharge(2, 400), 0.0));
        check("UNLIMITED is always 0",
              eq(MileagePolicy.UNLIMITED.calculateExtraCharge(10, 9999), 0.0));

        // existing invoice 1: base=165, damage=120, addon=37.5, discount=0, total=322.5
        Invoice inv1 = byInvID(invoices, 1);
        check("Invoice 1 total computed = 322.5", eq(inv1.calculateTotal(), 322.5));
        check("Invoice 1 paid amount excludes refund/deposit",
              eq(inv1.calculatePaidAmount(), 148.5));
        // ⚠ Seed inconsistency: invoice 1 baseAmount (165.0) does NOT match
        // calculateRentalCost(v101, 3 days) = 195.0
        warn("Invoice 1 baseAmount (165.0) != Vehicle.calculateRentalCost (195.0) — "
              + "seed assumes BASIC insurance, vehicle has STANDARD");

        // ⚠ Reservation 1 prepayment 148.5 doesn't equal estimated total 232.5
        Reservation res1 = byResID(reservations, 1);
        double expectedPrepay = byID(vehicles, 101).calculateRentalCost(3)
                + addonCost(res1.getAddons(), 3);
        warn("Reservation 1 prePaymentAmount=" + res1.getPrePaymentAmount()
              + " vs expected from rental+addons=" + expectedPrepay);

        // sweep: invoice base vs calculated rental (looking for more mismatches)
        for (Invoice inv : invoices) {
            RentalContract c = inv.getRentalContract();
            if (c == null || c.getReservation() == null) continue;
            Reservation r = c.getReservation();
            int days = r.calculateDuration();
            Vehicle v = r.getVehicle();
            double expected = v.calculateRentalCost(days);
            if (Math.abs(inv.getBaseAmount() - expected) > 0.01) {
                warn("Invoice " + inv.getInvoiceID() + " base=" + inv.getBaseAmount()
                     + " but calculateRentalCost(" + v.getPlateNumber() + "," + days
                     + ")=" + expected);
            }
        }

        // -----------------------------------------------------------
        section("Feature 4: Loyalty");
        check("fromPoints(0) == BRONZE",   LoyaltyTier.fromPoints(0)    == LoyaltyTier.BRONZE);
        check("fromPoints(499) == BRONZE", LoyaltyTier.fromPoints(499)  == LoyaltyTier.BRONZE);
        check("fromPoints(500) == SILVER", LoyaltyTier.fromPoints(500)  == LoyaltyTier.SILVER);
        check("fromPoints(1499) == SILVER",LoyaltyTier.fromPoints(1499) == LoyaltyTier.SILVER);
        check("fromPoints(1500) == GOLD",  LoyaltyTier.fromPoints(1500) == LoyaltyTier.GOLD);

        // earnPoints: 100 amount → 10 points
        Customer c1 = new Customer(900, "T", "T", "x@x.com", 0, LoyaltyTier.BRONZE);
        c1.earnPoints(5500);
        check("earnPoints(5500)==550→SILVER", c1.getLoyaltyPoints() == 550 && c1.getLoyaltyTier()==LoyaltyTier.SILVER);
        c1.earnPoints(15000);
        check("earnPoints crosses GOLD",     c1.getLoyaltyTier() == LoyaltyTier.GOLD);

        // verify stored tier matches stored points for every customer
        for (Customer c : customers) {
            LoyaltyTier expected = LoyaltyTier.fromPoints(c.getLoyaltyPoints());
            if (c.getLoyaltyTier() != expected) {
                fail("Customer " + c.getUserID() + " stored tier=" + c.getLoyaltyTier()
                     + " but fromPoints(" + c.getLoyaltyPoints() + ")=" + expected);
            }
        }

        // -----------------------------------------------------------
        section("Feature 5: Maintenance");
        Vehicle v103 = byID(vehicles, 103);
        check("v103 distance to next maintenance <= 0 (overdue)",
              v103.calculateDistanceToNextMaintenance() <= 0);
        check("v103 needsMaintenance == true", v103.needsMaintenance());

        // task 2 SCHEDULED → vehicle status IN_MAINTENANCE
        check("Maintenance task 2 SCHEDULED",
              byTaskID(tasks, 2).getStatus() == MaintenanceStatus.SCHEDULED);
        check("Maintenance task 1 COMPLETED",
              byTaskID(tasks, 1).getStatus() == MaintenanceStatus.COMPLETED);

        // simulate complete: should not mutate persisted file (we don't save)
        MaintenanceTask sched = byTaskID(tasks, 2);
        Vehicle sv = sched.getVehicle();
        VehicleStatus before = sv.getStatus();
        int beforeMileage = sv.getLastMaintenanceMileage();
        sched.completeMaintenance();
        check("After completeMaintenance status==AVAILABLE",
              sv.getStatus() == VehicleStatus.AVAILABLE);
        check("After completeMaintenance lastMaintenanceMileage updated",
              sv.getLastMaintenanceMileage() == sv.getCurrentMileage());
        // restore in-memory only (we never call saveAll)
        sv.setStatus(before);
        sv.setLastMaintenanceMileage(beforeMileage);
        sched.setStatus(MaintenanceStatus.SCHEDULED);

        // schedule on RENTED should fail
        try {
            MaintenanceTask t = new MaintenanceTask(0, new Date(), "x", MaintenanceStatus.SCHEDULED);
            t.setVehicle(byID(vehicles, 102)); // RENTED
            t.scheduleMaintenance();
            fail("Scheduling on RENTED vehicle should throw");
        } catch (IllegalStateException ex) {
            pass("Scheduling on RENTED throws IllegalStateException");
        }
        // (cleanup attempt: undo registration to vehicle list — but task list is mutated,
        // see warning below)
        warn("MaintenanceTask.setVehicle() permanently registers task on Vehicle.maintenanceTasks "
              + "even when scheduling later fails — leaks an in-memory phantom task");

        // -----------------------------------------------------------
        section("Feature 6: Damage Assessment");
        check("Damage assessment 1 connected to v101 + agent 10 + invoice 1",
              byAssessmentID(assessments, 1).getVehicle().getVehicleID() == 101
              && byAssessmentID(assessments, 1).getRentalAgent().getEmployeeID() == 10
              && byAssessmentID(assessments, 1).getInvoice() != null
              && byAssessmentID(assessments, 1).getInvoice().getInvoiceID() == 1);

        DamageAssessment unresolved = byAssessmentID(assessments, 3);
        check("Damage 3 has no invoice (unresolved)", unresolved.getInvoice() == null);

        // -----------------------------------------------------------
        section("Feature 7: Branch Report");
        BranchReport bornovaReport = byReportID(reports, 1);
        // recompute using same logic to compare
        BranchManager selin = byManagerID(managers, 30);
        BranchReport recomputed = selin.generateReport(bornova);
        check("Bornova report revenue matches recomputation",
              eq(bornovaReport.getTotalRevenue(), recomputed.getTotalRevenue()));
        // remove the dummy from the branch's history (leave-no-trace)
        bornova.getBranchReports().remove(recomputed);
        selin.getGeneratedReports().remove(recomputed);

        // -----------------------------------------------------------
        section("Cross-cut consistency checks");
        // every contract's depositAmount > 0
        for (RentalContract c : contracts) {
            if (c.getDepositAmount() <= 0) {
                fail("Contract " + c.getContractID() + " has non-positive deposit");
            }
        }
        // every reservation's vehicle exists and isn't dangling
        for (Reservation r : reservations) {
            if (r.getVehicle() == null || r.getCustomer() == null) {
                fail("Reservation " + r.getReservationID() + " has null vehicle/customer");
            }
        }
        // payments referencing reservations should match reservation.prepayment
        for (Reservation r : reservations) {
            Payment pre = r.getPrepayment();
            if (pre != null && pre.getPaymentPurpose() != PaymentPurpose.PREPAYMENT) {
                fail("Reservation " + r.getReservationID() + " has prepayment with non-PREPAYMENT purpose");
            }
        }

        // employee-branch back link integrity
        for (Branch b : branches) {
            for (Employee e : b.getEmployees()) {
                if (e.getBranch() != b) {
                    fail("Employee " + e.getEmployeeID() + " not back-linked to "
                         + b.getName());
                }
            }
        }

        // BranchManager.managedBranch == Branch.branchManager
        for (BranchManager m : managers) {
            Branch managed = m.getManagedBranch();
            if (managed != null && managed.getBranchManager() != m) {
                fail("BranchManager " + m.getEmployeeID() + " not bidirectionally linked");
            }
        }

        // every paid amount on contracts is consistent with payments file
        for (RentalContract c : contracts) {
            if (c.getInvoice() == null) continue;
            double paidFromInvoice = c.getInvoice().calculatePaidAmount();
            double paidFromPayments = 0.0;
            for (Payment p : payments) {
                if (p.getInvoice() == c.getInvoice()
                        && p.getPaymentPurpose() != PaymentPurpose.REFUND
                        && p.getPaymentPurpose() != PaymentPurpose.DEPOSIT) {
                    paidFromPayments += p.getAmount();
                }
            }
            if (Math.abs(paidFromInvoice - paidFromPayments) > 0.01) {
                fail("Contract " + c.getContractID() + " invoice paid mismatch: invoice="
                     + paidFromInvoice + " payments=" + paidFromPayments);
            }
        }

        // -----------------------------------------------------------
        section("Edge cases");
        // ID parsers reject negatives
        try { new Customer(-1, "n", "s", "e", 0, LoyaltyTier.BRONZE); fail("neg userID accepted"); }
        catch (IllegalArgumentException ex) { pass("Negative userID rejected"); }
        try { byID(vehicles, 101).setCurrentMileage(-1); fail("neg mileage accepted"); }
        catch (IllegalArgumentException ex) { pass("Negative mileage rejected"); }
        try { new Payment(0, -1.0, new Date(), PaymentPurpose.REFUND, 1); fail("neg amount accepted"); }
        catch (IllegalArgumentException ex) { pass("Negative payment amount rejected"); }

        // Reservation with end<=start should reject in calculateDuration
        try {
            Reservation bad = new Reservation(0, new Date(), new Date(), ReservationStatus.PENDING);
            bad.calculateDuration();
            fail("Same-start/end calculateDuration should throw");
        } catch (IllegalStateException ex) {
            pass("Same-start/end reservation duration rejected");
        }

        // -----------------------------------------------------------
        section("Summary");
        System.out.println("PASSED:   " + passed);
        System.out.println("FAILED:   " + failed);
        System.out.println("WARNINGS: " + warnings);
        if (failed > 0) System.exit(1);
    }

    // -- helpers --
    private static double addonCost(ArrayList<Addon> as, int days) {
        double t = 0; for (Addon a : as) t += a.calculateCost(days); return t;
    }

    private static Vehicle byID(ArrayList<Vehicle> vs, int id) {
        for (Vehicle v : vs) if (v.getVehicleID()==id) return v; return null;
    }
    private static Customer byUserID(ArrayList<Customer> cs, int id) {
        for (Customer c : cs) if (c.getUserID()==id) return c; return null;
    }
    private static Branch byBranchID(ArrayList<Branch> bs, int id) {
        for (Branch b : bs) if (b.getBranchID()==id) return b; return null;
    }
    private static Reservation byResID(ArrayList<Reservation> rs, int id) {
        for (Reservation r : rs) if (r.getReservationID()==id) return r; return null;
    }
    private static Invoice byInvID(ArrayList<Invoice> is, int id) {
        for (Invoice i : is) if (i.getInvoiceID()==id) return i; return null;
    }
    private static MaintenanceTask byTaskID(ArrayList<MaintenanceTask> ts, int id) {
        for (MaintenanceTask t : ts) if (t.getMaintenanceID()==id) return t; return null;
    }
    private static DamageAssessment byAssessmentID(ArrayList<DamageAssessment> as, int id) {
        for (DamageAssessment a : as) if (a.getAssessmentID()==id) return a; return null;
    }
    private static BranchReport byReportID(ArrayList<BranchReport> rs, int id) {
        for (BranchReport r : rs) if (r.getReportID()==id) return r; return null;
    }
    private static BranchManager byManagerID(ArrayList<BranchManager> ms, int id) {
        for (BranchManager m : ms) if (m.getEmployeeID()==id) return m; return null;
    }

    private static Date mkDate(int y, int m, int d) {
        Calendar c = Calendar.getInstance();
        c.clear(); c.set(y, m, d); return c.getTime();
    }
    private static Date daysFromToday(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, days); return c.getTime();
    }
    private static boolean eq(double a, double b) { return Math.abs(a-b) < 0.001; }

    private static void section(String name) {
        System.out.println();
        System.out.println("=== " + name + " ===");
    }
    private static void check(String label, boolean ok) {
        if (ok) pass(label); else fail(label);
    }
    private static void pass(String label) { passed++; System.out.println("  PASS  " + label); }
    private static void fail(String label) { failed++; System.out.println("  FAIL  " + label); }
    private static void warn(String label) { warnings++; System.out.println("  WARN  " + label); }
}
