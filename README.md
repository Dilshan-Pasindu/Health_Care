# 🏥 Medicare Payment & Billing Management Module

## 📌 Overview
The Payment & Billing Management module handles the core financial transactions of the Medicare system. This module is responsible for generating secure invoices, processing direct and insurance payments, validating payment details in real-time, and maintaining a robust billing history with client-side PDF export capabilities.

The interface was designed with a modern, secure, and responsive layout, adopting dark glassmorphism design principles to ensure a premium user experience.

---

## 💻 Tech Stack & Languages Used

*   **Java (Spring Boot):** The core backend language. Handles HTTP requests, business logic, file-based data persistence (`billing.txt`), and invoice routing.
*   **HTML5 & Thymeleaf:** Used for server-side view rendering, dynamically injecting backend variables into the frontend interface.
*   **CSS3:** Handles the styling, incorporating modern aesthetics like flexbox/grid layouts, linear gradients, hover micro-animations, and glassmorphism.
*   **JavaScript (Vanilla):** Powers all the client-side logic, including real-time input formatting, mathematical validations, dynamic tab switching, table filtering, and generating downloadable PDF receipts via `jsPDF`.

---

## ✅ Security & Form Validations

To ensure data integrity and prevent faulty transactions, extensive client-side validations were built using JavaScript and Regular Expressions (Regex) before any data is allowed to be sent to the server.

*   **Credit Card Validations (Algorithmic & Regex):** 
    *   **Luhn Algorithm:** Mathematically verifies the 16-digit card number to prevent typos.
    *   **Network Detection:** Uses Regex (e.g., `/^4/` for Visa, `/^5[1-5]/` for Mastercard) to automatically detect and display the card brand icon as the user types.
    *   **Input Formatting:** Automatically injects spaces every 4 digits while stripping out non-numeric characters.
*   **Expiry Date Validation:**
    *   Enforces a strict `MM/YY` format (`^\d{2}\/\d{2}$`).
    *   Dynamically extracts the month and year, comparing it against the current JavaScript `Date()` object to reject expired cards and block impossible months (e.g., `13/26`).
*   **CVV & Cardholder Name:**
    *   CVV is strictly locked to 3 or 4 digits.
    *   Name input uses Regex (`/[^A-Z\s]/g`) to sanitize numbers and special characters, enforcing an uppercase format with a minimum length constraint.
*   **Google Pay Validation:**
    *   Uses a domain-specific Regex (`/^[^\s@]+@gmail\.com$/i`) to ensure the user inputs an email address strictly belonging to `@gmail.com`. The "Pay" button remains locked until this evaluates to true.

---

## 🧠 Data Structures & Algorithms (DSA)

### 1. The Luhn Algorithm (Modulus 10 Check)
*   **Implementation:** Used in the frontend `payment-window.html`.
*   **Purpose:** A checksum formula used to validate identification numbers. The script mathematically verifies the credit card number before submission, instantly catching accidental keystrokes.

### 2. Linear Search Algorithm
*   **Implementation:** Used in the `filterTable()` JavaScript function on the `payment-history.html` page.
*   **Purpose:** Iterates row-by-row sequentially through the UI table elements to match the user's search query (Invoice ID, Patient Name, etc.) against the dataset, instantly hiding non-matching elements without needing a database query.

### 3. Stream API Data Pipelines 
*   **Implementation:** Used heavily in `BillingService.java`.
*   **Purpose:** Java 8 Streams are utilized to traverse, filter, and map array datasets efficiently without using bulky `for` loops (e.g., filtering invoices by patient ID).
*   *Note on Sorting:* The data pipeline architecture is built so that sorting algorithms can easily be chained into these streams (e.g., via `Comparator.comparing().reversed()`) to manipulate how records are ordered.

### 4. Dynamic Arrays (Lists)
*   **Implementation:** `List<Invoice>` and `List<String>`.
*   **Purpose:** Java's `ArrayList` handles dynamic memory allocation for storing and mutating parsed invoice records mapped from the flat text database (`billing.txt`).

---

## 🏗️ Object-Oriented Programming (OOP) Concepts Applied

The backend architecture relies on core OOP principles to ensure the code remains modular, reusable, and secure:

### 1. Inheritance
*   **Concept:** Child classes inherit properties from a parent class.
*   **Implementation:** `DirectPayment.java` and `InsurancePayment.java` both use the `extends` keyword to inherit from the `Payment.java` parent class, eliminating redundant field declarations like `amount` and `invoiceId`.

### 2. Abstraction
*   **Concept:** Hiding complex implementation details and showing only the essential blueprints.
*   **Implementation:** The `Payment.java` class is declared as `abstract`. It defines abstract methods like `calculateFinalAmount()`, meaning you cannot instantiate a raw "Payment" object—you must use a concrete implementation.

### 3. Polymorphism
*   **Concept:** The ability for an object to take on many forms (e.g., Method overriding).
*   **Implementation:** Both `DirectPayment` and `InsurancePayment` override (`@Override`) the `calculateFinalAmount()` method:
    *   `DirectPayment` simply returns the raw `amount`.
    *   `InsurancePayment` returns `amount * 0.8` (applying a 20% insurance discount). 
    *   The backend system treats both types as generic `Payment` objects, but executing `.calculateFinalAmount()` dynamically runs the correct mathematical logic based on the specific object type at runtime.

### 4. Encapsulation
*   **Concept:** Binding data and methods together while keeping sensitive data safe from outside interference.
*   **Implementation:** In the `Invoice.java` data model, all fields (like `invoiceId`, `amount`, `status`) are marked `private`. External classes cannot mutate these fields directly; they must use the designated `getter` and `setter` methods, ensuring controlled memory access.
