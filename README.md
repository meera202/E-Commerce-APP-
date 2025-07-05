# E-Commerce-APP-
this is a simple E-Commerce JAVA APP 

to run this project : 

1-you should install java SDK from here : https://developers.redhat.com/products/openjdk/   

2-clone the code on your PC 

3-on terminal (ex-VS code terminal ,...) run this command : 

& "C:\Program Files (x86)\Java\jdk1.8.0_60\bin\javac.exe" ECommerceApp.java

java ECommerceApp

*note: this path to JAVA JDK on your PC : "C:\Program Files (x86)\Java\jdk1.8.0_60\bin\javac.exe"

============= the output :

=== Test 1: Valid Order ===

** Shipment notice **

2x Cheese 400g

1x Biscuits 700g

1x TV 10000g

Total package weight 11.1kg

** Checkout receipt **

2x Cheese 200

1x Biscuits 150

1x TV 1500

----------------------
Subtotal 1850

Shipping 333

Amount 2183

=== Test 2: Invalid Order - Quantity Exceeds Stock ===

Checkout error: Not enough stock.

=== Test 3: Invalid Order - Product Out of Stock During Checkout ===

** Shipment notice **

6x Cheese 1200g

Total package weight 1.2kg

** Checkout receipt **

6x Cheese 600

----------------------

Subtotal 600

Shipping 36

Amount 636

Checkout error: Product out of stock: Cheese




