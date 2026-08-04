# Task 3: Relational Database Design

My solution for Task 3 of the Walmart Advanced Software Engineering Job Simulation on Forage.

The task was to design a normalized relational database for Walmart's pet department. The database combines product, customer transaction, and shipment information into one consistent schema.

## Entity Relationship Diagram

[View the Walmart Pet Department ERD as a PDF](walmart-pet-department-erd.pdf)

## Requirements Covered

The design supports:

- Pet food with weight, flavor, and a target health condition.
- Pet toys with material and durability.
- Pet apparel with color, size, and care instructions.
- A manufacturer and one or more applicable animal types for every product.
- Customers with names and unique email addresses.
- Transactions containing one or more products and their quantities.
- Walmart locations with names and ZIP codes.
- Shipments with origin and destination locations plus product quantities.

## Schema Design

### Products and Manufacturers

`Product` stores the attributes shared by every product: its identifier, name, and manufacturer. `Manufacturer` is a separate entity, so its name is stored once and referenced by any number of products.

The category-specific attributes are stored in three subtype tables:

- `PetFood` stores weight, weight unit, flavor, and target health condition.
- `PetToy` stores material and durability.
- `PetApparel` stores color, size, and care instructions.

Each subtype uses `product_id` as both its primary key and a foreign key to `Product`. The subtypes are complete and disjoint: every product belongs to exactly one subtype.

### Products and Animals

A product can be suitable for several animal types, and an animal type can have many suitable products. `ProductAnimal` resolves this many-to-many relationship with the composite primary key `(product_id, animal_id)`.

This design allows a single product to be associated with animals such as dogs and cats without duplicating the product record.

### Customers and Transactions

`CustomerTransaction` records the customer and transaction date. A customer can make many transactions, while each transaction belongs to exactly one customer.

`TransactionItem` connects transactions to products and stores the quantity purchased. Its composite primary key `(transaction_id, product_id)` prevents duplicate product lines within the same transaction. Multiple units of a product are represented by a positive `quantity` value.

### Locations and Shipments

`Location` stores a Walmart location's name and ZIP code. ZIP codes use a character type because they can begin with zero or include a ZIP+4 hyphen.

`Shipment` contains `origin_location_id` and `destination_location_id`. Both columns reference `Location.location_id`, but their names preserve their distinct origin and destination roles. The diagram uses a shared relationship to the `Location` entity for these role-specific references.

`ShipmentItem` connects shipments to products and records a positive quantity for each product in the shipment.

## Normalization

### First Normal Form

Each table has a primary key, and each column contains a single atomic value. Repeating collections, such as the products in a transaction or shipment, are represented as separate line-item rows.

### Second Normal Form

Attributes in tables with composite keys depend on the complete key. For example, a transaction quantity describes a particular product in a particular transaction, while a shipment quantity describes a particular product in a particular shipment.

### Third Normal Form

Non-key attributes depend only on their table's key. Manufacturer names are stored in `Manufacturer` instead of being repeated in product rows, and category-specific product attributes are kept in their appropriate subtype tables.

These choices reduce duplicated data and prevent update, insertion, and deletion anomalies.

## Constraints and Assumptions

- Every product belongs to exactly one product subtype.
- Every product is associated with one or more animal types.
- Customer email addresses and animal type names are unique.
- Transaction and shipment quantities must be greater than zero.
- A shipment's origin and destination must be different locations.
- Product weights consist of a numeric value and unit.
- Prices, payments, inventory levels, addresses, and shipment dates are outside the given requirements.

## What I Learned

I learned how normalization converts business requirements into entities with clear responsibilities. Shared product information belongs in a parent table, while subtype tables are useful when different categories require different attributes.

I also learned why many-to-many relationships require junction tables. `ProductAnimal`, `TransactionItem`, and `ShipmentItem` keep the schema relational while allowing each association to carry its own rules or data.

## Reflection

The most important design decision was separating common product attributes from food, toy, and apparel details. Repeating the name and manufacturer in every category table would make queries and future updates harder to maintain.

The transaction and shipment requirements also showed that an ERD should model business events as well as objects. The line-item tables capture the products involved in each event and provide the correct place for quantities.

This exercise improved my ability to identify entities, choose primary and foreign keys, assign relationship cardinalities, and explain how a normalized schema protects data consistency.
