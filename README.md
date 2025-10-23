# MediPort

## Overview
MediPort is a web-based pharmacy inventory management system designed to address the inefficiencies of manual inventory tracking and purchasing in pharmacies. The platform empowers pharmacies to manage medicine stocks, orders, and supplier communications with a modern, user-friendly interface. By streamlining workflows, MediPort helps reduce stockouts, enhance data accuracy, and improve patient care delivery.

## System Purpose:
- Pharmacies track medicine inventory and place orders when stock runs low
- Suppliers fulfill orders and maintain medicine catalogs
- Organizations oversee multiple pharmacies and send alerts when medicines fall below minimum thresholds

# The evolution of Idea
- https://www.indiatoday.in/india/story/no-medicine-shortage-patients-last-stage-maharashtra-nanded-government-hospital-deaths-2443689-2023-10-03
- https://www.indiaspend.com/more-indians-die-of-poor-quality-care-than-due-to-lack-of-access-to-healthcare-1-6-million-64432/
- https://www.biospectrumindia.com/news/17/14709/27-deaths-in-india-caused-by-poor-access-to-drugs-and-knowledge-medicus-.html

These where the main reasons which paved for the development of this project

## Key User Flows:
- Pharmacy Flow: Signup → Profile setup → View stock → Place orders → Receive messages from org
- Supplier Flow: Login → View orders → Update order status → Manage medicine catalog
- Organization Flow: Login → View low stock alerts → Inspect pharmacy inventory → Send messages → Adjust minimum thresholds

## Desired End State - Spring Boot Application
### Technology Stack:
- Framework: Spring Boot 3.x
- View Layer: Thymeleaf (equivalent to EJS)
- Database: MySQL (same database, restructured schema)
- ORM: Spring Data JPA with Hibernate
- Security: Spring Security with BCrypt password encoding
- Session Management: Spring Session
- Build Tool: Maven
- Java Version: Java 17+
### Architecture Pattern:
- Layered architecture: Controller → Service → Repository → Entity
- Proper separation of concerns
- RESTful principles where applicable
- DTO pattern for data transfer
- Normalized database schema (eliminating dynamic table creation)
### Key Architectural Decisions:
- ✅ View Technology: Server-side rendering with Thymeleaf (matching current EJS approach)
- ✅ Database Design: Normalized schema with proper foreign keys (eliminating dynamic tables)
- ✅ Authentication: Spring Security with form-based login
- ✅ Session Management: Replace global variables with proper session attributes
- ✅ WebSocket: REMOVE - Not actively used in current implementation

## Architecture

![Architecture Diagram](IMG/System%20arch.png)

## UML Diagram

![UML DIAGRAM](IMG/UML.png)


