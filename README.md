# Coupons Management System

## Overview
The Coupons Management System is a RESTful API designed for managing discount coupons in an e-commerce platform. It offers full CRUD operations on coupons and supports applying these coupons to shopping carts based on various criteria. The system ensures a smooth, scalable, and maintainable solution while handling real-world constraints like coupon expiration, product-specific discounts, and Buy X Get Y (BxGy) deals.

## Technologies used :
Java, Springboot, Hibernate, Spring Data JPA, MySQL


## Functionality

1. **Coupon Management**
   - **Create Coupon:** Create new coupons with different types (cart-wise, product-wise, bxgy) and define associated rules, including thresholds, discounts, and expiration dates. if expiration date is not provided then by defailt a expire date of 30 days from the created date will be considered.
   -     http://localhost:8080/coupons - POST (For Cart-wise, Product-wise & Buy X Get Y)
        <img width="960" alt="Screenshot 2025-01-26 215026" src="https://github.com/user-attachments/assets/bd5b82cf-9cea-4b4e-b50f-3c548b28b330" />
        <img width="960" alt="Screenshot 2025-01-26 215136" src="https://github.com/user-attachments/assets/40b961a4-e572-4c8c-84ca-5b58f0ecf15f" />


    - **Get All Coupons:** Fetch a list of all available coupons.
     
          http://localhost:8080/coupons - GET
      <img width="960" alt="Screenshot 2025-01-26 215814" src="https://github.com/user-attachments/assets/32a41dbf-f081-4aac-90ee-2a1952518423" />


   - **Get Coupon by ID:** Retrieve a coupon by its unique identifier.
     
          http://localhost:8080/coupons/1 - GET
      <img width="960" alt="Screenshot 2025-01-26 215858" src="https://github.com/user-attachments/assets/b4ac0e41-b9db-4245-bf2a-d4101d506a04" />


   - **Update Coupon:** Modify existing coupons by changing their type, rules, or expiration date.
     
         http://localhost:8080/coupons/1 - PUT (To Update)
       <img width="960" alt="Screenshot 2025-01-26 220012" src="https://github.com/user-attachments/assets/37966059-502b-4726-a447-06350a346da6" />

   - **Delete Coupon:** Delete coupons by their unique ID, preventing them from being applied to future carts.
     
          http://localhost:8080/coupons/3 - DELETE (To Delete)
       <img width="960" alt="Screenshot 2025-01-26 224812" src="https://github.com/user-attachments/assets/2a2f85ab-8fdb-4010-bc61-7d984a40954c" />

  
2. **Coupon Application**
   - **Get Applicable Coupons:** Based on the contents of a cart, the system checks which coupons are eligible and returns a list of those that can be applied.
     
          http://localhost:8080/coupons/applicable-coupons - POST
       <img width="960" alt="Screenshot 2025-01-26 220202" src="https://github.com/user-attachments/assets/6c9a709f-df16-4799-b172-38b769c722a4" />


   - **Apply Coupon:** Apply a coupon for cart items. If coupon is expired then coupon cannot be applied and appropriate message is returned to User.
     
         http://localhost:8080/coupons/apply-coupon/2 - POST 
     <img width="960" alt="Screenshot 2025-01-26 220333" src="https://github.com/user-attachments/assets/f3e62c88-3ce3-48d3-9d5f-e73e6d9d2456" />

     <img width="960" alt="Screenshot 2025-01-26 220426" src="https://github.com/user-attachments/assets/bd31035e-3589-40b3-831e-0711a9705cc8" />





3. **Expiration Management**
   - Automatically ignore coupons that are past their expiration date when fetching applicable coupons.

4. **Logging**
   - Integrated logging for monitoring API calls, including coupon creation, application, and error tracking.

5. **Junit**
   - Implement unit tests for your methods using JUnit and Mockito for all methods in service  `CouponService`,`CouponManagerService`,`DiscountHelperService`.
   <img width="960" alt="Screenshot 2025-01-26 222527" src="https://github.com/user-attachments/assets/631a7e49-cc96-48b0-be00-1255adaaead5" />



## Edge Cases
The following edge cases have been considered and documented to ensure that the system is robust and handles real-world scenarios effectively.

### 1. **Coupon Constraints**

   - **Expired Coupons:**
     - Coupons that have passed their expiration date should not be considered when determining applicable coupons.
     - Coupons applied manually after expiration should result in a rejection response with a proper error message.

   - **Threshold Violations (CART_WISE Coupons):**
     - CART_WISE coupons require a minimum cart total (threshold) to be met. If the total value of the cart is below the defined threshold, the coupon should not be applicable.

   - **Product Availability (PRODUCT_WISE Coupons):**
     - For PRODUCT_WISE coupons, the coupon should only be applied if the specific product exists in the cart and the quantity of the product meets the required amount (if specified).

   - **Buy X Get Y (BxGy Coupons):**
     - All products required for the Buy X part of the coupon should be present in the cart with the specified quantities. If any product is missing or the quantity is insufficient, the coupon should not apply.
     - Get Y products should be correctly added to the cart, and their discount calculated according to the coupon's definition.

### 2. **Null Handling**

   - **Null Cart or Cart Items:**
     - If the cart is null or if the list of cart items is null, an appropriate error should be thrown, preventing the system from processing incomplete data.

   - **Null Coupon Details:**
     - Coupons with missing or incomplete details (e.g., missing discount percentages or product IDs) should be ignored when determining applicable coupons or applying discounts.

### 3. **Discount Calculation**

   - **Zero or Negative Discounts:**
     - Ensure the system gracefully handles scenarios where a coupon has a 0% or negative discount value. Such coupons should either be ignored or result in no change to the cart total.

   - **Percentage Discounts on Small Items:**
     - Consider edge cases where a percentage discount results in very small fractional amounts. For example, rounding errors when applying a 5% discount on a $0.99 product.

### 4. **Database Integrity and Performance**

   - **Database Indexing:**
     - Coupons should be indexed by relevant fields, such as `expire_at` which improves the lookups while fetching for non-expired coupons.
     - Indexing can prevent performance degradation, especially when dealing with large datasets (e.g., thousands of coupons in a busy e-commerce site).

   - **Concurrent Modifications:**
     - Handle concurrent access to the same coupon or cart (e.g., two users trying to apply different coupons simultaneously). Use optimistic locking or other techniques to ensure data integrity.


### 5. **Expired Coupon Detection and Error Messaging**

   - **Invalid Coupons:**
     - Proper handling of invalid coupons should include meaningful error messages when:
     - A coupon does not exist.
     - A coupon is expired.
     - A coupon does not meet cart requirements (e.g., thresholds, required items).
  
   - **Error Logging:**
     - Ensure that errors, especially related to coupon application and business logic, are logged for future diagnostics.

  
---

## Limitations and Assumptions

- **Assumption:** The coupon details in the database are correctly formatted and do not contain erroneous data.
- **Assumption:** To create product wise coupons the product exists in the Database. 
- **Assumption:** The system assumes the cart data is always complete and up to date, reflecting the latest product prices and quantities.
- **Limitation:** The current implementation does not handle user-specific coupon limits (e.g., restricting the number of times a user can apply a specific coupon).
- **Limitation:** The performance optimizations for large-scale datasets (e.g., caching, asynchronous processing) can be implemented.
  
---
## Unimplemented RestAPIs and Functionalities:

> Note : These are the cases which I have the approach & thought to implement but couldn't implement due to time constraints. 

* GET __/api/coupons/bxgy :__ The implementaion of fetching a bxgy type coupon which includes deserialization of List<Product> object.

* GET __/api/coupons/apply-coupon/bxgy/1 :__ Check for the buy products and  apply the coupon and calculate the discount,totalPrice & finalPrice if there are any getProducts present in the cart. 
---
## Conclusion
The Coupons Management System is designed to be scalable, maintainable, and efficient, handling various real-world constraints like expiration dates, product-specific discounts, and BxGy deals. This README outlines the system's core functionality, along with comprehensive documentation of edge cases to ensure robustness. The limitations and assumptions section provides clarity on the current scope and future enhancement opportunities.
