## Database Schema - Normalized Design

## Table: `users`

**Purpose**: Consolidated authentication and user management

### Columns:

* `id` **BIGINT** PRIMARY KEY AUTO_INCREMENT
* `username` **VARCHAR(255)** NOT NULL UNIQUE
* `email` **VARCHAR(255)** UNIQUE
* `password` **VARCHAR(255)** NOT NULL *(BCrypt hashed)*
* `role` **ENUM('PHARMACY', 'SUPPLIER', 'ORGANIZATION')** NOT NULL
* `enabled` **BOOLEAN** DEFAULT TRUE
* `created_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP
* `updated_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Indexes:

* PRIMARY KEY on `id`
* UNIQUE INDEX on `username`
* UNIQUE INDEX on `email`
* INDEX on `role`

### Migration from:

* `login1` table

### Changes:

* Added `id` as primary key
* Changed `category` to `role` with ENUM type
* Added `enabled` flag for account management
* Added `created_at` and `updated_at` timestamps
* Password will be **BCrypt hashed** (currently plain text in some records)

---

## Table: `pharmacies`

**Purpose**: Store pharmacy profile information

### Columns:

* `id` **BIGINT** PRIMARY KEY AUTO_INCREMENT
* `user_id` **BIGINT** NOT NULL UNIQUE
* `pharmacy_name` **VARCHAR(255)** NOT NULL
* `location` **VARCHAR(255)** NOT NULL
* `pin_code` **INT** NOT NULL
* `contact_number` **BIGINT** NOT NULL
* `established_date` **DATE** NOT NULL
* `created_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP
* `updated_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Foreign Keys:

* `user_id` REFERENCES `users(id)` ON DELETE CASCADE

### Indexes:

* PRIMARY KEY on `id`
* UNIQUE INDEX on `user_id`
* INDEX on `location`

### Migration from:

* `pharmacy` table

### Changes:

* Added `id` as primary key
* Changed `username` to `user_id` foreign key
* Renamed:

    * `pharmacyname` → `pharmacy_name`
    * `pinno` → `pin_code`
    * `contactnumber` → `contact_number`
    * `established` → `established_date`
* Added timestamps

---

## Table: `medicines`

**Purpose**: Master catalog of all medicines

### Columns:

* `id` **BIGINT** PRIMARY KEY AUTO_INCREMENT
* `medicine_code` **VARCHAR(255)** NOT NULL UNIQUE
* `description` **VARCHAR(500)** NOT NULL
* `created_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP
* `updated_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Indexes:

* PRIMARY KEY on `id`
* UNIQUE INDEX on `medicine_code`

### Migration from:

* Extracted from dynamic pharmacy tables and `medsup` table

### Changes:

* New centralized table
* `medicine_code` replaces `med_id`
* Normalized to eliminate duplication

---

## Table: `pharmacy_inventory`

**Purpose**: Track medicine stock levels for each pharmacy (replaces dynamic tables)

### Columns:

* `id` **BIGINT** PRIMARY KEY AUTO_INCREMENT
* `pharmacy_id` **BIGINT** NOT NULL
* `medicine_id` **BIGINT** NOT NULL
* `price` **DECIMAL(10,2)**
* `minimum_quantity` **INT** NOT NULL DEFAULT 0
* `current_quantity` **INT** NOT NULL DEFAULT 0
* `last_updated` **DATE**
* `created_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP
* `updated_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Foreign Keys:

* `pharmacy_id` REFERENCES `pharmacies(id)` ON DELETE CASCADE
* `medicine_id` REFERENCES `medicines(id)` ON DELETE CASCADE

### Unique Constraint:

* UNIQUE(`pharmacy_id`, `medicine_id`)

### Indexes:

* PRIMARY KEY on `id`
* UNIQUE INDEX on (`pharmacy_id`, `medicine_id`)
* INDEX on `pharmacy_id`
* INDEX on `medicine_id`
* INDEX on `current_quantity` *(for low stock queries)*

### Migration from:

* Dynamic tables (`baby123`, `mims123`, etc.) and `admin` table

### Changes:

* Consolidated all pharmacy-specific medicine tables into one
* `med_id` → `medicine_id` (foreign key)
* `quantity` → `current_quantity`
* `min_count` → `minimum_quantity`
* Added proper foreign key relationships

---

## Table: `supplier_catalog`

**Purpose**: Track which medicines each supplier can provide

### Columns:

* `id` **BIGINT** PRIMARY KEY AUTO_INCREMENT
* `supplier_id` **BIGINT** NOT NULL
* `medicine_id` **BIGINT** NOT NULL
* `availability` **ENUM('AVAILABLE', 'NOT_AVAILABLE')** NOT NULL DEFAULT 'AVAILABLE'
* `created_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP
* `updated_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Foreign Keys:

* `supplier_id` REFERENCES `users(id)` ON DELETE CASCADE
* `medicine_id` REFERENCES `medicines(id)` ON DELETE CASCADE

### Unique Constraint:

* UNIQUE(`supplier_id`, `medicine_id`)

### Indexes:

* PRIMARY KEY on `id`
* UNIQUE INDEX on (`supplier_id`, `medicine_id`)
* INDEX on `supplier_id`
* INDEX on `medicine_id`
* INDEX on `availability`

### Migration from:

* `medsup` table

### Changes:

* `username` → `supplier_id` foreign key
* `med_id` → `medicine_id` foreign key
* Removed `descrip` (now in `medicines` table)
* `availability` changed to ENUM type

---

## Table: `orders`

**Purpose**: Track medicine orders from pharmacies to suppliers

### Columns:

* `id` **BIGINT** PRIMARY KEY AUTO_INCREMENT
* `pharmacy_id` **BIGINT** NOT NULL
* `supplier_id` **BIGINT** NOT NULL
* `medicine_id` **BIGINT** NOT NULL
* `quantity` **INT** NOT NULL
* `status` **ENUM('NOT_SUPPLIED', 'SUPPLIED', 'RECEIVED')** NOT NULL DEFAULT 'NOT_SUPPLIED'
* `ordered_date` **DATE** NOT NULL
* `supplied_date` **DATE**
* `received_date` **DATE**
* `created_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP
* `updated_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Foreign Keys:

* `pharmacy_id` REFERENCES `pharmacies(id)` ON DELETE CASCADE
* `supplier_id` REFERENCES `users(id)` ON DELETE CASCADE
* `medicine_id` REFERENCES `medicines(id)` ON DELETE CASCADE

### Indexes:

* PRIMARY KEY on `id`
* INDEX on `pharmacy_id`
* INDEX on `supplier_id`
* INDEX on `status`
* INDEX on `ordered_date`

### Migration from:

* `med_order` table

### Changes:

* `order_id` → `id` (AUTO_INCREMENT)
* `username` → `pharmacy_id` foreign key
* `sup_id` → `supplier_id` foreign key
* `med_id` → `medicine_id` foreign key
* `ordered_on` → `ordered_date`
* Added `supplied_date` and `received_date` for tracking
* `status` changed to ENUM type

---

## Table: `messages`

**Purpose**: Communication from organization to pharmacies

### Columns:

* `id` **BIGINT** PRIMARY KEY AUTO_INCREMENT
* `pharmacy_id` **BIGINT** NOT NULL
* `medicine_id` **BIGINT** NOT NULL
* `message_text` **VARCHAR(500)** NOT NULL
* `sent_date` **DATE** NOT NULL
* `read_status` **BOOLEAN** DEFAULT FALSE
* `created_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP
* `updated_at` **TIMESTAMP** DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Foreign Keys:

* `pharmacy_id` REFERENCES `pharmacies(id)` ON DELETE CASCADE
* `medicine_id` REFERENCES `medicines(id)` ON DELETE CASCADE

### Indexes:

* PRIMARY KEY on `id`
* INDEX on `pharmacy_id`
* INDEX on `sent_date`
* INDEX on `read_status`

### Migration from:

* `message` table

### Changes:

* Added `id` AUTO_INCREMENT primary key
* `username` → `pharmacy_id` foreign key
* `med_id` → `medicine_id` foreign key
* `msg` → `message_text`
* `send_on` → `sent_date`
* Added `read_status` for tracking
* Removed composite primary key

---

## Spring Boot Project Structure

mediport/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── mediport/
│   │   │           ├── MediportApplication.java
│   │   │           ├── config/
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   └── WebConfig.java
│   │   │           ├── entity/
│   │   │           │   ├── User.java
│   │   │           │   ├── Pharmacy.java
│   │   │           │   ├── Medicine.java
│   │   │           │   ├── PharmacyInventory.java
│   │   │           │   ├── SupplierCatalog.java
│   │   │           │   ├── Order.java
│   │   │           │   └── Message.java
│   │   │           ├── repository/
│   │   │           │   ├── UserRepository.java
│   │   │           │   ├── PharmacyRepository.java
│   │   │           │   ├── MedicineRepository.java
│   │   │           │   ├── PharmacyInventoryRepository.java
│   │   │           │   ├── SupplierCatalogRepository.java
│   │   │           │   ├── OrderRepository.java
│   │   │           │   └── MessageRepository.java
│   │   │           ├── service/
│   │   │           │   ├── UserService.java
│   │   │           │   ├── PharmacyService.java
│   │   │           │   ├── InventoryService.java
│   │   │           │   ├── SupplierService.java
│   │   │           │   ├── OrderService.java
│   │   │           │   └── MessageService.java
│   │   │           ├── controller/
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── PharmacyController.java
│   │   │           │   ├── SupplierController.java
│   │   │           │   └── OrganizationController.java
│   │   │           ├── dto/
│   │   │           │   ├── UserRegistrationDto.java
│   │   │           │   ├── PharmacyProfileDto.java
│   │   │           │   ├── InventoryDto.java
│   │   │           │   ├── OrderDto.java
│   │   │           │   └── MessageDto.java
│   │   │           └── enums/
│   │   │               ├── UserRole.java
│   │   │               ├── OrderStatus.java
│   │   │               └── Availability.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   ├── phar.css
│   │       │   │   └── signup_styles.css
│   │       │   └── js/
│   │       │       └── (if needed)
│   │       └── templates/
│   │           ├── login.html
│   │           ├── signup.html
│   │           ├── pharmacy/
│   │           │   ├── home.html
│   │           │   ├── stock.html
│   │           │   ├── order.html
│   │           │   ├── message.html
│   │           │   └── profile.html
│   │           ├── supplier/
│   │           │   ├── home.html
│   │           │   └── list.html
│   │           └── organization/
│   │               ├── home.html
│   │               ├── view.html
│   │               ├── order.html
│   │               └── message.html
│   └── test/
│       └── java/
│           └── com/
│               └── mediport/
└── pom.xml

---

## Entity Specifications
## Entity: `User`

**File**: `src/main/java/com/mediport/entity/User.java`
**Purpose**: Represents all system users (pharmacies, suppliers, organizations)

### 🏷 Annotations:

* `@Entity`
* `@Table(name = "users")`

### 🧩 Fields:

* `id (Long)`

    * `@Id`
    * `@GeneratedValue(strategy = GenerationType.IDENTITY)`

* `username (String)`

    * `@Column(unique = true, nullable = false)`

* `email (String)`

    * `@Column(unique = true)`

* `password (String)`

    * `@Column(nullable = false)` *(BCrypt hashed)*

* `role (UserRole enum)`

    * `@Enumerated(EnumType.STRING)`
    * `@Column(nullable = false)`

* `enabled (Boolean)`

    * `@Column(nullable = false)`

* `createdAt (LocalDateTime)`

    * `@Column(name = "created_at")`

* `updatedAt (LocalDateTime)`

    * `@Column(name = "updated_at")`

### 🔗 Relationships:

* `@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)`
  → `Pharmacy pharmacy` *(if role = PHARMACY)*

### 🔧 Methods:

* Standard **getters/setters**
* `equals()` and `hashCode()` based on `id`

---

## Entity: `Pharmacy`

**File**: `src/main/java/com/mediport/entity/Pharmacy.java`
**Purpose**: Pharmacy profile information

### 🏷 Annotations:

* `@Entity`
* `@Table(name = "pharmacies")`

### 🧩 Fields:

* `id (Long)`

    * `@Id`
    * `@GeneratedValue(strategy = GenerationType.IDENTITY)`

* `user (User)`

    * `@OneToOne`
    * `@JoinColumn(name = "user_id", unique = true, nullable = false)`

* `pharmacyName (String)`

    * `@Column(name = "pharmacy_name", nullable = false)`

* `location (String)`

    * `@Column(nullable = false)`

* `pinCode (Integer)`

    * `@Column(name = "pin_code", nullable = false)`

* `contactNumber (Long)`

    * `@Column(name = "contact_number", nullable = false)`

* `establishedDate (LocalDate)`

    * `@Column(name = "established_date", nullable = false)`

* `createdAt (LocalDateTime)`

* `updatedAt (LocalDateTime)`

### 🔗 Relationships:

* `@OneToOne` → `User user`
* `@OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL)`
  → `Set<PharmacyInventory> inventory`

---

## Entity: `Medicine`

**File**: `src/main/java/com/mediport/entity/Medicine.java`
**Purpose**: Master medicine catalog

### 🏷 Annotations:

* `@Entity`
* `@Table(name = "medicines")`

### 🧩 Fields:

* `id (Long)`

    * `@Id`
    * `@GeneratedValue(strategy = GenerationType.IDENTITY)`

* `medicineCode (String)`

    * `@Column(name = "medicine_code", unique = true, nullable = false)`

* `description (String)`

    * `@Column(nullable = false, length = 500)`

* `createdAt (LocalDateTime)`

* `updatedAt (LocalDateTime)`

---

## Entity: `PharmacyInventory`

**File**: `src/main/java/com/mediport/entity/PharmacyInventory.java`
**Purpose**: Medicine stock for each pharmacy

### 🏷 Annotations:

* `@Entity`
* `@Table(name = "pharmacy_inventory", uniqueConstraints = @UniqueConstraint(columnNames = {"pharmacy_id", "medicine_id"}))`

### 🧩 Fields:

* `id (Long)`

    * `@Id`
    * `@GeneratedValue(strategy = GenerationType.IDENTITY)`

* `pharmacy (Pharmacy)`

    * `@ManyToOne`
    * `@JoinColumn(name = "pharmacy_id", nullable = false)`

* `medicine (Medicine)`

    * `@ManyToOne`
    * `@JoinColumn(name = "medicine_id", nullable = false)`

* `price (BigDecimal)`

    * `@Column(precision = 10, scale = 2)`

* `minimumQuantity (Integer)`

    * `@Column(name = "minimum_quantity", nullable = false)`

* `currentQuantity (Integer)`

    * `@Column(name = "current_quantity", nullable = false)`

* `lastUpdated (LocalDate)`

    * `@Column(name = "last_updated")`

* `createdAt (LocalDateTime)`

* `updatedAt (LocalDateTime)`

### 🧠 Business Logic:

* `isLowStock()` → returns `true` if `currentQuantity < minimumQuantity`

---

## Entity: `SupplierCatalog`

**File**: `src/main/java/com/mediport/entity/SupplierCatalog.java`
**Purpose**: Supplier's available medicines

### 🏷 Annotations:

* `@Entity`
* `@Table(name = "supplier_catalog", uniqueConstraints = @UniqueConstraint(columnNames = {"supplier_id", "medicine_id"}))`

### 🧩 Fields:

* `id (Long)`

    * `@Id`
    * `@GeneratedValue(strategy = GenerationType.IDENTITY)`

* `supplier (User)`

    * `@ManyToOne`
    * `@JoinColumn(name = "supplier_id", nullable = false)`

* `medicine (Medicine)`

    * `@ManyToOne`
    * `@JoinColumn(name = "medicine_id", nullable = false)`

* `availability (Availability enum)`

    * `@Enumerated(EnumType.STRING)`
    * `@Column(nullable = false)`

* `createdAt (LocalDateTime)`

* `updatedAt (LocalDateTime)`

---

## Entity: `Order`

**File**: `src/main/java/com/mediport/entity/Order.java`
**Purpose**: Medicine orders

### 🏷 Annotations:

* `@Entity`
* `@Table(name = "orders")`

### 🧩 Fields:

* `id (Long)`

    * `@Id`
    * `@GeneratedValue(strategy = GenerationType.IDENTITY)`

* `pharmacy (Pharmacy)`

    * `@ManyToOne`
    * `@JoinColumn(name = "pharmacy_id", nullable = false)`

* `supplier (User)`

    * `@ManyToOne`
    * `@JoinColumn(name = "supplier_id", nullable = false)`

* `medicine (Medicine)`

    * `@ManyToOne`
    * `@JoinColumn(name = "medicine_id", nullable = false)`

* `quantity (Integer)`

    * `@Column(nullable = false)`

* `status (OrderStatus enum)`

    * `@Enumerated(EnumType.STRING)`
    * `@Column(nullable = false)`

* `orderedDate (LocalDate)`

    * `@Column(name = "ordered_date", nullable = false)`

* `suppliedDate (LocalDate)`

    * `@Column(name = "supplied_date")`

* `receivedDate (LocalDate)`

    * `@Column(name = "received_date")`

* `createdAt (LocalDateTime)`

* `updatedAt (LocalDateTime)`

---

## Entity: `Message`

**File**: `src/main/java/com/mediport/entity/Message.java`
**Purpose**: Organization messages to pharmacies

### 🏷 Annotations:

* `@Entity`
* `@Table(name = "messages")`

### 🧩 Fields:

* `id (Long)`

    * `@Id`
    * `@GeneratedValue(strategy = GenerationType.IDENTITY)`

* `pharmacy (Pharmacy)`

    * `@ManyToOne`
    * `@JoinColumn(name = "pharmacy_id", nullable = false)`

* `medicine (Medicine)`

    * `@ManyToOne`
    * `@JoinColumn(name = "medicine_id", nullable = false)`

* `messageText (String)`

    * `@Column(name = "message_text", nullable = false, length = 500)`

* `sentDate (LocalDate)`

    * `@Column(name = "sent_date", nullable = false)`

* `readStatus (Boolean)`

    * `@Column(name = "read_status")`

* `createdAt (LocalDateTime)`

* `updatedAt (LocalDateTime)`

---

## Enum Specifications

### Enum: UserRole
File: src/main/java/com/mediport/enums/UserRole.java
Values:
- PHARMACY
- SUPPLIER
- ORGANIZATION
---
### Enum: OrderStatus
File: src/main/java/com/mediport/enums/OrderStatus.java
Values:
- NOT_SUPPLIED
- SUPPLIED
- RECEIVED
---
### Enum: Availability
File: src/main/java/com/mediport/enums/Availability.java
Values:
- AVAILABLE
- NOT_AVAILABLE

---

## Repository Layer Specifications

All repositories extend JpaRepository with appropriate entity type and ID type

## Repository: UserRepository

**File**: `src/main/java/com/mediport/repository/UserRepository.java`
**Interface extends**: `JpaRepository<User, Long>`

### Custom Query Methods:

* `Optional<User> findByUsername(String username)`
* `Optional<User> findByEmail(String email)`
* `List<User> findByRole(UserRole role)`
* `boolean existsByUsername(String username)`
* `boolean existsByEmail(String email)`

---

## Repository: PharmacyRepository

**File**: `src/main/java/com/mediport/repository/PharmacyRepository.java`
**Interface extends**: `JpaRepository<Pharmacy, Long>`

### Custom Query Methods:

* `Optional<Pharmacy> findByUser(User user)`
* `Optional<Pharmacy> findByUserId(Long userId)`
* `List<Pharmacy> findAll()` (inherited)

---

## Repository: MedicineRepository

**File**: `src/main/java/com/mediport/repository/MedicineRepository.java`
**Interface extends**: `JpaRepository<Medicine, Long>`

### Custom Query Methods:

* `Optional<Medicine> findByMedicineCode(String medicineCode)`
* `boolean existsByMedicineCode(String medicineCode)`

---

## Repository: PharmacyInventoryRepository

**File**: `src/main/java/com/mediport/repository/PharmacyInventoryRepository.java`
**Interface extends**: `JpaRepository<PharmacyInventory, Long>`

### Custom Query Methods:

* `List<PharmacyInventory> findByPharmacy(Pharmacy pharmacy)`
* `List<PharmacyInventory> findByPharmacyId(Long pharmacyId)`
* `Optional<PharmacyInventory> findByPharmacyAndMedicine(Pharmacy pharmacy, Medicine medicine)`
* `@Query("SELECT pi FROM PharmacyInventory pi WHERE pi.currentQuantity < pi.minimumQuantity")` - `List<PharmacyInventory> findLowStockItems()`
* `@Query("SELECT pi FROM PharmacyInventory pi WHERE pi.pharmacy.id = :pharmacyId AND pi.currentQuantity < pi.minimumQuantity")` - `List<PharmacyInventory> findLowStockItemsByPharmacy(@Param("pharmacyId") Long pharmacyId)`

---

## Repository: SupplierCatalogRepository

**File**: `src/main/java/com/mediport/repository/SupplierCatalogRepository.java`
**Interface extends**: `JpaRepository<SupplierCatalog, Long>`

### Custom Query Methods:

* `List<SupplierCatalog> findBySupplier(User supplier)`
* `List<SupplierCatalog> findBySupplierId(Long supplierId)`
* `List<SupplierCatalog> findByAvailability(Availability availability)`
* `List<SupplierCatalog> findByMedicine(Medicine medicine)`

---

## Repository: OrderRepository

**File**: `src/main/java/com/mediport/repository/OrderRepository.java`
**Interface extends**: `JpaRepository<Order, Long>`

### Custom Query Methods:

* `List<Order> findByPharmacy(Pharmacy pharmacy)`
* `List<Order> findByPharmacyId(Long pharmacyId)`
* `List<Order> findBySupplier(User supplier)`
* `List<Order> findBySupplierId(Long supplierId)`
* `List<Order> findByStatus(OrderStatus status)`
* `List<Order> findByPharmacyAndStatus(Pharmacy pharmacy, OrderStatus status)`

---

## Repository: MessageRepository

**File**: `src/main/java/com/mediport/repository/MessageRepository.java`
**Interface extends**: `JpaRepository<Message, Long>`

### Custom Query Methods:

* `List<Message> findByPharmacy(Pharmacy pharmacy)`
* `List<Message> findByPharmacyId(Long pharmacyId)`
* `List<Message> findByReadStatus(Boolean readStatus)`
* `List<Message> findByPharmacyAndReadStatus(Pharmacy pharmacy, Boolean readStatus)`
* `List<Message> findAllByOrderBySentDateDesc()`

---
## Service Layer Specifications

## Service: `UserService`

**File**: `src/main/java/com/mediport/service/UserService.java`

### Dependencies:

* `UserRepository`
* `PasswordEncoder (BCrypt)`

### Methods:

* `User registerUser(UserRegistrationDto dto)`

    * Validate username and email uniqueness
    * Hash password using BCrypt
    * Set `enabled = true`
    * Save user entity
    * Return saved user

* `User findByUsername(String username)`

    * Query user by username
    * Throw exception if not found
    * Return user

* `boolean existsByUsername(String username)`

    * Check username existence
    * Return boolean

* `boolean existsByEmail(String email)`

    * Check email existence
    * Return boolean

---

## Service: `PharmacyService`

**File**: `src/main/java/com/mediport/service/PharmacyService.java`

### Dependencies:

* `PharmacyRepository`
* `UserRepository`

### Methods:

* `Pharmacy createPharmacyProfile(PharmacyProfileDto dto, User user)`

    * Validate user has `PHARMACY` role
    * Create `Pharmacy` entity linked to user
    * Save pharmacy
    * Return saved pharmacy

* `Pharmacy findByUser(User user)`

    * Query pharmacy by user
    * Throw exception if not found
    * Return pharmacy

* `Pharmacy findByUserId(Long userId)`

    * Query pharmacy by user ID
    * Throw exception if not found
    * Return pharmacy

* `List<Pharmacy> findAllPharmacies()`

    * Query all pharmacies
    * Return list

---

## Service: `InventoryService`

**File**: `src/main/java/com/mediport/service/InventoryService.java`

### Dependencies:

* `PharmacyInventoryRepository`
* `PharmacyRepository`
* `MedicineRepository`

### Methods:

* `List<PharmacyInventory> getPharmacyStock(Long pharmacyId)`

    * Query all inventory for pharmacy
    * Return list sorted by medicine code

* `PharmacyInventory addOrUpdateStock(InventoryDto dto, Long pharmacyId)`

    * Find or create inventory entry
    * If new: Set medicine, pharmacy, price, minimumQuantity, currentQuantity
    * If existing: Update price (if provided), currentQuantity
    * Set `lastUpdated` to current date
    * Save inventory
    * Return saved inventory

* `List<PharmacyInventory> getAllLowStockItems()`

    * Query all items where `currentQuantity < minimumQuantity`
    * Return list for organization dashboard

* `List<PharmacyInventory> getLowStockItemsByPharmacy(Long pharmacyId)`

    * Query low stock items for specific pharmacy
    * Return list

* `void updateMinimumQuantity(Long pharmacyId, String medicineCode, Integer newMinimum)`

    * Find inventory by pharmacy and medicine code
    * Update `minimumQuantity`
    * Save inventory

---

## Service: `OrderService`

**File**: `src/main/java/com/mediport/service/OrderService.java`

### Dependencies:

* `OrderRepository`
* `PharmacyRepository`
* `UserRepository` (for suppliers)
* `MedicineRepository`
* `PharmacyInventoryRepository`

### Methods:

* `Order placeOrder(OrderDto dto, Long pharmacyId)`

    * Validate supplier exists and has `SUPPLIER` role
    * Validate medicine exists
    * Create `Order` entity
    * Set `status = NOT_SUPPLIED`
    * Set `orderedDate = current date`
    * Save order
    * Return saved order

* `List<Order> getPharmacyOrders(Long pharmacyId)`

    * Query orders by pharmacy
    * Return list sorted by `orderedDate` descending

* `List<Order> getSupplierOrders(Long supplierId)`

    * Query orders by supplier
    * Return list sorted by `orderedDate` descending

* `Order updateOrderStatus(Long orderId, OrderStatus newStatus)`

    * Find order by ID
    * Update status
    * If status = `SUPPLIED`: Set `suppliedDate = current date`
    * If status = `RECEIVED`:

        * Set `receivedDate = current date`
        * Update pharmacy inventory: `currentQuantity += order.quantity`
    * Save order
    * Return updated order

---

## Service: `SupplierService`

**File**: `src/main/java/com/mediport/service/SupplierService.java`

### Dependencies:

* `SupplierCatalogRepository`
* `UserRepository`
* `MedicineRepository`

### Methods:

* `List<SupplierCatalog> getSupplierCatalog(Long supplierId)`

    * Query catalog entries for supplier
    * Return list

* `SupplierCatalog addOrUpdateCatalogItem(Long supplierId, String medicineCode, String description, Availability availability)`

    * Find or create medicine (if description provided)
    * Find or create catalog entry
    * Update availability
    * Save catalog entry
    * Return saved entry

* `List<SupplierCatalog> getAllAvailableSuppliers()`

    * Query all catalog entries
    * Return list for pharmacy ordering

---

## Service: `MessageService`

**File**: `src/main/java/com/mediport/service/MessageService.java`

### Dependencies:

* `MessageRepository`
* `PharmacyRepository`
* `MedicineRepository`

### Methods:

* `Message sendMessage(Long pharmacyId, String medicineCode, String messageText)`

    * Find pharmacy by ID
    * Find medicine by code
    * Create `Message` entity
    * Set `sentDate = current date`
    * Set `readStatus = false`
    * Save message
    * Return saved message

* `List<Message> getPharmacyMessages(Long pharmacyId)`

    * Query messages by pharmacy
    * Return list sorted by `sentDate` descending

* `List<Message> getAllMessages()`

    * Query all messages (for organization view)
    * Return list sorted by `sentDate` descending

* `void markAsRead(Long messageId)`

    * Find message by ID
    * Set `readStatus = true`
    * Save message

---

## Controller Layer Specifications

## Controller: `AuthController`

**File**: `src/main/java/com/mediport/controller/AuthController.java`
**Base Path**: `/`

### Dependencies:

* `UserService`
* `PharmacyService`

---

### `GET /signup`

**Purpose**: Display signup page
**View**: `signup.html`
**Model Attributes**: None
**Returns**: Signup page with registration form

---

### `POST /signup`

**Purpose**: Process user registration
**Request Parameters**:

* `username` (String, required)
* `email` (String, optional)
* `password` (String, required)
* `category` (String, required) - Maps to `UserRole` enum

**Process**:

* Validate username and email uniqueness
* Create `UserRegistrationDto`
* Call `UserService.registerUser(dto)`
* Store username in session
* Redirect based on role:

    * `PHARMACY`: `/signup/pharprof.html`
    * `SUPPLIER`: `/login`
    * `ORGANIZATION`: `/login`

**Error Handling**:

* If username/email exists → redirect to `/signup` with error message

---

### `GET /login`

**Purpose**: Display login page
**View**: `login.html`
**Security**: Uses Spring Security
**Returns**: Login page

---

### `POST /login` (Handled by Spring Security)

**Purpose**: Authenticate user
**Process**:

* On success, redirect based on role:

    * `PHARMACY`: `/pharmacy/home`
    * `SUPPLIER`: `/supplier/home`
    * `ORGANIZATION`: `/organization/home`
* On failure → `/login?error`

---

### `GET /pharprof.html` or `GET /signup/pharprof`

**Purpose**: Display pharmacy profile setup form
**View**: `pharprof.html`
**Security**: `PHARMACY` role required
**Returns**: Pharmacy profile form

---

### `POST /pharprof`

**Purpose**: Save pharmacy profile
**Request Parameters**:

* `pharmacyname` (String)
* `location` (String)
* `pinno` (Integer)
* `contactnumber` (Long)
* `established` (LocalDate)

**Process**:

* Get authenticated user
* Create `PharmacyProfileDto`
* Call `PharmacyService.createPharmacyProfile(dto, user)`
* Redirect to `/pharmacy/home`

**Returns**: Redirect to pharmacy home page

---

## Controller: `PharmacyController`

**File**: `src/main/java/com/mediport/controller/PharmacyController.java`
**Base Path**: `/pharmacy`
**Security**: All endpoints require `PHARMACY` role

### Dependencies:

* `PharmacyService`
* `InventoryService`
* `OrderService`
* `MessageService`
* `UserService`

---

### `GET /home` or `GET /pharome-details` or `GET /pharome.html`

**Purpose**: Display pharmacy home page
**View**: `pharmacy/home.html`
**Model Attributes**:

* `pharmacyDetails`

**Process**:

* Get authenticated user
* Query pharmacy profile
* Redirect to `/pharprof.html` if not found

---

### `GET /stock` or `GET /pharstock-details` or `GET /pharstock.html`

**Purpose**: Display pharmacy stock
**View**: `pharmacy/stock.html`
**Model Attributes**:

* `stockData` (List of `PharmacyInventory`)
* `usernameconst1` (String)

**Thymeleaf Logic**:

* Red background if `currentQuantity < minimumQuantity`

---

### `POST /newstock`

**Purpose**: Add/update stock
**Request Parameters**:

* `med_id`, `descrip`, `price`, `min_count`, `quantity`

**Process**:

* Create `InventoryDto`
* Call `InventoryService.addOrUpdateStock()`

**Returns**: Redirect to stock page

---

### `GET /order` or `GET /pharorder-details` or `GET /pharorder.html`

**Purpose**: Show orders + supplier catalog
**View**: `pharmacy/order.html`
**Model Attributes**:

* `orderData`
* `suplistData1`
* `usernameconst1`

---

### `POST /orderstock`

**Purpose**: Place a new order
**Request Parameters**:

* `med_id`, `quantity`, `sup_id`

**Process**:

* Create `OrderDto`
* Call `OrderService.placeOrder()`

**Returns**: Redirect to order page

---

### `POST /orderpharstatus`

**Purpose**: Mark order as received
**Request Parameters**:

* `order_id`, `status`

**Process**:

* Validate order belongs to pharmacy
* Call `OrderService.updateOrderStatus(..., RECEIVED)`

---

### `GET /message` or `GET /pharmessage-details` or `GET /pharmessage.html`

**Purpose**: View messages
**View**: `pharmacy/message.html`
**Model Attributes**:

* `pharmsgData`
* `usernameconst1`

---

## Controller: `SupplierController`

**File**: `src/main/java/com/mediport/controller/SupplierController.java`
**Base Path**: `/supplier`
**Security**: All endpoints require `SUPPLIER` role

### Dependencies:

* `SupplierService`
* `OrderService`
* `PharmacyService`

---

### `GET /home` or `GET /supplier-details` or `GET /supome.html`

**Purpose**: Supplier home page with orders
**View**: `supplier/home.html`
**Model Attributes**:

* `supplierData`
* `pharmacyData`
* `supplierconst1`

---

### `POST /ordersupstatus`

**Purpose**: Mark order as supplied
**Request Parameters**:

* `order_id`, `status`

**Process**:

* Validate ownership
* Call `OrderService.updateOrderStatus(..., SUPPLIED)`

---

### `GET /list` or `GET /suplist-details` or `GET /suplist.html`

**Purpose**: Manage supplier catalog
**View**: `supplier/list.html`
**Model Attributes**:

* `suplistData`
* `supplierconst1`

---

### `POST /listsup`

**Purpose**: Add/update catalog item
**Request Parameters**:

* `med_id`, `descrip`, `availability`

**Process**:

* Map availability
* Call `SupplierService.addOrUpdateCatalogItem()`

---

## Controller: `OrganizationController`

**File**: `src/main/java/com/mediport/controller/OrganizationController.java`
**Base Path**: `/organization`
**Security**: All endpoints require `ORGANIZATION` role

### Dependencies:

* `InventoryService`
* `PharmacyService`
* `OrderService`
* `MessageService`

**Session Attributes**:

* `selectedPharmacyId` (Long)

---

### `GET /home` or `GET /organization-details` or `GET /orgome.html`

**Purpose**: Dashboard with low stock
**View**: `organization/home.html`
**Model Attributes**:

* `organData`

**Logic**: Highlight rows with low stock

---

### `POST /changemin`

**Purpose**: Update minimum quantity
**Request Parameters**:

* `username`, `med_id`, `min_count`

**Process**:

* Call `InventoryService.updateMinimumQuantity()`

---

### `GET /view` or `GET /orgview-details` or `GET /orgview.html`

**Purpose**: View specific pharmacy stock
**View**: `organization/view.html`
**Model Attributes**:

* `pharmacyData`, `orgstockData` (if selected)

---

### `POST /orgshowstock`

**Purpose**: Select pharmacy for stock view
**Request Parameters**:

* `username`

**Process**:

* Find pharmacy
* Store `selectedPharmacyId` in session
* Redirect to `/organization/view`

---

### `GET /order` or `GET /orgorder-details` or `GET /orgorder.html`

**Purpose**: View pharmacy orders
**View**: `organization/order.html`
**Model Attributes**:

* `orgorderData`

---

### `POST /orgshoworder`

**Purpose**: Select pharmacy to view orders
**Request Parameters**:

* `username`

**Process**:

* Store pharmacy ID in session
* Redirect to `/organization/order`

---

### `GET /message` or `GET /orgmessage-details` or `GET /orgmessage.html`

**Purpose**: View/send messages
**View**: `organization/message.html`
**Model Attributes**:

* `orgmsgData`

---

### `POST /orgmsg`

**Purpose**: Send message to pharmacy
**Request Parameters**:

* `username`, `med_id`, `msg`

**Process**:

* Call `MessageService.sendMessage(...)`
* Redirect to `/organization/message`

---

## Security Configuration

**File**: `src/main/java/com/mediport/config/SecurityConfig.java`
**Purpose**: Configure Spring Security

---

## 🔐 Password Encoding

* Use `BCryptPasswordEncoder` with default strength **(10)**

---

## 👤 Authentication

* **Form-based login** at `/login`
* **UserDetailsService** implementation:

    * Load user by username from `UserRepository`
    * Map `User` entity to `UserDetails` with role as authority

---

## 🔒 Authorization Rules

* `/`, `/login`, `/signup`, `/css/**`, `/js/**` — `permitAll`
* `/pharmacy/**` — `hasRole('PHARMACY')`
* `/supplier/**` — `hasRole('SUPPLIER')`
* `/organization/**` — `hasRole('ORGANIZATION')`
* All other requests — `authenticated`

---

## 🔑 Login Configuration

* **Login page**: `/login`
* **Login processing URL**: `/login` (POST)
* **Success handler**: Custom success handler that redirects based on role:

    * `PHARMACY` → `/pharmacy/home`
    * `SUPPLIER` → `/supplier/home`
    * `ORGANIZATION` → `/organization/home`
* **Failure URL**: `/login?error=true`

---

## 🚪 Logout Configuration

* **Logout URL**: `/logout`
* **Logout success URL**: `/login?logout=true`
* **Invalidate session**: `true`

---

## 🧾 Session Management

* **Session creation policy**: `IF_REQUIRED`
* **Maximum sessions per user**: `1`
* **Session fixation protection**: `migrateSession`
---


